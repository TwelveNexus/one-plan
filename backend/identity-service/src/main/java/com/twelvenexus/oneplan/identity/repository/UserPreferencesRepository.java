package com.twelvenexus.oneplan.identity.repository;

import com.twelvenexus.oneplan.identity.model.UserPreferences;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {}
