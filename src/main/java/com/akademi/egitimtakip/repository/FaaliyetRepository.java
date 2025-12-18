package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Faaliyet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Faaliyet Repository
 * 
 * Faaliyet entity'si için JPA repository. Faaliyet verilerine erişim sağlar.
 * Specification desteği ile dinamik filtreleme yapılabilir.
 */
@Repository
public interface FaaliyetRepository extends JpaRepository<Faaliyet, Long>, JpaSpecificationExecutor<Faaliyet> {
    
    List<Faaliyet> findByProjeId(Long projeId);
    
    List<Faaliyet> findByTarihBetween(LocalDate baslangic, LocalDate bitis);
    
    List<Faaliyet> findByTuru(String turu);
}

