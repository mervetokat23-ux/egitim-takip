package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.SorumluDTO;
import com.akademi.egitimtakip.entity.Role;
import com.akademi.egitimtakip.entity.Sorumlu;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-14T20:34:22+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class SorumluMapperImpl implements SorumluMapper {

    @Override
    public SorumluDTO toDTO(Sorumlu sorumlu) {
        if ( sorumlu == null ) {
            return null;
        }

        SorumluDTO sorumluDTO = new SorumluDTO();

        sorumluDTO.setRoleId( sorumluRoleId( sorumlu ) );
        sorumluDTO.setRoleName( sorumluRoleName( sorumlu ) );
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

    @Override
    public Sorumlu toEntity(SorumluDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Sorumlu sorumlu = new Sorumlu();

        sorumlu.setId( dto.getId() );
        sorumlu.setAd( dto.getAd() );
        sorumlu.setSoyad( dto.getSoyad() );
        sorumlu.setEmail( dto.getEmail() );
        sorumlu.setTelefon( dto.getTelefon() );
        List<String> list = dto.getUnvanlar();
        if ( list != null ) {
            sorumlu.setUnvanlar( new ArrayList<String>( list ) );
        }

        return sorumlu;
    }

    @Override
    public List<SorumluDTO> toDTOList(List<Sorumlu> sorumlular) {
        if ( sorumlular == null ) {
            return null;
        }

        List<SorumluDTO> list = new ArrayList<SorumluDTO>( sorumlular.size() );
        for ( Sorumlu sorumlu : sorumlular ) {
            list.add( toDTO( sorumlu ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(SorumluDTO dto, Sorumlu entity) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setAd( dto.getAd() );
        entity.setSoyad( dto.getSoyad() );
        entity.setEmail( dto.getEmail() );
        entity.setTelefon( dto.getTelefon() );
        if ( entity.getUnvanlar() != null ) {
            List<String> list = dto.getUnvanlar();
            if ( list != null ) {
                entity.getUnvanlar().clear();
                entity.getUnvanlar().addAll( list );
            }
            else {
                entity.setUnvanlar( null );
            }
        }
        else {
            List<String> list = dto.getUnvanlar();
            if ( list != null ) {
                entity.setUnvanlar( new ArrayList<String>( list ) );
            }
        }
    }

    private Long sorumluRoleId(Sorumlu sorumlu) {
        if ( sorumlu == null ) {
            return null;
        }
        Role role = sorumlu.getRole();
        if ( role == null ) {
            return null;
        }
        Long id = role.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String sorumluRoleName(Sorumlu sorumlu) {
        if ( sorumlu == null ) {
            return null;
        }
        Role role = sorumlu.getRole();
        if ( role == null ) {
            return null;
        }
        String name = role.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
