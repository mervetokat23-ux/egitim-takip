package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.OdemeRequestDTO;
import com.akademi.egitimtakip.dto.OdemeResponseDTO;
import com.akademi.egitimtakip.entity.Odeme;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-14T20:34:22+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class OdemeMapperImpl implements OdemeMapper {

    @Autowired
    private EgitimMapper egitimMapper;

    @Override
    public OdemeResponseDTO toResponseDTO(Odeme odeme) {
        if ( odeme == null ) {
            return null;
        }

        OdemeResponseDTO odemeResponseDTO = new OdemeResponseDTO();

        odemeResponseDTO.setEgitim( egitimMapper.toResponseDTO( odeme.getEgitim() ) );
        odemeResponseDTO.setSorumlu( egitimMapper.toSorumluDTO( odeme.getSorumlu() ) );
        odemeResponseDTO.setId( odeme.getId() );
        odemeResponseDTO.setBirimUcret( odeme.getBirimUcret() );
        odemeResponseDTO.setToplamUcret( odeme.getToplamUcret() );
        odemeResponseDTO.setOdemeKaynagi( odeme.getOdemeKaynagi() );
        odemeResponseDTO.setDurum( odeme.getDurum() );
        odemeResponseDTO.setOperasyon( odeme.getOperasyon() );
        odemeResponseDTO.setIsDeleted( odeme.getIsDeleted() );
        odemeResponseDTO.setCreatedAt( odeme.getCreatedAt() );
        odemeResponseDTO.setUpdatedAt( odeme.getUpdatedAt() );

        return odemeResponseDTO;
    }

    @Override
    public Odeme toEntity(OdemeRequestDTO requestDTO) {
        if ( requestDTO == null ) {
            return null;
        }

        Odeme odeme = new Odeme();

        odeme.setBirimUcret( requestDTO.getBirimUcret() );
        odeme.setToplamUcret( requestDTO.getToplamUcret() );
        odeme.setOdemeKaynagi( requestDTO.getOdemeKaynagi() );
        odeme.setDurum( requestDTO.getDurum() );
        odeme.setOperasyon( requestDTO.getOperasyon() );
        odeme.setIsDeleted( requestDTO.getIsDeleted() );

        return odeme;
    }

    @Override
    public void updateEntityFromDTO(OdemeRequestDTO requestDTO, Odeme odeme) {
        if ( requestDTO == null ) {
            return;
        }

        if ( requestDTO.getBirimUcret() != null ) {
            odeme.setBirimUcret( requestDTO.getBirimUcret() );
        }
        if ( requestDTO.getToplamUcret() != null ) {
            odeme.setToplamUcret( requestDTO.getToplamUcret() );
        }
        if ( requestDTO.getOdemeKaynagi() != null ) {
            odeme.setOdemeKaynagi( requestDTO.getOdemeKaynagi() );
        }
        if ( requestDTO.getDurum() != null ) {
            odeme.setDurum( requestDTO.getDurum() );
        }
        if ( requestDTO.getOperasyon() != null ) {
            odeme.setOperasyon( requestDTO.getOperasyon() );
        }
        if ( requestDTO.getIsDeleted() != null ) {
            odeme.setIsDeleted( requestDTO.getIsDeleted() );
        }
    }
}
