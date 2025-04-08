package org.server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean validateToken(String token) {
        System.out.println("Ej, to działa XD");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

//    public Long getUserIdFromRequest(HttpServletRequest request) {
//        String jwt = Arrays.stream((request).getCookies() != null ? ((HttpServletRequest) request).getCookies() : new Cookie[0])
//                .filter(cookie -> "JWT".equals(cookie.getName()))
//                .map(Cookie::getValue)
//                .findFirst()
//                .orElse(null);
//        if (jwt != null) {
//            String tokenPayload = validateToken(jwt);
//            return Long.valueOf(tokenPayload.replace("UserId: ", ""));
//        } else return null;
//    }
}

