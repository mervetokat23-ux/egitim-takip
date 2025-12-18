package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Egitim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Egitim Repository
 * 
 * Egitim entity'si için JPA repository. Eğitim verilerine erişim sağlar.
 * Specification desteği ile dinamik filtreleme yapılabilir.
 */
@Repository
public interface EgitimRepository extends JpaRepository<Egitim, Long>, JpaSpecificationExecutor<Egitim> {
    
    List<Egitim> findByAdContainingIgnoreCase(String ad);
    
    List<Egitim> findByDurum(String durum);
    
    List<Egitim> findByBaslangicTarihiBetween(LocalDate baslangic, LocalDate bitis);
}

