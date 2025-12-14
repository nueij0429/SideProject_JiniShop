package com.jinishop.jinishop.auth.repository;

import java.util.Optional;

import com.jinishop.jinishop.auth.domain.RefreshToken;
import com.jinishop.jinishop.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);
}