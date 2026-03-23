package com.mastermind.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastermind.api.dto.FeedbackDTO;
import com.mastermind.api.dto.GuessDTO;
import com.mastermind.api.model.Match;
import com.mastermind.api.model.User;
import com.mastermind.api.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final ObjectMapper objectMapper;
    private static final List<String> AVAILABLE_COLORS = Arrays.asList("A", "B", "C", "D", "E", "F");
    private static final int MAX_ATTEMPTS = 10;

    public MatchService(MatchRepository matchRepository, ObjectMapper objectMapper) {
        this.matchRepository = matchRepository;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> startNewMatch(User user) {
        // Gera a combinação secreta de 4 letras
        Random random = new Random();
        List<String> secretCodeList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            secretCodeList.add(AVAILABLE_COLORS.get(random.nextInt(AVAILABLE_COLORS.size())));
        }
        
        String secretCode;
        try {
            secretCode = objectMapper.writeValueAsString(secretCodeList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao gerar código secreto", e);
        }

        Match match = Match.builder()
                .user(user)
                .secretCode(secretCode)
                .attemptsMatrix("[]") // Inicia com array JSON vazio
                .status("IN_PROGRESS")
                .build();

        match = matchRepository.save(match);
        return Map.of("matchId", match.getId());
    }

    public FeedbackDTO submitAttempt(String matchId, User user, GuessDTO guessDTO) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Partida não encontrada"));

        if (!match.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Esta partida pertence a outro jogador");
        }

        if (!"IN_PROGRESS".equals(match.getStatus())) {
            throw new IllegalArgumentException("Esta partida já foi encerrada");
        }

        try {
            // Recupera a combinação secreta e o histórico de tentativas do banco
            List<String> secret = objectMapper.readValue(match.getSecretCode(), new TypeReference<>() {});
            List<List<String>> history = objectMapper.readValue(match.getAttemptsMatrix(), new TypeReference<>() {});

            if (history.size() >= MAX_ATTEMPTS) {
                throw new IllegalArgumentException("Número máximo de tentativas alcançado");
            }

            // Lógica do Mastermind: Conta acertos exatos e parciais
            List<String> guess = guessDTO.combination();
            int exactMatches = 0;
            int partialMatches = 0;
            
            boolean[] secretUsed = new boolean[4];
            boolean[] guessUsed = new boolean[4];

            // 1ª Passagem: Posições exatas
            for (int i = 0; i < 4; i++) {
                if (guess.get(i).equals(secret.get(i))) {
                    exactMatches++;
                    secretUsed[i] = true;
                    guessUsed[i] = true;
                }
            }

            // 2ª Passagem: Cores corretas na posição errada
            for (int i = 0; i < 4; i++) {
                if (!guessUsed[i]) {
                    for (int j = 0; j < 4; j++) {
                        if (!secretUsed[j] && guess.get(i).equals(secret.get(j))) {
                            partialMatches++;
                            secretUsed[j] = true;
                            break;
                        }
                    }
                }
            }

            // Atualiza histórico
            history.add(guess);
            match.setAttemptsMatrix(objectMapper.writeValueAsString(history));

            // Verifica condições de vitória ou derrota
            if (exactMatches == 4) {
                match.setStatus("WON");
                finalizeMatch(match, history.size());
            } else if (history.size() >= MAX_ATTEMPTS) {
                match.setStatus("LOST");
                finalizeMatch(match, MAX_ATTEMPTS);
            }

            matchRepository.save(match);
            return new FeedbackDTO(exactMatches, partialMatches, match.getStatus());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar dados da partida", e);
        }
    }

    private void finalizeMatch(Match match, int totalAttempts) {
        match.setFinishedAt(LocalDateTime.now());
        long seconds = ChronoUnit.SECONDS.between(match.getStartedAt(), match.getFinishedAt());
        match.setDurationInSeconds(seconds);
        
        // Exemplo simples de pontuação: 1000 base - (tentativas * 50) - (segundos * 2)
        int score = 1000 - (totalAttempts * 50) - (int)(seconds * 2);
        match.setFinalScore(Math.max(score, 0)); // Evita pontuação negativa
    }
}