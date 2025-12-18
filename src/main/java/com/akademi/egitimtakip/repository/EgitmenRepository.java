package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Egitmen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Egitmen Repository
 * 
 * Egitmen entity'si için JPA repository. Eğitmen verilerine erişim sağlar.
 */
@Repository
public interface EgitmenRepository extends JpaRepository<Egitmen, Long> {
    
    Optional<Egitmen> findByEmail(String email);
    
    List<Egitmen> findByAdContainingIgnoreCaseOrSoyadContainingIgnoreCase(String ad, String soyad);
}

