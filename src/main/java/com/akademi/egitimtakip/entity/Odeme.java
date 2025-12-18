package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Odeme Entity
 * 
 * Ödemeleri temsil eder. Egitim ve Sorumlu ile ilişkilidir.
 * H2 database'de otomatik olarak tablo oluşturulur.
 * 
 * Soft delete desteği: isDeleted = true olan kayıtlar silinmiş sayılır.
 */
@Entity
@Table(name = "odeme", indexes = {
    @Index(name = "idx_odeme_egitim_id", columnList = "egitim_id"),
    @Index(name = "idx_odeme_sorumlu_id", columnList = "sorumlu_id"),
    @Index(name = "idx_odeme_durum", columnList = "durum"),
    @Index(name = "idx_odeme_is_deleted", columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Odeme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "birim_ucret", precision = 10, scale = 2)
    private BigDecimal birimUcret;

    @Column(name = "toplam_ucret", precision = 10, scale = 2)
    private BigDecimal toplamUcret;

    @Column(name = "odeme_kaynagi", length = 200)
    private String odemeKaynagi;

    @Column(length = 50)
    private String durum; // Örn: "Beklemede", "Ödendi", "İptal"

    @Column(length = 100)
    private String operasyon; // Örn: "Havale", "Nakit", "POS", "Sistem içi"

    // Soft Delete
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // Timestamp fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-One relationship with Egitim
    // Bir ödeme bir eğitime ait olabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "egitim_id", nullable = false)
    private Egitim egitim;

    // Many-to-One relationship with Sorumlu
    // Bir ödemeden bir sorumlu sorumlu olabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sorumlu_id")
    private Sorumlu sorumlu;
}

