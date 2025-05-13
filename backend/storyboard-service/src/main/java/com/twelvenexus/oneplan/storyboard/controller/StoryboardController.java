package com.twelvenexus.oneplan.storyboard.controller;

import com.twelvenexus.oneplan.storyboard.document.StoryboardCanvas;
import com.twelvenexus.oneplan.storyboard.dto.*;
import com.twelvenexus.oneplan.storyboard.mapper.StoryboardMapper;
import com.twelvenexus.oneplan.storyboard.model.Storyboard;
import com.twelvenexus.oneplan.storyboard.model.StoryCard;
import com.twelvenexus.oneplan.storyboard.model.StoryRelationship;
import com.twelvenexus.oneplan.storyboard.service.StoryboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/storyboards")
@RequiredArgsConstructor
@Tag(name = "Storyboards", description = "Storyboard management API")
public class StoryboardController {
    
    private final StoryboardService storyboardService;
    private final StoryboardMapper storyboardMapper;
    
    // Storyboard endpoints
    @PostMapping
    @Operation(summary = "Create a new storyboard")
    public ResponseEntity<StoryboardResponseDto> createStoryboard(@Valid @RequestBody CreateStoryboardDto dto) {
        Storyboard storyboard = storyboardMapper.toEntity(dto);
        Storyboard created = storyboardService.createStoryboard(storyboard);
        return ResponseEntity.status(HttpStatus.CREATED).body(storyboardMapper.toDto(created));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get storyboard by ID")
    public ResponseEntity<StoryboardResponseDto> getStoryboard(@PathVariable String id) {
        Storyboard storyboard = storyboardService.getStoryboard(id);
        return ResponseEntity.ok(storyboardMapper.toDto(storyboard));
    }
    
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all storyboards for a project")
    public ResponseEntity<List<StoryboardResponseDto>> getProjectStoryboards(@PathVariable String projectId) {
        List<Storyboard> storyboards = storyboardService.getStoryboardsByProject(projectId);
        List<StoryboardResponseDto> dtos = storyboards.stream()
                .map(storyboardMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a storyboard")
    public ResponseEntity<StoryboardResponseDto> updateStoryboard(
            @PathVariable String id,
            @Valid @RequestBody UpdateStoryboardDto dto) {
        Storyboard storyboard = storyboardService.getStoryboard(id);
        storyboardMapper.updateEntity(storyboard, dto);
        Storyboard updated = storyboardService.updateStoryboard(id, storyboard);
        return ResponseEntity.ok(storyboardMapper.toDto(updated));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a storyboard")
    public ResponseEntity<Void> deleteStoryboard(@PathVariable String id) {
        storyboardService.deleteStoryboard(id);
        return ResponseEntity.noContent().build();
    }
    
    // Story card endpoints
    @PostMapping("/cards")
    @Operation(summary = "Create a new story card")
    public ResponseEntity<StoryCardResponseDto> createStoryCard(@Valid @RequestBody CreateStoryCardDto dto) {
        StoryCard storyCard = storyboardMapper.toEntity(dto);
        StoryCard created = storyboardService.createStoryCard(storyCard);
        return ResponseEntity.status(HttpStatus.CREATED).body(storyboardMapper.toDto(created));
    }
    
    @GetMapping("/cards/{id}")
    @Operation(summary = "Get story card by ID")
    public ResponseEntity<StoryCardResponseDto> getStoryCard(@PathVariable String id) {
        StoryCard storyCard = storyboardService.getStoryCard(id);
        return ResponseEntity.ok(storyboardMapper.toDto(storyCard));
    }
    
    @GetMapping("/{storyboardId}/cards")
    @Operation(summary = "Get all story cards for a storyboard")
    public ResponseEntity<List<StoryCardResponseDto>> getStoryboardCards(@PathVariable String storyboardId) {
        List<StoryCard> cards = storyboardService.getStoryCardsByStoryboard(storyboardId);
        List<StoryCardResponseDto> dtos = cards.stream()
                .map(storyboardMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @DeleteMapping("/cards/{id}")
    @Operation(summary = "Delete a story card")
    public ResponseEntity<Void> deleteStoryCard(@PathVariable String id) {
        storyboardService.deleteStoryCard(id);
        return ResponseEntity.noContent().build();
    }
    
    // Canvas endpoints
    @GetMapping("/{storyboardId}/canvas")
    @Operation(summary = "Get canvas data for a storyboard")
    public ResponseEntity<StoryboardCanvas> getCanvas(@PathVariable String storyboardId) {
        StoryboardCanvas canvas = storyboardService.getCanvas(storyboardId);
        return ResponseEntity.ok(canvas);
    }
    
    @PutMapping("/{storyboardId}/canvas")
    @Operation(summary = "Update canvas data for a storyboard")
    public ResponseEntity<StoryboardCanvas> updateCanvas(
            @PathVariable String storyboardId,
            @RequestBody CanvasUpdateDto dto) {
        StoryboardCanvas canvas = new StoryboardCanvas();
        canvas.setCanvasSettings(dto.getCanvasSettings());
        canvas.setElements(dto.getElements());
        canvas.setConnections(dto.getConnections());
        canvas.setModifiedBy(dto.getModifiedBy());
        canvas.setLastModified(LocalDateTime.now());
        
        StoryboardCanvas saved = storyboardService.saveCanvas(storyboardId, canvas);
        return ResponseEntity.ok(saved);
    }
    
    // Sharing endpoints
    @PostMapping("/{id}/share")
    @Operation(summary = "Share a storyboard")
    public ResponseEntity<StoryboardResponseDto> shareStoryboard(
            @PathVariable String id,
            @RequestBody ShareStoryboardDto dto) {
        Storyboard shared = storyboardService.shareStoryboard(
                id, 
                dto.getPasswordProtected(), 
                dto.getPassword(), 
                dto.getExpiryDays()
        );
        return ResponseEntity.ok(storyboardMapper.toDto(shared));
    }
    
    @DeleteMapping("/{id}/share")
    @Operation(summary = "Revoke storyboard sharing")
    public ResponseEntity<Void> revokeShare(@PathVariable String id) {
        storyboardService.revokeShare(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/shared/{shareToken}")
    @Operation(summary = "Access shared storyboard")
    public ResponseEntity<StoryboardResponseDto> getSharedStoryboard(
            @PathVariable String shareToken,
            @RequestParam(required = false) String password) {
        Storyboard storyboard = storyboardService.getSharedStoryboard(shareToken, password);
        return ResponseEntity.ok(storyboardMapper.toDto(storyboard));
    }
}
