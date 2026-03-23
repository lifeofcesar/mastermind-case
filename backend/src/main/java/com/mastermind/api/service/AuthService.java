package com.mastermind.api.service;

import com.mastermind.api.dto.LoginDTO;
import com.mastermind.api.dto.RegisterDTO;
import com.mastermind.api.model.User;
import com.mastermind.api.repository.UserRepository;
import com.mastermind.api.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public User register(RegisterDTO data) {
        if (userRepository.findByEmailOrUsername(data.email(), data.username()).isPresent()) {
            throw new IllegalArgumentException("Email ou Username já cadastrado.");
        }
        
        User newUser = User.builder()
                .username(data.username())
                .email(data.email())
                .password(passwordEncoder.encode(data.password()))
                .bestScore(0)
                .build();
                
        return userRepository.save(newUser);
    }

    public String login(LoginDTO data) {
        User user = userRepository.findByEmailOrUsername(data.login(), data.login())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        return tokenService.generateToken(user);
    }
}