package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ActivityLog Repository
 * 
 * Kullanıcı aktivite loglarına erişim sağlar.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    /**
     * Belirli bir kullanıcının aktivite loglarını getirir
     */
    List<ActivityLog> findByUserId(Long userId);
    
    /**
     * Belirli bir aksiyon türüne göre logları getirir
     */
    List<ActivityLog> findByAction(String action);
    
    /**
     * Belirli bir entity türüne göre logları getirir
     */
    List<ActivityLog> findByEntityType(String entityType);
    
    /**
     * Belirli bir entity'nin tüm aktivitelerini getirir
     */
    List<ActivityLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Belirli bir tarih aralığındaki aktiviteleri getirir
     */
    List<ActivityLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}





