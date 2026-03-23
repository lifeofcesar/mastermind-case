package com.mastermind.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record GuessDTO(
        @NotNull(message = "A tentativa não pode ser nula")
        @Size(min = 4, max = 4, message = "A tentativa deve ter exatamente 4 cores/letras")
        List<String> combination
) {}