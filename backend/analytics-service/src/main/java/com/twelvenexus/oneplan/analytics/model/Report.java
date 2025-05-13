package com.twelvenexus.oneplan.analytics.model;

import com.twelvenexus.oneplan.analytics.enums.ReportType;
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
@Table(name = "reports")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Report {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @ElementCollection
    @CollectionTable(name = "report_parameters")
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value", length = 1000)
    private Map<String, String> parameters;

    @Column(nullable = false)
    private UUID createdBy;

    private String schedule; // Cron expression for scheduled reports

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime lastRunAt;

    private LocalDateTime nextRunAt;

    @Column(columnDefinition = "TEXT")
    private String lastRunResult;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
