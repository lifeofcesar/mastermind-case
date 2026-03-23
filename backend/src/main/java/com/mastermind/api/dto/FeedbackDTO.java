package com.mastermind.api.dto;

public record FeedbackDTO(
        int exactMatches, 
        int partialMatches, 
        String matchStatus
) {}