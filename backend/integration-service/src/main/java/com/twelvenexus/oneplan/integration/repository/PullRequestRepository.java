package com.twelvenexus.oneplan.integration.repository;

import com.twelvenexus.oneplan.integration.model.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, String> {
    
    List<PullRequest> findByConnectionIdOrderByCreatedDateDesc(String connectionId);
    
    Optional<PullRequest> findByConnectionIdAndPrNumber(String connectionId, Integer prNumber);
    
    List<PullRequest> findByConnectionIdAndState(String connectionId, String state);
}
