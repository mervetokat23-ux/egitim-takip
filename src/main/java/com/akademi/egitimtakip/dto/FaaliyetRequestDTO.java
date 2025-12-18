package com.akademi.egitimtakip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Faaliyet Request DTO
 * 
 * POST ve PUT endpoint'leri için faaliyet verisi taşır.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaaliyetRequestDTO {

    @NotNull(message = "Tarih boş olamaz")
    private LocalDate tarih;

    @NotBlank(message = "İsim boş olamaz")
    @Size(max = 200, message = "İsim en fazla 200 karakter olabilir")
    private String isim;

    @Size(max = 100, message = "Türü en fazla 100 karakter olabilir")
    private String turu; // Örn: "Toplantı", "Eğitim", "Seminer", "Workshop"

    private Long projeId;

    // Many-to-many relationship IDs
    private Set<Long> sorumluIds = new HashSet<>();
}

