package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.SorumluDTO;
import com.akademi.egitimtakip.entity.Sorumlu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Sorumlu Mapper
 * 
 * Maps between Sorumlu entity and SorumluDTO.
 * Includes role information mapping.
 */
@Mapper(componentModel = "spring")
public interface SorumluMapper {
    
    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.name", target = "roleName")
    SorumluDTO toDTO(Sorumlu sorumlu);
    
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "egitimler", ignore = true)
    @Mapping(target = "faaliyetler", ignore = true)
    Sorumlu toEntity(SorumluDTO dto);
    
    List<SorumluDTO> toDTOList(List<Sorumlu> sorumlular);
    
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "egitimler", ignore = true)
    @Mapping(target = "faaliyetler", ignore = true)
    void updateEntityFromDTO(SorumluDTO dto, @MappingTarget Sorumlu entity);
}
