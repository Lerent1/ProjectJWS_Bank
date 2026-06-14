package org.example.projectjws_bank.config;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        //PUBLIC
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // ADMIN
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/v1/kyc/upload")
                        .hasRole("CUSTOMER")

                        // TAFF
                        .requestMatchers("/api/v1/kyc/**")
                        .hasAnyRole("STAFF", "ADMIN")

                        // USER MANAGEMENT
                        .requestMatchers("/api/v1/users/**")
                        .hasRole("ADMIN")

                        // CUSTOMER
                        .requestMatchers("/api/v1/accounts/**")
                        .hasRole("CUSTOMER")

                        .requestMatchers("/api/v1/transactions/**")
                        .hasRole("CUSTOMER")

                        // DEFAULT
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}