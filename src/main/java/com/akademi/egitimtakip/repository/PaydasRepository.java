package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Paydas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Paydas Repository
 * 
 * Paydas entity'si için JPA repository. Paydaş verilerine erişim sağlar.
 */
@Repository
public interface PaydasRepository extends JpaRepository<Paydas, Long> {
    
    List<Paydas> findByAdContainingIgnoreCase(String ad);
    
    List<Paydas> findByTip(String tip);
}

