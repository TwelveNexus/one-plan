package com.twelvenexus.oneplan.identity.repository;

import com.twelvenexus.oneplan.identity.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);
}
