package com.twelvenexus.oneplan.task.repository;

import com.twelvenexus.oneplan.task.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTaskIdOrderByCreatedAtDesc(UUID taskId);
    List<Comment> findByParentIdOrderByCreatedAtAsc(UUID parentId);
    List<Comment> findByAuthorId(UUID authorId);
}
