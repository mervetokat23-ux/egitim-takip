package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kullanici Repository
 * 
 * Kullanici entity'si için JPA repository. Authentication işlemleri için kullanılır.
 */
@Repository
public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {
    
    Optional<Kullanici> findByEmail(String email);
    
    Optional<Kullanici> findByKullaniciAdi(String kullaniciAdi);
    
    boolean existsByEmail(String email);
    
    boolean existsByKullaniciAdi(String kullaniciAdi);
}

