package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.*;
import com.akademi.egitimtakip.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

/**
 * Egitim Mapper
 * 
 * MapStruct ile Entity ve DTO arasında mapping yapar.
 * H2 database ile uyumlu çalışır.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EgitimMapper {

    /**
     * Entity'den Response DTO'ya mapping
     */
    @Mapping(target = "kategoriler", source = "kategoriler")
    @Mapping(target = "egitmenler", source = "egitmenler")
    @Mapping(target = "sorumlular", source = "sorumlular")
    @Mapping(target = "paydaslar", source = "paydaslar")
    @Mapping(target = "proje", source = "proje")
    EgitimResponseDTO toResponseDTO(Egitim egitim);

    /**
     * Request DTO'dan Entity'ye mapping (ilişkiler hariç)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "kategoriler", ignore = true)
    @Mapping(target = "egitmenler", ignore = true)
    @Mapping(target = "sorumlular", ignore = true)
    @Mapping(target = "paydaslar", ignore = true)
    @Mapping(target = "proje", ignore = true)
    Egitim toEntity(EgitimRequestDTO requestDTO);

    /**
     * Request DTO'dan Entity'ye güncelleme (mevcut entity'yi günceller)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "kategoriler", ignore = true)
    @Mapping(target = "egitmenler", ignore = true)
    @Mapping(target = "sorumlular", ignore = true)
    @Mapping(target = "paydaslar", ignore = true)
    @Mapping(target = "proje", ignore = true)
    void updateEntityFromDTO(EgitimRequestDTO requestDTO, @MappingTarget Egitim egitim);

    // Nested DTO mappings
    KategoriDTO toKategoriDTO(Kategori kategori);
    EgitmenDTO toEgitmenDTO(Egitmen egitmen);
    SorumluDTO toSorumluDTO(Sorumlu sorumlu);
    PaydasDTO toPaydasDTO(Paydas paydas);
    
    // Proje mapping
    default EgitimResponseDTO.ProjeSimpleDTO toProjeSimpleDTO(Proje proje) {
        if (proje == null) return null;
        return new EgitimResponseDTO.ProjeSimpleDTO(proje.getId(), proje.getIsim());
    }

    // Set mappings
    Set<KategoriDTO> toKategoriDTOSet(Set<Kategori> kategoriler);
    Set<EgitmenDTO> toEgitmenDTOSet(Set<Egitmen> egitmenler);
    Set<SorumluDTO> toSorumluDTOSet(Set<Sorumlu> sorumlular);
    Set<PaydasDTO> toPaydasDTOSet(Set<Paydas> paydaslar);
}

