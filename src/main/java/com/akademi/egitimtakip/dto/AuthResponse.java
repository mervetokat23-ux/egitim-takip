package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Auth Response DTO
 * 
 * Authentication işlemleri sonucu dönen JWT token ve kullanıcı bilgileri.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String adSoyad;
    private String rol;
    private String message;
}

