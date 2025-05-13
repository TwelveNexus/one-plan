package com.twelvenexus.oneplan.requirement.service.impl;

import com.twelvenexus.oneplan.requirement.exception.RequirementNotFoundException;
import com.twelvenexus.oneplan.requirement.model.Requirement;
import com.twelvenexus.oneplan.requirement.repository.RequirementRepository;
import com.twelvenexus.oneplan.requirement.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequirementServiceImpl implements RequirementService {
    
    private final RequirementRepository requirementRepository;
    
    @Override
    public Requirement createRequirement(Requirement requirement) {
        // TODO: Add more validation
        return requirementRepository.save(requirement);
    }
    
    @Override
    public Requirement updateRequirement(String id, Requirement requirement) {
        Requirement existing = getRequirement(id);
        existing.setTitle(requirement.getTitle());
        existing.setDescription(requirement.getDescription());
        existing.setCategory(requirement.getCategory());
        existing.setStatus(requirement.getStatus());
        existing.setPriority(requirement.getPriority());
        existing.setTags(requirement.getTags());
        existing.setVersion(existing.getVersion() + 1);
        return requirementRepository.save(existing);
    }
    
    @Override
    public Requirement getRequirement(String id) {
        return requirementRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RequirementNotFoundException("Requirement with id " + id + " not found"));
    }
    
    @Override
    public List<Requirement> getRequirementsByProject(String projectId) {
        return requirementRepository.findByProjectIdAndIsDeletedFalse(projectId);
    }
    
    @Override
    public List<Requirement> getRequirementsByProjectAndStatus(String projectId, String status) {
        return requirementRepository.findByProjectIdAndStatusAndIsDeletedFalse(projectId, status);
    }
    
    @Override
    public List<Requirement> searchRequirements(String projectId, String search) {
        return requirementRepository.searchByProjectId(projectId, search);
    }
    
    @Override
    public void deleteRequirement(String id) {
        Requirement requirement = getRequirement(id);
        requirement.setIsDeleted(true);
        requirementRepository.save(requirement);
    }
    
    @Override
    public Requirement analyzeRequirement(String id) {
        // TODO: Implement AI analysis
        Requirement requirement = getRequirement(id);
        // Placeholder for AI analysis
        requirement.setAiScore(85.0f);
        requirement.setAiSuggestions("{\"suggestions\": [\"Add more specific acceptance criteria\", \"Clarify user role\"]}");
        return requirementRepository.save(requirement);
    }
}
