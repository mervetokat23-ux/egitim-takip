package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.EgitmenDTO;
import com.akademi.egitimtakip.entity.Egitmen;
import com.akademi.egitimtakip.mapper.EgitmenMapper;
import com.akademi.egitimtakip.repository.EgitmenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EgitmenService {

    @Autowired
    private EgitmenRepository egitmenRepository;

    @Autowired
    private EgitmenMapper egitmenMapper;

    public List<EgitmenDTO> getAll() {
        return egitmenMapper.toDTOList(egitmenRepository.findAll());
    }

    public Page<EgitmenDTO> getAll(Pageable pageable) {
        return egitmenRepository.findAll(pageable).map(egitmenMapper::toDTO);
    }

    public EgitmenDTO getById(Long id) {
        Egitmen egitmen = egitmenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eğitmen bulunamadı: " + id));
        return egitmenMapper.toDTO(egitmen);
    }

    public EgitmenDTO create(EgitmenDTO dto) {
        Egitmen egitmen = egitmenMapper.toEntity(dto);
        egitmen = egitmenRepository.save(egitmen);
        return egitmenMapper.toDTO(egitmen);
    }

    public EgitmenDTO update(Long id, EgitmenDTO dto) {
        Egitmen egitmen = egitmenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eğitmen bulunamadı: " + id));
        
        egitmenMapper.updateEntityFromDTO(dto, egitmen);
        egitmen.setId(id); // ID'nin değişmediğinden emin ol
        
        egitmen = egitmenRepository.save(egitmen);
        return egitmenMapper.toDTO(egitmen);
    }

    public void delete(Long id) {
        if (!egitmenRepository.existsById(id)) {
            throw new RuntimeException("Eğitmen bulunamadı: " + id);
        }
        // İlişkisel bütünlük için gerekirse kontrol eklenebilir
        // Şimdilik direkt siliyoruz, egitim_egitmen tablosundan otomatik silinir (cascade ayarına bağlı)
        // Ancak many-to-many ilişkide genelde join tablodan silinir, eğitmen silinince eğitimler etkilenmez
        egitmenRepository.deleteById(id);
    }
}

