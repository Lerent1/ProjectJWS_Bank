package org.example.projectjws_bank.service;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.model.entity.User;
import org.example.projectjws_bank.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay user"));

        String role = user.getRole()
                .getRoleName()
                .replace("ROLE_", "");

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}