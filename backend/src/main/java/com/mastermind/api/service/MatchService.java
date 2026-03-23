package com.mastermind.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastermind.api.dto.FeedbackDTO;
import com.mastermind.api.dto.GuessDTO;
import com.mastermind.api.dto.MatchHistoryDTO;
import com.mastermind.api.dto.RankingDTO;
import com.mastermind.api.model.Match;
import com.mastermind.api.model.User;
import com.mastermind.api.repository.MatchRepository;
import com.mastermind.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    private static final List<String> AVAILABLE_COLORS = Arrays.asList("A", "B", "C", "D", "E", "F");
    private static final int MAX_ATTEMPTS = 10; // Conforme requisito 3.4 [cite: 33]

    public MatchService(MatchRepository matchRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Map<String, String> startNewMatch(User user) {
        // Gera a combinação secreta de 4 letras aleatórias 
        Random random = new Random();
        List<String> secretCodeList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            secretCodeList.add(AVAILABLE_COLORS.get(random.nextInt(AVAILABLE_COLORS.size())));
        }
        
        try {
            String secretCode = objectMapper.writeValueAsString(secretCodeList);
            Match match = Match.builder()
                    .user(user)
                    .secretCode(secretCode)
                    .attemptsMatrix("[]")
                    .status("IN_PROGRESS")
                    .build();

            match = matchRepository.save(match);
            return Map.of("matchId", match.getId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao iniciar partida", e);
        }
    }

    @Transactional
    public FeedbackDTO submitAttempt(String matchId, User user, GuessDTO guessDTO) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Partida não encontrada"));

        if (!match.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Acesso negado a esta partida");
        }

        if (!"IN_PROGRESS".equals(match.getStatus())) {
            throw new IllegalArgumentException("Partida já finalizada");
        }

        try {
            List<String> secret = objectMapper.readValue(match.getSecretCode(), new TypeReference<>() {});
            List<List<String>> history = objectMapper.readValue(match.getAttemptsMatrix(), new TypeReference<>() {});

            if (history.size() >= MAX_ATTEMPTS) {
                throw new IllegalArgumentException("Limite de tentativas excedido");
            }

            // Lógica Mastermind: O backend responde apenas com o número de acertos 
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

            // 2ª Passagem: Cores corretas em posições erradas
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

            history.add(guess);
            match.setAttemptsMatrix(objectMapper.writeValueAsString(history));

            // Validação de fim de jogo [cite: 33]
            if (exactMatches == 4) {
                match.setStatus("WON");
                finalizeMatch(match, history.size(), user);
            } else if (history.size() >= MAX_ATTEMPTS) {
                match.setStatus("LOST");
                finalizeMatch(match, MAX_ATTEMPTS, user);
            }

            matchRepository.save(match);
            return new FeedbackDTO(exactMatches, partialMatches, match.getStatus());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar tentativa", e);
        }
    }

    private void finalizeMatch(Match match, int attempts, User user) {
        match.setFinishedAt(LocalDateTime.now());
        long seconds = ChronoUnit.SECONDS.between(match.getStartedAt(), match.getFinishedAt());
        match.setDurationInSeconds(seconds);
        
        // Cálculo de pontuação: prioriza menos tentativas e menos tempo 
        int score = Math.max(0, 1000 - (attempts * 60) - (int)(seconds * 2));
        match.setFinalScore(score);

        // Atualiza melhor pontuação do usuário se necessário 
        if (user.getBestScore() == null || score > user.getBestScore()) {
            user.setBestScore(score);
            userRepository.save(user);
        }
    }

    public List<MatchHistoryDTO> getUserHistory(User user) {
        return matchRepository.findAllByUserIdOrderByStartedAtDesc(user.getId())
                .stream()
                .map(m -> new MatchHistoryDTO(m.getId(), m.getFinalScore(), m.getStatus(), m.getStartedAt()))
                .toList();
    }

    public List<RankingDTO> getGlobalRanking() {
        return userRepository.findTop10ByOrderByBestScoreDesc()
                .stream()
                .map(u -> new RankingDTO(u.getUsername(), u.getBestScore()))
                .toList();
    }
}