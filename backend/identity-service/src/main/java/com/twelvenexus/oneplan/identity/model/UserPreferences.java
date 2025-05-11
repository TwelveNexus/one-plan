package com.twelvenexus.oneplan.identity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    @Id
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String theme = "light";
    
    private String language = "en";
    
    @Column(columnDefinition = "TEXT")
    private String notificationSettings = "{}"; // JSON string for notification settings
}
