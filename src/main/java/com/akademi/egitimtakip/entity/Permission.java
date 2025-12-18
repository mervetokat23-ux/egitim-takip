package com.akademi.egitimtakip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Permission Entity
 * 
 * Represents a specific permission in the system.
 * Permissions are defined by a module (e.g., education, payment) and an action (e.g., view, create, update, delete).
 */
@Entity
@Table(name = "permissions", 
    indexes = {
        @Index(name = "idx_permissions_module", columnList = "module"),
        @Index(name = "idx_permissions_action", columnList = "action"),
        @Index(name = "idx_permissions_module_action", columnList = "module, action")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_module_action", columnNames = {"module", "action"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module", length = 100, nullable = false)
    private String module;

    @Column(name = "action", length = 20, nullable = false)
    private String action;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Helper method to create a permission key
     */
    public String getPermissionKey() {
        return module + ":" + action;
    }

    /**
     * Override equals for Set operations
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return module != null && module.equals(that.module) 
            && action != null && action.equals(that.action);
    }

    /**
     * Override hashCode for Set operations
     */
    @Override
    public int hashCode() {
        return 31 * (module != null ? module.hashCode() : 0) 
             + (action != null ? action.hashCode() : 0);
    }
}


