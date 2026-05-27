package com.svenson95.track_e_backend.auth.service;

import com.svenson95.track_e_backend.auth.dto.GoogleUserInfoDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final Key secretKey;

  public JwtService(@Value("${jwt.secret}") String secret) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("JWT_SECRET environment variable not set!");
    }

    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(GoogleUserInfoDTO userInfo) {
    final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7; // 7 days

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

    Map<String, Object> claims = new HashMap<>();

    claims.put("userId", userInfo.userId());
    claims.put("email", userInfo.email());
    claims.put("name", userInfo.name());
    claims.put("picture", userInfo.picture());

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userInfo.email())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(this.secretKey)
        .compact();
  }

  public Claims validateToken(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(this.secretKey)
          .build()
          .parseClaimsJws(token)
          .getBody();

    } catch (ExpiredJwtException ex) {
      throw new TokenExpiredException("JWT expired", ex);

    } catch (MalformedJwtException
        | UnsupportedJwtException
        | SignatureException
        | IllegalArgumentException ex) {
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
