package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.FaaliyetRequestDTO;
import com.akademi.egitimtakip.dto.FaaliyetResponseDTO;
import com.akademi.egitimtakip.dto.SorumluDTO;
import com.akademi.egitimtakip.entity.Faaliyet;
import com.akademi.egitimtakip.entity.Sorumlu;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-14T20:34:23+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class FaaliyetMapperImpl implements FaaliyetMapper {

    @Autowired
    private ProjeMapper projeMapper;

    @Override
    public FaaliyetResponseDTO toResponseDTO(Faaliyet faaliyet) {
        if ( faaliyet == null ) {
            return null;
        }

        FaaliyetResponseDTO faaliyetResponseDTO = new FaaliyetResponseDTO();

        faaliyetResponseDTO.setProje( projeMapper.toResponseDTO( faaliyet.getProje() ) );
        faaliyetResponseDTO.setSorumlular( toSorumluDTOSet( faaliyet.getSorumlular() ) );
        faaliyetResponseDTO.setId( faaliyet.getId() );
        faaliyetResponseDTO.setTarih( faaliyet.getTarih() );
        faaliyetResponseDTO.setIsim( faaliyet.getIsim() );
        faaliyetResponseDTO.setTuru( faaliyet.getTuru() );

        return faaliyetResponseDTO;
    }

    @Override
    public Faaliyet toEntity(FaaliyetRequestDTO requestDTO) {
        if ( requestDTO == null ) {
            return null;
        }

        Faaliyet faaliyet = new Faaliyet();

        faaliyet.setTarih( requestDTO.getTarih() );
        faaliyet.setIsim( requestDTO.getIsim() );
        faaliyet.setTuru( requestDTO.getTuru() );

        return faaliyet;
    }

    @Override
    public void updateEntityFromDTO(FaaliyetRequestDTO requestDTO, Faaliyet faaliyet) {
        if ( requestDTO == null ) {
            return;
        }

        if ( requestDTO.getTarih() != null ) {
            faaliyet.setTarih( requestDTO.getTarih() );
        }
        if ( requestDTO.getIsim() != null ) {
            faaliyet.setIsim( requestDTO.getIsim() );
        }
        if ( requestDTO.getTuru() != null ) {
            faaliyet.setTuru( requestDTO.getTuru() );
        }
    }

    @Override
    public Set<SorumluDTO> toSorumluDTOSet(Set<Sorumlu> sorumlular) {
        if ( sorumlular == null ) {
            return null;
        }

        Set<SorumluDTO> set = new LinkedHashSet<SorumluDTO>( Math.max( (int) ( sorumlular.size() / .75f ) + 1, 16 ) );
        for ( Sorumlu sorumlu : sorumlular ) {
            set.add( sorumluToSorumluDTO( sorumlu ) );
        }

        return set;
    }

    protected SorumluDTO sorumluToSorumluDTO(Sorumlu sorumlu) {
        if ( sorumlu == null ) {
            return null;
        }

        SorumluDTO sorumluDTO = new SorumluDTO();

        sorumluDTO.setId( sorumlu.getId() );
        sorumluDTO.setAd( sorumlu.getAd() );
        sorumluDTO.setSoyad( sorumlu.getSoyad() );
        sorumluDTO.setEmail( sorumlu.getEmail() );
        sorumluDTO.setTelefon( sorumlu.getTelefon() );
        List<String> list = sorumlu.getUnvanlar();
        if ( list != null ) {
            sorumluDTO.setUnvanlar( new ArrayList<String>( list ) );
        }

        return sorumluDTO;
    }
}
