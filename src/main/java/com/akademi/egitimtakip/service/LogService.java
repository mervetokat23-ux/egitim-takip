package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.entity.*;
import com.akademi.egitimtakip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * LogService
 * 
 * Tüm loglama işlemlerini merkezi olarak yönetir.
 * @Async ile asenkron çalışarak performansı etkilemez.
 */
@Service
@Transactional
public class LogService {

    @Autowired
    private ApiLogRepository apiLogRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Autowired
    private PerformanceLogRepository performanceLogRepository;

    @Autowired
    private FrontendLogRepository frontendLogRepository;

    /**
     * API log kaydı oluşturur
     */
    @Async
    public void logApiCall(Long userId, String endpoint, String httpMethod, Integer statusCode,
                          String requestBody, String responseBody, Long durationMs, String ip) {
        try {
            ApiLog log = new ApiLog();
            log.setUserId(userId);
            log.setEndpoint(endpoint);
            log.setHttpMethod(httpMethod);
            log.setStatusCode(statusCode);
            log.setRequestBody(truncate(requestBody, 5000));
            log.setResponseBody(truncate(responseBody, 5000));
            log.setDurationMs(durationMs);
            log.setIp(ip);
            apiLogRepository.save(log);
        } catch (Exception e) {
            // Loglama hatası uygulamayı etkilememeli
            System.err.println("API log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Kullanıcı aktivitesi kaydeder
     */
    @Async
    public void logActivity(Long userId, String action, String entityType, Long entityId, String description) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setDescription(truncate(description, 1000));
            activityLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Activity log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Hata kaydı oluşturur
     */
    @Async
    public void logError(Long userId, String endpoint, Exception exception) {
        try {
            ErrorLog log = new ErrorLog();
            log.setUserId(userId);
            log.setEndpoint(endpoint);
            log.setExceptionType(exception.getClass().getName());
            log.setMessage(truncate(exception.getMessage(), 2000));
            log.setStacktrace(truncate(getStackTrace(exception), 10000));
            errorLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Error log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Performans logu kaydeder
     */
    @Async
    public void logPerformance(String endpoint, Long durationMs, String methodName) {
        try {
            // Sadece yavaş çalışan işlemleri logla (örn: 1000ms üzeri)
            if (durationMs > 1000) {
                PerformanceLog log = new PerformanceLog();
                log.setEndpoint(endpoint);
                log.setDurationMs(durationMs);
                log.setMethodName(methodName);
                performanceLogRepository.save(log);
            }
        } catch (Exception e) {
            System.err.println("Performance log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Frontend logu kaydeder
     */
    @Async
    public void logFrontend(Long userId, String action, String page, String details) {
        try {
            FrontendLog log = new FrontendLog();
            log.setUserId(userId);
            log.setAction(action);
            log.setPage(page);
            log.setDetails(truncate(details, 2000));
            frontendLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Frontend log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Belirli tarih aralığındaki API loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<ApiLog> getApiLogs(LocalDateTime start, LocalDateTime end) {
        return apiLogRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Belirli bir kullanıcının aktivitelerini getirir
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getUserActivities(Long userId) {
        return activityLogRepository.findByUserId(userId);
    }

    /**
     * Son hataları getirir
     */
    @Transactional(readOnly = true)
    public List<ErrorLog> getRecentErrors() {
        return errorLogRepository.findTop100ByOrderByCreatedAtDesc();
    }

    /**
     * Yavaş çalışan endpoint'leri getirir
     */
    @Transactional(readOnly = true)
    public List<PerformanceLog> getSlowEndpoints() {
        return performanceLogRepository.findTop50ByOrderByDurationMsDesc();
    }

    // Helper methods

    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }

    private String getStackTrace(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }
}





