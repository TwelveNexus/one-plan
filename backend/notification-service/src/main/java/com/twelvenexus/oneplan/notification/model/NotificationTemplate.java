package com.twelvenexus.oneplan.notification.model;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "notification_templates")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationTemplate {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @EqualsAndHashCode.Include
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;
    
    @Column(nullable = false)
    private String language = "en";
    
    @Column(nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String bodyTemplate;
    
    @ElementCollection
    @CollectionTable(name = "template_variables")
    @MapKeyColumn(name = "variable_name")
    @Column(name = "description")
    private Map<String, String> variables;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
