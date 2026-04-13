package com.example.aimeetingtranscription.service;

import com.example.aimeetingtranscription.dto.auth.AuthResponse;
import com.example.aimeetingtranscription.dto.auth.LoginRequest;
import com.example.aimeetingtranscription.dto.auth.SignupRequest;
import com.example.aimeetingtranscription.entity.User;
import com.example.aimeetingtranscription.exception.AppException;
import com.example.aimeetingtranscription.repository.UserRepository;
import com.example.aimeetingtranscription.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse signup(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new AppException("Email already registered");
        }

        User user = userRepository.save(User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .build());

        return AuthResponse.builder()
                .email(user.getEmail())
                .token(jwtService.generateToken(user.getEmail()))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        return AuthResponse.builder()
                .email(email)
                .token(jwtService.generateToken(email))
                .build();
    }
}
