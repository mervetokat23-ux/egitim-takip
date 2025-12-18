package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.PaydasDTO;
import com.akademi.egitimtakip.entity.Egitim;
import com.akademi.egitimtakip.entity.Paydas;
import com.akademi.egitimtakip.entity.Proje;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PaydasMapper {
    
    @Mapping(target = "egitimler", source = "egitimler", qualifiedByName = "toEgitimSimpleDTO")
    @Mapping(target = "projeler", source = "projeler", qualifiedByName = "toProjeSimpleDTO")
    PaydasDTO toDTO(Paydas paydas);
    
    @Mapping(target = "egitimler", ignore = true)
    @Mapping(target = "projeler", ignore = true)
    Paydas toEntity(PaydasDTO dto);
    
    List<PaydasDTO> toDTOList(List<Paydas> paydaslar);
    
    @Mapping(target = "egitimler", ignore = true)
    @Mapping(target = "projeler", ignore = true)
    void updateEntityFromDTO(PaydasDTO dto, @MappingTarget Paydas entity);

    @Named("toEgitimSimpleDTO")
    default Set<PaydasDTO.EgitimSimpleDTO> toEgitimSimpleDTO(Set<Egitim> egitimler) {
        if (egitimler == null) return null;
        return egitimler.stream()
                .map(e -> new PaydasDTO.EgitimSimpleDTO(e.getId(), e.getAd()))
                .collect(Collectors.toSet());
    }

    @Named("toProjeSimpleDTO")
    default Set<PaydasDTO.ProjeSimpleDTO> toProjeSimpleDTO(Set<Proje> projeler) {
        if (projeler == null) return null;
        return projeler.stream()
                .map(p -> new PaydasDTO.ProjeSimpleDTO(p.getId(), p.getIsim()))
                .collect(Collectors.toSet());
    }
}

