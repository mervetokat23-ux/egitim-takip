package com.akademi.egitimtakip.aspect;

import com.akademi.egitimtakip.service.PerformanceLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PerformanceAspect
 * 
 * @Service annotasyonlu sınıflardaki tüm public metodları yakalar.
 * 1 saniyeden uzun süren işlemleri performance_logs tablosuna kaydeder.
 */
@Aspect
@Component
public class PerformanceAspect {

    @Autowired
    private PerformanceLogService performanceLogService;

    /**
     * @Service annotasyonlu sınıflardaki tüm public metodları yakalar
     * execution(* com.akademi.egitimtakip.service.*.*(..)) pattern'i:
     * - * : herhangi bir return type
     * - com.akademi.egitimtakip.service.* : service package'ındaki tüm sınıflar
     * - .*(..) : herhangi bir metod, herhangi bir parametre
     */
    @Around("execution(* com.akademi.egitimtakip.service.*.*(..)) && " +
            "!execution(* com.akademi.egitimtakip.service.PerformanceLogService.*(..)) && " +
            "!execution(* com.akademi.egitimtakip.service.LogService.*(..)) && " +
            "!execution(* com.akademi.egitimtakip.service.ApiLogService.*(..)) && " +
            "!execution(* com.akademi.egitimtakip.service.ActivityLogService.*(..)) && " +
            "!execution(* com.akademi.egitimtakip.service.ErrorLogService.*(..))")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // Başlangıç zamanı
        long startTime = System.currentTimeMillis();
        
        Object result = null;
        String methodName = null;
        
        try {
            // Metodu çalıştır
            result = joinPoint.proceed();
        } finally {
            try {
                // Bitiş zamanı
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                // Method bilgilerini al
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                String className = signature.getDeclaringType().getSimpleName();
                String method = signature.getName();
                methodName = className + "." + method;
                
                // Performans logu kaydet (sadece 1000ms üzeri)
                // PerformanceLogService içinde kontrol var ama burada da yapabiliriz
                if (duration > 1000) {
                    performanceLogService.savePerformanceLog(
                        methodName,  // endpoint alanına method adı
                        duration,
                        methodName   // methodName alanı
                    );
                }
                
            } catch (Exception e) {
                // Loglama hatası uygulamayı etkilememeli
                System.err.println("PerformanceAspect hatası: " + e.getMessage());
            }
        }
        
        return result;
    }
}





