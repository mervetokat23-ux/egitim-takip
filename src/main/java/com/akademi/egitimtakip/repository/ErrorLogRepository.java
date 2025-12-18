package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ErrorLog Repository
 * 
 * Hata loglarına erişim sağlar.
 */
@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    
    /**
     * Belirli bir kullanıcının hata loglarını getirir
     */
    List<ErrorLog> findByUserId(Long userId);
    
    /**
     * Belirli bir endpoint'teki hataları getirir
     */
    List<ErrorLog> findByEndpointContainingIgnoreCase(String endpoint);
    
    /**
     * Belirli bir exception türüne göre hataları getirir
     */
    List<ErrorLog> findByExceptionType(String exceptionType);
    
    /**
     * Belirli bir tarih aralığındaki hataları getirir
     */
    List<ErrorLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * En son hataları getirir (sıralı)
     */
    List<ErrorLog> findTop100ByOrderByCreatedAtDesc();
}





