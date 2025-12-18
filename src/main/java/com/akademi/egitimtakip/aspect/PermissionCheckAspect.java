package com.akademi.egitimtakip.aspect;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.service.PermissionCheckService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Permission Check Aspect
 * 
 * AOP aspect that intercepts methods annotated with @RequirePermission
 * and checks if the current user has the required permission.
 * 
 * ADMIN role automatically bypasses all permission checks.
 */
@Aspect
@Component
@Order(1) // Execute before other aspects
public class PermissionCheckAspect {

    private static final Logger logger = LoggerFactory.getLogger(PermissionCheckAspect.class);

    @Autowired
    private PermissionCheckService permissionCheckService;

    /**
     * Before advice for methods annotated with @RequirePermission
     * Checks permission before method execution
     */
    @Before("@annotation(com.akademi.egitimtakip.annotation.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        
        if (annotation != null) {
            String module = annotation.module();
            String action = annotation.action();
            
            logger.debug("Checking permission {}.{} for method {}.{}", 
                    module, action, 
                    joinPoint.getTarget().getClass().getSimpleName(),
                    method.getName());
            
            // This will throw PermissionDeniedException if check fails
            permissionCheckService.checkPermission(module, action);
            
            logger.debug("Permission {}.{} granted for method {}.{}", 
                    module, action,
                    joinPoint.getTarget().getClass().getSimpleName(),
                    method.getName());
        }
    }

    /**
     * Before advice for classes annotated with @RequirePermission
     * Applies permission check to all methods in the class
     */
    @Before("@within(com.akademi.egitimtakip.annotation.RequirePermission)")
    public void checkClassPermission(JoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        RequirePermission annotation = targetClass.getAnnotation(RequirePermission.class);
        
        // Skip if method has its own @RequirePermission (it will be handled separately)
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(RequirePermission.class)) {
            return;
        }
        
        if (annotation != null) {
            String module = annotation.module();
            String action = annotation.action();
            
            logger.debug("Checking class-level permission {}.{} for {}.{}", 
                    module, action, 
                    targetClass.getSimpleName(),
                    method.getName());
            
            permissionCheckService.checkPermission(module, action);
        }
    }
}

