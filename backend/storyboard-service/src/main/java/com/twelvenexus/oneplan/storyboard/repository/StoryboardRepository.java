package com.twelvenexus.oneplan.storyboard.repository;

import com.twelvenexus.oneplan.storyboard.model.Storyboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryboardRepository extends JpaRepository<Storyboard, String> {
    
    List<Storyboard> findByProjectId(String projectId);
    
    List<Storyboard> findByProjectIdAndStatus(String projectId, String status);
    
    Optional<Storyboard> findByShareToken(String shareToken);
    
    @Query("SELECT s FROM Storyboard s WHERE s.projectId = :projectId " +
           "AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Storyboard> searchByProjectId(@Param("projectId") String projectId, @Param("search") String search);
    
    List<Storyboard> findByCreatedBy(String userId);
}
