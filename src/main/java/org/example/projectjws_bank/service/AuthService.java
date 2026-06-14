package org.example.projectjws_bank.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.exception.BadRequestException;
import org.example.projectjws_bank.exception.NotFoundException;
import org.example.projectjws_bank.model.dto.request.ForgotPasswordRequest;
import org.example.projectjws_bank.model.dto.request.LoginRequest;
import org.example.projectjws_bank.model.dto.response.AuthResponse;
import org.example.projectjws_bank.model.entity.RefreshToken;
import org.example.projectjws_bank.model.entity.TokenBlacklist;
import org.example.projectjws_bank.model.entity.User;
import org.example.projectjws_bank.repository.RefreshTokenRepository;
import org.example.projectjws_bank.repository.TokenBlacklistRepository;
import org.example.projectjws_bank.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    // LOGIN
    @Transactional
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User khong ton tai"));

        refreshTokenRepository.revokeAllByUser(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        String accessToken = jwtService.generateToken(userDetails);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    //  REFRESH
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new NotFoundException("Invalid refresh token"));

        if (Boolean.TRUE.equals(token.getRevoked())) {
            throw new BadRequestException("Token da bi revoke");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token het han");
        }

        User user = token.getUser();

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        String newAccessToken = jwtService.generateToken(
                userDetailsService.loadUserByUsername(user.getUsername())
        );

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User khong ton tai"));

        if (!user.getEmail().equals(request.getEmail())) {
            throw new BadRequestException("Email khong dung");
        }

        if (request.getNewPassword().length() < 6) {
            throw new BadRequestException("Password qua ngan");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // LOGOUT
//   (String authorizationHeader){
//   String token = extractBearer(authorizationHeader);

    @Transactional
    public void logout(String token) {

        if (token == null || token.isBlank()) {
            throw new BadRequestException("Access token khong duoc de trong");
        }

        if (tokenBlacklistRepository.existsByToken(token)) {
            throw new BadRequestException("Token da duoc logout truoc do");
        }

        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            throw new BadRequestException("Token khong hop le hoac da het han");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User khong ton tai"));

        refreshTokenRepository.revokeAllByUser(user);

        tokenBlacklistRepository.save(
                TokenBlacklist.builder()
                        .token(token)
                        .expiryAt(
                                LocalDateTime.ofInstant(
                                        jwtService.extractExpiration(token).toInstant(),
                                        java.time.ZoneId.systemDefault()
                                )
                        )
                        .user(user)
                        .build()
        );
    }
}
