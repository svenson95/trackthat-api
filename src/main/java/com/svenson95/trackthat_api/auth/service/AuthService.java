package com.svenson95.trackthat_api.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.svenson95.trackthat_api.auth.dto.AuthDTO;
import com.svenson95.trackthat_api.auth.dto.ErrorDTO;
import com.svenson95.trackthat_api.auth.service.DatabaseService.UserNotFoundException;
import com.svenson95.trackthat_api.database.model.User;

import io.jsonwebtoken.Claims;

@Service
public class AuthService {
  private final DatabaseService databaseService;
  private final JwtService jwtService;

  public AuthService(DatabaseService databaseService, JwtService jwtService) {
    this.databaseService = databaseService;
    this.jwtService = jwtService;
  }

  public AuthDTO verifyAuthHeader(String authHeader) {
    String token = extractBearerToken(authHeader);

    if (token == null || token.isBlank()) {
      throw new MissingAuthHeaderException();
    }

    Claims claims = jwtService.validateToken(token);
    String userId = (String) claims.get("userId");

    if (userId == null || userId.isBlank()) {
      throw new UserIdNotDefinedException();
    }

    User user = databaseService.findByUserId(userId).orElseThrow(UserNotFoundException::new);

    String refreshedToken = jwtService.refreshToken(claims);

    return new AuthDTO(refreshedToken, user);
  }

  public ResponseEntity<ErrorDTO> unauthorized(String message) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDTO(message));
  }

  private String extractBearerToken(String authHeader) {
    final String bearerPrefix = "Bearer ";

    if (authHeader == null || !authHeader.startsWith(bearerPrefix)) {
      return null;
    }

    String token = authHeader.substring(bearerPrefix.length());

    return token.isBlank() ? null : token;
  }

  public static class MissingAuthHeaderException extends RuntimeException {
  }

  public static class UserIdNotDefinedException extends RuntimeException {
  }
}
