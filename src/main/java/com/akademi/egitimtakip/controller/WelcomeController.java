package com.akademi.egitimtakip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Welcome Controller
 * 
 * Root path için basit bir welcome mesajı döndürür.
 */
@RestController
@CrossOrigin(origins = "*")
public class WelcomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Akademi Eğitim Takip Sistemi API");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("endpoints", Map.of(
            "auth", "/auth/login, /auth/register",
            "egitim", "/egitim (GET, POST, PUT, DELETE)",
            "proje", "/proje (GET, POST, PUT, DELETE)",
            "odeme", "/odeme (GET, POST, PUT, DELETE)",
            "durum", "/durum (GET, POST, PUT, DELETE)",
            "faaliyet", "/faaliyet (GET, POST, PUT, DELETE)",
            "swagger", "/swagger-ui.html",
            "h2-console", "/h2-console"
        ));
        return ResponseEntity.ok(response);
    }
}

