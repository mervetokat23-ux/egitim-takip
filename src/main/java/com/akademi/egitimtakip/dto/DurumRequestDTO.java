package com.akademi.egitimtakip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Durum Request DTO
 * 
 * POST ve PUT endpoint'leri için durum verisi taşır.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DurumRequestDTO {

    @NotNull(message = "Eğitim ID boş olamaz")
    private Long egitimId;

    @NotBlank(message = "Durum boş olamaz")
    @Size(max = 50, message = "Durum en fazla 50 karakter olabilir")
    private String durum; // Örn: "Planlandı", "Devam Ediyor", "Tamamlandı", "İptal"

    @Size(max = 100, message = "Operasyon en fazla 100 karakter olabilir")
    private String operasyon; // Örn: "Ekleme", "Güncelleme", "Silme"
}

