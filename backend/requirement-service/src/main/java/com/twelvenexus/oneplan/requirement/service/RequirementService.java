package com.twelvenexus.oneplan.requirement.service;

import com.twelvenexus.oneplan.requirement.model.Requirement;
import java.util.List;

public interface RequirementService {
    Requirement createRequirement(Requirement requirement);
    Requirement updateRequirement(String id, Requirement requirement);
    Requirement getRequirement(String id);
    List<Requirement> getRequirementsByProject(String projectId);
    List<Requirement> getRequirementsByProjectAndStatus(String projectId, String status);
    List<Requirement> searchRequirements(String projectId, String search);
    void deleteRequirement(String id);
    Requirement analyzeRequirement(String id);
}
