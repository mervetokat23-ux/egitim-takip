package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.entity.Kullanici;
import com.akademi.egitimtakip.entity.Permission;
import com.akademi.egitimtakip.entity.Rol;
import com.akademi.egitimtakip.entity.Role;
import com.akademi.egitimtakip.exception.PermissionDeniedException;
import com.akademi.egitimtakip.repository.KullaniciRepository;
import com.akademi.egitimtakip.repository.RoleRepository;
import com.akademi.egitimtakip.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Permission Check Service
 * 
 * Provides methods to check if the current user has specific permissions.
 * ADMIN role automatically bypasses all permission checks.
 * Also supports legacy Kullanici.rol enum for backward compatibility.
 */
@Service
@Transactional(readOnly = true)
public class PermissionCheckService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionCheckService.class);
    private static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * Check if current user has the specified permission.
     * First checks legacy Kullanici.rol enum (backward compatibility),
     * then checks new Role-Permission system.
     * ADMIN role bypasses all permission checks.
     * 
     * @param module Module name (e.g., "education", "trainer")
     * @param action Action name (e.g., "view", "create", "update", "delete")
     * @return true if user has permission, false otherwise
     */
    public boolean hasPermission(String module, String action) {
        String username = SecurityUtils.getCurrentUsername();
        
        // If not authenticated, no permission
        if (username == null) {
            logger.debug("Permission check failed: User not authenticated for {}.{}", module, action);
            return false;
        }

        // STEP 1: Check legacy Kullanici.rol enum (backward compatibility)
        // This is critical for existing users who don't have the new Role system yet
        if (checkLegacyKullaniciRol(username, module, action)) {
            return true;
        }

        // STEP 2: Get current user's role from new Role system
        Role role = SecurityUtils.getCurrentUserRole();
        
        // If no role assigned in new system, deny access
        if (role == null) {
            logger.debug("Permission check failed: No role assigned to user '{}' for {}.{}", 
                    username, module, action);
            return false;
        }

        // ADMIN bypass: Allow all permissions
        if (ADMIN_ROLE.equals(role.getName())) {
            logger.debug("Permission granted: ADMIN bypass for user '{}' - {}.{}", 
                    username, module, action);
            return true;
        }

        // Check if role has the specific permission
        Role roleWithPermissions = roleRepository.findByIdWithPermissions(role.getId())
                .orElse(null);
        
        if (roleWithPermissions == null) {
            logger.debug("Permission check failed: Role not found for user '{}'", username);
            return false;
        }

        boolean hasPermission = roleWithPermissions.getPermissions().stream()
                .anyMatch(p -> p.getModule().equals(module) && p.getAction().equals(action));

        if (hasPermission) {
            logger.debug("Permission granted: User '{}' has {}.{}", username, module, action);
        } else {
            logger.debug("Permission denied: User '{}' lacks {}.{}", username, module, action);
        }

        return hasPermission;
    }

    /**
     * Check legacy Kullanici.rol enum for backward compatibility.
     * If user has ADMIN enum role, grant all permissions.
     * If user has SORUMLU enum role, grant most permissions.
     * 
     * @param username User's email/username
     * @param module Module name
     * @param action Action name
     * @return true if legacy role grants permission
     */
    private boolean checkLegacyKullaniciRol(String username, String module, String action) {
        try {
            Optional<Kullanici> kullaniciOpt = kullaniciRepository.findByEmail(username);
            
            if (kullaniciOpt.isEmpty()) {
                return false;
            }

            Kullanici kullanici = kullaniciOpt.get();
            Rol legacyRol = kullanici.getRol();

            if (legacyRol == null) {
                return false;
            }

            // ADMIN enum role: full access to everything
            if (legacyRol == Rol.ADMIN) {
                logger.debug("Permission granted: Legacy ADMIN enum for user '{}' - {}.{}", 
                        username, module, action);
                return true;
            }

            // SORUMLU enum role: can view, create, update, delete most modules
            if (legacyRol == Rol.SORUMLU) {
                // Admin-only modules - only view allowed for non-admins
                if ("roles".equals(module) || "permissions".equals(module)) {
                    if ("view".equals(action)) {
                        logger.debug("Permission granted: Legacy SORUMLU view for user '{}' - {}.{}", 
                                username, module, action);
                        return true;
                    }
                    logger.debug("Permission denied: Legacy SORUMLU cannot {} on {}", action, module);
                    return false;
                }
                
                // SORUMLU can do everything on other modules
                logger.debug("Permission granted: Legacy SORUMLU enum for user '{}' - {}.{}", 
                        username, module, action);
                return true;
            }

            // EGITMEN enum role: limited access (view only)
            if (legacyRol == Rol.EGITMEN) {
                // EGITMEN can only view
                if ("view".equals(action)) {
                    logger.debug("Permission granted: Legacy EGITMEN view for user '{}' - {}.{}", 
                            username, module, action);
                    return true;
                }
                logger.debug("Permission denied: Legacy EGITMEN cannot {} - {}.{}", 
                        action, module, action);
                return false;
            }

            return false;
        } catch (Exception e) {
            logger.error("Error checking legacy Kullanici.rol for user '{}': {}", username, e.getMessage());
            return false;
        }
    }

    /**
     * Check permission and throw exception if denied.
     * 
     * @param module Module name
     * @param action Action name
     * @throws PermissionDeniedException if user lacks permission
     */
    public void checkPermission(String module, String action) throws PermissionDeniedException {
        if (!hasPermission(module, action)) {
            String username = SecurityUtils.getCurrentUsername();
            
            // Log the permission denial
            logPermissionDenial(module, action, username);
            
            throw new PermissionDeniedException(module, action, username);
        }
    }

    /**
     * Check if current user is admin (either legacy or new role system)
     * @return true if ADMIN role
     */
    public boolean isAdmin() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return false;
        }

        // Check legacy Kullanici.rol
        try {
            Optional<Kullanici> kullaniciOpt = kullaniciRepository.findByEmail(username);
            if (kullaniciOpt.isPresent() && kullaniciOpt.get().getRol() == Rol.ADMIN) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Error checking legacy admin status: {}", e.getMessage());
        }

        // Check new Role system
        return SecurityUtils.isAdmin();
    }

    /**
     * Check if user has any of the specified permissions
     * 
     * @param permissions Array of "module.action" strings
     * @return true if user has any of the permissions
     */
    public boolean hasAnyPermission(String... permissions) {
        for (String permission : permissions) {
            String[] parts = permission.split("\\.");
            if (parts.length == 2 && hasPermission(parts[0], parts[1])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user has all of the specified permissions
     * 
     * @param permissions Array of "module.action" strings
     * @return true if user has all of the permissions
     */
    public boolean hasAllPermissions(String... permissions) {
        for (String permission : permissions) {
            String[] parts = permission.split("\\.");
            if (parts.length != 2 || !hasPermission(parts[0], parts[1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Log permission denial for audit
     */
    private void logPermissionDenial(String module, String action, String username) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String description = String.format(
                "Permission denied: User '%s' attempted to access %s.%s", 
                username, module, action
            );
            
            activityLogService.saveActivityLog(
                userId,
                "PERMISSION_DENIED",
                "Authorization",
                null,
                description
            );
        } catch (Exception e) {
            logger.error("Failed to log permission denial", e);
        }
    }

    /**
     * Get all permissions for current user's role
     * @return Set of Permission objects or empty set
     */
    public java.util.Set<Permission> getCurrentUserPermissions() {
        Role role = SecurityUtils.getCurrentUserRole();
        if (role == null) {
            return java.util.Collections.emptySet();
        }

        Role roleWithPermissions = roleRepository.findByIdWithPermissions(role.getId())
                .orElse(null);
        
        if (roleWithPermissions == null) {
            return java.util.Collections.emptySet();
        }

        return roleWithPermissions.getPermissions();
    }
}
