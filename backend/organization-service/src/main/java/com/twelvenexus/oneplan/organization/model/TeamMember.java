package com.twelvenexus.oneplan.organization.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_member")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TeamMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "team_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID teamId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;
    
    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;
    
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamMemberRole role;
    
    @Column(name = "permissions", columnDefinition = "JSON")
    private String permissions;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "invited_by", columnDefinition = "BINARY(16)")
    private UUID invitedBy;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamMemberStatus status;
    
    public enum TeamMemberRole {
        MEMBER, ADMIN, GUEST
    }
    
    public enum TeamMemberStatus {
        ACTIVE, INACTIVE, PENDING
    }
}
