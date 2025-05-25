package com.twelvenexus.oneplan.identity.security;

import com.twelvenexus.oneplan.identity.model.User;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
  private UUID id;
  private String email;
  private String password;
  private boolean active;
  private Collection<? extends GrantedAuthority> authorities;

  public static CustomUserDetails build(User user) {
    List<GrantedAuthority> authorities =
        user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toList());

    return new CustomUserDetails(
        user.getId(),
        user.getEmail(),
        user.getPasswordHash(),
        user.getStatus() == User.UserStatus.ACTIVE,
        authorities);
  }

  public CustomUserDetails(
      UUID id,
      String email,
      String password,
      boolean active,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.active = active;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public UUID getId() {
    return id;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return active;
  }
}
