package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Durum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Durum Repository
 * 
 * Durum entity'si için JPA repository. Durum verilerine erişim sağlar.
 * Specification desteği ile dinamik filtreleme yapılabilir.
 */
@Repository
public interface DurumRepository extends JpaRepository<Durum, Long>, JpaSpecificationExecutor<Durum> {
    
    List<Durum> findByEgitimId(Long egitimId);
    
    List<Durum> findByDurum(String durum);
}

