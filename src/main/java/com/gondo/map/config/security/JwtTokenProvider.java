package com.gondo.map.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final UserDetailsService userDetailsService;

    // AccessToken: 60분
    private static final long ACCESS_TOKEN_VALID_SECONDS = 60 * 60 * 1000L;

    // RefreshToken: 14일
    private static final long REFRESH_TOKEN_VALID_SECONDS = 14 * 24 * 60 * 60 * 1000L;


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public ResponseCookie getJwtRoleCookie(String role) {
        return ResponseCookie.from("role", role)
                .maxAge(ACCESS_TOKEN_VALID_SECONDS)
                .path("/")
                .secure(false)
                .sameSite("Lax")
//                .sameSite("None")
                .httpOnly(false)
                .build();
    }

    public ResponseCookie getJwtCookie(String userEmail) {
        String token = createAccessToken(userEmail);
        return ResponseCookie.from("access_token", token)
                .maxAge(ACCESS_TOKEN_VALID_SECONDS)
                .path("/")
                .secure(false)
                .sameSite("Lax")
//                .sameSite("None")
                .httpOnly(true)
                .build();
    }

    public ResponseCookie getJwtRefreshCookie(String userEmail) {
        String refreshToken = createRefreshToken(userEmail);
        return ResponseCookie.from("refresh_token", refreshToken)
                .maxAge(REFRESH_TOKEN_VALID_SECONDS)
                .path("/")
                .secure(false)
                .sameSite("Lax")
                .httpOnly(true) // Refresh Token은 HttpOnly로 설정
                .build();
    }

    private String createAccessToken(String userId) {
        return Jwts.builder()
                .claims(this.getClaims(userId))
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + ACCESS_TOKEN_VALID_SECONDS))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
                .signWith(getSecretKey())
                .compact();
    }

    private String createRefreshToken(String userId) {
        return Jwts.builder()
                .claims(this.getClaims(userId))
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + REFRESH_TOKEN_VALID_SECONDS))
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getClaims(String userId) {
        return Jwts.claims().subject(userId).add("userId", userId).build();
    }

    public Authentication getAuthentication(String token) {
        String userId = getUserId(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String getUserId(String token) {
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("access_token"))
                .map(Cookie::getValue).findFirst()
                .orElse(null);
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh_token"))
                .map(Cookie::getValue).findFirst()
                .orElse(null);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.error("jwt expired : {}", e.getMessage());
            return true;
        } catch (Exception e) {
            log.error("jwt parsing error : {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
//            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return !claimsJws.getBody().getExpiration().before(new Date());
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


}
