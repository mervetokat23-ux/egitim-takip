package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Role DTO
 * 
 * Data Transfer Object for Role entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<PermissionDTO> permissions = new HashSet<>();
}


