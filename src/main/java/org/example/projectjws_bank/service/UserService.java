package org.example.projectjws_bank.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.exception.BadRequestException;
import org.example.projectjws_bank.exception.NotFoundException;
import org.example.projectjws_bank.model.dto.request.RegisterRequest;
import org.example.projectjws_bank.model.dto.request.UserUpdateRequest;
import org.example.projectjws_bank.model.dto.response.RegisterResponse;
import org.example.projectjws_bank.model.dto.response.UserResponse;
import org.example.projectjws_bank.model.entity.Account;
import org.example.projectjws_bank.model.entity.Role;
import org.example.projectjws_bank.model.entity.User;
import org.example.projectjws_bank.repository.AccountRepository;
import org.example.projectjws_bank.repository.RoleRepository;
import org.example.projectjws_bank.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("Email da ton tai");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new BadRequestException("Username da ton tai");
        }

        Role role = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new NotFoundException("Role khong tim thay"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .createdAt(LocalDateTime.now())
                .role(role)
                .enabled(true)
                .isKyc(false)
                .build();

        userRepository.save(user);

        // TẠO ACCOUNT
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .pinCode(passwordEncoder.encode(request.getPinCode()))
                .balance(BigDecimal.ZERO)
                .active(true)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        accountRepository.save(account);

        user.setAccount(account);

        return new RegisterResponse(
                user.getId(),
                account.getAccountNumber()
        );
    }

    public Page<UserResponse> getAllUsers(int page, int size){
        Pageable pageable = PageRequest.of(page,size);

        return userRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public UserResponse getUserById(Long id){
        User user = userRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay user"));

        return toResponse(user);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay user"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEnabled(request.getEnabled());

        userRepository.save(user);

        return toResponse(user);
    }

    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay user"));

        userRepository.delete(user);
    }

    // FIX NPE
    private UserResponse toResponse(User user){

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .enabled(user.getEnabled())
                .isKyc(user.getIsKyc())
                .role(user.getRole().getRoleName())
                .accountNumber(user.getAccount() != null
                        ? user.getAccount().getAccountNumber()
                        : null
                )
                .build();
    }

    private String generateAccountNumber(){
        return "RB" + System.currentTimeMillis();
    }

    @Transactional
    public RegisterResponse createStaff(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("Email da ton tai");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new BadRequestException("Username da ton tai");
        }

        Role role = roleRepository.findByRoleName("ROLE_STAFF")
                .orElseThrow(() -> new NotFoundException("Role STAFF khong tim thay"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .createdAt(LocalDateTime.now())
                .role(role)
                .enabled(true)
                .isKyc(true)
                .build();

        userRepository.save(user);

        return new RegisterResponse(user.getId(), null);
    }
}