//package com.example.testfrontendbackenddb.security;
//
//import io.jsonwebtoken.*;
//import org.springframework.stereotype.Component;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//    private final String SECRET_KEY = "changeThisSecret";
//
//    public String generateToken(String email, Integer studentId) {
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("studentId", studentId)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//
//    public Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public Integer extractStudentId(String token) {
//        return extractAllClaims(token).get("studentId", Integer.class);
//    }
//}