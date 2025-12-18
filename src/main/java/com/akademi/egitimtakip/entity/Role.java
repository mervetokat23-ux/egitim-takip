package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Role Entity
 * 
 * Represents a user role in the system (e.g., ADMIN, STAFF, READONLY).
 * Roles have associated permissions that define what actions they can perform.
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_roles_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-Many relationship with Permission
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /**
     * Helper method to check if role has a specific permission
     */
    public boolean hasPermission(String module, String action) {
        return permissions.stream()
            .anyMatch(p -> p.getModule().equalsIgnoreCase(module) 
                       && p.getAction().equalsIgnoreCase(action));
    }

    /**
     * Helper method to add a permission to this role
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    /**
     * Helper method to remove a permission from this role
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }
}


