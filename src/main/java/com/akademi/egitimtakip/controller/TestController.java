package com.akademi.egitimtakip.controller;

import com.akademi.egitimtakip.entity.Kullanici;
import com.akademi.egitimtakip.entity.Rol;
import com.akademi.egitimtakip.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Test Controller
 * 
 * Test ve debug için basit endpoint'ler sağlar.
 */
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers() {
        List<Kullanici> users = kullaniciRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", users.size());
        response.put("users", users.stream().map(u -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", u.getId());
            userMap.put("email", u.getEmail());
            userMap.put("adSoyad", u.getAdSoyad());
            userMap.put("rol", u.getRol());
            userMap.put("durum", u.getDurum());
            return userMap;
        }).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    /**
     * Manuel kullanıcı oluşturma endpoint'i (test için)
     * GET ve POST ile çalışır (tarayıcıdan kolay erişim için)
     */
    @GetMapping("/create-users")
    @PostMapping("/create-users")
    public ResponseEntity<Map<String, Object>> createTestUsers() {
        Map<String, Object> response = new HashMap<>();
        int created = 0;

        // Admin kullanıcısı
        if (!kullaniciRepository.existsByEmail("admin@akademi.com")) {
            Kullanici admin = new Kullanici();
            admin.setAdSoyad("Sistem Yöneticisi");
            admin.setEmail("admin@akademi.com");
            admin.setSifreHash(passwordEncoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);
            admin.setDurum(true);
            kullaniciRepository.save(admin);
            created++;
        }

        // Sorumlu kullanıcısı
        if (!kullaniciRepository.existsByEmail("sorumlu@akademi.com")) {
            Kullanici sorumlu = new Kullanici();
            sorumlu.setAdSoyad("Eğitim Sorumlusu");
            sorumlu.setEmail("sorumlu@akademi.com");
            sorumlu.setSifreHash(passwordEncoder.encode("sorumlu123"));
            sorumlu.setRol(Rol.SORUMLU);
            sorumlu.setDurum(true);
            kullaniciRepository.save(sorumlu);
            created++;
        }

        // Eğitmen kullanıcısı
        if (!kullaniciRepository.existsByEmail("egitmen@akademi.com")) {
            Kullanici egitmen = new Kullanici();
            egitmen.setAdSoyad("Eğitmen");
            egitmen.setEmail("egitmen@akademi.com");
            egitmen.setSifreHash(passwordEncoder.encode("egitmen123"));
            egitmen.setRol(Rol.EGITMEN);
            egitmen.setDurum(true);
            kullaniciRepository.save(egitmen);
            created++;
        }

        response.put("message", created + " kullanıcı oluşturuldu");
        response.put("created", created);
        response.put("users", getUsers().getBody().get("users"));
        return ResponseEntity.ok(response);
    }
}
