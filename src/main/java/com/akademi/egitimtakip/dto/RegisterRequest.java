package com.akademi.egitimtakip.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Register Request DTO
 * 
 * Kullanıcı kayıt isteği için veri transfer objesi.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Ad soyad boş olamaz")
    @Size(max = 200, message = "Ad soyad en fazla 200 karakter olabilir")
    private String adSoyad;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    @Size(max = 200, message = "Email en fazla 200 karakter olabilir")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String sifre;

    private String rol; // ADMIN, SORUMLU, EGITMEN
}

