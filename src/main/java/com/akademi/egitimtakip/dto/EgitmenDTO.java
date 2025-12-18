package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Egitmen DTO
 * 
 * Eğitmen bilgilerini taşımak için kullanılır (nested DTO).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EgitmenDTO {
    private Long id;
    private String ad;
    private String soyad;
    private String email;
    private String telefon;
    private String il;
    private String calismaYeri;
}

