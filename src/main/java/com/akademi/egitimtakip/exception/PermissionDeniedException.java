package com.akademi.egitimtakip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * PermissionDeniedException
 * 
 * Thrown when a user attempts to access a resource without the required permission.
 * Results in HTTP 403 Forbidden response.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class PermissionDeniedException extends RuntimeException {

    private final String module;
    private final String action;
    private final String username;

    public PermissionDeniedException(String message) {
        super(message);
        this.module = null;
        this.action = null;
        this.username = null;
    }

    public PermissionDeniedException(String module, String action) {
        super(String.format("Permission denied: %s.%s", module, action));
        this.module = module;
        this.action = action;
        this.username = null;
    }

    public PermissionDeniedException(String module, String action, String username) {
        super(String.format("Permission denied: User '%s' lacks permission '%s.%s'", 
                username, module, action));
        this.module = module;
        this.action = action;
        this.username = username;
    }

    public String getModule() {
        return module;
    }

    public String getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

    public String getPermissionKey() {
        if (module != null && action != null) {
            return module + "." + action;
        }
        return null;
    }
}

