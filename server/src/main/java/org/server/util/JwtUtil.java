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
    private int expiration;

    private String generateToken(String payload) {
        return Jwts.builder()
                .claim("GithubToken", payload)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Cookie generateJwtHttpCookie(String token) {
        Cookie cookie = new Cookie("jwt", this.generateToken(token));
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Przy użyciu https ustawić true
        cookie.setPath("/");
        cookie.setMaxAge(expiration);
        return cookie;
    }

    public boolean validateToken(String token) {
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

