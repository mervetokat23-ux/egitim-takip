package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.PerformanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PerformanceLog Repository
 * 
 * Performans loglarına erişim sağlar.
 */
@Repository
public interface PerformanceLogRepository extends JpaRepository<PerformanceLog, Long> {
    
    /**
     * Belirli bir endpoint'in performans loglarını getirir
     */
    List<PerformanceLog> findByEndpointContainingIgnoreCase(String endpoint);
    
    /**
     * Yavaş çalışan işlemleri getirir (belirli bir sürenin üzeri)
     */
    List<PerformanceLog> findByDurationMsGreaterThan(Long durationMs);
    
    /**
     * Belirli bir tarih aralığındaki performans loglarını getirir
     */
    List<PerformanceLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * En yavaş çalışan endpoint'leri getirir
     */
    List<PerformanceLog> findTop50ByOrderByDurationMsDesc();
    
    /**
     * Endpoint'lere göre ortalama süreleri hesaplar
     */
    @Query("SELECT p.endpoint, AVG(p.durationMs) FROM PerformanceLog p GROUP BY p.endpoint ORDER BY AVG(p.durationMs) DESC")
    List<Object[]> findAverageDurationByEndpoint();
}





