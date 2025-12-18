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
 * Proje Entity
 * 
 * Projeleri temsil eder. Egitim, Sorumlu ve Paydas ile ilişkilidir.
 */
@Entity
@Table(name = "proje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String isim;

    @Column(name = "baslangic_tarihi")
    private LocalDate baslangicTarihi;

    @Column(name = "tarih")
    private LocalDate tarih;

    @Column(name = "proje_hakkinda", length = 2000)
    private String projeHakkinda;

    // Many-to-One relationship with Sorumlu (Egitim Sorumlu)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "egitim_sorumlu_id")
    private Sorumlu egitimSorumlu;

    // Many-to-One relationship with Paydas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paydas_id")
    private Paydas paydas;

    // One-to-Many relationship with Faaliyet
    @OneToMany(mappedBy = "proje", cascade = CascadeType.ALL)
    private Set<Faaliyet> faaliyetler = new HashSet<>();
}
