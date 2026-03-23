package com.mastermind.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "O login (email ou username) é obrigatório") 
        String login,
        
        @NotBlank(message = "A senha é obrigatória") 
        String password
) {}