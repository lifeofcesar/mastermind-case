package com.mastermind.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // código único da partida

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String secretCode; // a resposta esperada gerada pelo backend

    @Column(columnDefinition = "TEXT")
    private String attemptsMatrix; // Histórico de tentativas em JSON

    private Integer finalScore;
    
    private Long durationInSeconds; // tempo de duração do jogo

    private String status; // IN_PROGRESS, WON, LOST

    @Column(updatable = false)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        status = "IN_PROGRESS";
    }
}