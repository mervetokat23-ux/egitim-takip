package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Faaliyet Response DTO
 * 
 * GET endpoint'leri için faaliyet verisi döndürür.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaaliyetResponseDTO {

    private Long id;
    private LocalDate tarih;
    private String isim;
    private String turu;

    // Nested DTOs
    private ProjeResponseDTO proje;
    private Set<SorumluDTO> sorumlular = new HashSet<>();
}

