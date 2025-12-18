package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.ProjeRequestDTO;
import com.akademi.egitimtakip.dto.ProjeResponseDTO;
import com.akademi.egitimtakip.entity.Paydas;
import com.akademi.egitimtakip.entity.Proje;
import com.akademi.egitimtakip.entity.Sorumlu;
import com.akademi.egitimtakip.mapper.ProjeMapper;
import com.akademi.egitimtakip.repository.PaydasRepository;
import com.akademi.egitimtakip.repository.ProjeRepository;
import com.akademi.egitimtakip.repository.SorumluRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjeService {

    @Autowired
    private ProjeRepository projeRepository;

    @Autowired
    private SorumluRepository sorumluRepository;

    @Autowired
    private PaydasRepository paydasRepository;

    @Autowired
    private ProjeMapper projeMapper;

    public List<ProjeResponseDTO> getAll() {
        return projeMapper.toResponseDTOList(projeRepository.findAll());
    }

    public Page<ProjeResponseDTO> getAll(Pageable pageable) {
        return projeRepository.findAll(pageable).map(projeMapper::toResponseDTO);
    }

    public ProjeResponseDTO getById(Long id) {
        Proje proje = projeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı: " + id));
        return projeMapper.toResponseDTO(proje);
    }

    public ProjeResponseDTO create(ProjeRequestDTO dto) {
        Proje proje = projeMapper.toEntity(dto);
        mapRelations(dto, proje);
        proje = projeRepository.save(proje);
        return projeMapper.toResponseDTO(proje);
    }

    public ProjeResponseDTO update(Long id, ProjeRequestDTO dto) {
        Proje proje = projeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı: " + id));
        
        projeMapper.updateEntityFromDTO(dto, proje);
        mapRelations(dto, proje);
        proje.setId(id);
        
        proje = projeRepository.save(proje);
        return projeMapper.toResponseDTO(proje);
    }

    public void delete(Long id) {
        if (!projeRepository.existsById(id)) {
            throw new RuntimeException("Proje bulunamadı: " + id);
        }
        projeRepository.deleteById(id);
    }

    private void mapRelations(ProjeRequestDTO dto, Proje proje) {
        if (dto.getEgitimSorumluId() != null) {
            Sorumlu sorumlu = sorumluRepository.findById(dto.getEgitimSorumluId())
                    .orElseThrow(() -> new RuntimeException("Sorumlu bulunamadı: " + dto.getEgitimSorumluId()));
            proje.setEgitimSorumlu(sorumlu);
        } else {
            proje.setEgitimSorumlu(null);
        }
        
        if (dto.getPaydasId() != null) {
            Paydas paydas = paydasRepository.findById(dto.getPaydasId())
                    .orElseThrow(() -> new RuntimeException("Paydaş bulunamadı: " + dto.getPaydasId()));
            proje.setPaydas(paydas);
        } else {
            proje.setPaydas(null);
        }
    }
}
