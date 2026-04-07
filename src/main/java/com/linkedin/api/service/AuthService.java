package com.linkedin.api.service;

import com.linkedin.api.dto.request.AuthRequest;
import com.linkedin.api.dto.response.AuthResponse;
import com.linkedin.api.entity.User;
import com.linkedin.api.exception.BadRequestException;
import com.linkedin.api.exception.ConflictException;
import com.linkedin.api.repository.mysql.UserRepository;
import com.linkedin.api.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(AuthRequest.Register req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email already registered: " + req.getEmail());
        }

        User user = User.builder()
                .email(req.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .build();

        userRepository.save(user);

        String access  = jwtUtils.generateAccessToken(user.getEmail());
        String refresh = jwtUtils.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public AuthResponse login(AuthRequest.Login req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

            User user = userRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            String access  = jwtUtils.generateAccessToken(user.getEmail());
            String refresh = jwtUtils.generateRefreshToken(user.getEmail());

            return AuthResponse.builder()
                    .accessToken(access)
                    .refreshToken(refresh)
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        }
    }

    public AuthResponse refreshToken(AuthRequest.RefreshToken req) {
        String token = req.getRefreshToken();
        if (!jwtUtils.validateToken(token)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        String email = jwtUtils.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return AuthResponse.builder()
                .accessToken(jwtUtils.generateAccessToken(email))
                .refreshToken(jwtUtils.generateRefreshToken(email))
                .userId(user.getUserId())
                .email(email)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
