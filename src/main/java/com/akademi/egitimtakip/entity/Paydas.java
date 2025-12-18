package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Paydas Entity
 * 
 * Eğitim paydaşlarını (stakeholder) temsil eder. 
 * Bir paydaş birden fazla eğitimle ve projeyle ilişkilendirilebilir.
 */
@Entity
@Table(name = "paydas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paydas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String ad;

    @Column(length = 200)
    private String email;

    @Column(length = 20)
    private String telefon;

    @Column(length = 500)
    private String adres;

    @Column(length = 100)
    private String tip; // Örn: "Kurum", "Birey", "STK", "Kamu" vb.

    // Many-to-many relationship with Egitim
    // Join table will be created automatically: egitim_paydas
    @ManyToMany(mappedBy = "paydaslar")
    private Set<Egitim> egitimler = new HashSet<>();

    // One-to-many relationship with Proje
    // Bir paydaş birden fazla projede yer alabilir (Proje tarafında paydas_id FK var)
    @OneToMany(mappedBy = "paydas", cascade = CascadeType.ALL)
    private Set<Proje> projeler = new HashSet<>();
}
