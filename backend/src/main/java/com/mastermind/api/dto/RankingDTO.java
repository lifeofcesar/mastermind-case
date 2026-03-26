package com.mastermind.api.dto;

import java.time.LocalDateTime;

public record RankingDTO(
    String username,
    Long durationInSeconds,
    Integer attempts,
    LocalDateTime date
) {}