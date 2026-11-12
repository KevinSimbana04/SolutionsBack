package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.user.ActivateToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivateTokenRepository  extends JpaRepository<ActivateToken, Long> {
    Optional<ActivateToken> findByToken(String token);
}
