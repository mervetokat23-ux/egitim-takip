package com.akademi.egitimtakip.interceptor;

import com.akademi.egitimtakip.service.ApiLogService;
import com.akademi.egitimtakip.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;

/**
 * LogInterceptor
 * 
 * Tüm HTTP isteklerini ve yanıtlarını yakalar, api_logs tablosuna kaydeder.
 * Request/Response body'leri ContentCachingWrapper ile yakalanır.
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String USER_ID_ATTRIBUTE = "userId";

    @Autowired
    private ApiLogService apiLogService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Başlangıç zamanını kaydet
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        // JWT token'dan kullanıcı ID'sini al (opsiyonel)
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.getEmailFromToken(token);
                // Email'den userId bulmak için gerekirse UserRepository kullanılabilir
                // Şimdilik sadece token'ın varlığını kontrol ediyoruz
                request.setAttribute(USER_ID_ATTRIBUTE, email); // veya userId
            }
        } catch (Exception e) {
            // Token yoksa veya geçersizse, userId null olacak
        }

        return true; // İsteğin devam etmesine izin ver
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        try {
            // Süre hesaplama
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            long duration = startTime != null ? System.currentTimeMillis() - startTime : 0L;

            // Kullanıcı ID (varsa)
            String userEmail = (String) request.getAttribute(USER_ID_ATTRIBUTE);
            Long userId = null; // Email'den userId'ye çevirebilirsiniz

            // Request bilgileri
            String endpoint = request.getRequestURI();
            String httpMethod = request.getMethod();
            Integer statusCode = response.getStatus();
            String ip = getClientIp(request);

            // Request body (ContentCachingRequestWrapper kullanılmışsa)
            String requestBody = null;
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    requestBody = new String(buf, 0, Math.min(buf.length, 5000), StandardCharsets.UTF_8);
                }
            }

            // Response body (ContentCachingResponseWrapper kullanılmışsa)
            String responseBody = null;
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    responseBody = new String(buf, 0, Math.min(buf.length, 5000), StandardCharsets.UTF_8);
                }
            }

            // Loglama işleminden hariç tutulacak endpoint'ler
            if (shouldLog(endpoint)) {
                // Asenkron olarak log kaydet
                apiLogService.saveApiLog(userId, endpoint, httpMethod, statusCode, 
                    requestBody, responseBody, duration, ip);
            }

        } catch (Exception e) {
            // Loglama hatası uygulamayı etkilememeli
            System.err.println("LogInterceptor hatası: " + e.getMessage());
        }
    }

    /**
     * İsteğin loglanıp loglanmayacağını kontrol eder
     */
    private boolean shouldLog(String endpoint) {
        // H2 console, static resource ve log endpoint'lerini loglamayalım
        return endpoint != null &&
               !endpoint.startsWith("/h2-console") &&
               !endpoint.startsWith("/logs") &&
               !endpoint.contains("/static") &&
               !endpoint.contains("/favicon.ico") &&
               !endpoint.endsWith(".js") &&
               !endpoint.endsWith(".css") &&
               !endpoint.endsWith(".png") &&
               !endpoint.endsWith(".jpg");
    }

    /**
     * Client IP adresini alır (proxy arkasındaysa X-Forwarded-For header'ını kontrol eder)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Birden fazla IP varsa ilkini al
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}





