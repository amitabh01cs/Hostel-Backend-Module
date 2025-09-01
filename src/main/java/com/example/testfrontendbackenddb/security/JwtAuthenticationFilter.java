//package com.example.testfrontendbackenddb.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String jwt = authHeader.substring(7);
//            try {
//                Integer studentId = jwtUtil.extractStudentId(jwt);
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(studentId, null, null);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } catch (Exception e) {
//                // Ignore invalid/expired token
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}