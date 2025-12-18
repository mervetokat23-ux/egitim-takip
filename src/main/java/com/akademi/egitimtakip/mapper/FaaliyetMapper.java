package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.FaaliyetRequestDTO;
import com.akademi.egitimtakip.dto.FaaliyetResponseDTO;
import com.akademi.egitimtakip.entity.Faaliyet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

/**
 * Faaliyet Mapper
 * 
 * MapStruct ile Entity ve DTO arasında mapping yapar.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {ProjeMapper.class})
public interface FaaliyetMapper {

    /**
     * Entity'den Response DTO'ya mapping
     */
    @Mapping(target = "proje", source = "proje")
    @Mapping(target = "sorumlular", source = "sorumlular")
    FaaliyetResponseDTO toResponseDTO(Faaliyet faaliyet);

    /**
     * Request DTO'dan Entity'ye mapping (ilişkiler hariç)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proje", ignore = true)
    @Mapping(target = "sorumlular", ignore = true)
    Faaliyet toEntity(FaaliyetRequestDTO requestDTO);

    /**
     * Request DTO'dan Entity'ye güncelleme
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proje", ignore = true)
    @Mapping(target = "sorumlular", ignore = true)
    void updateEntityFromDTO(FaaliyetRequestDTO requestDTO, @MappingTarget Faaliyet faaliyet);

    // Set mappings
    Set<com.akademi.egitimtakip.dto.SorumluDTO> toSorumluDTOSet(Set<com.akademi.egitimtakip.entity.Sorumlu> sorumlular);
}

