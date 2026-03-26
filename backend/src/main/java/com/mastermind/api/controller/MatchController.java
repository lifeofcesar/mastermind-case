package com.mastermind.api.controller;

import com.mastermind.api.dto.FeedbackDTO;
import com.mastermind.api.dto.GuessDTO;
import com.mastermind.api.dto.MatchHistoryDTO;
import com.mastermind.api.model.User;
import com.mastermind.api.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        return ResponseEntity.ok(matchService.startNewMatch(user));
    }

    @PostMapping("/{id}/guess")
    public ResponseEntity<FeedbackDTO> submitGuess(@PathVariable String id, @RequestBody GuessDTO guessDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(matchService.submitAttempt(id, user, guessDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMatchState(@PathVariable String id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(matchService.getMatchState(id, user));
    }

    @PostMapping("/{id}/surrender")
    public ResponseEntity<Map<String, String>> surrenderMatch(@PathVariable String id, @AuthenticationPrincipal User user) {
        matchService.surrenderMatch(id, user);
        return ResponseEntity.ok(Map.of("message", "Partida encerrada"));
    }
    @GetMapping("/history")
    public ResponseEntity<List<MatchHistoryDTO>> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(matchService.getUserHistory(user));
    }
}