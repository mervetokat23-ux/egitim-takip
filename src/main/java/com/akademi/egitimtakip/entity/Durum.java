package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Durum Entity
 * 
 * Eğitim durumlarını temsil eder. Egitim ile ilişkilidir.
 * H2 database'de otomatik olarak tablo oluşturulur.
 */
@Entity
@Table(name = "durum")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Durum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String durum; // Örn: "Planlandı", "Devam Ediyor", "Tamamlandı", "İptal"

    @Column(length = 100)
    private String operasyon; // Örn: "Ekleme", "Güncelleme", "Silme"

    // Many-to-One relationship with Egitim
    // Bir durum bir eğitime ait olabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "egitim_id", nullable = false)
    private Egitim egitim;
}

