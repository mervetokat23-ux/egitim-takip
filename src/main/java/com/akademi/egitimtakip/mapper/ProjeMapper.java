package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.ProjeRequestDTO;
import com.akademi.egitimtakip.dto.ProjeResponseDTO;
import com.akademi.egitimtakip.entity.Faaliyet;
import com.akademi.egitimtakip.entity.Proje;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SorumluMapper.class, PaydasMapper.class})
public interface ProjeMapper {
    
    @Mapping(target = "egitimSorumlu", source = "egitimSorumlu")
    @Mapping(target = "paydas", source = "paydas")
    @Mapping(target = "faaliyetler", source = "faaliyetler", qualifiedByName = "toFaaliyetSimpleDTO")
    ProjeResponseDTO toResponseDTO(Proje proje);
    
    @Mapping(target = "egitimSorumlu", ignore = true)
    @Mapping(target = "paydas", ignore = true)
    @Mapping(target = "faaliyetler", ignore = true)
    Proje toEntity(ProjeRequestDTO dto);
    
    List<ProjeResponseDTO> toResponseDTOList(List<Proje> projeler);
    
    @Mapping(target = "egitimSorumlu", ignore = true)
    @Mapping(target = "paydas", ignore = true)
    @Mapping(target = "faaliyetler", ignore = true)
    void updateEntityFromDTO(ProjeRequestDTO dto, @MappingTarget Proje entity);

    @Named("toFaaliyetSimpleDTO")
    default List<ProjeResponseDTO.FaaliyetSimpleDTO> toFaaliyetSimpleDTO(Set<Faaliyet> faaliyetler) {
        if (faaliyetler == null) return null;
        return faaliyetler.stream()
                .map(f -> new ProjeResponseDTO.FaaliyetSimpleDTO(f.getId(), f.getIsim(), f.getTuru(), f.getTarih()))
                .collect(Collectors.toList());
    }
}
