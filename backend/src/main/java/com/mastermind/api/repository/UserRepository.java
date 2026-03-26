package com.mastermind.api.repository;

import com.mastermind.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailOrUsername(String email, String username);
    
    List<User> findTop10ByOrderByBestScoreDesc();

    // NOVOS MÉTODOS PARA VALIDAÇÃO DE DUPLICATAS
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}