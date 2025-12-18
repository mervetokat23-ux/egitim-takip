package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kategori Repository
 * 
 * Kategori entity'si için JPA repository. Kategori verilerine erişim sağlar.
 */
@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {
    
    Optional<Kategori> findByAd(String ad);
    
    boolean existsByAd(String ad);
}

