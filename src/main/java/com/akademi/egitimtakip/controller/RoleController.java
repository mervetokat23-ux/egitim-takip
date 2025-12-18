package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.annotation.RequirePermission;
import com.akademi.egitimtakip.dto.RoleDTO;
import com.akademi.egitimtakip.dto.RoleRequestDTO;
import com.akademi.egitimtakip.dto.SorumluDTO;
import com.akademi.egitimtakip.service.ActivityLogService;
import com.akademi.egitimtakip.service.RoleService;
import com.akademi.egitimtakip.service.SorumluService;
import com.akademi.egitimtakip.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Role Controller
 * 
 * REST API for managing roles and their permissions.
 * Only accessible by ADMIN users (super admin bypass enabled).
 */
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
@Tag(name = "Role Management", description = "Manage roles and their permissions")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private SorumluService sorumluService;

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * GET /api/roles - Tüm rolleri listele
     * Only ADMIN can access (using PreAuthorize for super admin bypass)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all roles", description = "Retrieve all roles with their permissions")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * GET /api/roles/{id} - ID ile rol getir
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get role by ID", description = "Retrieve a specific role with its permissions")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        try {
            RoleDTO role = roleService.getRoleById(id);
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/roles/name/{name} - İsim ile rol getir
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get role by name", description = "Retrieve a role by its name")
    public ResponseEntity<?> getRoleByName(@PathVariable String name) {
        try {
            RoleDTO role = roleService.getRoleByName(name);
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/roles - Yeni rol oluştur
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new role", description = "Create a new role with permissions")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequestDTO requestDTO) {
        try {
            RoleDTO role = roleService.createRole(requestDTO);
            
            // Log role creation
            logAuthorizationChange("ROLE_CREATE", null, role.getId(), 
                    "Created role: " + role.getName());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * PUT /api/roles/{id} - Rol güncelle
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update role", description = "Update an existing role")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO requestDTO) {
        try {
            RoleDTO role = roleService.updateRole(id, requestDTO);
            
            // Log role update
            logAuthorizationChange("ROLE_UPDATE", null, role.getId(), 
                    "Updated role: " + role.getName());
            
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * DELETE /api/roles/{id} - Rol sil
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete role", description = "Delete a role")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            RoleDTO role = roleService.getRoleById(id); // Get before delete for logging
            roleService.deleteRole(id);
            
            // Log role deletion
            logAuthorizationChange("ROLE_DELETE", null, id, 
                    "Deleted role: " + role.getName());
            
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/roles/{roleId}/permissions/{permissionId} - Role permission ekle
     */
    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add permission to role", description = "Add a permission to a role")
    public ResponseEntity<?> addPermissionToRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        try {
            RoleDTO role = roleService.addPermissionToRole(roleId, permissionId);
            
            // Log permission addition
            logAuthorizationChange("PERMISSION_ADDED", null, roleId, 
                    String.format("Added permission %d to role: %s", permissionId, role.getName()));
            
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * DELETE /api/roles/{roleId}/permissions/{permissionId} - Rolden permission kaldır
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove permission from role", description = "Remove a permission from a role")
    public ResponseEntity<?> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        try {
            RoleDTO role = roleService.removePermissionFromRole(roleId, permissionId);
            
            // Log permission removal
            logAuthorizationChange("PERMISSION_REMOVED", null, roleId, 
                    String.format("Removed permission %d from role: %s", permissionId, role.getName()));
            
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/roles/{roleId}/permissions/check - Permission kontrol et
     */
    @GetMapping("/{roleId}/permissions/check")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Check permission", description = "Check if role has a specific permission")
    public ResponseEntity<Map<String, Boolean>> checkPermission(
            @PathVariable Long roleId,
            @RequestParam String module,
            @RequestParam String action) {
        try {
            boolean hasPermission = roleService.hasPermission(roleId, module, action);
            Map<String, Boolean> response = new HashMap<>();
            response.put("hasPermission", hasPermission);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * PUT /api/roles/assign/{sorumluId}/{roleId} - Kullanıcıya rol ata
     */
    @PutMapping("/assign/{sorumluId}/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign role to user", description = "Assign a role to a responsible person")
    public ResponseEntity<?> assignRoleToUser(
            @PathVariable Long sorumluId,
            @PathVariable Long roleId) {
        try {
            SorumluDTO sorumlu = sorumluService.assignRole(sorumluId, roleId);
            
            // Log role assignment
            RoleDTO role = roleService.getRoleById(roleId);
            logAuthorizationChange("ROLE_ASSIGNED", sorumluId, roleId, 
                    String.format("Assigned role '%s' to user: %s %s", 
                            role.getName(), sorumlu.getAd(), sorumlu.getSoyad()));
            
            return ResponseEntity.ok(sorumlu);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * DELETE /api/roles/unassign/{sorumluId} - Kullanıcının rolünü kaldır
     */
    @DeleteMapping("/unassign/{sorumluId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove role from user", description = "Remove role assignment from a responsible person")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Long sorumluId) {
        try {
            SorumluDTO sorumlu = sorumluService.removeRole(sorumluId);
            
            // Log role removal
            logAuthorizationChange("ROLE_REMOVED", sorumluId, null, 
                    String.format("Removed role from user: %s %s", sorumlu.getAd(), sorumlu.getSoyad()));
            
            return ResponseEntity.ok(sorumlu);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // Helper methods

    /**
     * Log authorization changes
     */
    private void logAuthorizationChange(String actionType, Long userId, Long roleId, String details) {
        try {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            activityLogService.saveActivityLog(
                currentUserId,
                actionType,
                "AUTHORIZATION",
                roleId,
                details
            );
        } catch (Exception e) {
            // Silently ignore logging errors
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
