package com.samazon.application.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.samazon.application.security.services.CustomUserDetails;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

    @Value("${samazon.app.jwtSecret}")
    private String JWT_SECRETE;

    @Value("${samazon.app.jwtExpirationMs}")
    private int JWT_EXPIRATION_MS;

    @Value("${samazon.app.jwtCookieName}")
    private String JWT_COOKIE_NAME;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public String getHeaderJWT(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        logger.info("Authorization header: {}", headerAuth);
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    public String getCookieJWT(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, JWT_COOKIE_NAME);
        if (cookie != null) {
            logger.info("JWT Cookie found: {}", cookie.getValue());
            return cookie.getValue();
        } else {
            logger.warn("JWT Cookie not found");
        }
        return null;
    }

    public String generateJwtTokenForUser(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS))
                .signWith(key())
                .compact();
    }

    public ResponseCookie generateJwtCookieForUser(CustomUserDetails userDetails) {
        String jwt = generateJwtTokenForUser(userDetails);
        logger.info("Generated JWT: {}", jwt);
        return ResponseCookie.from(JWT_COOKIE_NAME, jwt)
                .httpOnly(true)
                .secure(true)
                .path("/api")
                .maxAge(JWT_EXPIRATION_MS / 1000)
                .build();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(JWT_COOKIE_NAME, "")
                .path("/api")
                .build();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRETE));
    }

}
