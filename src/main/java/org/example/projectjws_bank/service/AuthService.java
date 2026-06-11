package org.example.projectjws_bank.service;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.exception.BadRequestException;
import org.example.projectjws_bank.exception.NotFoundException;
import org.example.projectjws_bank.model.dto.request.LoginRequest;
import org.example.projectjws_bank.model.dto.response.AuthResponse;
import org.example.projectjws_bank.model.entity.RefreshToken;
import org.example.projectjws_bank.repository.RefreshTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(token)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new NotFoundException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token het han");
        }

        String newAccessToken = jwtService.generateToken(
                userDetailsService.loadUserByUsername(
                        token.getUser().getUsername()
                )
        );

        return new AuthResponse(newAccessToken);
    }

    public void logout(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new NotFoundException("Token khong tim thay"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
