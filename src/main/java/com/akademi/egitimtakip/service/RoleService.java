package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.PermissionDTO;
import com.akademi.egitimtakip.dto.RoleDTO;
import com.akademi.egitimtakip.dto.RoleRequestDTO;
import com.akademi.egitimtakip.entity.Permission;
import com.akademi.egitimtakip.entity.Role;
import com.akademi.egitimtakip.repository.PermissionRepository;
import com.akademi.egitimtakip.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Role Service
 * 
 * Service for managing roles and their permissions.
 */
@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Get all roles
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get role by ID
     */
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));
        return toDTO(role);
    }

    /**
     * Get role by name
     */
    @Transactional(readOnly = true)
    public RoleDTO getRoleByName(String name) {
        Role role = roleRepository.findByNameWithPermissions(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
        return toDTO(role);
    }

    /**
     * Create new role
     */
    public RoleDTO createRole(RoleRequestDTO requestDTO) {
        // Check if role already exists
        if (roleRepository.existsByName(requestDTO.getName())) {
            throw new RuntimeException("Role already exists: " + requestDTO.getName());
        }

        Role role = new Role();
        role.setName(requestDTO.getName());
        role.setDescription(requestDTO.getDescription());

        // Add permissions
        if (requestDTO.getPermissionIds() != null && !requestDTO.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllById(requestDTO.getPermissionIds())
            );
            role.setPermissions(permissions);
        }

        role = roleRepository.save(role);
        return toDTO(role);
    }

    /**
     * Update role
     */
    public RoleDTO updateRole(Long id, RoleRequestDTO requestDTO) {
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));

        // Check if name is being changed and if new name already exists
        if (!role.getName().equals(requestDTO.getName()) 
            && roleRepository.existsByName(requestDTO.getName())) {
            throw new RuntimeException("Role name already exists: " + requestDTO.getName());
        }

        role.setName(requestDTO.getName());
        role.setDescription(requestDTO.getDescription());

        // Update permissions
        if (requestDTO.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllById(requestDTO.getPermissionIds())
            );
            role.setPermissions(permissions);
        }

        role = roleRepository.save(role);
        return toDTO(role);
    }

    /**
     * Delete role
     */
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found: " + id);
        }
        roleRepository.deleteById(id);
    }

    /**
     * Add permission to role
     */
    public RoleDTO addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));

        role.addPermission(permission);
        role = roleRepository.save(role);
        return toDTO(role);
    }

    /**
     * Remove permission from role
     */
    public RoleDTO removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));

        role.removePermission(permission);
        role = roleRepository.save(role);
        return toDTO(role);
    }

    /**
     * Check if role has permission
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(Long roleId, String module, String action) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
        return role.hasPermission(module, action);
    }

    /**
     * Convert Role entity to DTO
     */
    private RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        
        if (role.getPermissions() != null) {
            Set<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                    .map(this::toPermissionDTO)
                    .collect(Collectors.toSet());
            dto.setPermissions(permissionDTOs);
        }
        
        return dto;
    }

    /**
     * Convert Permission entity to DTO
     */
    private PermissionDTO toPermissionDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setModule(permission.getModule());
        dto.setAction(permission.getAction());
        dto.setDescription(permission.getDescription());
        dto.setCreatedAt(permission.getCreatedAt());
        return dto;
    }
}


