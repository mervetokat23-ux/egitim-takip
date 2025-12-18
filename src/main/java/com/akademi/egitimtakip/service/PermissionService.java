package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.PermissionDTO;
import com.akademi.egitimtakip.entity.Permission;
import com.akademi.egitimtakip.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Permission Service
 * 
 * Service for managing permissions.
 */
@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Get all permissions
     */
    @Transactional(readOnly = true)
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAllByOrderByModuleAscActionAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get permission by ID
     */
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + id));
        return toDTO(permission);
    }

    /**
     * Get permissions by module
     */
    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByModule(String module) {
        return permissionRepository.findByModule(module).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get permissions by action
     */
    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByAction(String action) {
        return permissionRepository.findByAction(action).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new permission
     */
    public PermissionDTO createPermission(String module, String action, String description) {
        // Check if permission already exists
        if (permissionRepository.existsByModuleAndAction(module, action)) {
            throw new RuntimeException("Permission already exists: " + module + ":" + action);
        }

        Permission permission = new Permission();
        permission.setModule(module);
        permission.setAction(action);
        permission.setDescription(description);

        permission = permissionRepository.save(permission);
        return toDTO(permission);
    }

    /**
     * Update permission
     */
    public PermissionDTO updatePermission(Long id, String description) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + id));

        permission.setDescription(description);
        permission = permissionRepository.save(permission);
        return toDTO(permission);
    }

    /**
     * Delete permission
     */
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission not found: " + id);
        }
        permissionRepository.deleteById(id);
    }

    /**
     * Convert Permission entity to DTO
     */
    private PermissionDTO toDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setModule(permission.getModule());
        dto.setAction(permission.getAction());
        dto.setDescription(permission.getDescription());
        dto.setCreatedAt(permission.getCreatedAt());
        return dto;
    }
}


