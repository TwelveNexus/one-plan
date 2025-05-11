package com.twelvenexus.oneplan.tenant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "domain", nullable = false, unique = true)
    private String domain;
    
    @Column(name = "plan", nullable = false)
    private String plan;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantStatus status;
    
    @Column(name = "settings", columnDefinition = "JSON")
    private String settings;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum TenantStatus {
        ACTIVE, SUSPENDED, TRIALING
    }
}
