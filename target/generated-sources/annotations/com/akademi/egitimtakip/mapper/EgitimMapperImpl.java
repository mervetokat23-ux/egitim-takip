package com.akademi.egitimtakip.mapper;

import com.akademi.egitimtakip.dto.EgitimRequestDTO;
import com.akademi.egitimtakip.dto.EgitimResponseDTO;
import com.akademi.egitimtakip.dto.EgitmenDTO;
import com.akademi.egitimtakip.dto.KategoriDTO;
import com.akademi.egitimtakip.dto.PaydasDTO;
import com.akademi.egitimtakip.dto.SorumluDTO;
import com.akademi.egitimtakip.entity.Egitim;
import com.akademi.egitimtakip.entity.Egitmen;
import com.akademi.egitimtakip.entity.Kategori;
import com.akademi.egitimtakip.entity.Paydas;
import com.akademi.egitimtakip.entity.Proje;
import com.akademi.egitimtakip.entity.Sorumlu;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-14T20:34:23+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class EgitimMapperImpl implements EgitimMapper {

    @Override
    public EgitimResponseDTO toResponseDTO(Egitim egitim) {
        if ( egitim == null ) {
            return null;
        }

        EgitimResponseDTO egitimResponseDTO = new EgitimResponseDTO();

        egitimResponseDTO.setKategoriler( toKategoriDTOSet( egitim.getKategoriler() ) );
        egitimResponseDTO.setEgitmenler( toEgitmenDTOSet( egitim.getEgitmenler() ) );
        egitimResponseDTO.setSorumlular( toSorumluDTOSet( egitim.getSorumlular() ) );
        egitimResponseDTO.setPaydaslar( toPaydasDTOSet( egitim.getPaydaslar() ) );
        egitimResponseDTO.setProje( toProjeSimpleDTO( egitim.getProje() ) );
        egitimResponseDTO.setId( egitim.getId() );
        egitimResponseDTO.setAd( egitim.getAd() );
        egitimResponseDTO.setEgitimKodu( egitim.getEgitimKodu() );
        egitimResponseDTO.setProgramId( egitim.getProgramId() );
        egitimResponseDTO.setSeviye( egitim.getSeviye() );
        egitimResponseDTO.setHedefKitle( egitim.getHedefKitle() );
        egitimResponseDTO.setAciklama( egitim.getAciklama() );
        egitimResponseDTO.setBaslangicTarihi( egitim.getBaslangicTarihi() );
        egitimResponseDTO.setBitisTarihi( egitim.getBitisTarihi() );
        egitimResponseDTO.setEgitimSaati( egitim.getEgitimSaati() );
        egitimResponseDTO.setDurum( egitim.getDurum() );

        return egitimResponseDTO;
    }

    @Override
    public Egitim toEntity(EgitimRequestDTO requestDTO) {
        if ( requestDTO == null ) {
            return null;
        }

        Egitim egitim = new Egitim();

        egitim.setAd( requestDTO.getAd() );
        egitim.setEgitimKodu( requestDTO.getEgitimKodu() );
        egitim.setProgramId( requestDTO.getProgramId() );
        egitim.setSeviye( requestDTO.getSeviye() );
        egitim.setHedefKitle( requestDTO.getHedefKitle() );
        egitim.setAciklama( requestDTO.getAciklama() );
        egitim.setBaslangicTarihi( requestDTO.getBaslangicTarihi() );
        egitim.setBitisTarihi( requestDTO.getBitisTarihi() );
        egitim.setEgitimSaati( requestDTO.getEgitimSaati() );
        egitim.setDurum( requestDTO.getDurum() );

        return egitim;
    }

    @Override
    public void updateEntityFromDTO(EgitimRequestDTO requestDTO, Egitim egitim) {
        if ( requestDTO == null ) {
            return;
        }

        if ( requestDTO.getAd() != null ) {
            egitim.setAd( requestDTO.getAd() );
        }
        if ( requestDTO.getEgitimKodu() != null ) {
            egitim.setEgitimKodu( requestDTO.getEgitimKodu() );
        }
        if ( requestDTO.getProgramId() != null ) {
            egitim.setProgramId( requestDTO.getProgramId() );
        }
        if ( requestDTO.getSeviye() != null ) {
            egitim.setSeviye( requestDTO.getSeviye() );
        }
        if ( requestDTO.getHedefKitle() != null ) {
            egitim.setHedefKitle( requestDTO.getHedefKitle() );
        }
        if ( requestDTO.getAciklama() != null ) {
            egitim.setAciklama( requestDTO.getAciklama() );
        }
        if ( requestDTO.getBaslangicTarihi() != null ) {
            egitim.setBaslangicTarihi( requestDTO.getBaslangicTarihi() );
        }
        if ( requestDTO.getBitisTarihi() != null ) {
            egitim.setBitisTarihi( requestDTO.getBitisTarihi() );
        }
        if ( requestDTO.getEgitimSaati() != null ) {
            egitim.setEgitimSaati( requestDTO.getEgitimSaati() );
        }
        if ( requestDTO.getDurum() != null ) {
            egitim.setDurum( requestDTO.getDurum() );
        }
    }

    @Override
    public KategoriDTO toKategoriDTO(Kategori kategori) {
        if ( kategori == null ) {
            return null;
        }

        KategoriDTO kategoriDTO = new KategoriDTO();

        kategoriDTO.setId( kategori.getId() );
        kategoriDTO.setAd( kategori.getAd() );
        kategoriDTO.setAciklama( kategori.getAciklama() );

        return kategoriDTO;
    }

    @Override
    public EgitmenDTO toEgitmenDTO(Egitmen egitmen) {
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
    public SorumluDTO toSorumluDTO(Sorumlu sorumlu) {
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

    @Override
    public PaydasDTO toPaydasDTO(Paydas paydas) {
        if ( paydas == null ) {
            return null;
        }

        PaydasDTO paydasDTO = new PaydasDTO();

        paydasDTO.setId( paydas.getId() );
        paydasDTO.setAd( paydas.getAd() );
        paydasDTO.setEmail( paydas.getEmail() );
        paydasDTO.setTelefon( paydas.getTelefon() );
        paydasDTO.setAdres( paydas.getAdres() );
        paydasDTO.setTip( paydas.getTip() );
        paydasDTO.setEgitimler( egitimSetToEgitimSimpleDTOSet( paydas.getEgitimler() ) );
        paydasDTO.setProjeler( projeSetToProjeSimpleDTOSet( paydas.getProjeler() ) );

        return paydasDTO;
    }

    @Override
    public Set<KategoriDTO> toKategoriDTOSet(Set<Kategori> kategoriler) {
        if ( kategoriler == null ) {
            return null;
        }

        Set<KategoriDTO> set = new LinkedHashSet<KategoriDTO>( Math.max( (int) ( kategoriler.size() / .75f ) + 1, 16 ) );
        for ( Kategori kategori : kategoriler ) {
            set.add( toKategoriDTO( kategori ) );
        }

        return set;
    }

    @Override
    public Set<EgitmenDTO> toEgitmenDTOSet(Set<Egitmen> egitmenler) {
        if ( egitmenler == null ) {
            return null;
        }

        Set<EgitmenDTO> set = new LinkedHashSet<EgitmenDTO>( Math.max( (int) ( egitmenler.size() / .75f ) + 1, 16 ) );
        for ( Egitmen egitmen : egitmenler ) {
            set.add( toEgitmenDTO( egitmen ) );
        }

        return set;
    }

    @Override
    public Set<SorumluDTO> toSorumluDTOSet(Set<Sorumlu> sorumlular) {
        if ( sorumlular == null ) {
            return null;
        }

        Set<SorumluDTO> set = new LinkedHashSet<SorumluDTO>( Math.max( (int) ( sorumlular.size() / .75f ) + 1, 16 ) );
        for ( Sorumlu sorumlu : sorumlular ) {
            set.add( toSorumluDTO( sorumlu ) );
        }

        return set;
    }

    @Override
    public Set<PaydasDTO> toPaydasDTOSet(Set<Paydas> paydaslar) {
        if ( paydaslar == null ) {
            return null;
        }

        Set<PaydasDTO> set = new LinkedHashSet<PaydasDTO>( Math.max( (int) ( paydaslar.size() / .75f ) + 1, 16 ) );
        for ( Paydas paydas : paydaslar ) {
            set.add( toPaydasDTO( paydas ) );
        }

        return set;
    }

    protected PaydasDTO.EgitimSimpleDTO egitimToEgitimSimpleDTO(Egitim egitim) {
        if ( egitim == null ) {
            return null;
        }

        PaydasDTO.EgitimSimpleDTO egitimSimpleDTO = new PaydasDTO.EgitimSimpleDTO();

        egitimSimpleDTO.setId( egitim.getId() );
        egitimSimpleDTO.setAd( egitim.getAd() );

        return egitimSimpleDTO;
    }

    protected Set<PaydasDTO.EgitimSimpleDTO> egitimSetToEgitimSimpleDTOSet(Set<Egitim> set) {
        if ( set == null ) {
            return null;
        }

        Set<PaydasDTO.EgitimSimpleDTO> set1 = new LinkedHashSet<PaydasDTO.EgitimSimpleDTO>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Egitim egitim : set ) {
            set1.add( egitimToEgitimSimpleDTO( egitim ) );
        }

        return set1;
    }

    protected PaydasDTO.ProjeSimpleDTO projeToProjeSimpleDTO(Proje proje) {
        if ( proje == null ) {
            return null;
        }

        PaydasDTO.ProjeSimpleDTO projeSimpleDTO = new PaydasDTO.ProjeSimpleDTO();

        projeSimpleDTO.setId( proje.getId() );
        projeSimpleDTO.setIsim( proje.getIsim() );

        return projeSimpleDTO;
    }

    protected Set<PaydasDTO.ProjeSimpleDTO> projeSetToProjeSimpleDTOSet(Set<Proje> set) {
        if ( set == null ) {
            return null;
        }

        Set<PaydasDTO.ProjeSimpleDTO> set1 = new LinkedHashSet<PaydasDTO.ProjeSimpleDTO>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Proje proje : set ) {
            set1.add( projeToProjeSimpleDTO( proje ) );
        }

        return set1;
    }
}
