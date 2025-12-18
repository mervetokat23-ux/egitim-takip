package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ApiLog Repository
 * 
 * API log kayıtlarına erişim sağlar.
 */
@Repository
public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
    
    /**
     * Belirli bir kullanıcının API loglarını getirir
     */
    List<ApiLog> findByUserId(Long userId);
    
    /**
     * Belirli bir endpoint'in loglarını getirir
     */
    List<ApiLog> findByEndpointContainingIgnoreCase(String endpoint);
    
    /**
     * Belirli bir tarih aralığındaki logları getirir
     */
    List<ApiLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Belirli bir HTTP metodu için logları getirir
     */
    List<ApiLog> findByHttpMethod(String httpMethod);
    
    /**
     * Yavaş çalışan endpoint'leri bulmak için (örn: 1000ms üzeri)
     */
    List<ApiLog> findByDurationMsGreaterThan(Long durationMs);
}





