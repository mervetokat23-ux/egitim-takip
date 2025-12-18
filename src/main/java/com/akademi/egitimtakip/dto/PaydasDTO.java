package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Paydas DTO
 * 
 * Paydaş bilgilerini taşımak için kullanılır.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaydasDTO {
    private Long id;
    private String ad;
    private String email;
    private String telefon;
    private String adres;
    private String tip;
    
    // Detay görünümü için basit listeler (ID ve Ad)
    private Set<EgitimSimpleDTO> egitimler;
    private Set<ProjeSimpleDTO> projeler;

    // Nested simple classes for list display to avoid recursion
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EgitimSimpleDTO {
        private Long id;
        private String ad;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ProjeSimpleDTO {
        private Long id;
        private String isim;
    }
}
