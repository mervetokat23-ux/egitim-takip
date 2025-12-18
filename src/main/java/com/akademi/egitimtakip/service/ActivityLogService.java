package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.entity.ActivityLog;
import com.akademi.egitimtakip.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ActivityLogService
 * 
 * Kullanıcı aktivite loglarını yönetir.
 * ActionLogAspect tarafından kullanılır.
 */
@Service
@Transactional
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    /**
     * Activity log kaydı oluşturur (asenkron)
     * 
     * @param userId Kullanıcı ID (opsiyonel, null olabilir)
     * @param action Aksiyon türü (CREATE, UPDATE, DELETE, VIEW, vb.)
     * @param entityType Entity türü (Egitim, Sorumlu, Proje, vb.)
     * @param entityId Entity ID
     * @param description Açıklama
     */
    @Async
    public void saveActivityLog(Long userId, String action, String entityType, Long entityId, String description) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setAction(truncate(action, 100));
            log.setEntityType(truncate(entityType, 100));
            log.setEntityId(entityId);
            log.setDescription(truncate(description, 1000));
            
            activityLogRepository.save(log);
            
            // Opsiyonel: Konsola da yazdır (development ortamında yararlı)
            System.out.println(String.format(
                "📝 Activity Log: [%s] %s (Entity: %s, ID: %s) by User: %s - %s",
                action, description, entityType, 
                entityId != null ? entityId.toString() : "N/A", 
                userId != null ? userId.toString() : "anonymous",
                LocalDateTime.now()
            ));
        } catch (Exception e) {
            // Loglama hatası uygulamayı etkilememeli
            System.err.println("Activity log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Belirli bir kullanıcının aktivitelerini getirir
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivitiesByUserId(Long userId) {
        return activityLogRepository.findByUserId(userId);
    }

    /**
     * Belirli bir aksiyon türüne göre logları getirir
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivitiesByAction(String action) {
        return activityLogRepository.findByAction(action);
    }

    /**
     * Belirli bir entity türüne göre logları getirir
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivitiesByEntityType(String entityType) {
        return activityLogRepository.findByEntityType(entityType);
    }

    /**
     * Belirli bir entity'nin tüm aktivitelerini getirir
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivitiesByEntity(String entityType, Long entityId) {
        return activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    /**
     * Belirli bir tarih aralığındaki aktiviteleri getirir
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivitiesByDateRange(LocalDateTime start, LocalDateTime end) {
        return activityLogRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Tüm aktivite loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getAllActivities() {
        return activityLogRepository.findAll();
    }

    /**
     * Belirli bir aktivite kaydını siler
     */
    public void deleteActivity(Long id) {
        activityLogRepository.deleteById(id);
    }

    /**
     * Belirli bir tarihten eski aktiviteleri siler (temizleme)
     */
    public void deleteOldActivities(LocalDateTime before) {
        List<ActivityLog> oldLogs = activityLogRepository.findByCreatedAtBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0), 
            before
        );
        activityLogRepository.deleteAll(oldLogs);
        System.out.println(String.format(
            "🗑️  %d eski aktivite log kaydı silindi (tarih: %s öncesi)", 
            oldLogs.size(), before
        ));
    }

    /**
     * Entity silme aksiyonlarını loglar
     */
    @Async
    public void logDelete(Long userId, String entityType, Long entityId, String entityName) {
        String description = String.format("%s silindi: %s (ID: %d)", entityType, entityName, entityId);
        saveActivityLog(userId, "DELETE", entityType, entityId, description);
    }

    /**
     * Entity oluşturma aksiyonlarını loglar
     */
    @Async
    public void logCreate(Long userId, String entityType, Long entityId, String entityName) {
        String description = String.format("Yeni %s oluşturuldu: %s", entityType, entityName);
        saveActivityLog(userId, "CREATE", entityType, entityId, description);
    }

    /**
     * Entity güncelleme aksiyonlarını loglar
     */
    @Async
    public void logUpdate(Long userId, String entityType, Long entityId, String entityName) {
        String description = String.format("%s güncellendi: %s", entityType, entityName);
        saveActivityLog(userId, "UPDATE", entityType, entityId, description);
    }

    /**
     * Entity görüntüleme aksiyonlarını loglar
     */
    @Async
    public void logView(Long userId, String entityType, Long entityId) {
        String description = String.format("%s görüntülendi", entityType);
        saveActivityLog(userId, "VIEW", entityType, entityId, description);
    }

    /**
     * Export aksiyonlarını loglar
     */
    @Async
    public void logExport(Long userId, String entityType, String format) {
        String description = String.format("%s listesi export edildi (%s formatında)", entityType, format);
        saveActivityLog(userId, "EXPORT", entityType, null, description);
    }

    // Helper methods

    /**
     * Filtrelere göre activity loglarını pagination ile getirir
     */
    @Transactional(readOnly = true)
    public Page<ActivityLog> getLogsByFilters(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String entityType,
            String action,
            Long entityId,
            Pageable pageable) {
        
        // Tüm logları al
        List<ActivityLog> allLogs = activityLogRepository.findAll();
        
        // Filtreleme
        List<ActivityLog> filteredLogs = allLogs.stream()
            .filter(log -> userId == null || (log.getUserId() != null && log.getUserId().equals(userId)))
            .filter(log -> startDate == null || log.getCreatedAt().isAfter(startDate) || log.getCreatedAt().isEqual(startDate))
            .filter(log -> endDate == null || log.getCreatedAt().isBefore(endDate) || log.getCreatedAt().isEqual(endDate))
            .filter(log -> entityType == null || (log.getEntityType() != null && log.getEntityType().equalsIgnoreCase(entityType)))
            .filter(log -> action == null || (log.getAction() != null && log.getAction().equalsIgnoreCase(action)))
            .filter(log -> entityId == null || (log.getEntityId() != null && log.getEntityId().equals(entityId)))
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
        
        // Pagination uygula
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredLogs.size());
        
        List<ActivityLog> pageContent = filteredLogs.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredLogs.size());
    }

    /**
     * String'i belirli bir uzunlukta keser
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength);
    }
}

