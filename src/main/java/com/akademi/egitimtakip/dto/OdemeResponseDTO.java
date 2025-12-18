package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Odeme Response DTO
 * 
 * GET endpoint'leri için ödeme verisi döndürür.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OdemeResponseDTO {

    private Long id;
    private BigDecimal birimUcret;
    private BigDecimal toplamUcret;
    private String odemeKaynagi;
    private String durum;
    private String operasyon;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested DTOs
    private EgitimResponseDTO egitim;
    private SorumluDTO sorumlu;
}

