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
 * Egitim Entity
 * 
 * Eğitimleri temsil eder. Kategori, Egitmen, Sorumlu ve Paydas ile many-to-many ilişkilere sahiptir.
 * Proje ile many-to-many ilişkisi vardır.
 */
@Entity
@Table(name = "egitim")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Egitim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String ad;

    @Column(name = "egitim_kodu", length = 50)
    private String egitimKodu; // Eğitim kodu

    @Column(name = "program_id", length = 50)
    private String programId; // Program ID

    @Column(length = 20)
    private String seviye; // Temel, Orta, İleri

    @Column(name = "hedef_kitle", length = 500)
    private String hedefKitle; // Virgülle ayrılmış: Ortaokul, Lise, Üniversite, Kurum Personeli, Mezun

    @Column(length = 1000)
    private String aciklama;

    @Column(name = "baslangic_tarihi")
    private LocalDate baslangicTarihi;

    @Column(name = "bitis_tarihi")
    private LocalDate bitisTarihi;

    @Column(name = "egitim_saati")
    private Integer egitimSaati; // Dakika cinsinden

    @Column(length = 50)
    private String durum; // Örn: "Planlandı", "Devam Ediyor", "Tamamlandı", "İptal"

    // Many-to-many relationship with Kategori
    @ManyToMany
    @JoinTable(
        name = "egitim_kategori",
        joinColumns = @JoinColumn(name = "egitim_id"),
        inverseJoinColumns = @JoinColumn(name = "kategori_id")
    )
    private Set<Kategori> kategoriler = new HashSet<>();

    // Many-to-many relationship with Egitmen
    @ManyToMany
    @JoinTable(
        name = "egitim_egitmen",
        joinColumns = @JoinColumn(name = "egitim_id"),
        inverseJoinColumns = @JoinColumn(name = "egitmen_id")
    )
    private Set<Egitmen> egitmenler = new HashSet<>();

    // Many-to-many relationship with Sorumlu
    @ManyToMany
    @JoinTable(
        name = "egitim_sorumlu",
        joinColumns = @JoinColumn(name = "egitim_id"),
        inverseJoinColumns = @JoinColumn(name = "sorumlu_id")
    )
    private Set<Sorumlu> sorumlular = new HashSet<>();

    // Many-to-many relationship with Paydas
    @ManyToMany
    @JoinTable(
        name = "egitim_paydas",
        joinColumns = @JoinColumn(name = "egitim_id"),
        inverseJoinColumns = @JoinColumn(name = "paydas_id")
    )
    private Set<Paydas> paydaslar = new HashSet<>();

    // Many-to-One relationship with Proje
    // Bir eğitim bir projeye ait olabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proje_id")
    private Proje proje;
}
