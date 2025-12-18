package com.akademi.egitimtakip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

/**
 * Proje Request DTO
 * 
 * POST ve PUT endpoint'leri için proje verisi taşır.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjeRequestDTO {

    @NotBlank(message = "Proje ismi boş olamaz")
    @Size(max = 200, message = "Proje ismi en fazla 200 karakter olabilir")
    private String isim;

    private LocalDate baslangicTarihi;

    private LocalDate tarih;

    @Size(max = 2000, message = "Proje hakkında en fazla 2000 karakter olabilir")
    private String projeHakkinda;

    // Foreign key IDs
    private Long egitimSorumluId;
    private Long paydasId;
}
