package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * FrontendLog Entity
 * 
 * Frontend'den gelen kullanıcı aksiyonlarını kaydeder.
 * Kullanıcı davranışlarını analiz etmek için kullanılır.
 */
@Entity
@Table(name = "frontend_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FrontendLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action", length = 255, nullable = false)
    private String action; // BUTTON_CLICK, FORM_SUBMIT, PAGE_VIEW, etc.

    @Column(name = "page", length = 255)
    private String page;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}





