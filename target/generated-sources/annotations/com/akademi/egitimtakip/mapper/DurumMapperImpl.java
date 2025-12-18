package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.DurumRequestDTO;
import com.akademi.egitimtakip.dto.DurumResponseDTO;
import com.akademi.egitimtakip.entity.Durum;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-14T20:34:23+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class DurumMapperImpl implements DurumMapper {

    @Autowired
    private EgitimMapper egitimMapper;

    @Override
    public DurumResponseDTO toResponseDTO(Durum durum) {
        if ( durum == null ) {
            return null;
        }

        DurumResponseDTO durumResponseDTO = new DurumResponseDTO();

        durumResponseDTO.setEgitim( egitimMapper.toResponseDTO( durum.getEgitim() ) );
        durumResponseDTO.setId( durum.getId() );
        durumResponseDTO.setDurum( durum.getDurum() );
        durumResponseDTO.setOperasyon( durum.getOperasyon() );

        return durumResponseDTO;
    }

    @Override
    public Durum toEntity(DurumRequestDTO requestDTO) {
        if ( requestDTO == null ) {
            return null;
        }

        Durum durum = new Durum();

        durum.setDurum( requestDTO.getDurum() );
        durum.setOperasyon( requestDTO.getOperasyon() );

        return durum;
    }

    @Override
    public void updateEntityFromDTO(DurumRequestDTO requestDTO, Durum durum) {
        if ( requestDTO == null ) {
            return;
        }

        if ( requestDTO.getDurum() != null ) {
            durum.setDurum( requestDTO.getDurum() );
        }
        if ( requestDTO.getOperasyon() != null ) {
            durum.setOperasyon( requestDTO.getOperasyon() );
        }
    }
}
