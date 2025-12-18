package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Egitmen Entity
 * 
 * Eğitmenleri temsil eder. Bir eğitmen birden fazla eğitimde görev alabilir (many-to-many).
 */
@Entity
@Table(name = "egitmen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Egitmen {

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

    @Column(length = 50)
    private String il;

    @Column(length = 200)
    private String calismaYeri;

    // Many-to-many relationship with Egitim
    // Join table will be created automatically: egitim_egitmen
    @ManyToMany(mappedBy = "egitmenler")
    private Set<Egitim> egitimler = new HashSet<>();
}

