package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.KategoriDTO;
import com.akademi.egitimtakip.entity.Kategori;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-14T20:34:23+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class KategoriMapperImpl extends KategoriMapper {

    @Override
    public KategoriDTO toDTO(Kategori kategori) {
        if ( kategori == null ) {
            return null;
        }

        KategoriDTO kategoriDTO = new KategoriDTO();

        kategoriDTO.setUstKategoriId( kategoriUstKategoriId( kategori ) );
        kategoriDTO.setUstKategoriAd( kategoriUstKategoriAd( kategori ) );
        kategoriDTO.setId( kategori.getId() );
        kategoriDTO.setAd( kategori.getAd() );
        kategoriDTO.setAciklama( kategori.getAciklama() );

        return kategoriDTO;
    }

    @Override
    public Kategori toEntity(KategoriDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Kategori kategori = new Kategori();

        kategori.setId( dto.getId() );
        kategori.setAd( dto.getAd() );
        kategori.setAciklama( dto.getAciklama() );

        return kategori;
    }

    @Override
    public List<KategoriDTO> toDTOList(List<Kategori> kategoriler) {
        if ( kategoriler == null ) {
            return null;
        }

        List<KategoriDTO> list = new ArrayList<KategoriDTO>( kategoriler.size() );
        for ( Kategori kategori : kategoriler ) {
            list.add( toDTO( kategori ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(KategoriDTO dto, Kategori entity) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setAd( dto.getAd() );
        entity.setAciklama( dto.getAciklama() );
    }

    private Long kategoriUstKategoriId(Kategori kategori) {
        if ( kategori == null ) {
            return null;
        }
        Kategori ustKategori = kategori.getUstKategori();
        if ( ustKategori == null ) {
            return null;
        }
        Long id = ustKategori.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String kategoriUstKategoriAd(Kategori kategori) {
        if ( kategori == null ) {
            return null;
        }
        Kategori ustKategori = kategori.getUstKategori();
        if ( ustKategori == null ) {
            return null;
        }
        String ad = ustKategori.getAd();
        if ( ad == null ) {
            return null;
        }
        return ad;
    }
}
