package com.akademi.egitimtakip.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RequirePermission Annotation
 * 
 * Marks a method as requiring a specific permission to access.
 * Used with PermissionCheckAspect for authorization.
 * 
 * Usage:
 * @RequirePermission(module = "education", action = "view")
 * public ResponseEntity<?> getEducations() { ... }
 * 
 * ADMIN role automatically bypasses all permission checks.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * Module name (e.g., "education", "trainer", "payment")
     */
    String module();
    
    /**
     * Action name (e.g., "view", "create", "update", "delete")
     */
    String action();
    
    /**
     * Optional description for documentation
     */
    String description() default "";
}

