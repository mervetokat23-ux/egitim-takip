package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.OdemeRequestDTO;
import com.akademi.egitimtakip.dto.OdemeResponseDTO;
import com.akademi.egitimtakip.service.ActivityLogService;
import com.akademi.egitimtakip.service.OdemeService;
import com.akademi.egitimtakip.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Odeme Controller
 * 
 * Ödeme CRUD endpoint'lerini sağlar.
 * Permission-based authorization: payment module
 * Tüm işlemler activity log'a kaydedilir.
 */
@RestController
@RequestMapping("/odeme")
@CrossOrigin(origins = "*")
@Tag(name = "Ödeme Yönetimi", description = "Ödeme CRUD işlemleri ve filtreleme")
public class OdemeController {

    @Autowired
    private OdemeService odemeService;
    
    @Autowired
    private ActivityLogService activityLogService;

    /**
     * GET /odeme - Tüm ödemeleri listele
     * Required Permission: payment.view
     */
    @GetMapping
    @RequirePermission(module = "payment", action = "view", description = "View all payments")
    @Operation(summary = "Tüm ödemeleri listele", description = "Sayfalama ve filtreleme ile ödemeleri getirir")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Başarılı")
    })
    public ResponseEntity<Page<OdemeResponseDTO>> getAllOdemeler(
            @Parameter(description = "Sayfa numarası (0'dan başlar)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına kayıt sayısı") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sıralama (örn: id,desc)") 
            @RequestParam(defaultValue = "id,desc") String sort,
            @Parameter(description = "Eğitim ID filtresi") 
            @RequestParam(required = false) Long egitimId,
            @Parameter(description = "Durum filtresi (Ödendi, Bekliyor, İptal)") 
            @RequestParam(required = false) String durum,
            @Parameter(description = "Sorumlu ID filtresi") 
            @RequestParam(required = false) Long sorumluId,
            @Parameter(description = "Ödeme kaynağı filtresi") 
            @RequestParam(required = false) String odemeKaynagi) {
        
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        String sortField = sortParams[0];
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        Page<OdemeResponseDTO> odemeler = odemeService.getAllOdemeler(
            pageable, egitimId, durum, sorumluId, odemeKaynagi);
        return ResponseEntity.ok(odemeler);
    }

    /**
     * GET /odeme/{id} - Ödeme detayı getir
     * Required Permission: payment.view
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "payment", action = "view", description = "View payment by ID")
    @Operation(summary = "Ödeme detayı", description = "ID ile ödeme detayını getirir")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Başarılı"),
        @ApiResponse(responseCode = "404", description = "Ödeme bulunamadı")
    })
    public ResponseEntity<?> getOdemeById(@PathVariable Long id) {
        try {
            OdemeResponseDTO odeme = odemeService.getOdemeById(id);
            
            // Log görüntüleme aksiyonu
            Long userId = SecurityUtils.getCurrentUserId();
            activityLogService.logView(userId, "PAYMENT", id);
            
            return ResponseEntity.ok(odeme);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Ödeme bulunamadı: " + id));
        }
    }

    /**
     * POST /odeme - Yeni ödeme oluştur
     * Required Permission: payment.create
     */
    @PostMapping
    @RequirePermission(module = "payment", action = "create", description = "Create new payment")
    @Operation(summary = "Yeni ödeme oluştur", description = "Yeni ödeme kaydı oluşturur")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ödeme oluşturuldu"),
        @ApiResponse(responseCode = "400", description = "Geçersiz veri"),
        @ApiResponse(responseCode = "403", description = "Yetki yok")
    })
    public ResponseEntity<?> createOdeme(@Valid @RequestBody OdemeRequestDTO requestDTO) {
        try {
            OdemeResponseDTO response = odemeService.createOdeme(requestDTO);
            
            // Log oluşturma aksiyonu
            Long userId = SecurityUtils.getCurrentUserId();
            String logDescription = String.format(
                "Yeni ödeme oluşturuldu: Eğitim ID=%d, Tutar=%s, Kaynak=%s, Durum=%s",
                requestDTO.getEgitimId(),
                requestDTO.getToplamUcret(),
                requestDTO.getOdemeKaynagi(),
                requestDTO.getDurum()
            );
            activityLogService.logCreate(userId, "PAYMENT", response.getId(), logDescription);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validasyon hatası: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Ödeme oluşturulamadı: " + e.getMessage()));
        }
    }

    /**
     * PUT /odeme/{id} - Ödeme güncelle
     * Required Permission: payment.update
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "payment", action = "update", description = "Update payment")
    @Operation(summary = "Ödemeyi güncelle", description = "Mevcut ödeme kaydını günceller")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ödeme güncellendi"),
        @ApiResponse(responseCode = "400", description = "Geçersiz veri"),
        @ApiResponse(responseCode = "403", description = "Yetki yok"),
        @ApiResponse(responseCode = "404", description = "Ödeme bulunamadı")
    })
    public ResponseEntity<?> updateOdeme(
            @PathVariable Long id,
            @Valid @RequestBody OdemeRequestDTO requestDTO) {
        try {
            OdemeResponseDTO response = odemeService.updateOdeme(id, requestDTO);
            
            // Log güncelleme aksiyonu
            Long userId = SecurityUtils.getCurrentUserId();
            String logDescription = String.format(
                "Ödeme güncellendi: ID=%d, Tutar=%s, Durum=%s",
                id,
                requestDTO.getToplamUcret(),
                requestDTO.getDurum()
            );
            activityLogService.logUpdate(userId, "PAYMENT", id, logDescription);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Validasyon hatası: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Ödeme güncellenemedi: " + e.getMessage()));
        }
    }

    /**
     * DELETE /odeme/{id} - Ödeme sil
     * Required Permission: payment.delete
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "payment", action = "delete", description = "Delete payment")
    @Operation(summary = "Ödemeyi sil", description = "Ödeme kaydını siler (soft delete)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ödeme silindi"),
        @ApiResponse(responseCode = "403", description = "Yetki yok"),
        @ApiResponse(responseCode = "404", description = "Ödeme bulunamadı")
    })
    public ResponseEntity<?> deleteOdeme(@PathVariable Long id) {
        try {
            // Silmeden önce ödeme bilgisini al (loglama için)
            OdemeResponseDTO odeme = odemeService.getOdemeById(id);
            
            odemeService.deleteOdeme(id);
            
            // Log silme aksiyonu
            Long userId = SecurityUtils.getCurrentUserId();
            String logDescription = String.format(
                "Ödeme silindi: ID=%d, Eğitim=%s, Tutar=%s",
                id,
                odeme.getEgitim() != null ? odeme.getEgitim().getAd() : "N/A",
                odeme.getToplamUcret()
            );
            activityLogService.logDelete(userId, "PAYMENT", id, logDescription);
            
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Ödeme silinemedi: " + e.getMessage()));
        }
    }
    
    /**
     * POST /odeme/calculate-total - Toplam ücret hesapla
     * Required Permission: payment.view (basit hesaplama için view yeterli)
     */
    @PostMapping("/calculate-total")
    @RequirePermission(module = "payment", action = "view", description = "Calculate total price")
    @Operation(summary = "Toplam ücret hesapla", description = "Birim ücret ve miktara göre toplam ücreti hesaplar")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Hesaplama başarılı"),
        @ApiResponse(responseCode = "400", description = "Geçersiz parametreler")
    })
    public ResponseEntity<?> calculateTotalPrice(
            @Parameter(description = "Birim ücret") 
            @RequestParam BigDecimal unitPrice,
            @Parameter(description = "Miktar (varsayılan: 1)") 
            @RequestParam(required = false, defaultValue = "1") Integer quantity) {
        try {
            BigDecimal totalPrice = odemeService.calculateTotalPrice(unitPrice, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("unitPrice", unitPrice);
            response.put("quantity", quantity);
            response.put("totalPrice", totalPrice);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Hesaplama hatası: " + e.getMessage()));
        }
    }
    
    // Helper method
    
    /**
     * Hata response'u oluşturur
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
