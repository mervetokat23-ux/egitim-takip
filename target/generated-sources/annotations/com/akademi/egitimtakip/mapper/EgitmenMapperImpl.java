package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.EgitmenDTO;
import com.akademi.egitimtakip.entity.Egitmen;
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
public class EgitmenMapperImpl implements EgitmenMapper {

    @Override
    public EgitmenDTO toDTO(Egitmen egitmen) {
        if ( egitmen == null ) {
            return null;
        }

        EgitmenDTO egitmenDTO = new EgitmenDTO();

        egitmenDTO.setId( egitmen.getId() );
        egitmenDTO.setAd( egitmen.getAd() );
        egitmenDTO.setSoyad( egitmen.getSoyad() );
        egitmenDTO.setEmail( egitmen.getEmail() );
        egitmenDTO.setTelefon( egitmen.getTelefon() );
        egitmenDTO.setIl( egitmen.getIl() );
        egitmenDTO.setCalismaYeri( egitmen.getCalismaYeri() );

        return egitmenDTO;
    }

    @Override
    public Egitmen toEntity(EgitmenDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Egitmen egitmen = new Egitmen();

        egitmen.setId( dto.getId() );
        egitmen.setAd( dto.getAd() );
        egitmen.setSoyad( dto.getSoyad() );
        egitmen.setEmail( dto.getEmail() );
        egitmen.setTelefon( dto.getTelefon() );
        egitmen.setIl( dto.getIl() );
        egitmen.setCalismaYeri( dto.getCalismaYeri() );

        return egitmen;
    }

    @Override
    public List<EgitmenDTO> toDTOList(List<Egitmen> egitmenler) {
        if ( egitmenler == null ) {
            return null;
        }

        List<EgitmenDTO> list = new ArrayList<EgitmenDTO>( egitmenler.size() );
        for ( Egitmen egitmen : egitmenler ) {
            list.add( toDTO( egitmen ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(EgitmenDTO dto, Egitmen entity) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setAd( dto.getAd() );
        entity.setSoyad( dto.getSoyad() );
        entity.setEmail( dto.getEmail() );
        entity.setTelefon( dto.getTelefon() );
        entity.setIl( dto.getIl() );
        entity.setCalismaYeri( dto.getCalismaYeri() );
    }
}
