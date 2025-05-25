package com.twelvenexus.oneplan.identity.service;

import com.twelvenexus.oneplan.identity.config.JwtConfig;
import com.twelvenexus.oneplan.identity.dto.AuthResponse;
import com.twelvenexus.oneplan.identity.dto.LoginRequest;
import com.twelvenexus.oneplan.identity.dto.SignupRequest;
import com.twelvenexus.oneplan.identity.dto.UserDto;
import com.twelvenexus.oneplan.identity.model.User;
import com.twelvenexus.oneplan.identity.repository.UserRepository;
import com.twelvenexus.oneplan.identity.security.CustomUserDetails;
import com.twelvenexus.oneplan.identity.security.JwtTokenProvider;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider tokenProvider;
  private final JwtConfig jwtConfig;

  public AuthResponse login(LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    // Update last login time
    User user =
        userRepository
            .findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
    user.setLastLogin(LocalDateTime.now());
    userRepository.save(user);

    String accessToken = tokenProvider.generateToken(authentication);
    String refreshToken = tokenProvider.generateRefreshToken(userDetails.getUsername());

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(jwtConfig.getTokenValidityInSeconds())
        .user(mapUserToDto(user))
        .build();
  }

  public AuthResponse signup(SignupRequest signupRequest) {
    if (userRepository.existsByEmail(signupRequest.getEmail())) {
      throw new RuntimeException("Email is already in use");
    }

    // Create a new user
    User user = new User();
    user.setFirstName(signupRequest.getFirstName());
    user.setLastName(signupRequest.getLastName());
    user.setEmail(signupRequest.getEmail());
    user.setPasswordHash(passwordEncoder.encode(signupRequest.getPassword()));
    user.setStatus(User.UserStatus.ACTIVE);

    Set<String> roles = new HashSet<>();
    roles.add("ROLE_USER");
    user.setRoles(roles);

    user = userRepository.save(user);

    // Generate tokens
    String accessToken = tokenProvider.generateToken(user.getEmail());
    String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(jwtConfig.getTokenValidityInSeconds())
        .user(mapUserToDto(user))
        .build();
  }

  public AuthResponse refreshToken(String refreshToken) {
    if (!tokenProvider.validateToken(refreshToken)) {
      throw new RuntimeException("Invalid refresh token");
    }

    String email = tokenProvider.getEmailFromToken(refreshToken);
    User user =
        userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    String accessToken = tokenProvider.generateToken(email);
    String newRefreshToken = tokenProvider.generateRefreshToken(email);

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(newRefreshToken)
        .expiresIn(jwtConfig.getTokenValidityInSeconds())
        .user(mapUserToDto(user))
        .build();
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
