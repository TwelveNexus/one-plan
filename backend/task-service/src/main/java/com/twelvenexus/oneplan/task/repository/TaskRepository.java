package com.twelvenexus.oneplan.task.repository;

import com.twelvenexus.oneplan.task.model.Task;
import com.twelvenexus.oneplan.task.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectId(UUID projectId);
    List<Task> findByAssigneeId(UUID assigneeId);
    List<Task> findByProjectIdAndStatus(UUID projectId, TaskStatus status);
    List<Task> findByParentId(UUID parentId);
    
    @Query("SELECT t FROM Task t WHERE t.projectId = :projectId AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Task> searchByTitleOrDescription(UUID projectId, String searchTerm);
}
