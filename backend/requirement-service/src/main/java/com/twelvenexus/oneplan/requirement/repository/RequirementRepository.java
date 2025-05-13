package com.twelvenexus.oneplan.requirement.repository;

import com.twelvenexus.oneplan.requirement.model.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, String> {
    
    List<Requirement> findByProjectIdAndIsDeletedFalse(String projectId);
    
    List<Requirement> findByProjectIdAndStatusAndIsDeletedFalse(String projectId, String status);
    
    List<Requirement> findByProjectIdAndCategoryAndIsDeletedFalse(String projectId, String category);
    
    @Query("SELECT r FROM Requirement r WHERE r.projectId = :projectId " +
           "AND r.isDeleted = false " +
           "AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Requirement> searchByProjectId(@Param("projectId") String projectId, @Param("search") String search);
    
    Optional<Requirement> findByIdAndIsDeletedFalse(String id);
}
