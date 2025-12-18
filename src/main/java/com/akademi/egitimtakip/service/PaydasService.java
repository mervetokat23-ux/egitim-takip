package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.PaydasDTO;
import com.akademi.egitimtakip.entity.Paydas;
import com.akademi.egitimtakip.mapper.PaydasMapper;
import com.akademi.egitimtakip.repository.PaydasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaydasService {

    @Autowired
    private PaydasRepository paydasRepository;

    @Autowired
    private PaydasMapper paydasMapper;

    public List<PaydasDTO> getAll() {
        return paydasMapper.toDTOList(paydasRepository.findAll());
    }

    public Page<PaydasDTO> getAll(Pageable pageable) {
        return paydasRepository.findAll(pageable).map(paydasMapper::toDTO);
    }

    public PaydasDTO getById(Long id) {
        Paydas paydas = paydasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paydaş bulunamadı: " + id));
        return paydasMapper.toDTO(paydas);
    }

    public PaydasDTO create(PaydasDTO dto) {
        Paydas paydas = paydasMapper.toEntity(dto);
        paydas = paydasRepository.save(paydas);
        return paydasMapper.toDTO(paydas);
    }

    public PaydasDTO update(Long id, PaydasDTO dto) {
        Paydas paydas = paydasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paydaş bulunamadı: " + id));
        
        paydasMapper.updateEntityFromDTO(dto, paydas);
        paydas.setId(id);
        
        paydas = paydasRepository.save(paydas);
        return paydasMapper.toDTO(paydas);
    }

    public void delete(Long id) {
        if (!paydasRepository.existsById(id)) {
            throw new RuntimeException("Paydaş bulunamadı: " + id);
        }
        paydasRepository.deleteById(id);
    }
}

