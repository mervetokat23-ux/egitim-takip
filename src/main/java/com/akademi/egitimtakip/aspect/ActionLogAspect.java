package com.akademi.egitimtakip.aspect;

import com.akademi.egitimtakip.annotation.LogAction;
import com.akademi.egitimtakip.service.ActivityLogService;
import com.akademi.egitimtakip.util.JwtUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * ActionLogAspect
 * 
 * @LogAction annotation'ı bulunan metodları yakalar ve activity_logs tablosuna kaydeder.
 * AOP (Aspect-Oriented Programming) kullanarak otomatik loglama sağlar.
 */
@Aspect
@Component
public class ActionLogAspect {

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private JwtUtil jwtUtil;

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * @LogAction annotation'ı bulunan tüm metodları yakalar
     */
    @Around("@annotation(com.akademi.egitimtakip.annotation.LogAction)")
    public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAction logAction = method.getAnnotation(LogAction.class);

        Object result = null;
        boolean hasError = false;

        try {
            // Metodu çalıştır
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            hasError = true;
            // Hata durumunda loglansın mı?
            if (!logAction.logOnError()) {
                throw ex; // Hatayı yukarı fırlat, loglama
            }
            // Aksi halde hatayı logla ve yine fırlat
        }

        // Loglama (asenkron)
        try {
            // Kullanıcı ID'sini al
            Long userId = getCurrentUserId();

            // Entity ID'yi al
            Long entityId = extractEntityId(logAction, joinPoint.getArgs(), result);

            // Açıklamayı oluştur (SpEL expression varsa değerlendir)
            String description = evaluateDescription(logAction.description(), joinPoint.getArgs(), result);

            // Activity log kaydet
            activityLogService.saveActivityLog(
                userId,
                logAction.action(),
                logAction.entityType(),
                entityId,
                description
            );

        } catch (Exception e) {
            // Loglama hatası uygulamayı etkilememeli
            System.err.println("ActionLogAspect hatası: " + e.getMessage());
        }

        // Hata varsa fırlat
        if (hasError) {
            throw new RuntimeException("Method execution failed"); // veya orijinal exception
        }

        return result;
    }

    /**
     * Mevcut kullanıcının ID'sini alır
     * 1. SecurityContext'ten (Spring Security)
     * 2. JWT token'dan (Authorization header)
     */
    private Long getCurrentUserId() {
        try {
            // Spring Security Authentication'dan al
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                if (email != null && !email.equals("anonymousUser")) {
                    // Email'i userId'ye çevirebilirsiniz
                    // Şimdilik email'i description'a ekleyebiliriz
                    return null; // veya kullaniciRepository.findByEmail(email).getId()
                }
            }

            // JWT token'dan al
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    String email = jwtUtil.getEmailFromToken(token);
                    // Email'den userId'yi bul
                    // return kullaniciRepository.findByEmail(email).getId();
                    return null; // Şimdilik null
                }
            }
        } catch (Exception e) {
            System.err.println("getCurrentUserId hatası: " + e.getMessage());
        }
        return null;
    }

    /**
     * Entity ID'yi annotation ayarlarına göre çıkarır
     */
    private Long extractEntityId(LogAction logAction, Object[] args, Object result) {
        try {
            int paramIndex = logAction.entityIdParam();

            if (paramIndex == -1) {
                // Return value'dan ID al
                return extractIdFromObject(result);
            } else if (paramIndex >= 0 && paramIndex < args.length) {
                // Belirtilen parametreden ID al
                Object param = args[paramIndex];
                return extractIdFromObject(param);
            }
        } catch (Exception e) {
            System.err.println("extractEntityId hatası: " + e.getMessage());
        }
        return null;
    }

    /**
     * Object'ten ID field'ını reflection ile çıkarır
     */
    private Long extractIdFromObject(Object obj) {
        if (obj == null) return null;

        try {
            // Long tipinde ise direkt dön
            if (obj instanceof Long) {
                return (Long) obj;
            }

            // Integer tipinde ise Long'a çevir
            if (obj instanceof Integer) {
                return ((Integer) obj).longValue();
            }

            // Object'in getId() metodunu çağır
            Method getIdMethod = obj.getClass().getMethod("getId");
            Object idObj = getIdMethod.invoke(obj);
            
            if (idObj instanceof Long) {
                return (Long) idObj;
            } else if (idObj instanceof Integer) {
                return ((Integer) idObj).longValue();
            }
        } catch (Exception e) {
            // getId() metodu yok veya çağrılamadı
        }
        return null;
    }

    /**
     * Description'ı SpEL expression ile değerlendirir
     * Örnek: "Yeni eğitim: #{result.ad}" → "Yeni eğitim: Java Eğitimi"
     * Örnek: "ID: #{args[0]}" → "ID: 123"
     */
    private String evaluateDescription(String template, Object[] args, Object result) {
        if (template == null || template.isEmpty()) {
            return "";
        }

        // SpEL expression var mı kontrol et
        if (!template.contains("#{")) {
            return template;
        }

        try {
            // Evaluation context oluştur
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("args", args);
            context.setVariable("result", result);

            // Expression'ı parse et ve değerlendir
            Expression expression = parser.parseExpression(template, new org.springframework.expression.common.TemplateParserContext());
            return expression.getValue(context, String.class);
        } catch (Exception e) {
            // SpEL hatası varsa template'i olduğu gibi döndür
            System.err.println("SpEL evaluation hatası: " + e.getMessage());
            return template;
        }
    }
}





