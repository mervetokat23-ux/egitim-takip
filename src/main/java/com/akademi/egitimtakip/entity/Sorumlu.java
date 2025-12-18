package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sorumlu Entity
 * 
 * Eğitim ve faaliyetlerden sorumlu kişileri temsil eder. 
 * Bir sorumlu birden fazla eğitimde ve faaliyette görev alabilir (many-to-many).
 * Bir sorumlu birden fazla ünvana sahip olabilir (multi-select).
 */
@Entity
@Table(name = "sorumlu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sorumlu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String ad;

    @Column(nullable = false, length = 100)
    private String soyad;

    @Column(length = 200)
    private String email;

    @Column(length = 20)
    private String telefon;

    // ElementCollection: Birden fazla ünvan için
    // Ünvanlar: Müfredat Sorumlusu, Operasyon Sorumlusu, Proje Sorumlusu, TGTD, Medya Sorumlusu
    @ElementCollection
    @CollectionTable(name = "sorumlu_unvanlar", joinColumns = @JoinColumn(name = "sorumlu_id"))
    @Column(name = "unvan", length = 100)
    private List<String> unvanlar = new ArrayList<>();

    // Many-to-many relationship with Egitim
    // Join table will be created automatically: egitim_sorumlu
    @ManyToMany(mappedBy = "sorumlular")
    private Set<Egitim> egitimler = new HashSet<>();

    // Many-to-many relationship with Faaliyet
    // Join table will be created automatically: faaliyet_sorumlu
    @ManyToMany(mappedBy = "sorumlular")
    private Set<Faaliyet> faaliyetler = new HashSet<>();

    // Many-to-One relationship with Role
    // Bir sorumlu kişinin bir rolü olabilir (ADMIN, STAFF, READONLY)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
}

