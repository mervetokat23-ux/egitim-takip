package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ActivityLog Entity
 * 
 * Kullanıcı aksiyonlarını kaydeder.
 * Hangi kullanıcı, hangi entity üzerinde ne yaptı gibi bilgileri tutar.
 */
@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action", length = 100, nullable = false)
    private String action; // CREATE, UPDATE, DELETE, VIEW, EXPORT, etc.

    @Column(name = "entity_type", length = 100)
    private String entityType; // Egitim, Sorumlu, Proje, etc.

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "description", length = 1000)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}





