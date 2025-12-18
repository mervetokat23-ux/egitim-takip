package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.DurumRequestDTO;
import com.akademi.egitimtakip.dto.DurumResponseDTO;
import com.akademi.egitimtakip.entity.Durum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Durum Mapper
 * 
 * MapStruct ile Entity ve DTO arasında mapping yapar.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {EgitimMapper.class})
public interface DurumMapper {

    /**
     * Entity'den Response DTO'ya mapping
     */
    @Mapping(target = "egitim", source = "egitim")
    DurumResponseDTO toResponseDTO(Durum durum);

    /**
     * Request DTO'dan Entity'ye mapping (ilişkiler hariç)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "egitim", ignore = true)
    Durum toEntity(DurumRequestDTO requestDTO);

    /**
     * Request DTO'dan Entity'ye güncelleme
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "egitim", ignore = true)
    void updateEntityFromDTO(DurumRequestDTO requestDTO, @MappingTarget Durum durum);
}

