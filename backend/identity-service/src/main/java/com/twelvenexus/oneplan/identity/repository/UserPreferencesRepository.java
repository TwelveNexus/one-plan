package com.twelvenexus.oneplan.identity.repository;

import com.twelvenexus.oneplan.identity.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {
}
