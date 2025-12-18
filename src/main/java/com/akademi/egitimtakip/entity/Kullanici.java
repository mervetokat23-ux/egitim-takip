package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Kullanici Entity
 * 
 * Sistem kullanıcılarını temsil eder. JWT authentication için kullanılır.
 * H2 database'de otomatik olarak tablo oluşturulur.
 */
@Entity
@Table(name = "kullanici")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Kullanici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ad_soyad", nullable = false, length = 200)
    private String adSoyad;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "sifre_hash", nullable = false, length = 255)
    private String sifreHash; // BCrypt ile hash'lenmiş şifre

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(nullable = false)
    private Boolean durum = true; // Aktif/Pasif durumu

    @Column(name = "son_giris_tarihi")
    private LocalDateTime sonGirisTarihi;

    // Link to Sorumlu for role-based access
    @Column(name = "sorumlu_id")
    private Long sorumluId;

    // Optional: username field for login
    @Column(name = "kullanici_adi", length = 100, unique = true)
    private String kullaniciAdi;
}

