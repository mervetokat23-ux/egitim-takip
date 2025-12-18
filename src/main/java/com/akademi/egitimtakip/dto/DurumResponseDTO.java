package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Durum Response DTO
 * 
 * GET endpoint'leri için durum verisi döndürür.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DurumResponseDTO {

    private Long id;
    private String durum;
    private String operasyon;

    // Nested DTO
    private EgitimResponseDTO egitim;
}

