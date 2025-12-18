package com.akademi.egitimtakip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Akademi Eğitim Takip Sistemi - Main Application Class
 * 
 * Spring Boot uygulamasının giriş noktası.
 * Eğitim, eğitmen, sorumlu, proje, faaliyet, paydaş ve ödeme yönetimi için REST API sağlar.
 */
@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy
public class EgitimTakipApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgitimTakipApplication.class, args);
    }
}

