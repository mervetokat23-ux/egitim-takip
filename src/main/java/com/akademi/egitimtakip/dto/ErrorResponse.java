package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ErrorResponse DTO
 * 
 * API hatalarında standart JSON response formatı.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * HTTP durum kodu
     */
    private int status;
    
    /**
     * Hata mesajı (kullanıcıya gösterilecek)
     */
    private String message;
    
    /**
     * Hata detayı (teknik bilgi, opsiyonel)
     */
    private String details;
    
    /**
     * Hatanın oluştuğu endpoint
     */
    private String path;
    
    /**
     * Hata zamanı
     */
    private LocalDateTime timestamp;
    
    /**
     * Exception türü (opsiyonel, development ortamında yararlı)
     */
    private String exceptionType;

    /**
     * Hızlı factory metod
     */
    public static ErrorResponse of(int status, String message, String path) {
        return new ErrorResponse(
            status, 
            message, 
            null, 
            path, 
            LocalDateTime.now(), 
            null
        );
    }

    /**
     * Detaylı factory metod
     */
    public static ErrorResponse of(int status, String message, String details, String path, String exceptionType) {
        return new ErrorResponse(
            status, 
            message, 
            details, 
            path, 
            LocalDateTime.now(), 
            exceptionType
        );
    }
}





