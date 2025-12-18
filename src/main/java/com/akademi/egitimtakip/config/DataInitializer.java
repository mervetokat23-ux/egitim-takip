package com.akademi.egitimtakip.config;

import com.akademi.egitimtakip.entity.Kullanici;
import com.akademi.egitimtakip.entity.Rol;
import com.akademi.egitimtakip.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer
 * 
 * Uygulama başladığında varsayılan kullanıcıları oluşturur.
 * H2 database ile uyumlu çalışır.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Admin kullanıcısı oluştur (eğer yoksa)
        if (!kullaniciRepository.existsByEmail("admin@akademi.com")) {
            Kullanici admin = new Kullanici();
            admin.setAdSoyad("Sistem Yöneticisi");
            admin.setEmail("admin@akademi.com");
            admin.setSifreHash(passwordEncoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);
            admin.setDurum(true);
            kullaniciRepository.save(admin);
            System.out.println("✓ Admin kullanıcısı oluşturuldu: admin@akademi.com / admin123");
        }

        // Sorumlu kullanıcısı oluştur (eğer yoksa)
        if (!kullaniciRepository.existsByEmail("sorumlu@akademi.com")) {
            Kullanici sorumlu = new Kullanici();
            sorumlu.setAdSoyad("Eğitim Sorumlusu");
            sorumlu.setEmail("sorumlu@akademi.com");
            sorumlu.setSifreHash(passwordEncoder.encode("sorumlu123"));
            sorumlu.setRol(Rol.SORUMLU);
            sorumlu.setDurum(true);
            kullaniciRepository.save(sorumlu);
            System.out.println("✓ Sorumlu kullanıcısı oluşturuldu: sorumlu@akademi.com / sorumlu123");
        }

        // Eğitmen kullanıcısı oluştur (eğer yoksa)
        if (!kullaniciRepository.existsByEmail("egitmen@akademi.com")) {
            Kullanici egitmen = new Kullanici();
            egitmen.setAdSoyad("Eğitmen");
            egitmen.setEmail("egitmen@akademi.com");
            egitmen.setSifreHash(passwordEncoder.encode("egitmen123"));
            egitmen.setRol(Rol.EGITMEN);
            egitmen.setDurum(true);
            kullaniciRepository.save(egitmen);
            System.out.println("✓ Eğitmen kullanıcısı oluşturuldu: egitmen@akademi.com / egitmen123");
        }
    }
}

