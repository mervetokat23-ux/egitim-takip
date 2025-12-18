package com.akademi.egitimtakip.service;

import com.akademi.egitimtakip.dto.AuthResponse;
import com.akademi.egitimtakip.dto.LoginRequest;
import com.akademi.egitimtakip.dto.RegisterRequest;
import com.akademi.egitimtakip.entity.Kullanici;
import com.akademi.egitimtakip.entity.Rol;
import com.akademi.egitimtakip.repository.KullaniciRepository;
import com.akademi.egitimtakip.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Auth Service
 * 
 * Kullanıcı kayıt ve giriş işlemlerini yönetir.
 * BCrypt ile şifre hash'leme ve JWT token oluşturma işlemlerini gerçekleştirir.
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Yeni kullanıcı kaydı oluşturur
     */
    public AuthResponse register(RegisterRequest request) {
        // Email kontrolü
        if (kullaniciRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email adresi zaten kayıtlı");
        }

        // Rol kontrolü ve varsayılan rol atama
        Rol rol;
        try {
            rol = request.getRol() != null ? Rol.valueOf(request.getRol().toUpperCase()) : Rol.SORUMLU;
        } catch (IllegalArgumentException e) {
            rol = Rol.SORUMLU; // Varsayılan rol
        }

        // Yeni kullanıcı oluştur
        Kullanici kullanici = new Kullanici();
        kullanici.setAdSoyad(request.getAdSoyad());
        kullanici.setEmail(request.getEmail());
        kullanici.setSifreHash(passwordEncoder.encode(request.getSifre()));
        kullanici.setRol(rol);
        kullanici.setDurum(true);

        kullanici = kullaniciRepository.save(kullanici);

        // JWT token oluştur
        String token = jwtUtil.generateToken(kullanici.getEmail(), kullanici.getRol().name());

        return new AuthResponse(
                token,
                kullanici.getEmail(),
                kullanici.getAdSoyad(),
                kullanici.getRol().name(),
                "Kayıt başarılı"
        );
    }

    /**
     * Kullanıcı giriş işlemi
     */
    public AuthResponse login(LoginRequest request) {
        System.out.println("AuthService.login: Giriş denemesi başlatıldı: " + request.getEmail());
        // Kullanıcıyı email ile bul
        Kullanici kullanici = kullaniciRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("AuthService.login: Kullanıcı bulunamadı veya email hatalı: " + request.getEmail());
                    return new RuntimeException("Email veya şifre hatalı");
                });
        System.out.println("AuthService.login: Kullanıcı bulundu: " + kullanici.getEmail());

        // Şifre kontrolü
        if (!passwordEncoder.matches(request.getSifre(), kullanici.getSifreHash())) {
            System.out.println("AuthService.login: Şifre hatalı: " + request.getEmail());
            throw new RuntimeException("Email veya şifre hatalı");
        }
        System.out.println("AuthService.login: Şifre doğru: " + request.getEmail());

        // Durum kontrolü
        if (!kullanici.getDurum()) {
            System.out.println("AuthService.login: Kullanıcı pasif durumda: " + kullanici.getEmail());
            throw new RuntimeException("Hesabınız pasif durumda");
        }
        System.out.println("AuthService.login: Kullanıcı durumu aktif: " + kullanici.getEmail() + ", Rol: " + kullanici.getRol().name());

        // Son giriş tarihini güncelle
        kullanici.setSonGirisTarihi(LocalDateTime.now());
        kullaniciRepository.save(kullanici);

        // JWT token oluştur
        String token = jwtUtil.generateToken(kullanici.getEmail(), kullanici.getRol().name());
        System.out.println("AuthService.login: JWT Token başarıyla oluşturuldu, Rol claim: " + kullanici.getRol().name());

        return new AuthResponse(
                token,
                kullanici.getEmail(),
                kullanici.getAdSoyad(),
                kullanici.getRol().name(), // Burada zaten rol.name() kullanılıyor
                "Giriş başarılı"
        );
    }
}

