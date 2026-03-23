package com.mastermind.api.controller;

import com.mastermind.api.dto.MatchHistoryDTO;
import com.mastermind.api.dto.RankingDTO;
import com.mastermind.api.model.User;
import com.mastermind.api.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RankingController {

    private final MatchService matchService;

    public RankingController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<MatchHistoryDTO>> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(matchService.getUserHistory(user));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingDTO>> getRanking() {
        return ResponseEntity.ok(matchService.getGlobalRanking());
    }
}