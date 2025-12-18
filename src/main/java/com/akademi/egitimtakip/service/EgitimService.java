package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.annotation.LogAction;
import com.akademi.egitimtakip.dto.EgitimRequestDTO;
import com.akademi.egitimtakip.dto.EgitimResponseDTO;
import com.akademi.egitimtakip.entity.*;
import com.akademi.egitimtakip.mapper.EgitimMapper;
import com.akademi.egitimtakip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Egitim Service
 * 
 * Eğitim CRUD işlemlerini yönetir. Sayfalama ve filtreleme desteği sağlar.
 * H2 database ile uyumlu çalışır.
 */
@Service
@Transactional
public class EgitimService {

    @Autowired
    private EgitimRepository egitimRepository;

    @Autowired
    private KategoriRepository kategoriRepository;

    @Autowired
    private EgitmenRepository egitmenRepository;

    @Autowired
    private SorumluRepository sorumluRepository;

    @Autowired
    private PaydasRepository paydasRepository;

    @Autowired
    private ProjeRepository projeRepository;

    @Autowired
    private EgitimMapper egitimMapper;

    /**
     * Tüm eğitimleri sayfalama ve filtreleme ile getirir
     */
    @Transactional(readOnly = true)
    public Page<EgitimResponseDTO> getAllEgitimler(Pageable pageable, String il, Integer yil, String durum) {
        Specification<Egitim> spec = Specification.where(null);

        // Durum filtresi
        if (durum != null && !durum.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("durum"), durum));
        }

        // Yıl filtresi (başlangıç tarihi yılına göre)
        if (yil != null) {
            LocalDate startDate = LocalDate.of(yil, 1, 1);
            LocalDate endDate = LocalDate.of(yil, 12, 31);
            spec = spec.and((root, query, cb) -> 
                cb.between(root.get("baslangicTarihi"), startDate, endDate)
            );
        }

        // İl filtresi (şimdilik basit string arama - ileride entity eklenebilir)
        // Not: İl bilgisi şu an entity'de yok, bu filtre şimdilik çalışmayacak
        // İleride Proje entity'si eklendiğinde il bilgisi oradan alınabilir

        Page<Egitim> egitimler = egitimRepository.findAll(spec, pageable);
        return egitimler.map(egitimMapper::toResponseDTO);
    }

    /**
     * ID ile eğitim detayını getirir (ilişkiler dahil)
     */
    @Transactional(readOnly = true)
    public EgitimResponseDTO getEgitimById(Long id) {
        Egitim egitim = egitimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eğitim bulunamadı: " + id));
        return egitimMapper.toResponseDTO(egitim);
    }

    /**
     * Yeni eğitim oluşturur
     */
    @LogAction(
        action = "CREATE",
        entityType = "Egitim",
        description = "Yeni eğitim oluşturuldu: #{result.ad}",
        entityIdParam = -1  // Return value'dan ID al
    )
    public EgitimResponseDTO createEgitim(EgitimRequestDTO requestDTO) {
        Egitim egitim = egitimMapper.toEntity(requestDTO);
        
        // İlişkili entity'leri yükle ve ata
        setRelationships(egitim, requestDTO);
        
        egitim = egitimRepository.save(egitim);
        return egitimMapper.toResponseDTO(egitim);
    }

    /**
     * Mevcut eğitimi günceller
     */
    @LogAction(
        action = "UPDATE",
        entityType = "Egitim",
        description = "Eğitim güncellendi: #{result.ad}",
        entityIdParam = 0  // İlk parametre (id) entity ID'si
    )
    public EgitimResponseDTO updateEgitim(Long id, EgitimRequestDTO requestDTO) {
        Egitim egitim = egitimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eğitim bulunamadı: " + id));
        
        // Entity'yi güncelle
        egitimMapper.updateEntityFromDTO(requestDTO, egitim);
        
        // İlişkileri güncelle
        setRelationships(egitim, requestDTO);
        
        egitim = egitimRepository.save(egitim);
        return egitimMapper.toResponseDTO(egitim);
    }

    /**
     * Eğitimi siler
     */
    @LogAction(
        action = "DELETE",
        entityType = "Egitim",
        description = "Eğitim silindi (ID: #{args[0]})",
        entityIdParam = 0  // İlk parametre (id) entity ID'si
    )
    public void deleteEgitim(Long id) {
        if (!egitimRepository.existsById(id)) {
            throw new RuntimeException("Eğitim bulunamadı: " + id);
        }
        egitimRepository.deleteById(id);
    }

    /**
     * İlişkili entity'leri yükler ve atar
     */
    private void setRelationships(Egitim egitim, EgitimRequestDTO requestDTO) {
        // Kategoriler
        Set<Kategori> kategoriler = new HashSet<>();
        if (requestDTO.getKategoriIds() != null && !requestDTO.getKategoriIds().isEmpty()) {
            kategoriler = new HashSet<>(kategoriRepository.findAllById(requestDTO.getKategoriIds()));
        }
        egitim.setKategoriler(kategoriler);

        // Eğitmenler
        Set<Egitmen> egitmenler = new HashSet<>();
        if (requestDTO.getEgitmenIds() != null && !requestDTO.getEgitmenIds().isEmpty()) {
            egitmenler = new HashSet<>(egitmenRepository.findAllById(requestDTO.getEgitmenIds()));
        }
        egitim.setEgitmenler(egitmenler);

        // Sorumlular
        Set<Sorumlu> sorumlular = new HashSet<>();
        if (requestDTO.getSorumluIds() != null && !requestDTO.getSorumluIds().isEmpty()) {
            sorumlular = new HashSet<>(sorumluRepository.findAllById(requestDTO.getSorumluIds()));
        }
        egitim.setSorumlular(sorumlular);

        // Paydaşlar
        Set<Paydas> paydaslar = new HashSet<>();
        if (requestDTO.getPaydasIds() != null && !requestDTO.getPaydasIds().isEmpty()) {
            paydaslar = new HashSet<>(paydasRepository.findAllById(requestDTO.getPaydasIds()));
        }
        egitim.setPaydaslar(paydaslar);

        // Proje
        if (requestDTO.getProjeId() != null) {
            Proje proje = projeRepository.findById(requestDTO.getProjeId())
                    .orElseThrow(() -> new RuntimeException("Proje bulunamadı: " + requestDTO.getProjeId()));
            egitim.setProje(proje);
        } else {
            egitim.setProje(null);
        }
    }
}

