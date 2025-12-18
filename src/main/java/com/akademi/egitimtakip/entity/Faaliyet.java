package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Faaliyet Entity
 * 
 * Faaliyetleri temsil eder. Proje ve Sorumlu ile ilişkilidir.
 * Sorumlu ile many-to-many ilişki vardır (.cursorrules'a göre).
 * H2 database'de otomatik olarak tablo oluşturulur.
 */
@Entity
@Table(name = "faaliyet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Faaliyet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate tarih;

    @Column(nullable = false, length = 200)
    private String isim;

    @Column(length = 100)
    private String turu; // Örn: "Toplantı", "Eğitim", "Seminer", "Workshop"

    // Many-to-One relationship with Proje
    // Bir faaliyet bir projeye ait olabilir (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proje_id")
    private Proje proje;

    // Many-to-many relationship with Sorumlu
    // Join table: faaliyet_sorumlu (faaliyet_id, sorumlu_id)
    // Bir faaliyetten birden fazla sorumlu sorumlu olabilir, bir sorumlu birden fazla faaliyette görev alabilir
    @ManyToMany
    @JoinTable(
        name = "faaliyet_sorumlu",
        joinColumns = @JoinColumn(name = "faaliyet_id"),
        inverseJoinColumns = @JoinColumn(name = "sorumlu_id")
    )
    private Set<Sorumlu> sorumlular = new HashSet<>();
}

