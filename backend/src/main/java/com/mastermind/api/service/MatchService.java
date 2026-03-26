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
    private static final int MAX_ATTEMPTS = 10;

    public MatchService(MatchRepository matchRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Map<String, String> startNewMatch(User user) {
        List<Match> userMatches = matchRepository.findAllByUserIdOrderByStartedAtDesc(user.getId());
        for (Match m : userMatches) {
            if ("IN_PROGRESS".equals(m.getStatus())) {
                m.setStatus("LOST");
                finalizeMatch(m, MAX_ATTEMPTS, user);
                matchRepository.save(m);
            }
        }

        // REGRA CLÁSSICA: Sorteia 4 cores independentes (Permite repetição)
        List<String> secretCodeList = new ArrayList<>();
        Random random = new Random();
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

        if (!match.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("Acesso negado a esta partida");
        if (!"IN_PROGRESS".equals(match.getStatus())) throw new IllegalArgumentException("Partida já finalizada");

        try {
            List<String> secret = objectMapper.readValue(match.getSecretCode(), new TypeReference<>() {});
            List<List<String>> history = objectMapper.readValue(match.getAttemptsMatrix(), new TypeReference<>() {});

            if (history.size() >= MAX_ATTEMPTS) throw new IllegalArgumentException("Limite de tentativas excedido");

            List<String> guess = guessDTO.combination();
            if (guess == null || guess.size() != 4) throw new IllegalArgumentException("O palpite deve conter exatamente 4 posições");

            int exactMatches = 0;
            int partialMatches = 0;
            
            boolean[] secretUsed = new boolean[4];
            boolean[] guessUsed = new boolean[4];
            String[] letterStatuses = new String[]{"WRONG", "WRONG", "WRONG", "WRONG"};

            for (int i = 0; i < 4; i++) {
                if (guess.get(i).equals(secret.get(i))) {
                    exactMatches++; secretUsed[i] = true; guessUsed[i] = true; letterStatuses[i] = "EXACT";
                }
            }

            for (int i = 0; i < 4; i++) {
                if (!guessUsed[i]) {
                    for (int j = 0; j < 4; j++) {
                        if (!secretUsed[j] && guess.get(i).equals(secret.get(j))) {
                            partialMatches++; secretUsed[j] = true; letterStatuses[i] = "PARTIAL"; break;
                        }
                    }
                }
            }

            history.add(guess);
            match.setAttemptsMatrix(objectMapper.writeValueAsString(history));

            if (exactMatches == 4) {
                match.setStatus("WON");
                finalizeMatch(match, history.size(), user);
            } else if (history.size() >= MAX_ATTEMPTS) {
                match.setStatus("LOST");
                finalizeMatch(match, MAX_ATTEMPTS, user);
            }

            matchRepository.save(match);
            return new FeedbackDTO(exactMatches, partialMatches, match.getStatus(), Arrays.asList(letterStatuses));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar tentativa", e);
        }
    }

    public Map<String, Object> getMatchState(String matchId, User user) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("Partida não encontrada"));
        if (!match.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("Acesso negado");

        try {
            List<String> secret = objectMapper.readValue(match.getSecretCode(), new TypeReference<>() {});
            List<List<String>> history = objectMapper.readValue(match.getAttemptsMatrix(), new TypeReference<>() {});

            List<Map<String, Object>> attemptsData = new ArrayList<>();
            for (List<String> guess : history) {
                int exactMatches = 0; int partialMatches = 0;
                boolean[] secretUsed = new boolean[4]; boolean[] guessUsed = new boolean[4];
                String[] letterStatuses = new String[]{"WRONG", "WRONG", "WRONG", "WRONG"};

                for (int i = 0; i < 4; i++) {
                    if (guess.get(i).equals(secret.get(i))) {
                        exactMatches++; secretUsed[i] = true; guessUsed[i] = true; letterStatuses[i] = "EXACT";
                    }
                }
                for (int i = 0; i < 4; i++) {
                    if (!guessUsed[i]) {
                        for (int j = 0; j < 4; j++) {
                            if (!secretUsed[j] && guess.get(i).equals(secret.get(j))) {
                                partialMatches++; secretUsed[j] = true; letterStatuses[i] = "PARTIAL"; break;
                            }
                        }
                    }
                }
                Map<String, Object> attempt = new HashMap<>();
                attempt.put("guess", guess);
                attempt.put("feedback", new FeedbackDTO(exactMatches, partialMatches, match.getStatus(), Arrays.asList(letterStatuses)));
                attemptsData.add(attempt);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("matchId", match.getId());
            response.put("status", match.getStatus());
            response.put("attemptsHistory", attemptsData);
            
            long elapsed = ChronoUnit.SECONDS.between(match.getStartedAt(), LocalDateTime.now());
            if(match.getFinishedAt() != null) elapsed = match.getDurationInSeconds();
            response.put("secondsElapsed", elapsed);

            return response;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao carregar estado da partida", e);
        }
    }

    @Transactional
    public void surrenderMatch(String matchId, User user) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("Partida não encontrada"));
        if (!match.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("Acesso negado");
        
        if ("IN_PROGRESS".equals(match.getStatus())) {
            match.setStatus("LOST");
            int attempts = 0;
            try {
                List<List<String>> history = objectMapper.readValue(match.getAttemptsMatrix(), new TypeReference<>() {});
                attempts = history.size();
            } catch (Exception e) {}
            finalizeMatch(match, attempts, user);
            matchRepository.save(match);
        }
    }

    private void finalizeMatch(Match match, int attempts, User user) {
        match.setFinishedAt(LocalDateTime.now());
        long seconds = ChronoUnit.SECONDS.between(match.getStartedAt(), match.getFinishedAt());
        match.setDurationInSeconds(seconds);
        int score = Math.max(0, 1000 - (attempts * 60) - (int)(seconds * 2));
        match.setFinalScore(score);

        if (user.getBestScore() == null || score > user.getBestScore()) {
            user.setBestScore(score);
            userRepository.save(user);
        }
    }

    public List<MatchHistoryDTO> getUserHistory(User user) {
        return matchRepository.findAllByUserIdOrderByStartedAtDesc(user.getId())
                .stream()
                .map(m -> {
                    int attempts = 0;
                    try {
                        List<List<String>> history = objectMapper.readValue(m.getAttemptsMatrix(), new TypeReference<>() {});
                        attempts = history.size();
                    } catch (Exception e) {}
                    return new MatchHistoryDTO(m.getId(), m.getFinalScore(), attempts, m.getStatus(), m.getStartedAt(), m.getDurationInSeconds());
                })
                .toList();
    }

    public List<RankingDTO> getGlobalRanking() {
        return matchRepository.findTop10ByStatusOrderByDurationInSecondsAsc("WON")
                .stream()
                .map(m -> {
                    int attempts = 0;
                    try {
                        List<List<String>> history = objectMapper.readValue(m.getAttemptsMatrix(), new TypeReference<>() {});
                        attempts = history.size();
                    } catch (Exception e) {}
                    return new RankingDTO(m.getUser().getUsername(), m.getDurationInSeconds(), attempts, m.getFinishedAt());
                })
                .toList();
    }
}