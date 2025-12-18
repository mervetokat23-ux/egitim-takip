package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Kategori DTO
 * 
 * Kategori bilgilerini taşımak için kullanılır.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KategoriDTO {
    private Long id;
    private String ad;
    private String aciklama;
    private Long ustKategoriId; // Parent ID
    private String ustKategoriAd; // Parent Name (for display)
}
