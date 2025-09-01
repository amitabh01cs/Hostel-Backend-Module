package com.example.testfrontendbackenddb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOriginPatterns(List.of(
                        "http://localhost:*",
                        "https://*.vercel.app"   // allows any vercel subdomain
                ));
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                corsConfig.setAllowedHeaders(List.of("*"));
                corsConfig.setExposedHeaders(List.of("Authorization", "Content-Type"));
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            );
        // ⚠️ Don't enable formLogin() unless you need HTML login form
        return http.build();
    }
}
