package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * PerformanceLog Entity
 * 
 * Yavaş çalışan işlemleri ve performans metriklerini kaydeder.
 * Performans optimizasyonu için kullanılır.
 */
@Entity
@Table(name = "performance_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(name = "duration_ms", nullable = false)
    private Long durationMs;

    @Column(name = "method_name", length = 255)
    private String methodName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}





