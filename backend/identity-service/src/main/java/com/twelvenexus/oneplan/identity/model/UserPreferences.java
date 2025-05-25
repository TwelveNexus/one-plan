package com.twelvenexus.oneplan.identity.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
  @Id
  @Column(name = "user_id", columnDefinition = "BINARY(16)")
  private UUID userId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
  private User user;

  private String theme = "light";

  private String language = "en";

  @Column(columnDefinition = "TEXT")
  private String notificationSettings = "{}"; // JSON string for notification settings
}
