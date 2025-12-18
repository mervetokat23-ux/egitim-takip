package com.akademi.egitimtakip.repository;

import com.akademi.egitimtakip.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Permission Repository
 * 
 * Repository for Permission entity operations.
 * Provides methods to find permissions by module and action.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * Find permission by module and action
     */
    Optional<Permission> findByModuleAndAction(String module, String action);
    
    /**
     * Find all permissions for a specific module
     */
    List<Permission> findByModule(String module);
    
    /**
     * Find all permissions for a specific action across all modules
     */
    List<Permission> findByAction(String action);
    
    /**
     * Check if permission exists by module and action
     */
    boolean existsByModuleAndAction(String module, String action);
    
    /**
     * Find all permissions ordered by module and action
     */
    List<Permission> findAllByOrderByModuleAscActionAsc();
}


