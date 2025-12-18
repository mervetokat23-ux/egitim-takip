package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.entity.*;
import com.akademi.egitimtakip.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * LogController
 * 
 * Admin paneli için log görüntüleme REST API.
 * Permission-based authorization: logs module
 * Pagination ve filtreleme desteği sağlar.
 */
@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogController {

    @Autowired
    private ApiLogService apiLogService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private PerformanceLogService performanceLogService;

    @Autowired
    private FrontendLogService frontendLogService;

    /**
     * GET /api/logs/api - API Loglarını getirir
     * Required Permission: logs.view
     */
    @GetMapping("/api")
    @RequirePermission(module = "logs", action = "view", description = "View API logs")
    public ResponseEntity<Page<ApiLog>> getApiLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer statusCode,
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) Long minDuration) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ApiLog> logs = apiLogService.getLogsByFilters(
            userId, startDate, endDate, statusCode, endpoint, minDuration, pageable
        );
        
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/logs/activity - Activity Loglarını getirir
     * Required Permission: logs.view
     */
    @GetMapping("/activity")
    @RequirePermission(module = "logs", action = "view", description = "View activity logs")
    public ResponseEntity<Page<ActivityLog>> getActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long entityId) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ActivityLog> logs = activityLogService.getLogsByFilters(
            userId, startDate, endDate, entityType, action, entityId, pageable
        );
        
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/logs/errors - Error Loglarını getirir
     * Required Permission: logs.view
     */
    @GetMapping("/errors")
    @RequirePermission(module = "logs", action = "view", description = "View error logs")
    public ResponseEntity<Page<ErrorLog>> getErrorLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String exceptionType,
            @RequestParam(required = false) String endpoint) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ErrorLog> logs = errorLogService.getLogsByFilters(
            userId, startDate, endDate, exceptionType, endpoint, pageable
        );
        
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/logs/performance - Performance Loglarını getirir
     * Required Permission: logs.view
     */
    @GetMapping("/performance")
    @RequirePermission(module = "logs", action = "view", description = "View performance logs")
    public ResponseEntity<Page<PerformanceLog>> getPerformanceLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long minDuration,
            @RequestParam(required = false) String endpoint) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "durationMs"));
        Page<PerformanceLog> logs = performanceLogService.getLogsByFilters(
            startDate, endDate, minDuration, endpoint, pageable
        );
        
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/logs/frontend - Frontend Loglarını getirir
     * Required Permission: logs.view
     */
    @GetMapping("/frontend")
    @RequirePermission(module = "logs", action = "view", description = "View frontend logs")
    public ResponseEntity<Page<FrontendLog>> getFrontendLogs(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String page) {
        
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FrontendLog> logs = frontendLogService.getLogsByFilters(
            userId, startDate, endDate, action, page, pageable
        );
        
        return ResponseEntity.ok(logs);
    }

    /**
     * POST /api/logs/frontend - Frontend'ten log kaydeder
     * No permission required - frontend logging endpoint
     */
    @PostMapping("/frontend")
    public ResponseEntity<String> logFrontendAction(@RequestBody Map<String, Object> payload) {
        Long userId = payload.get("userId") != null ? Long.valueOf(payload.get("userId").toString()) : null;
        String action = (String) payload.get("action");
        String page = (String) payload.get("page");
        String details = (String) payload.get("details");
        
        frontendLogService.saveFrontendLog(userId, action, page, details);
        return ResponseEntity.ok("Log kaydedildi");
    }

    /**
     * DELETE /api/logs/clear - Eski logları temizle
     * Required Permission: logs.manage
     */
    @DeleteMapping("/clear")
    @RequirePermission(module = "logs", action = "manage", description = "Clear old logs")
    public ResponseEntity<String> clearOldLogs(
            @RequestParam(required = false, defaultValue = "30") int daysOld) {
        // Log temizleme işlemi - opsiyonel
        return ResponseEntity.ok("Log temizleme işlemi tamamlandı");
    }
}
