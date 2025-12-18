package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.entity.ErrorLog;
import com.akademi.egitimtakip.repository.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ErrorLogService
 * 
 * Uygulama hatalarını error_logs tablosuna kaydeder.
 * GlobalExceptionHandler tarafından kullanılır.
 */
@Service
@Transactional
public class ErrorLogService {

    @Autowired
    private ErrorLogRepository errorLogRepository;

    /**
     * Hata kaydı oluşturur (asenkron)
     * 
     * @param userId Kullanıcı ID (opsiyonel, null olabilir)
     * @param endpoint Hatanın oluştuğu endpoint
     * @param exception Exception nesnesi
     */
    @Async
    public void saveErrorLog(Long userId, String endpoint, Exception exception) {
        try {
            ErrorLog errorLog = new ErrorLog();
            errorLog.setUserId(userId);
            errorLog.setEndpoint(truncate(endpoint, 500));
            errorLog.setExceptionType(exception.getClass().getName());
            errorLog.setMessage(truncate(exception.getMessage(), 2000));
            errorLog.setStacktrace(truncate(getStackTrace(exception), 10000));
            
            errorLogRepository.save(errorLog);
            
            // Konsola da yazdır (development ortamında yararlı)
            System.err.println(String.format(
                "🔴 Error Log: [%s] %s at %s by User: %s - %s",
                exception.getClass().getSimpleName(),
                endpoint != null ? endpoint : "Unknown",
                LocalDateTime.now(),
                userId != null ? userId : "anonymous",
                exception.getMessage()
            ));
        } catch (Exception e) {
            // Loglama hatası uygulamayı etkilememeli
            System.err.println("Error log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Detaylı hata kaydı oluşturur
     */
    @Async
    public void saveErrorLog(Long userId, String endpoint, String exceptionType, 
                            String message, String stacktrace) {
        try {
            ErrorLog errorLog = new ErrorLog();
            errorLog.setUserId(userId);
            errorLog.setEndpoint(truncate(endpoint, 500));
            errorLog.setExceptionType(exceptionType);
            errorLog.setMessage(truncate(message, 2000));
            errorLog.setStacktrace(truncate(stacktrace, 10000));
            
            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            System.err.println("Error log kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Belirli bir kullanıcının hata loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<ErrorLog> getErrorsByUserId(Long userId) {
        return errorLogRepository.findByUserId(userId);
    }

    /**
     * Belirli bir endpoint'teki hataları getirir
     */
    @Transactional(readOnly = true)
    public List<ErrorLog> getErrorsByEndpoint(String endpoint) {
        return errorLogRepository.findByEndpointContainingIgnoreCase(endpoint);
    }

    /**
     * Belirli bir exception türüne göre hataları getirir
     */
    @Transactional(readOnly = true)
    public List<ErrorLog> getErrorsByExceptionType(String exceptionType) {
        return errorLogRepository.findByExceptionType(exceptionType);
    }

    /**
     * En son hataları getirir
     */
    @Transactional(readOnly = true)
    public List<ErrorLog> getRecentErrors() {
        return errorLogRepository.findTop100ByOrderByCreatedAtDesc();
    }

    /**
     * Belirli bir tarih aralığındaki hataları getirir
     */
    @Transactional(readOnly = true)
    public List<ErrorLog> getErrorsByDateRange(LocalDateTime start, LocalDateTime end) {
        return errorLogRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Belirli bir hata kaydını siler
     */
    public void deleteError(Long id) {
        errorLogRepository.deleteById(id);
    }

    /**
     * Belirli bir tarihten eski hataları siler (temizleme)
     */
    public void deleteOldErrors(LocalDateTime before) {
        List<ErrorLog> oldLogs = errorLogRepository.findByCreatedAtBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0), 
            before
        );
        errorLogRepository.deleteAll(oldLogs);
        System.out.println(String.format(
            "🗑️  %d eski hata log kaydı silindi (tarih: %s öncesi)", 
            oldLogs.size(), before
        ));
    }

    // Helper methods

    /**
     * Exception'ın stack trace'ini String'e çevirir
     */
    private String getStackTrace(Exception exception) {
        if (exception == null) return null;
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Filtrelere göre error loglarını pagination ile getirir
     */
    @Transactional(readOnly = true)
    public Page<ErrorLog> getLogsByFilters(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String exceptionType,
            String endpoint,
            Pageable pageable) {
        
        // Tüm logları al
        List<ErrorLog> allLogs = errorLogRepository.findAll();
        
        // Filtreleme
        List<ErrorLog> filteredLogs = allLogs.stream()
            .filter(log -> userId == null || (log.getUserId() != null && log.getUserId().equals(userId)))
            .filter(log -> startDate == null || log.getCreatedAt().isAfter(startDate) || log.getCreatedAt().isEqual(startDate))
            .filter(log -> endDate == null || log.getCreatedAt().isBefore(endDate) || log.getCreatedAt().isEqual(endDate))
            .filter(log -> exceptionType == null || (log.getExceptionType() != null && log.getExceptionType().contains(exceptionType)))
            .filter(log -> endpoint == null || (log.getEndpoint() != null && log.getEndpoint().contains(endpoint)))
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
        
        // Pagination uygula
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredLogs.size());
        
        List<ErrorLog> pageContent = filteredLogs.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filteredLogs.size());
    }

    /**
     * String'i belirli bir uzunlukta keser
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength) + "... [truncated]";
    }
}

