package com.mastermind.api.dto;

import java.time.LocalDateTime;

public record MatchHistoryDTO(
    String id,
    Integer score,
    Integer attempts,
    String status,
    LocalDateTime date,
    Long durationInSeconds // NOVO: Tempo gasto na partida
) {}