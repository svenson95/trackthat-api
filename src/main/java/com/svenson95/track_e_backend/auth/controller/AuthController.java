package com.svenson95.track_e_backend.auth.controller;

import com.svenson95.track_e_backend.auth.dto.AuthDTO;
import com.svenson95.track_e_backend.auth.dto.ErrorDTO;
import com.svenson95.track_e_backend.auth.dto.GoogleLoginRequestDTO;
import com.svenson95.track_e_backend.auth.dto.GoogleUserInfoDTO;
import com.svenson95.track_e_backend.auth.dto.TokenExpiredDTO;
import com.svenson95.track_e_backend.auth.service.AuthService;
import com.svenson95.track_e_backend.auth.service.DatabaseService;
import com.svenson95.track_e_backend.auth.service.GoogleAuthService;
import com.svenson95.track_e_backend.auth.service.JwtService;
import com.svenson95.track_e_backend.database.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final GoogleAuthService googleAuthService;
  private final JwtService jwtService;
  private final DatabaseService databaseService;

  public AuthController(
      AuthService authService,
      GoogleAuthService googleAuthService,
      JwtService jwtService,
      DatabaseService databaseService) {
    this.authService = authService;
    this.googleAuthService = googleAuthService;
    this.jwtService = jwtService;
    this.databaseService = databaseService;
  }

  @PostMapping("/google")
  public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequestDTO body) {
    String token = body.token();

    if (token == null || token.isBlank()) {
      return authService.unauthorized("Missing Google Token");
    }

    GoogleUserInfoDTO userInfo = googleAuthService.verifyToken(token);

    if (userInfo == null) {
      return authService.unauthorized("Invalid Google Token");
    }

    User user = databaseService.findOrCreateUser(userInfo);
    String jwt = jwtService.generateToken(userInfo);

    return ResponseEntity.ok(new AuthDTO(jwt, user));
  }

  @GetMapping("/verify")
  public ResponseEntity<?> verifyToken(
      @RequestHeader(value = "Authorization", required = false) String authHeader) {

    try {
      AuthDTO auth = authService.verifyAuthHeader(authHeader);
      return ResponseEntity.ok(auth);

    } catch (AuthService.MissingAuthHeaderException e) {
      return authService.unauthorized("Missing or invalid Authorization header");

    } catch (JwtService.TokenExpiredException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new TokenExpiredDTO(false, "Token expired", "relogin_with_google"));

    } catch (DatabaseService.UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("User not found"));

    } catch (Exception e) {
      return authService.unauthorized("Invalid token");
    }
  }
}
