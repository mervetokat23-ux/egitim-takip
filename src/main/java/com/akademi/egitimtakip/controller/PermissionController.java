package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.dto.PermissionDTO;
import com.akademi.egitimtakip.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Permission Controller
 * 
 * REST API for managing permissions.
 * Only accessible by ADMIN users.
 */
@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
@Tag(name = "Permission Management", description = "Manage system permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all permissions", description = "Retrieve all permissions ordered by module and action")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get permission by ID", description = "Retrieve a specific permission")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        try {
            PermissionDTO permission = permissionService.getPermissionById(id);
            return ResponseEntity.ok(permission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/module/{module}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get permissions by module", description = "Retrieve all permissions for a specific module")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByModule(@PathVariable String module) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByModule(module);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get permissions by action", description = "Retrieve all permissions for a specific action")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByAction(@PathVariable String action) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByAction(action);
        return ResponseEntity.ok(permissions);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new permission", description = "Create a new permission")
    public ResponseEntity<?> createPermission(
            @RequestParam String module,
            @RequestParam String action,
            @RequestParam(required = false) String description) {
        try {
            PermissionDTO permission = permissionService.createPermission(module, action, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(permission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update permission", description = "Update permission description")
    public ResponseEntity<?> updatePermission(
            @PathVariable Long id,
            @RequestParam String description) {
        try {
            PermissionDTO permission = permissionService.updatePermission(id, description);
            return ResponseEntity.ok(permission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete permission", description = "Delete a permission")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}


