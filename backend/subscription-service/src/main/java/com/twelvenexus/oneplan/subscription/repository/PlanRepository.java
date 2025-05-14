package com.twelvenexus.oneplan.subscription.repository;

import com.twelvenexus.oneplan.subscription.model.Plan;
import com.twelvenexus.oneplan.subscription.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {

    Optional<Plan> findByCode(String code);

    List<Plan> findByActiveTrue();

    List<Plan> findByActiveTrueOrderBySortOrder();

    List<Plan> findByTypeAndActiveTrue(PlanType type);

    @Query("SELECT p FROM Plan p WHERE p.active = true AND p.popular = true ORDER BY p.sortOrder")
    List<Plan> findPopularPlans();

    boolean existsByCode(String code);
}
