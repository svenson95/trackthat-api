package com.svenson95.trackthat_api.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
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

  static final Duration TOKEN_EXPIRATION = Duration.ofDays(7);

  private final SecretKey secretKey;
  private final Clock clock;

  @Autowired
  public JwtService(@Value("${jwt.secret}") String secret) {
    this(secret, Clock.systemUTC());
  }

  JwtService(String secret, Clock clock) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("JWT secret must be configured");
    }

    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.clock = clock;
  }

  public String generateToken(GoogleUserInfoDTO userInfo) {
    return generateToken(
        userInfo.userId(),
        userInfo.email(),
        userInfo.name(),
        userInfo.picture());
  }

  public String refreshToken(Claims claims) {
    return generateToken(
        claims.get("userId", String.class),
        claims.get("email", String.class),
        claims.get("name", String.class),
        claims.get("picture", String.class));
  }

  private String generateToken(
      String userId,
      String email,
      String name,
      String picture) {

    Instant issuedAt = clock.instant();
    Instant expiresAt = issuedAt.plus(TOKEN_EXPIRATION);

    Map<String, Object> claims = new HashMap<>();

    claims.put("userId", userId);
    claims.put("email", email);
    claims.put("name", name);
    claims.put("picture", picture);

    return Jwts.builder()
        .claims(claims)
        .subject(email)
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(expiresAt))
        .signWith(secretKey)
        .compact();
  }

  public Claims validateToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .clock(() -> Date.from(clock.instant()))
          .build()
          .parseSignedClaims(token)
          .getPayload();

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