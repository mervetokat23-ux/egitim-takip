package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.entity.ApiLog;
import com.akademi.egitimtakip.repository.ApiLogRepository;
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
 * ApiLogService
 * 
 * API log kayıtlarını yönetir.
 * LogInterceptor tarafından kullanılır.
 */
@Service
@Transactional
public class ApiLogService {

    @Autowired
    private ApiLogRepository apiLogRepository;

    /**
     * API log kaydı oluşturur (asenkron)
     * 
     * @param userId Kullanıcı ID (opsiyonel, null olabilir)
     * @param endpoint Endpoint URL
     * @param httpMethod HTTP metodu (GET, POST, PUT, DELETE, vb.)
     * @param statusCode HTTP durum kodu
     * @param requestBody Request body (JSON, form data, vb.)
     * @param responseBody Response body (JSON, HTML, vb.)
     * @param durationMs İşlem süresi (milisaniye)
     * @param ip Client IP adresi
     */
    @Async
    public void saveApiLog(Long userId, String endpoint, String httpMethod, Integer statusCode,
                          String requestBody, String responseBody, Long durationMs, String ip) {
        try {
            ApiLog apiLog = new ApiLog();
            apiLog.setUserId(userId);
            apiLog.setEndpoint(truncate(endpoint, 500));
            apiLog.setHttpMethod(httpMethod);
            apiLog.setStatusCode(statusCode);
            apiLog.setRequestBody(truncate(requestBody, 10000));
            apiLog.setResponseBody(truncate(responseBody, 10000));
            apiLog.setDurationMs(durationMs);
            apiLog.setIp(ip);
            
            apiLogRepository.save(apiLog);
            
            // Opsiyonel: Yavaş çalışan API'leri konsola logla
            if (durationMs != null && durationMs > 1000) {
                System.out.println(String.format(
                    "⚠️  Yavaş API: %s %s - %dms", 
                    httpMethod, endpoint, durationMs
                ));
            }
        } catch (Exception e) {
            // Loglama hatası uygulamayı etkilememeli
            System.err.println("API log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Belirli bir endpoint'in log kayıtlarını getirir
     */
    @Transactional(readOnly = true)
    public List<ApiLog> getLogsByEndpoint(String endpoint) {
        return apiLogRepository.findByEndpointContainingIgnoreCase(endpoint);
    }

    /**
     * Belirli bir kullanıcının API loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<ApiLog> getLogsByUserId(Long userId) {
        return apiLogRepository.findByUserId(userId);
    }

    /**
     * Yavaş çalışan API'leri getirir
     */
    @Transactional(readOnly = true)
    public List<ApiLog> getSlowApis(Long minDurationMs) {
        return apiLogRepository.findByDurationMsGreaterThan(minDurationMs);
    }

    /**
     * Belirli bir tarih aralığındaki API loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<ApiLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return apiLogRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Belirli bir HTTP metodu için logları getirir
     */
    @Transactional(readOnly = true)
    public List<ApiLog> getLogsByHttpMethod(String httpMethod) {
        return apiLogRepository.findByHttpMethod(httpMethod);
    }

    /**
     * Tüm API loglarını getirir (sayfalama ile kullanılmalı)
     */
    @Transactional(readOnly = true)
    public List<ApiLog> getAllLogs() {
        return apiLogRepository.findAll();
    }

    /**
     * Filtrelere göre API loglarını pagination ile getirir
     */
    @Transactional(readOnly = true)
    public Page<ApiLog> getLogsByFilters(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer statusCode,
            String endpoint,
            Long minDuration,
            Pageable pageable) {
        
        // Tüm logları al
        List<ApiLog> allLogs = apiLogRepository.findAll();
        
        // Filtreleme
        List<ApiLog> filteredLogs = allLogs.stream()
            .filter(log -> userId == null || (log.getUserId() != null && log.getUserId().equals(userId)))
            .filter(log -> startDate == null || log.getCreatedAt().isAfter(startDate) || log.getCreatedAt().isEqual(startDate))
            .filter(log -> endDate == null || log.getCreatedAt().isBefore(endDate) || log.getCreatedAt().isEqual(endDate))
            .filter(log -> statusCode == null || (log.getStatusCode() != null && log.getStatusCode().equals(statusCode)))
            .filter(log -> endpoint == null || (log.getEndpoint() != null && log.getEndpoint().contains(endpoint)))
            .filter(log -> minDuration == null || (log.getDurationMs() != null && log.getDurationMs() >= minDuration))
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // En yeni en üstte
            .collect(Collectors.toList());
        
        // Pagination uygula
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredLogs.size());
        
        List<ApiLog> pageContent = filteredLogs.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredLogs.size());
    }

    /**
     * Belirli bir log kaydını siler
     */
    public void deleteLog(Long id) {
        apiLogRepository.deleteById(id);
    }

    /**
     * Belirli bir tarihten eski logları siler (temizleme)
     */
    public void deleteOldLogs(LocalDateTime before) {
        List<ApiLog> oldLogs = apiLogRepository.findByCreatedAtBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0), 
            before
        );
        apiLogRepository.deleteAll(oldLogs);
        System.out.println(String.format(
            "🗑️  %d eski API log kaydı silindi (tarih: %s öncesi)", 
            oldLogs.size(), before
        ));
    }

    // Helper methods

    /**
     * String'i belirli bir uzunlukta keser
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength) + "... [truncated]";
    }
}

