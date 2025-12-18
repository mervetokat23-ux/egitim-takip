package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.OdemeRequestDTO;
import com.akademi.egitimtakip.dto.OdemeResponseDTO;
import com.akademi.egitimtakip.entity.Odeme;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Odeme Mapper
 * 
 * MapStruct ile Entity ve DTO arasında mapping yapar.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {EgitimMapper.class})
public interface OdemeMapper {

    /**
     * Entity'den Response DTO'ya mapping
     */
    @Mapping(target = "egitim", source = "egitim")
    @Mapping(target = "sorumlu", source = "sorumlu")
    OdemeResponseDTO toResponseDTO(Odeme odeme);

    /**
     * Request DTO'dan Entity'ye mapping (ilişkiler hariç)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "egitim", ignore = true)
    @Mapping(target = "sorumlu", ignore = true)
    Odeme toEntity(OdemeRequestDTO requestDTO);

    /**
     * Request DTO'dan Entity'ye güncelleme
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "egitim", ignore = true)
    @Mapping(target = "sorumlu", ignore = true)
    void updateEntityFromDTO(OdemeRequestDTO requestDTO, @MappingTarget Odeme odeme);
}

