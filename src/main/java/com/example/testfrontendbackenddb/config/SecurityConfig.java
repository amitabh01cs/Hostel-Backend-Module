package com.example.testfrontendbackenddb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(request -> {
                    var cors = new org.springframework.web.cors.CorsConfiguration();
                    cors.setAllowedOrigins(List.of(
                            "http://localhost:5174",
                            "https://indoreinstitute-hostel.vercel.app",
                            "https://hostel-management-system-aw7kcse44-amitabhs-projects.vercel.app"
                    ));
                    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    cors.setAllowedHeaders(List.of("*"));
                    cors.setAllowCredentials(true);
                    return cors;
                })
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .formLogin();

        return http.build();
    }
}