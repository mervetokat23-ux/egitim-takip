package com.akademi.egitimtakip.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Odeme Request DTO
 * 
 * POST ve PUT endpoint'leri için ödeme verisi taşır.
 * Validation kuralları ile veriler kontrol edilir.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OdemeRequestDTO {

    @NotNull(message = "Eğitim ID boş olamaz")
    private Long egitimId;

    @NotNull(message = "Birim ücret boş olamaz")
    @DecimalMin(value = "0.01", message = "Birim ücret 0'dan büyük olmalıdır")
    @Digits(integer = 10, fraction = 2, message = "Birim ücret geçersiz format (maksimum 10 rakam, 2 ondalık)")
    private BigDecimal birimUcret;

    @NotNull(message = "Toplam ücret boş olamaz")
    @DecimalMin(value = "0.01", message = "Toplam ücret 0'dan büyük olmalıdır")
    @Digits(integer = 10, fraction = 2, message = "Toplam ücret geçersiz format (maksimum 10 rakam, 2 ondalık)")
    private BigDecimal toplamUcret;

    @NotBlank(message = "Ödeme kaynağı boş olamaz")
    @Size(max = 200, message = "Ödeme kaynağı maksimum 200 karakter olabilir")
    private String odemeKaynagi;

    @NotBlank(message = "Durum boş olamaz")
    @Size(max = 50, message = "Durum maksimum 50 karakter olabilir")
    private String durum; // Örn: "Beklemede", "Ödendi", "İptal"

    @Size(max = 100, message = "Operasyon maksimum 100 karakter olabilir")
    private String operasyon; // Örn: "Havale", "Nakit", "POS", "Sistem içi"

    private Long sorumluId;
    
    // Soft delete (opsiyonel, frontend'den yönetilirse kullanılır)
    private Boolean isDeleted = false;
    
    // Optional: miktar (calculateTotalPrice için)
    @Min(value = 1, message = "Miktar en az 1 olmalıdır")
    private Integer miktar;
}

