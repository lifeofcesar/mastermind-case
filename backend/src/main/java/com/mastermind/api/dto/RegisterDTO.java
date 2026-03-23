package com.mastermind.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank(message = "O username é obrigatório") 
        String username,
        
        @NotBlank(message = "O email é obrigatório") 
        @Email(message = "Formato de email inválido") 
        String email,
        
        @NotBlank(message = "A senha é obrigatória") 
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") 
        String password
) {}