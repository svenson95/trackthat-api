package com.svenson95.trackthat_api.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.svenson95.trackthat_api.auth.dto.GoogleUserInfoDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private static final Duration TOKEN_EXPIRATION = Duration.ofDays(7);

  private final SecretKey secretKey;

  public JwtService(@Value("${jwt.secret}") String secret) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("JWT secret must be configured");
    }

    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(GoogleUserInfoDTO userInfo) {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plus(TOKEN_EXPIRATION);

    Map<String, Object> claims = new HashMap<>();

    claims.put("userId", userInfo.userId());
    claims.put("email", userInfo.email());
    claims.put("name", userInfo.name());
    claims.put("picture", userInfo.picture());

    return Jwts.builder()
        .claims(claims)
        .subject(userInfo.email())
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(expiresAt))
        .signWith(secretKey)
        .compact();
  }

  public Claims validateToken(String token) {
    try {
      return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

    } catch (ExpiredJwtException ex) {
      throw new TokenExpiredException("JWT expired", ex);

    } catch (JwtException | IllegalArgumentException ex) {
      throw new InvalidTokenException("Invalid JWT", ex);
    }
  }

  public static class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
