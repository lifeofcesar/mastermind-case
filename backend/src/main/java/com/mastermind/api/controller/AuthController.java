package com.mastermind.api.controller;

import com.mastermind.api.dto.LoginDTO;
import com.mastermind.api.dto.RegisterDTO;
import com.mastermind.api.model.User;
import com.mastermind.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        User newUser = authService.register(data);
        return ResponseEntity.ok(Map.of(
                "message", "Usuário criado com sucesso", 
                "username", newUser.getUsername()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO data) {
        String token = authService.login(data);
        return ResponseEntity.ok(Map.of("token", token));
    }
}