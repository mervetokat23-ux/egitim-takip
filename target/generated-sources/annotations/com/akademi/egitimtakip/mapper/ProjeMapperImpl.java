package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.ProjeRequestDTO;
import com.akademi.egitimtakip.dto.ProjeResponseDTO;
import com.akademi.egitimtakip.entity.Proje;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-14T20:34:23+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ProjeMapperImpl implements ProjeMapper {

    @Autowired
    private SorumluMapper sorumluMapper;
    @Autowired
    private PaydasMapper paydasMapper;

    @Override
    public ProjeResponseDTO toResponseDTO(Proje proje) {
        if ( proje == null ) {
            return null;
        }

        ProjeResponseDTO projeResponseDTO = new ProjeResponseDTO();

        projeResponseDTO.setEgitimSorumlu( sorumluMapper.toDTO( proje.getEgitimSorumlu() ) );
        projeResponseDTO.setPaydas( paydasMapper.toDTO( proje.getPaydas() ) );
        projeResponseDTO.setFaaliyetler( toFaaliyetSimpleDTO( proje.getFaaliyetler() ) );
        projeResponseDTO.setId( proje.getId() );
        projeResponseDTO.setIsim( proje.getIsim() );
        projeResponseDTO.setBaslangicTarihi( proje.getBaslangicTarihi() );
        projeResponseDTO.setTarih( proje.getTarih() );
        projeResponseDTO.setProjeHakkinda( proje.getProjeHakkinda() );

        return projeResponseDTO;
    }

    @Override
    public Proje toEntity(ProjeRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Proje proje = new Proje();

        proje.setIsim( dto.getIsim() );
        proje.setBaslangicTarihi( dto.getBaslangicTarihi() );
        proje.setTarih( dto.getTarih() );
        proje.setProjeHakkinda( dto.getProjeHakkinda() );

        return proje;
    }

    @Override
    public List<ProjeResponseDTO> toResponseDTOList(List<Proje> projeler) {
        if ( projeler == null ) {
            return null;
        }

        List<ProjeResponseDTO> list = new ArrayList<ProjeResponseDTO>( projeler.size() );
        for ( Proje proje : projeler ) {
            list.add( toResponseDTO( proje ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(ProjeRequestDTO dto, Proje entity) {
        if ( dto == null ) {
            return;
        }

        entity.setIsim( dto.getIsim() );
        entity.setBaslangicTarihi( dto.getBaslangicTarihi() );
        entity.setTarih( dto.getTarih() );
        entity.setProjeHakkinda( dto.getProjeHakkinda() );
    }
}
