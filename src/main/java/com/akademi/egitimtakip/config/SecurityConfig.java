package com.akademi.egitimtakip.config;

import com.akademi.egitimtakip.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security Configuration
 * 
 * Spring Security yapılandırması. JWT authentication ve role-based access control sağlar.
 * H2 database ile uyumlu çalışır, BCrypt ile şifre hash'leme yapar.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password Encoder Bean
     * BCrypt ile şifre hash'leme için kullanılır.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication Manager Bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS Configuration
     * 
     * Frontend'den gelen isteklere izin verir.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Security Filter Chain
     * 
     * Endpoint'lerin güvenlik ayarlarını yapar:
     * - /auth/** endpoint'leri herkese açık
     * - /h2-console/** H2 console erişimi için açık (dev ortamında)
     * - GET endpoint'leri public (okuma için)
     * - POST, PUT, DELETE endpoint'leri authenticated gerektirir
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS yapılandırması
            .csrf(csrf -> csrf.disable()) // JWT kullanıldığı için CSRF devre dışı
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/test/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/error").permitAll()

                // Frontend log endpoint'ini geçici olarak herkese açalım, yetkilendirme @RequirePermission ile yapılacak
                // Eğer bu endpoint için bir @RequirePermission anotasyonu varsa, bu kuralı kaldırıp anotasyona güvenmeliyiz.
                .requestMatchers(HttpMethod.POST, "/api/logs/frontend").permitAll()

                // Tüm diğer istekler kimlik doğrulaması gerektirir.
                // Detaylı yetkilendirme (@RequirePermission) aspect'i tarafından ele alınacaktır.
                .anyRequest().authenticated()
            )
            // H2 Console için frame options devre dışı
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            // JWT filter'ı ekle
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

