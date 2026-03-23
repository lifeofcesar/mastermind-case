package com.mastermind.api.repository;

import com.mastermind.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailOrUsername(String email, String username);
    List<User> findTop10ByOrderByBestScoreDesc();
}