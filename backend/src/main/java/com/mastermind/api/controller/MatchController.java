package com.mastermind.api.controller;

import com.mastermind.api.dto.FeedbackDTO;
import com.mastermind.api.dto.GuessDTO;
import com.mastermind.api.model.User;
import com.mastermind.api.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startMatch(@AuthenticationPrincipal User user) {
        Map<String, String> response = matchService.startNewMatch(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{matchId}/guess")
    public ResponseEntity<FeedbackDTO> submitGuess(
            @PathVariable String matchId,
            @RequestBody @Valid GuessDTO guessDTO,
            @AuthenticationPrincipal User user) {
        
        FeedbackDTO feedback = matchService.submitAttempt(matchId, user, guessDTO);
        return ResponseEntity.ok(feedback);
    }
}