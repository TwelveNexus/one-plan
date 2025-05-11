package com.twelvenexus.oneplan.identity.service;

import com.twelvenexus.oneplan.identity.dto.UserDto;
import com.twelvenexus.oneplan.identity.model.User;
import com.twelvenexus.oneplan.identity.model.UserPreferences;
import com.twelvenexus.oneplan.identity.repository.UserPreferencesRepository;
import com.twelvenexus.oneplan.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapUserToDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return mapUserToDto(user);
    }

    @Transactional
    public UserDto updateUser(UUID id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setAvatar(userDto.getAvatar());

        return mapUserToDto(userRepository.save(user));
    }

    @Transactional
    public void changePassword(UUID id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserPreferences getUserPreferences(UUID userId) {
        return userPreferencesRepository.findById(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
    }

    @Transactional
    public UserPreferences updateUserPreferences(UUID userId, UserPreferences preferences) {
        UserPreferences existingPreferences = userPreferencesRepository.findById(userId)
                .orElseGet(() -> createDefaultPreferences(userId));

        existingPreferences.setTheme(preferences.getTheme());
        existingPreferences.setLanguage(preferences.getLanguage());
        existingPreferences.setNotificationSettings(preferences.getNotificationSettings());

        return userPreferencesRepository.save(existingPreferences);
    }

    private UserPreferences createDefaultPreferences(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserPreferences preferences = new UserPreferences();
        preferences.setUser(user);
        return userPreferencesRepository.save(preferences);
    }

    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .status(user.getStatus().name())
                .roles(user.getRoles())
                .build();
    }
}
