package com.akademi.egitimtakip.util;

import com.akademi.egitimtakip.entity.Kullanici;
import com.akademi.egitimtakip.entity.Role;
import com.akademi.egitimtakip.entity.Sorumlu;
import com.akademi.egitimtakip.repository.KullaniciRepository;
import com.akademi.egitimtakip.repository.SorumluRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Security Utilities
 * 
 * Provides utility methods for accessing current user information
 * from the Spring Security context.
 */
@Component
public class SecurityUtils {

    private static KullaniciRepository kullaniciRepository;
    private static SorumluRepository sorumluRepository;

    @Autowired
    public void setKullaniciRepository(KullaniciRepository repo) {
        SecurityUtils.kullaniciRepository = repo;
    }

    @Autowired
    public void setSorumluRepository(SorumluRepository repo) {
        SecurityUtils.sorumluRepository = repo;
    }

    /**
     * Get current authenticated user's ID
     * @return User ID or null if not authenticated
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        Object principal = auth.getPrincipal();
        String username = null;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        if (username != null && kullaniciRepository != null) {
            Optional<Kullanici> kullanici = kullaniciRepository.findByEmail(username);
            if (kullanici.isEmpty()) {
                kullanici = kullaniciRepository.findByKullaniciAdi(username);
            }
            return kullanici.map(Kullanici::getId).orElse(null);
        }
        return null;
    }

    /**
     * Get current authenticated user's username
     * @return Username or null if not authenticated
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }

    /**
     * Get current user's Kullanici entity
     * @return Kullanici or null
     */
    public static Kullanici getCurrentKullanici() {
        String username = getCurrentUsername();
        if (username != null && kullaniciRepository != null) {
            // Try to find by email first, then by username
            Optional<Kullanici> kullanici = kullaniciRepository.findByEmail(username);
            if (kullanici.isEmpty()) {
                kullanici = kullaniciRepository.findByKullaniciAdi(username);
            }
            return kullanici.orElse(null);
        }
        return null;
    }

    /**
     * Get current user's Sorumlu entity (if linked)
     * @return Sorumlu or null
     */
    public static Sorumlu getCurrentSorumlu() {
        Kullanici kullanici = getCurrentKullanici();
        if (kullanici != null && kullanici.getSorumluId() != null && sorumluRepository != null) {
            return sorumluRepository.findById(kullanici.getSorumluId()).orElse(null);
        }
        return null;
    }

    /**
     * Get current user's Role
     * @return Role or null
     */
    public static Role getCurrentUserRole() {
        Sorumlu sorumlu = getCurrentSorumlu();
        if (sorumlu != null) {
            return sorumlu.getRole();
        }
        return null;
    }

    /**
     * Get current user's Role ID
     * @return Role ID or null
     */
    public static Long getCurrentUserRoleId() {
        Role role = getCurrentUserRole();
        return role != null ? role.getId() : null;
    }

    /**
     * Get current user's Role name
     * @return Role name or null
     */
    public static String getCurrentUserRoleName() {
        Role role = getCurrentUserRole();
        return role != null ? role.getName() : null;
    }

    /**
     * Check if current user is Admin
     * @return true if ADMIN role
     */
    public static boolean isAdmin() {
        String roleName = getCurrentUserRoleName();
        return "ADMIN".equals(roleName);
    }

    /**
     * Check if current user has specific role
     * @param roleName Role name to check
     * @return true if user has the role
     */
    public static boolean hasRole(String roleName) {
        String currentRole = getCurrentUserRoleName();
        return roleName != null && roleName.equals(currentRole);
    }

    /**
     * Check if user is authenticated
     * @return true if authenticated
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser");
    }
}

