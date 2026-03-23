package com.mastermind.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastermind.api.dto.FeedbackDTO;
import com.mastermind.api.dto.GuessDTO;
import com.mastermind.api.model.Match;
import com.mastermind.api.model.User;
import com.mastermind.api.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    // Usamos o objeto real para testar a serialização/deserialização do JSON de verdade
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private MatchService matchService;

    private User mockUser;
    private Match mockMatch;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().id(1L).username("testjogador").build();
        
        // Criamos uma partida mockada com a resposta secreta "A, B, C, D"
        mockMatch = Match.builder()
                .id("partida-123")
                .user(mockUser)
                .secretCode("[\"A\",\"B\",\"C\",\"D\"]")
                .attemptsMatrix("[]")
                .status("IN_PROGRESS")
                .startedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldStartNewMatch() {
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> {
            Match m = i.getArgument(0);
            m.setId("nova-partida-id");
            return m;
        });

        var result = matchService.startNewMatch(mockUser);

        assertNotNull(result.get("matchId"));
        assertEquals("nova-partida-id", result.get("matchId"));
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void shouldCalculateExactAndPartialMatchesCorrectly() {
        when(matchRepository.findById("partida-123")).thenReturn(Optional.of(mockMatch));
        
        // O jogador chuta: A (certo no lugar certo), C (certo no lugar errado), F (errado), D (certo no lugar certo)
        GuessDTO guess = new GuessDTO(Arrays.asList("A", "C", "F", "D"));
        
        FeedbackDTO feedback = matchService.submitAttempt("partida-123", mockUser, guess);

        assertEquals(2, feedback.exactMatches(), "Deveria ter 2 acertos exatos (A e D)");
        assertEquals(1, feedback.partialMatches(), "Deveria ter 1 acerto parcial (C)");
        assertEquals("IN_PROGRESS", feedback.matchStatus());
    }

    @Test
    void shouldWinMatchOnCorrectGuess() {
        when(matchRepository.findById("partida-123")).thenReturn(Optional.of(mockMatch));
        
        // Jogador acerta tudo
        GuessDTO guess = new GuessDTO(Arrays.asList("A", "B", "C", "D"));
        
        FeedbackDTO feedback = matchService.submitAttempt("partida-123", mockUser, guess);

        assertEquals(4, feedback.exactMatches());
        assertEquals("WON", feedback.matchStatus());
        assertEquals("WON", mockMatch.getStatus());
        assertNotNull(mockMatch.getFinishedAt());
    }

    @Test
    void shouldLoseMatchOnTenthAttempt() {
        // Simulamos que o banco de dados já salvou 9 tentativas anteriores em JSON
        String nineAttempts = "[[\"F\",\"F\",\"F\",\"F\"],[\"F\",\"F\",\"F\",\"F\"],[\"F\",\"F\",\"F\",\"F\"],[\"F\",\"F\",\"F\",\"F\"],[\"F\",\"F\",\"F\",\"F\"],[\"F\",\"F\",\"F\",\"F\"],[\"F\",\"F\",\"F\",\"F\"],[\"F\",\"F\",\"F\",\"F\"],[\"F\",\"F\",\"F\",\"F\"]]";
        mockMatch.setAttemptsMatrix(nineAttempts);
        
        when(matchRepository.findById("partida-123")).thenReturn(Optional.of(mockMatch));
        
        // Jogador erra a 10ª tentativa
        GuessDTO guess = new GuessDTO(Arrays.asList("E", "E", "E", "E"));
        
        FeedbackDTO feedback = matchService.submitAttempt("partida-123", mockUser, guess);

        assertEquals("LOST", feedback.matchStatus());
        assertEquals("LOST", mockMatch.getStatus());
    }
}