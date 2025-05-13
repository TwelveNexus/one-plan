package com.twelvenexus.oneplan.integration.repository;

import com.twelvenexus.oneplan.integration.model.OAuthState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OAuthStateRepository extends JpaRepository<OAuthState, String> {
    
    Optional<OAuthState> findByState(String state);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
