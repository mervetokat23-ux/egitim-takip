package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Proje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Proje Repository
 * 
 * Proje entity'si için JPA repository. Proje verilerine erişim sağlar.
 * Specification desteği ile dinamik filtreleme yapılabilir.
 */
@Repository
public interface ProjeRepository extends JpaRepository<Proje, Long>, JpaSpecificationExecutor<Proje> {

    /**
     * Proje ismine göre (case-insensitive) arama yapar.
     */
    java.util.List<Proje> findByIsimContainingIgnoreCase(String isim);
}

