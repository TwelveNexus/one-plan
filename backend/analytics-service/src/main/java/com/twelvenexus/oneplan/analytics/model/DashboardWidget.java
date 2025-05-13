package com.twelvenexus.oneplan.analytics.model;

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
@Table(name = "dashboard_widgets")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DashboardWidget {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_id", nullable = false)
    private Dashboard dashboard;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type; // chart, metric, table, etc.

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    @ElementCollection
    @CollectionTable(name = "widget_configuration")
    @MapKeyColumn(name = "config_key")
    @Column(name = "config_value", length = 1000)
    private Map<String, String> configuration;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
