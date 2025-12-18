package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.EgitmenDTO;
import com.akademi.egitimtakip.entity.Egitmen;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EgitmenMapper {
    EgitmenDTO toDTO(Egitmen egitmen);
    Egitmen toEntity(EgitmenDTO dto);
    List<EgitmenDTO> toDTOList(List<Egitmen> egitmenler);
    
    void updateEntityFromDTO(EgitmenDTO dto, @MappingTarget Egitmen entity);
}

