package com.twelvenexus.oneplan.identity.controller;

import com.twelvenexus.oneplan.identity.dto.ChangePasswordRequest;
import com.twelvenexus.oneplan.identity.dto.UserDto;
import com.twelvenexus.oneplan.identity.model.UserPreferences;
import com.twelvenexus.oneplan.identity.security.SecurityUtils;
import com.twelvenexus.oneplan.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isCurrentUser(#id)")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        // Get the current user's email from the security context
        // This is just a placeholder - you'll need to implement this
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isCurrentUser(#id)")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isCurrentUser(#id)")
    public ResponseEntity<Void> changePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isCurrentUser(#id)")
    public ResponseEntity<UserPreferences> getUserPreferences(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserPreferences(id));
    }

    @PutMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN') or @securityExpressions.isCurrentUser(#id)")
    public ResponseEntity<UserPreferences> updateUserPreferences(
            @PathVariable UUID id,
            @Valid @RequestBody UserPreferences preferences) {
        return ResponseEntity.ok(userService.updateUserPreferences(id, preferences));
    }
}
