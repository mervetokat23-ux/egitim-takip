package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.SorumluDTO;
import com.akademi.egitimtakip.entity.Role;
import com.akademi.egitimtakip.entity.Sorumlu;
import com.akademi.egitimtakip.mapper.SorumluMapper;
import com.akademi.egitimtakip.repository.RoleRepository;
import com.akademi.egitimtakip.repository.SorumluRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Sorumlu Service
 * 
 * Service for managing responsible persons (sorumlular).
 * Includes role assignment functionality.
 */
@Service
@Transactional
public class SorumluService {

    @Autowired
    private SorumluRepository sorumluRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SorumluMapper sorumluMapper;

    public List<SorumluDTO> getAll() {
        return sorumluMapper.toDTOList(sorumluRepository.findAll());
    }

    public Page<SorumluDTO> getAll(Pageable pageable) {
        return sorumluRepository.findAll(pageable).map(sorumluMapper::toDTO);
    }

    public SorumluDTO getById(Long id) {
        Sorumlu sorumlu = sorumluRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sorumlu bulunamadı: " + id));
        return sorumluMapper.toDTO(sorumlu);
    }

    public SorumluDTO create(SorumluDTO dto) {
        Sorumlu sorumlu = sorumluMapper.toEntity(dto);
        sorumlu = sorumluRepository.save(sorumlu);
        return sorumluMapper.toDTO(sorumlu);
    }

    public SorumluDTO update(Long id, SorumluDTO dto) {
        Sorumlu sorumlu = sorumluRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sorumlu bulunamadı: " + id));
        
        sorumluMapper.updateEntityFromDTO(dto, sorumlu);
        sorumlu.setId(id);
        
        sorumlu = sorumluRepository.save(sorumlu);
        return sorumluMapper.toDTO(sorumlu);
    }

    public void delete(Long id) {
        if (!sorumluRepository.existsById(id)) {
            throw new RuntimeException("Sorumlu bulunamadı: " + id);
        }
        sorumluRepository.deleteById(id);
    }

    /**
     * Assign a role to a responsible person
     * @param sorumluId Sorumlu ID
     * @param roleId Role ID
     * @return Updated SorumluDTO
     */
    public SorumluDTO assignRole(Long sorumluId, Long roleId) {
        Sorumlu sorumlu = sorumluRepository.findById(sorumluId)
                .orElseThrow(() -> new RuntimeException("Sorumlu bulunamadı: " + sorumluId));
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role bulunamadı: " + roleId));
        
        sorumlu.setRole(role);
        sorumlu = sorumluRepository.save(sorumlu);
        
        return sorumluMapper.toDTO(sorumlu);
    }

    /**
     * Remove role from a responsible person
     * @param sorumluId Sorumlu ID
     * @return Updated SorumluDTO
     */
    public SorumluDTO removeRole(Long sorumluId) {
        Sorumlu sorumlu = sorumluRepository.findById(sorumluId)
                .orElseThrow(() -> new RuntimeException("Sorumlu bulunamadı: " + sorumluId));
        
        sorumlu.setRole(null);
        sorumlu = sorumluRepository.save(sorumlu);
        
        return sorumluMapper.toDTO(sorumlu);
    }

    /**
     * Get responsible persons by role
     * @param roleId Role ID
     * @return List of SorumluDTO
     */
    public List<SorumluDTO> getByRole(Long roleId) {
        List<Sorumlu> sorumlular = sorumluRepository.findByRoleId(roleId);
        return sorumluMapper.toDTOList(sorumlular);
    }
}

