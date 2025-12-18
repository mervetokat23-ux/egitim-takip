package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Sorumlu DTO
 * 
 * Sorumlu bilgilerini taşımak için kullanılır (nested DTO).
 * Birden fazla ünvan desteği için unvanlar List<String> olarak tanımlandı.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SorumluDTO {
    private Long id;
    private String ad;
    private String soyad;
    private String email;
    private String telefon;
    private List<String> unvanlar = new ArrayList<>();
    private Long roleId;
    private String roleName;
}

