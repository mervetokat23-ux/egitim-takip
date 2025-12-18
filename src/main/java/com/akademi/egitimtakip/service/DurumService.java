package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.DurumRequestDTO;
import com.akademi.egitimtakip.dto.DurumResponseDTO;
import com.akademi.egitimtakip.entity.*;
import com.akademi.egitimtakip.mapper.DurumMapper;
import com.akademi.egitimtakip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Durum Service
 * 
 * Durum CRUD işlemlerini yönetir. Sayfalama ve filtreleme desteği sağlar.
 */
@Service
@Transactional
public class DurumService {

    @Autowired
    private DurumRepository durumRepository;

    @Autowired
    private EgitimRepository egitimRepository;

    @Autowired
    private DurumMapper durumMapper;

    /**
     * Tüm durumları sayfalama ve filtreleme ile getirir
     */
    @Transactional(readOnly = true)
    public Page<DurumResponseDTO> getAllDurumlar(Pageable pageable, Long egitimId, String durum) {
        Specification<Durum> spec = Specification.where(null);

        if (egitimId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("egitim").get("id"), egitimId));
        }

        if (durum != null && !durum.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("durum"), durum));
        }

        Page<Durum> durumlar = durumRepository.findAll(spec, pageable);
        return durumlar.map(durumMapper::toResponseDTO);
    }

    /**
     * ID ile durum detayını getirir
     */
    @Transactional(readOnly = true)
    public DurumResponseDTO getDurumById(Long id) {
        Durum durum = durumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Durum bulunamadı: " + id));
        return durumMapper.toResponseDTO(durum);
    }

    /**
     * Yeni durum oluşturur
     */
    public DurumResponseDTO createDurum(DurumRequestDTO requestDTO) {
        Durum durum = durumMapper.toEntity(requestDTO);
        
        // İlişkili entity'yi yükle ve ata
        Egitim egitim = egitimRepository.findById(requestDTO.getEgitimId())
                .orElseThrow(() -> new RuntimeException("Eğitim bulunamadı: " + requestDTO.getEgitimId()));
        durum.setEgitim(egitim);
        
        durum = durumRepository.save(durum);
        return durumMapper.toResponseDTO(durum);
    }

    /**
     * Mevcut durumu günceller
     */
    public DurumResponseDTO updateDurum(Long id, DurumRequestDTO requestDTO) {
        Durum durum = durumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Durum bulunamadı: " + id));
        
        durumMapper.updateEntityFromDTO(requestDTO, durum);
        
        // İlişkiyi güncelle
        Egitim egitim = egitimRepository.findById(requestDTO.getEgitimId())
                .orElseThrow(() -> new RuntimeException("Eğitim bulunamadı: " + requestDTO.getEgitimId()));
        durum.setEgitim(egitim);
        
        durum = durumRepository.save(durum);
        return durumMapper.toResponseDTO(durum);
    }

    /**
     * Durumu siler
     */
    public void deleteDurum(Long id) {
        if (!durumRepository.existsById(id)) {
            throw new RuntimeException("Durum bulunamadı: " + id);
        }
        durumRepository.deleteById(id);
    }
}

