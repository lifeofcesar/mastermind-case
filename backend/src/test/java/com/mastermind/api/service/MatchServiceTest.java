package com.mastermind.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastermind.api.dto.FeedbackDTO;
import com.mastermind.api.dto.GuessDTO;
import com.mastermind.api.model.Match;
import com.mastermind.api.model.User;
import com.mastermind.api.repository.MatchRepository;
import com.mastermind.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper(); // Usamos o real para testar o JSON de verdade

    @InjectMocks
    private MatchService matchService;

    private User testUser;
    private Match testMatch;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("hacker_master");

        testMatch = new Match();
        testMatch.setId("match-123");
        testMatch.setUser(testUser);
        testMatch.setStatus("IN_PROGRESS");
        testMatch.setSecretCode("[\"A\",\"B\",\"C\",\"D\"]"); // Código secreto fixo para os testes
        testMatch.setAttemptsMatrix("[]");
        testMatch.setStartedAt(LocalDateTime.now());
    }

    @Test
    void submitAttempt_DeveRetornarVitoria_QuandoPalpiteForExato() {
        // Arrange (Prepara o cenário)
        when(matchRepository.findById("match-123")).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);
        GuessDTO guess = new GuessDTO(List.of("A", "B", "C", "D")); // Palpite perfeito

        // Act (Executa a ação)
        FeedbackDTO feedback = matchService.submitAttempt("match-123", testUser, guess);

        // Assert (Verifica o resultado)
        assertEquals(4, feedback.exactMatches());
        assertEquals(0, feedback.partialMatches());
        assertEquals("WON", feedback.matchStatus());
    }

    @Test
    void submitAttempt_DeveRetornarParciais_QuandoCoresCertasNaPosicaoErrada() {
        // Arrange
        when(matchRepository.findById("match-123")).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);
        GuessDTO guess = new GuessDTO(List.of("D", "C", "B", "A")); // Tudo invertido

        // Act
        FeedbackDTO feedback = matchService.submitAttempt("match-123", testUser, guess);

        // Assert
        assertEquals(0, feedback.exactMatches());
        assertEquals(4, feedback.partialMatches());
        assertEquals("IN_PROGRESS", feedback.matchStatus());
    }

    @Test
    void submitAttempt_DeveLancarExcecao_QuandoPalpiteInvalido() {
        // Arrange
        when(matchRepository.findById("match-123")).thenReturn(Optional.of(testMatch));
        GuessDTO guess = new GuessDTO(List.of("A", "B")); // Faltando cores (só 2)

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchService.submitAttempt("match-123", testUser, guess);
        });

        assertEquals("O palpite deve conter exatamente 4 posições", exception.getMessage());
    }
}