package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.FrontendLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FrontendLog Repository
 * 
 * Frontend log kayıtlarına erişim sağlar.
 */
@Repository
public interface FrontendLogRepository extends JpaRepository<FrontendLog, Long>, JpaSpecificationExecutor<FrontendLog> {
    
    /**
     * Belirli bir kullanıcının frontend loglarını getirir
     */
    List<FrontendLog> findByUserId(Long userId);
    
    /**
     * Belirli bir kullanıcının frontend loglarını getirir (pagination)
     */
    Page<FrontendLog> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Belirli bir aksiyon türüne göre logları getirir
     */
    List<FrontendLog> findByAction(String action);
    
    /**
     * Belirli bir aksiyon türüne göre logları getirir (pagination)
     */
    Page<FrontendLog> findByAction(String action, Pageable pageable);
    
    /**
     * Belirli bir sayfaya ait logları getirir
     */
    List<FrontendLog> findByPageContainingIgnoreCase(String page);
    
    /**
     * Belirli bir sayfaya ait logları getirir (pagination)
     */
    Page<FrontendLog> findByPage(String page, Pageable pageable);
    
    /**
     * Belirli bir tarih aralığındaki frontend loglarını getirir
     */
    List<FrontendLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Belirli bir tarihten önce oluşturulan logları getirir
     */
    List<FrontendLog> findByCreatedAtBefore(LocalDateTime beforeDate);
    
    /**
     * En son frontend aktivitelerini getirir
     */
    List<FrontendLog> findTop100ByOrderByCreatedAtDesc();
}

