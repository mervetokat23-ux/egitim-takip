package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.PaydasDTO;
import com.akademi.egitimtakip.entity.Paydas;
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
public class PaydasMapperImpl implements PaydasMapper {

    @Override
    public PaydasDTO toDTO(Paydas paydas) {
        if ( paydas == null ) {
            return null;
        }

        PaydasDTO paydasDTO = new PaydasDTO();

        paydasDTO.setEgitimler( toEgitimSimpleDTO( paydas.getEgitimler() ) );
        paydasDTO.setProjeler( toProjeSimpleDTO( paydas.getProjeler() ) );
        paydasDTO.setId( paydas.getId() );
        paydasDTO.setAd( paydas.getAd() );
        paydasDTO.setEmail( paydas.getEmail() );
        paydasDTO.setTelefon( paydas.getTelefon() );
        paydasDTO.setAdres( paydas.getAdres() );
        paydasDTO.setTip( paydas.getTip() );

        return paydasDTO;
    }

    @Override
    public Paydas toEntity(PaydasDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Paydas paydas = new Paydas();

        paydas.setId( dto.getId() );
        paydas.setAd( dto.getAd() );
        paydas.setEmail( dto.getEmail() );
        paydas.setTelefon( dto.getTelefon() );
        paydas.setAdres( dto.getAdres() );
        paydas.setTip( dto.getTip() );

        return paydas;
    }

    @Override
    public List<PaydasDTO> toDTOList(List<Paydas> paydaslar) {
        if ( paydaslar == null ) {
            return null;
        }

        List<PaydasDTO> list = new ArrayList<PaydasDTO>( paydaslar.size() );
        for ( Paydas paydas : paydaslar ) {
            list.add( toDTO( paydas ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(PaydasDTO dto, Paydas entity) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setAd( dto.getAd() );
        entity.setEmail( dto.getEmail() );
        entity.setTelefon( dto.getTelefon() );
        entity.setAdres( dto.getAdres() );
        entity.setTip( dto.getTip() );
    }
}
