package com.twelvenexus.oneplan.integration.repository;

import com.twelvenexus.oneplan.integration.model.GitCommit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitCommitRepository extends JpaRepository<GitCommit, String> {
    
    List<GitCommit> findByConnectionIdOrderByAuthorDateDesc(String connectionId);
    
    Optional<GitCommit> findByConnectionIdAndCommitSha(String connectionId, String commitSha);
    
    List<GitCommit> findByConnectionIdAndBranch(String connectionId, String branch);
}
