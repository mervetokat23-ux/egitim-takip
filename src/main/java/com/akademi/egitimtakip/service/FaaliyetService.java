package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.FaaliyetRequestDTO;
import com.akademi.egitimtakip.dto.FaaliyetResponseDTO;
import com.akademi.egitimtakip.entity.*;
import com.akademi.egitimtakip.mapper.FaaliyetMapper;
import com.akademi.egitimtakip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Faaliyet Service
 * 
 * Faaliyet CRUD işlemlerini yönetir. Sayfalama ve filtreleme desteği sağlar.
 */
@Service
@Transactional
public class FaaliyetService {

    @Autowired
    private FaaliyetRepository faaliyetRepository;

    @Autowired
    private ProjeRepository projeRepository;

    @Autowired
    private SorumluRepository sorumluRepository;

    @Autowired
    private FaaliyetMapper faaliyetMapper;

    /**
     * Tüm faaliyetleri sayfalama ve filtreleme ile getirir
     */
    @Transactional(readOnly = true)
    public Page<FaaliyetResponseDTO> getAllFaaliyetler(Pageable pageable, Long projeId, String turu) {
        Specification<Faaliyet> spec = Specification.where(null);

        if (projeId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("proje").get("id"), projeId));
        }

        if (turu != null && !turu.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("turu"), turu));
        }

        Page<Faaliyet> faaliyetler = faaliyetRepository.findAll(spec, pageable);
        return faaliyetler.map(faaliyetMapper::toResponseDTO);
    }

    /**
     * ID ile faaliyet detayını getirir
     */
    @Transactional(readOnly = true)
    public FaaliyetResponseDTO getFaaliyetById(Long id) {
        Faaliyet faaliyet = faaliyetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faaliyet bulunamadı: " + id));
        return faaliyetMapper.toResponseDTO(faaliyet);
    }

    /**
     * Yeni faaliyet oluşturur
     */
    public FaaliyetResponseDTO createFaaliyet(FaaliyetRequestDTO requestDTO) {
        Faaliyet faaliyet = faaliyetMapper.toEntity(requestDTO);
        
        // İlişkili entity'leri yükle ve ata
        setRelationships(faaliyet, requestDTO);
        
        faaliyet = faaliyetRepository.save(faaliyet);
        return faaliyetMapper.toResponseDTO(faaliyet);
    }

    /**
     * Mevcut faaliyeti günceller
     */
    public FaaliyetResponseDTO updateFaaliyet(Long id, FaaliyetRequestDTO requestDTO) {
        Faaliyet faaliyet = faaliyetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faaliyet bulunamadı: " + id));
        
        faaliyetMapper.updateEntityFromDTO(requestDTO, faaliyet);
        setRelationships(faaliyet, requestDTO);
        
        faaliyet = faaliyetRepository.save(faaliyet);
        return faaliyetMapper.toResponseDTO(faaliyet);
    }

    /**
     * Faaliyeti siler
     */
    public void deleteFaaliyet(Long id) {
        if (!faaliyetRepository.existsById(id)) {
            throw new RuntimeException("Faaliyet bulunamadı: " + id);
        }
        faaliyetRepository.deleteById(id);
    }

    /**
     * İlişkili entity'leri yükler ve atar
     */
    private void setRelationships(Faaliyet faaliyet, FaaliyetRequestDTO requestDTO) {
        if (requestDTO.getProjeId() != null) {
            Proje proje = projeRepository.findById(requestDTO.getProjeId())
                    .orElseThrow(() -> new RuntimeException("Proje bulunamadı: " + requestDTO.getProjeId()));
            faaliyet.setProje(proje);
        } else {
            faaliyet.setProje(null);
        }

        // Sorumlular
        Set<Sorumlu> sorumlular = new HashSet<>();
        if (requestDTO.getSorumluIds() != null && !requestDTO.getSorumluIds().isEmpty()) {
            sorumlular = new HashSet<>(sorumluRepository.findAllById(requestDTO.getSorumluIds()));
        }
        faaliyet.setSorumlular(sorumlular);
    }
}

