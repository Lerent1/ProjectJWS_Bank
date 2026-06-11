package org.example.projectjws_bank.config;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.model.entity.Account;
import org.example.projectjws_bank.model.entity.Role;
import org.example.projectjws_bank.model.entity.User;
import org.example.projectjws_bank.repository.AccountRepository;
import org.example.projectjws_bank.repository.RoleRepository;
import org.example.projectjws_bank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {

            // ===== ROLE =====
            if (!roleRepository.existsByRoleName("ROLE_ADMIN")) {
                roleRepository.save(Role.builder().roleName("ROLE_ADMIN").build());
            }

            if (!roleRepository.existsByRoleName("ROLE_STAFF")) {
                roleRepository.save(Role.builder().roleName("ROLE_STAFF").build());
            }

            if (!roleRepository.existsByRoleName("ROLE_CUSTOMER")) {
                roleRepository.save(Role.builder().roleName("ROLE_CUSTOMER").build());
            }

            // ===== ADMIN =====
            if (!userRepository.existsByUsername("admin")) {

                Role adminRole = roleRepository
                        .findByRoleName("ROLE_ADMIN")
                        .orElseThrow();

                User admin = User.builder()
                        .fullName("System Admin")
                        .email("admin@gmail.com")
                        .username("admin")
                        .password(passwordEncoder.encode("123456"))
                        .enabled(true)
                        .isKyc(true)
                        .createdAt(LocalDateTime.now())
                        .role(adminRole)
                        .build();

                userRepository.save(admin);

                //TẠO ACCOUNT CHO ADMIN
                Account account = Account.builder()
                        .accountNumber("ADMIN001")
                        .pinCode(passwordEncoder.encode("1234"))
                        .balance(BigDecimal.ZERO)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .user(admin)
                        .build();

                accountRepository.save(account);

                admin.setAccount(account);
            }
        };
    }
}