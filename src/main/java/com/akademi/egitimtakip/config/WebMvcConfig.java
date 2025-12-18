package com.akademi.egitimtakip.config;

import com.akademi.egitimtakip.interceptor.LogInterceptor;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * WebMvcConfig
 * 
 * Spring MVC yapılandırması.
 * LogInterceptor'ı tüm endpoint'lere register eder.
 * ContentCachingWrapper filter'ı ekler.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LogInterceptor logInterceptor;

    /**
     * LogInterceptor'ı tüm endpoint'lere ekler
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**") // Tüm endpoint'ler
                .excludePathPatterns(
                    "/h2-console/**",  // H2 console hariç
                    "/swagger-ui/**",  // Swagger UI hariç
                    "/v3/api-docs/**", // OpenAPI docs hariç
                    "/static/**",      // Static kaynaklar hariç
                    "/favicon.ico"     // Favicon hariç
                );
    }

    /**
     * Request ve Response body'leri cache'lemek için filter
     * Bu sayede interceptor'da body içeriğini okuyabiliriz
     */
    @Bean
    public FilterRegistrationBean<ContentCachingFilter> contentCachingFilter() {
        FilterRegistrationBean<ContentCachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContentCachingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1); // İlk sırada çalışmalı
        return registrationBean;
    }

    /**
     * Content Caching Filter
     * Request ve Response'u wrap eder, body'leri tekrar okunabilir yapar
     */
    public static class ContentCachingFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                       FilterChain filterChain) throws ServletException, IOException {
            // Request'i wrap et (body'yi cache'le)
            ContentCachingRequestWrapper wrappedRequest = 
                new ContentCachingRequestWrapper(request);
            
            // Response'u wrap et (body'yi cache'le)
            ContentCachingResponseWrapper wrappedResponse = 
                new ContentCachingResponseWrapper(response);

            try {
                // Filter chain'i devam ettir
                filterChain.doFilter(wrappedRequest, wrappedResponse);
            } finally {
                // Response body'yi client'a kopyala
                // (ContentCachingResponseWrapper body'yi tutuyor, client'a göndermemiz gerekiyor)
                wrappedResponse.copyBodyToResponse();
            }
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            String path = request.getRequestURI();
            // H2 console ve static kaynakları filtreden hariç tut
            return path.startsWith("/h2-console") || 
                   path.contains("/static") ||
                   path.endsWith(".js") ||
                   path.endsWith(".css") ||
                   path.endsWith(".png") ||
                   path.endsWith(".jpg");
        }
    }
}





