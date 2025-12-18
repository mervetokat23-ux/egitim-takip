package com.akademi.egitimtakip.security;

import com.akademi.egitimtakip.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter
 * 
 * Her HTTP request'te JWT token'ı kontrol eder ve doğrular.
 * Token geçerliyse kullanıcıyı SecurityContext'e ekler.
 * H2 database ile uyumlu çalışır.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String HEADER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Authorization header kontrolü
        if (authHeader == null || !authHeader.startsWith(HEADER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Token'ı çıkar
            final String token = authHeader.substring(HEADER_PREFIX.length());
            
            // Token'dan email ve rol çıkar
            final String email = jwtUtil.getEmailFromToken(token);
            final String rol = jwtUtil.getRolFromToken(token);

            // Token geçerliliğini kontrol et - email ve rol null olmamalı
            if (email != null && rol != null && !rol.trim().isEmpty() 
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(token, email)) {
                    // Authentication oluştur - rol bilgisi ile
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()))
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // SecurityContext'e ekle
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Token geçersizse işleme devam et (401 dönecek)
            logger.error("JWT token doğrulama hatası", e);
            // SecurityContext'i temizle
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}

