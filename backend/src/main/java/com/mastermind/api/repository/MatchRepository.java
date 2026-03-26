package com.mastermind.api.repository;

import com.mastermind.api.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, String> {
    List<Match> findAllByUserIdOrderByStartedAtDesc(Long userId);
    List<Match> findTop10ByStatusOrderByDurationInSecondsAsc(String status);
}