package com.akademi.egitimtakip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Egitim Request DTO
 * 
 * POST ve PUT endpoint'leri için eğitim verisi taşır.
 * H2 database ile uyumlu çalışır.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EgitimRequestDTO {

    @NotBlank(message = "Eğitim adı boş olamaz")
    @Size(max = 200, message = "Eğitim adı en fazla 200 karakter olabilir")
    private String ad;

    @Size(max = 50, message = "Eğitim kodu en fazla 50 karakter olabilir")
    private String egitimKodu; // Eğitim kodu

    @Size(max = 50, message = "Program ID en fazla 50 karakter olabilir")
    private String programId; // Program ID

    @Size(max = 20, message = "Seviye en fazla 20 karakter olabilir")
    private String seviye; // Temel, Orta, İleri

    @Size(max = 500, message = "Hedef kitle en fazla 500 karakter olabilir")
    private String hedefKitle; // Virgülle ayrılmış: Ortaokul, Lise, Üniversite, Kurum Personeli, Mezun

    @Size(max = 1000, message = "Açıklama en fazla 1000 karakter olabilir")
    private String aciklama;

    private LocalDate baslangicTarihi;

    private LocalDate bitisTarihi;

    private Integer egitimSaati; // Dakika cinsinden

    @Size(max = 50, message = "Durum en fazla 50 karakter olabilir")
    private String durum;

    // İlişkili entity ID'leri (many-to-many)
    private Set<Long> kategoriIds = new HashSet<>();
    private Set<Long> egitmenIds = new HashSet<>();
    private Set<Long> sorumluIds = new HashSet<>();
    private Set<Long> paydasIds = new HashSet<>();
    
    // Proje ID (many-to-one)
    private Long projeId;
}

