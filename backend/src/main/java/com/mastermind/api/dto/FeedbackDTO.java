package com.mastermind.api.dto;

import java.util.List;

public record FeedbackDTO(
        int exactMatches, 
        int partialMatches, 
        String matchStatus,
        List<String> letterStatuses
) {}