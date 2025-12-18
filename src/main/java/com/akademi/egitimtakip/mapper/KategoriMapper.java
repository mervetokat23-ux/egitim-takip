package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.KategoriDTO;
import com.akademi.egitimtakip.entity.Kategori;
import com.akademi.egitimtakip.repository.KategoriRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class KategoriMapper {
    
    @Autowired
    protected KategoriRepository kategoriRepository;

    @Mapping(target = "ustKategoriId", source = "ustKategori.id")
    @Mapping(target = "ustKategoriAd", source = "ustKategori.ad")
    public abstract KategoriDTO toDTO(Kategori kategori);
    
    @Mapping(target = "ustKategori", ignore = true) // Service katmanında set edilecek
    @Mapping(target = "altKategoriler", ignore = true)
    @Mapping(target = "egitimler", ignore = true)
    public abstract Kategori toEntity(KategoriDTO dto);
    
    public abstract List<KategoriDTO> toDTOList(List<Kategori> kategoriler);
    
    @Mapping(target = "ustKategori", ignore = true)
    @Mapping(target = "altKategoriler", ignore = true)
    @Mapping(target = "egitimler", ignore = true)
    public abstract void updateEntityFromDTO(KategoriDTO dto, @MappingTarget Kategori entity);
}

