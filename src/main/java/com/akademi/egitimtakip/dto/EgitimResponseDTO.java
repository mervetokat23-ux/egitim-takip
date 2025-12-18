package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Egitim Response DTO
 * 
 * GET endpoint'leri için eğitim verisi döndürür.
 * Nested ilişkiler (Egitmen, Kategori, Sorumlu, Paydas) dahil edilir.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EgitimResponseDTO {

    private Long id;
    private String ad;
    private String egitimKodu; // Eğitim kodu
    private String programId; // Program ID
    private String seviye; // Temel, Orta, İleri
    private String hedefKitle; // Virgülle ayrılmış: Ortaokul, Lise, Üniversite, Kurum Personeli, Mezun
    private String aciklama;
    private LocalDate baslangicTarihi;
    private LocalDate bitisTarihi;
    private Integer egitimSaati;
    private String durum;

    // Nested DTOs
    private Set<KategoriDTO> kategoriler = new HashSet<>();
    private Set<EgitmenDTO> egitmenler = new HashSet<>();
    private Set<SorumluDTO> sorumlular = new HashSet<>();
    private Set<PaydasDTO> paydaslar = new HashSet<>();
    private ProjeSimpleDTO proje;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ProjeSimpleDTO {
        private Long id;
        private String isim;
    }
}

