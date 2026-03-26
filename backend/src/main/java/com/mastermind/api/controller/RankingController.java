package com.mastermind.api.controller;

import com.mastermind.api.dto.RankingDTO;
import com.mastermind.api.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final MatchService matchService;

    public RankingController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<RankingDTO>> getGlobalRanking() {
        return ResponseEntity.ok(matchService.getGlobalRanking());
    }
}