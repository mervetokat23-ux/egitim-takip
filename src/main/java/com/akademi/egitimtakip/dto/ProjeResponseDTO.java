package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Proje Response DTO
 * 
 * GET endpoint'leri için proje verisi döndürür.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjeResponseDTO {

    private Long id;
    private String isim;
    private LocalDate baslangicTarihi;
    private LocalDate tarih;
    private String projeHakkinda;

    // Nested DTOs
    private SorumluDTO egitimSorumlu;
    private PaydasDTO paydas;
    
    // Faaliyet listesi (basit DTO)
    private List<FaaliyetSimpleDTO> faaliyetler;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class FaaliyetSimpleDTO {
        private Long id;
        private String isim;
        private String turu;
        private LocalDate tarih;
    }
}
