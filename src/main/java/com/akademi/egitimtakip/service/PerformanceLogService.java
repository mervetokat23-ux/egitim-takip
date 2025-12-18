package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.entity.PerformanceLog;
import com.akademi.egitimtakip.repository.PerformanceLogRepository;
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
 * PerformanceLogService
 * 
 * Performans loglarını yönetir.
 * PerformanceAspect tarafından kullanılır.
 */
@Service
@Transactional
public class PerformanceLogService {

    @Autowired
    private PerformanceLogRepository performanceLogRepository;

    /**
     * Performans log kaydı oluşturur (asenkron)
     * Sadece 1 saniyeden uzun süren işlemler kaydedilir
     * 
     * @param endpoint Endpoint veya method adı
     * @param durationMs İşlem süresi (milisaniye)
     * @param methodName Metod adı (ClassName.methodName formatında)
     */
    @Async
    public void savePerformanceLog(String endpoint, Long durationMs, String methodName) {
        try {
            // Sadece yavaş işlemleri logla (1000ms üzeri)
            if (durationMs == null || durationMs <= 1000) {
                return;
            }

            PerformanceLog log = new PerformanceLog();
            log.setEndpoint(truncate(endpoint, 500));
            log.setDurationMs(durationMs);
            log.setMethodName(truncate(methodName, 255));
            
            performanceLogRepository.save(log);
            
            // Konsola da yazdır (kritik yavaş işlemleri görmek için)
            System.out.println(String.format(
                "⚠️  Yavaş İşlem: %s - %dms (%s)",
                methodName != null ? methodName : endpoint,
                durationMs,
                formatDuration(durationMs)
            ));
        } catch (Exception e) {
            // Loglama hatası uygulamayı etkilememeli
            System.err.println("Performance log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Belirli bir endpoint'in performans loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<PerformanceLog> getLogsByEndpoint(String endpoint) {
        return performanceLogRepository.findByEndpointContainingIgnoreCase(endpoint);
    }

    /**
     * Yavaş çalışan işlemleri getirir (belirli bir sürenin üzeri)
     */
    @Transactional(readOnly = true)
    public List<PerformanceLog> getSlowOperations(Long minDurationMs) {
        return performanceLogRepository.findByDurationMsGreaterThan(minDurationMs);
    }

    /**
     * En yavaş çalışan 50 işlemi getirir
     */
    @Transactional(readOnly = true)
    public List<PerformanceLog> getTop50SlowestOperations() {
        return performanceLogRepository.findTop50ByOrderByDurationMsDesc();
    }

    /**
     * Belirli bir tarih aralığındaki performans loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<PerformanceLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return performanceLogRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Endpoint'lere göre ortalama süreleri getirir
     */
    @Transactional(readOnly = true)
    public List<Object[]> getAverageDurationByEndpoint() {
        return performanceLogRepository.findAverageDurationByEndpoint();
    }

    /**
     * Tüm performans loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<PerformanceLog> getAllLogs() {
        return performanceLogRepository.findAll();
    }

    /**
     * Belirli bir performans kaydını siler
     */
    public void deleteLog(Long id) {
        performanceLogRepository.deleteById(id);
    }

    /**
     * Belirli bir tarihten eski performans loglarını siler (temizleme)
     */
    public void deleteOldLogs(LocalDateTime before) {
        List<PerformanceLog> oldLogs = performanceLogRepository.findByCreatedAtBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0), 
            before
        );
        performanceLogRepository.deleteAll(oldLogs);
        System.out.println(String.format(
            "🗑️  %d eski performans log kaydı silindi (tarih: %s öncesi)", 
            oldLogs.size(), before
        ));
    }

    /**
     * Performans istatistiklerini konsola yazdırır
     */
    public void printPerformanceStatistics() {
        List<Object[]> avgDurations = getAverageDurationByEndpoint();
        
        System.out.println("\n📊 Performans İstatistikleri:");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.printf("%-50s %-15s%n", "Endpoint/Method", "Ortalama Süre");
        System.out.println("───────────────────────────────────────────────────────");
        
        for (Object[] row : avgDurations) {
            String endpoint = (String) row[0];
            Double avgDuration = (Double) row[1];
            System.out.printf("%-50s %-15s%n", 
                truncate(endpoint, 50), 
                formatDuration(avgDuration.longValue())
            );
        }
        
        System.out.println("═══════════════════════════════════════════════════════\n");
    }

    // Helper methods

    /**
     * String'i belirli bir uzunlukta keser
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength);
    }

    /**
     * Filtrelere göre performance loglarını pagination ile getirir
     */
    @Transactional(readOnly = true)
    public Page<PerformanceLog> getLogsByFilters(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long minDuration,
            String endpoint,
            Pageable pageable) {
        
        // Tüm logları al
        List<PerformanceLog> allLogs = performanceLogRepository.findAll();
        
        // Filtreleme
        List<PerformanceLog> filteredLogs = allLogs.stream()
            .filter(log -> startDate == null || log.getCreatedAt().isAfter(startDate) || log.getCreatedAt().isEqual(startDate))
            .filter(log -> endDate == null || log.getCreatedAt().isBefore(endDate) || log.getCreatedAt().isEqual(endDate))
            .filter(log -> minDuration == null || (log.getDurationMs() != null && log.getDurationMs() >= minDuration))
            .filter(log -> endpoint == null || (log.getEndpoint() != null && log.getEndpoint().contains(endpoint)))
            .sorted((a, b) -> b.getDurationMs().compareTo(a.getDurationMs())) // En yavaş en üstte
            .collect(Collectors.toList());
        
        // Pagination uygula
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredLogs.size());
        
        List<PerformanceLog> pageContent = filteredLogs.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredLogs.size());
    }

    /**
     * Süreyi okunabilir formata çevirir
     */
    private String formatDuration(Long durationMs) {
        if (durationMs == null) return "0ms";
        
        if (durationMs < 1000) {
            return durationMs + "ms";
        } else if (durationMs < 60000) {
            return String.format("%.2fs", durationMs / 1000.0);
        } else {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
}

