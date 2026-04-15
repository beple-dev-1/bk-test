package com.example.bkpaymenttest.config;

import com.example.bkpaymenttest.common.interceptor.LogInterceptor;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.annotation.Bean;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .addPathPatterns("/api/**");
    }

    /**
     * iframe 허용 헤더 필터.
     * X-Frame-Options: ALLOWALL  → 모든 출처에서 iframe 허용
     * Content-Security-Policy: frame-ancestors * → 최신 브라우저용 동일 효과
     */
    @Bean
    public OncePerRequestFilter frameOptionsFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {
                response.setHeader("X-Frame-Options", "ALLOWALL");
                response.setHeader("Content-Security-Policy", "frame-ancestors *");
                filterChain.doFilter(request, response);
            }
        };
    }
}
