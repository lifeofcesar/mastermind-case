package com.mastermind.api.dto;

import java.time.LocalDateTime;

public record MatchHistoryDTO(
    String id,
    Integer score,
    String status,
    LocalDateTime date
) {}