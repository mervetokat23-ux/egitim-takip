package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Sorumlu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Sorumlu Repository
 * 
 * Sorumlu entity'si için JPA repository. Sorumlu verilerine erişim sağlar.
 */
@Repository
public interface SorumluRepository extends JpaRepository<Sorumlu, Long> {
    
    Optional<Sorumlu> findByEmail(String email);
    
    List<Sorumlu> findByAdContainingIgnoreCaseOrSoyadContainingIgnoreCase(String ad, String soyad);
    
    List<Sorumlu> findByRoleId(Long roleId);
    
    List<Sorumlu> findByRoleIsNull();
}

