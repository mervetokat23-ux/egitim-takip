package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Odeme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Odeme Repository
 * 
 * Odeme entity'si için JPA repository. Ödeme verilerine erişim sağlar.
 * Specification desteği ile dinamik filtreleme yapılabilir.
 */
@Repository
public interface OdemeRepository extends JpaRepository<Odeme, Long>, JpaSpecificationExecutor<Odeme> {
    
    List<Odeme> findByEgitimId(Long egitimId);
    
    List<Odeme> findByDurum(String durum);
    
    List<Odeme> findBySorumluId(Long sorumluId);
}

