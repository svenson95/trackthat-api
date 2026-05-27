package com.svenson95.track_e_backend.auth.service;

import com.svenson95.track_e_backend.auth.dto.AuthDTO;
import com.svenson95.track_e_backend.auth.dto.ErrorDTO;
import com.svenson95.track_e_backend.auth.service.DatabaseService.UserNotFoundException;
import com.svenson95.track_e_backend.database.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    return new AuthDTO(token, user);
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

  public static class MissingAuthHeaderException extends RuntimeException {}

  public static class UserIdNotDefinedException extends RuntimeException {}
}
