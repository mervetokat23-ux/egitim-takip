package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.OdemeRequestDTO;
import com.akademi.egitimtakip.dto.OdemeResponseDTO;
import com.akademi.egitimtakip.entity.*;
import com.akademi.egitimtakip.mapper.OdemeMapper;
import com.akademi.egitimtakip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Odeme Service
 * 
 * Ödeme CRUD işlemlerini yönetir. Sayfalama, filtreleme ve hesaplama desteği sağlar.
 * Business logic ve validasyon kurallarını içerir.
 */
@Service
@Transactional
public class OdemeService {

    @Autowired
    private OdemeRepository odemeRepository;

    @Autowired
    private EgitimRepository egitimRepository;

    @Autowired
    private SorumluRepository sorumluRepository;

    @Autowired
    private OdemeMapper odemeMapper;

    /**
     * Tüm ödemeleri sayfalama ve filtreleme ile getirir
     * 
     * @param pageable Sayfalama parametreleri
     * @param egitimId Eğitim ID filtresi (opsiyonel)
     * @param durum Durum filtresi (opsiyonel)
     * @param sorumluId Sorumlu ID filtresi (opsiyonel)
     * @param odemeKaynagi Ödeme kaynağı filtresi (opsiyonel)
     * @return Filtrelenmiş ödeme listesi
     */
    @Transactional(readOnly = true)
    public Page<OdemeResponseDTO> getAllOdemeler(
            Pageable pageable, 
            Long egitimId, 
            String durum, 
            Long sorumluId,
            String odemeKaynagi) {
        
        Specification<Odeme> spec = Specification.where(null);

        if (egitimId != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("egitim").get("id"), egitimId));
        }

        if (durum != null && !durum.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("durum"), durum));
        }
        
        if (sorumluId != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("sorumlu").get("id"), sorumluId));
        }
        
        if (odemeKaynagi != null && !odemeKaynagi.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("odemeKaynagi")), "%" + odemeKaynagi.toLowerCase() + "%"));
        }

        Page<Odeme> odemeler = odemeRepository.findAll(spec, pageable);
        return odemeler.map(odemeMapper::toResponseDTO);
    }

    /**
     * ID ile ödeme detayını getirir
     */
    @Transactional(readOnly = true)
    public OdemeResponseDTO getOdemeById(Long id) {
        Odeme odeme = odemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ödeme bulunamadı: " + id));
        return odemeMapper.toResponseDTO(odeme);
    }

    /**
     * Yeni ödeme oluşturur
     */
    public OdemeResponseDTO createOdeme(OdemeRequestDTO requestDTO) {
        // Validasyonlar
        validateOdeme(requestDTO);
        
        Odeme odeme = odemeMapper.toEntity(requestDTO);
        
        // İlişkili entity'leri yükle ve ata
        Egitim egitim = egitimRepository.findById(requestDTO.getEgitimId())
                .orElseThrow(() -> new RuntimeException("Eğitim bulunamadı: " + requestDTO.getEgitimId()));
        odeme.setEgitim(egitim);

        if (requestDTO.getSorumluId() != null) {
            Sorumlu sorumlu = sorumluRepository.findById(requestDTO.getSorumluId())
                    .orElseThrow(() -> new RuntimeException("Sorumlu bulunamadı: " + requestDTO.getSorumluId()));
            odeme.setSorumlu(sorumlu);
        }
        
        odeme = odemeRepository.save(odeme);
        return odemeMapper.toResponseDTO(odeme);
    }

    /**
     * Mevcut ödemeyi günceller
     */
    public OdemeResponseDTO updateOdeme(Long id, OdemeRequestDTO requestDTO) {
        // Validasyonlar
        validateOdeme(requestDTO);
        
        Odeme odeme = odemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ödeme bulunamadı: " + id));
        
        odemeMapper.updateEntityFromDTO(requestDTO, odeme);
        
        // İlişkileri güncelle
        Egitim egitim = egitimRepository.findById(requestDTO.getEgitimId())
                .orElseThrow(() -> new RuntimeException("Eğitim bulunamadı: " + requestDTO.getEgitimId()));
        odeme.setEgitim(egitim);

        if (requestDTO.getSorumluId() != null) {
            Sorumlu sorumlu = sorumluRepository.findById(requestDTO.getSorumluId())
                    .orElseThrow(() -> new RuntimeException("Sorumlu bulunamadı: " + requestDTO.getSorumluId()));
            odeme.setSorumlu(sorumlu);
        } else {
            odeme.setSorumlu(null);
        }
        
        odeme = odemeRepository.save(odeme);
        return odemeMapper.toResponseDTO(odeme);
    }

    /**
     * Ödemeyi siler (soft delete kullanılıyorsa soft delete, değilse hard delete)
     */
    public void deleteOdeme(Long id) {
        if (!odemeRepository.existsById(id)) {
            throw new RuntimeException("Ödeme bulunamadı: " + id);
        }
        odemeRepository.deleteById(id);
    }
    
    /**
     * Toplam ücreti hesaplar
     * 
     * @param unitPrice Birim ücret
     * @param quantity Miktar (varsayılan: 1)
     * @return Hesaplanan toplam ücret
     */
    public BigDecimal calculateTotalPrice(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Birim ücret geçersiz");
        }
        
        if (quantity == null || quantity < 1) {
            quantity = 1;
        }
        
        return unitPrice.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Ödeme validasyonları
     */
    private void validateOdeme(OdemeRequestDTO requestDTO) {
        // Birim ücret pozitif olmalı
        if (requestDTO.getBirimUcret() == null || 
            requestDTO.getBirimUcret().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Birim ücret 0'dan büyük olmalıdır");
        }
        
        // Toplam ücret pozitif olmalı
        if (requestDTO.getToplamUcret() == null || 
            requestDTO.getToplamUcret().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Toplam ücret 0'dan büyük olmalıdır");
        }
        
        // Ödeme kaynağı boş olmamalı
        if (requestDTO.getOdemeKaynagi() == null || requestDTO.getOdemeKaynagi().trim().isEmpty()) {
            throw new IllegalArgumentException("Ödeme kaynağı boş olamaz");
        }
        
        // Durum boş olmamalı
        if (requestDTO.getDurum() == null || requestDTO.getDurum().trim().isEmpty()) {
            throw new IllegalArgumentException("Durum boş olamaz");
        }
        
        // Toplam ücret, birim ücret ve miktar uyumlu mu kontrol et
        if (requestDTO.getMiktar() != null && requestDTO.getMiktar() > 0) {
            BigDecimal expectedTotal = calculateTotalPrice(
                requestDTO.getBirimUcret(), 
                requestDTO.getMiktar()
            );
            
            // Küçük yuvarlama farklarını tolere et
            BigDecimal difference = expectedTotal.subtract(requestDTO.getToplamUcret()).abs();
            if (difference.compareTo(new BigDecimal("0.01")) > 0) {
                throw new IllegalArgumentException(
                    "Toplam ücret, birim ücret x miktar ile uyumlu değil. " +
                    "Beklenen: " + expectedTotal + ", Verilen: " + requestDTO.getToplamUcret()
                );
            }
        }
    }
}

