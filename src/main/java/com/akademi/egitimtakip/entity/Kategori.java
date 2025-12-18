package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Kategori Entity
 * 
 * Eğitim kategorilerini temsil eder. Bir kategori birden fazla eğitime atanabilir (many-to-many).
 * Ayrıca kendi içinde hiyerarşik yapıya sahiptir (parent-child).
 */
@Entity
@Table(name = "kategori")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Kategori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String ad;

    @Column(length = 500)
    private String aciklama;

    // Self-referencing relationship for parent category
    @ManyToOne
    @JoinColumn(name = "ust_kategori_id")
    private Kategori ustKategori;

    // Self-referencing relationship for sub-categories
    @OneToMany(mappedBy = "ustKategori", cascade = CascadeType.ALL)
    private Set<Kategori> altKategoriler = new HashSet<>();

    // Many-to-many relationship with Egitim
    @ManyToMany(mappedBy = "kategoriler")
    private Set<Egitim> egitimler = new HashSet<>();
}
