package com.akademi.egitimtakip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Permission DTO
 * 
 * Data Transfer Object for Permission entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    
    private Long id;
    private String module;
    private String action;
    private String description;
    private LocalDateTime createdAt;
    
    /**
     * Get permission key (module:action)
     */
    public String getPermissionKey() {
        return module + ":" + action;
    }
}


