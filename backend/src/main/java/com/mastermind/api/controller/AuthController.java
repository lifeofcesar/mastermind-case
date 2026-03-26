package com.mastermind.api.controller;

import com.mastermind.api.dto.LoginDTO;
import com.mastermind.api.dto.RegisterDTO;
import com.mastermind.api.model.User;
import com.mastermind.api.repository.UserRepository;
import com.mastermind.api.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO data) {
        try {
            // Tenta fazer a autenticação com o Spring Security
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());
            
            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException e) {
            // ERRO ESPERADO: Senha errada ou usuário não existe (Retorna 401)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "E-mail, usuário ou senha incorretos."));

        } catch (Exception e) {
            // BUG NO CÓDIGO (Ex: NullPointer, ClassCast, Banco Duplicado) (Retorna 500)
            e.printStackTrace(); // Imprime a linha exata do erro no terminal do Spring Boot
            
            // Manda a CAUSA EXATA do erro para a tela vermelha do Angular
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "ERRO FATAL: " + e.toString()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO data) {
        if (userRepository.existsByEmail(data.email())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Este e-mail já está em uso."));
        }
        if (userRepository.existsByUsername(data.username())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Este nome de usuário já está em uso."));
        }

        User newUser = User.builder()
                .username(data.username())
                .email(data.email())
                .password(passwordEncoder.encode(data.password()))
                .build();

        userRepository.save(newUser);
        return ResponseEntity.ok(Map.of("message", "Usuário registrado com sucesso!"));
    }
}