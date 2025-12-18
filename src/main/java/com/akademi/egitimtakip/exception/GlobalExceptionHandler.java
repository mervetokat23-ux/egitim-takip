package com.akademi.egitimtakip.exception;

import com.akademi.egitimtakip.dto.ErrorResponse;
import com.akademi.egitimtakip.service.ErrorLogService;
import com.akademi.egitimtakip.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler
 * 
 * Tüm uygulama hatalarını yakalayan merkezi exception handler.
 * Hataları error_logs tablosuna kaydeder ve standart JSON response döner.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Runtime Exception'ları yakalar
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, 
            HttpServletRequest request) {
        
        // Kullanıcı ID'sini al
        Long userId = getCurrentUserId(request);
        
        // Hata logla
        errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
        
        // Response oluştur
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "İşlem sırasında bir hata oluştu: " + ex.getMessage(),
            ex.getClass().getSimpleName(),
            request.getRequestURI(),
            ex.getClass().getName()
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }

    /**
     * Tüm genel Exception'ları yakalar
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, 
            HttpServletRequest request) {
        
        // Kullanıcı ID'sini al
        Long userId = getCurrentUserId(request);
        
        // Hata logla
        errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
        
        // Response oluştur
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Beklenmeyen bir hata oluştu",
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getName()
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }

    /**
     * Validation hatalarını yakalar (@Valid annotation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        // Validation hatalarını topla
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Kullanıcı ID'sini al
        Long userId = getCurrentUserId(request);
        
        // Hata logla (validation hatası olarak)
        errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
        
        // Response oluştur
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Geçersiz veri",
            errors.toString(),
            request.getRequestURI(),
            "ValidationException"
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    /**
     * IllegalArgumentException hatalarını yakalar
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        Long userId = getCurrentUserId(request);
        errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    /**
     * NullPointerException hatalarını yakalar
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(
            NullPointerException ex,
            HttpServletRequest request) {
        
        Long userId = getCurrentUserId(request);
        errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Null değer hatası oluştu",
            "Gerekli bir veri bulunamadı",
            request.getRequestURI(),
            "NullPointerException"
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }

    /**
     * Authentication hatalarını yakalar (401 Unauthorized)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        
        // Authentication hatası, userId null olabilir
        errorLogService.saveErrorLog(null, request.getRequestURI(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            "Kimlik doğrulama başarısız",
            ex.getMessage(),
            request.getRequestURI(),
            "AuthenticationException"
        );
        
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse);
    }

    /**
     * Bad Credentials hatalarını yakalar (401 Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {
        
        errorLogService.saveErrorLog(null, request.getRequestURI(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            "Kullanıcı adı veya şifre hatalı",
            request.getRequestURI()
        );
        
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse);
    }

    /**
     * Access Denied hatalarını yakalar (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        Long userId = getCurrentUserId(request);
        errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            "Bu işlem için yetkiniz yok",
            ex.getMessage(),
            request.getRequestURI(),
            "AccessDeniedException"
        );
        
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorResponse);
    }

    /**
     * Permission Denied hatalarını yakalar (403 Forbidden)
     * Custom permission check sonucu oluşan hatalar için
     */
    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePermissionDeniedException(
            PermissionDeniedException ex,
            HttpServletRequest request) {
        
        Long userId = getCurrentUserId(request);
        errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
        
        String message = "Bu işlem için yetkiniz yok";
        if (ex.getPermissionKey() != null) {
            message = String.format("Bu işlem için '%s' yetkisine sahip değilsiniz", ex.getPermissionKey());
        }
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            message,
            ex.getMessage(),
            request.getRequestURI(),
            "PermissionDeniedException"
        );
        
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorResponse);
    }

    /**
     * Resource Not Found hatalarını yakalar (Custom exception için hazır)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        Long userId = getCurrentUserId(request);
        errorLogService.saveErrorLog(userId, request.getRequestURI(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse);
    }

    // Helper methods

    /**
     * Mevcut kullanıcının ID'sini alır (JWT token'dan)
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.getEmailFromToken(token);
                // Email'den userId'yi bulmak için KullaniciRepository kullanılabilir
                // Şimdilik null dönüyoruz
                return null;
            }
        } catch (Exception e) {
            // Token yoksa veya geçersizse
        }
        return null;
    }

    /**
     * Custom Resource Not Found Exception
     * (Gerekirse bu exception'ı projeye ekleyebilirsiniz)
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
        
        public ResourceNotFoundException(String resourceName, Long id) {
            super(resourceName + " bulunamadı: " + id);
        }
    }
}



