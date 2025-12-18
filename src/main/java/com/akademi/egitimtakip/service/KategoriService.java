package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.KategoriDTO;
import com.akademi.egitimtakip.entity.Kategori;
import com.akademi.egitimtakip.mapper.KategoriMapper;
import com.akademi.egitimtakip.repository.KategoriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class KategoriService {

    @Autowired
    private KategoriRepository kategoriRepository;

    @Autowired
    private KategoriMapper kategoriMapper;

    public List<KategoriDTO> getAll() {
        return kategoriMapper.toDTOList(kategoriRepository.findAll());
    }

    public Page<KategoriDTO> getAll(Pageable pageable) {
        return kategoriRepository.findAll(pageable).map(kategoriMapper::toDTO);
    }

    public KategoriDTO getById(Long id) {
        Kategori kategori = kategoriRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + id));
        return kategoriMapper.toDTO(kategori);
    }

    public KategoriDTO create(KategoriDTO dto) {
        Kategori kategori = kategoriMapper.toEntity(dto);
        
        if (dto.getUstKategoriId() != null) {
            Kategori ustKategori = kategoriRepository.findById(dto.getUstKategoriId())
                    .orElseThrow(() -> new RuntimeException("Üst kategori bulunamadı: " + dto.getUstKategoriId()));
            kategori.setUstKategori(ustKategori);
        }
        
        kategori = kategoriRepository.save(kategori);
        return kategoriMapper.toDTO(kategori);
    }

    public KategoriDTO update(Long id, KategoriDTO dto) {
        Kategori kategori = kategoriRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + id));
        
        kategoriMapper.updateEntityFromDTO(dto, kategori);
        
        if (dto.getUstKategoriId() != null) {
            // Kendisi üst kategorisi olamaz
            if (dto.getUstKategoriId().equals(id)) {
                throw new RuntimeException("Bir kategori kendi kendisinin üst kategorisi olamaz.");
            }
            
            Kategori ustKategori = kategoriRepository.findById(dto.getUstKategoriId())
                    .orElseThrow(() -> new RuntimeException("Üst kategori bulunamadı: " + dto.getUstKategoriId()));
            kategori.setUstKategori(ustKategori);
        } else {
            kategori.setUstKategori(null);
        }
        
        kategori.setId(id);
        kategori = kategoriRepository.save(kategori);
        return kategoriMapper.toDTO(kategori);
    }

    public void delete(Long id) {
        if (!kategoriRepository.existsById(id)) {
            throw new RuntimeException("Kategori bulunamadı: " + id);
        }
        // İlişkisel bütünlük kontrolü (alt kategoriler varsa silinemez veya cascade yapılabilir)
        // Şimdilik cascade=ALL olduğu için alt kategoriler de silinir (Entity tanımında).
        kategoriRepository.deleteById(id);
    }
}

