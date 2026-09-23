package com.svenson95.trackthat_api.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.svenson95.trackthat_api.auth.dto.AuthDTO;
import com.svenson95.trackthat_api.auth.dto.ErrorDTO;
import com.svenson95.trackthat_api.auth.dto.GoogleLoginRequestDTO;
import com.svenson95.trackthat_api.auth.dto.GoogleUserInfoDTO;
import com.svenson95.trackthat_api.auth.dto.TokenExpiredDTO;
import com.svenson95.trackthat_api.auth.service.AuthService;
import com.svenson95.trackthat_api.auth.service.DatabaseService;
import com.svenson95.trackthat_api.auth.service.GoogleAuthService;
import com.svenson95.trackthat_api.auth.service.JwtService;
import com.svenson95.trackthat_api.database.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth controller")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private JwtService jwtService;

    @Mock
    private DatabaseService databaseService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(
                authService,
                googleAuthService,
                jwtService,
                databaseService);
    }

    @Test
    @DisplayName("logs in a valid Google user")
    void logsInValidGoogleUser() {
        GoogleUserInfoDTO userInfo = new GoogleUserInfoDTO(
                "google-123",
                "user@example.com",
                "Test User",
                "picture");

        User user = new User();
        user.setGoogleId("google-123");

        when(googleAuthService.verifyToken("google-token"))
                .thenReturn(userInfo);

        when(databaseService.findOrCreateUser(userInfo))
                .thenReturn(user);

        when(jwtService.generateToken(userInfo))
                .thenReturn("jwt-token");

        ResponseEntity<?> response = authController.loginWithGoogle(
                new GoogleLoginRequestDTO("google-token"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isEqualTo(new AuthDTO("jwt-token", user));
    }

    @Test
    @DisplayName("rejects a missing Google token")
    void rejectsMissingGoogleToken() {
        ResponseEntity<ErrorDTO> unauthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO("Missing Google Token"));

        when(authService.unauthorized("Missing Google Token"))
                .thenReturn(unauthorized);

        ResponseEntity<?> response = authController.loginWithGoogle(
                new GoogleLoginRequestDTO(null));

        assertThat(response).isSameAs(unauthorized);

        verify(googleAuthService, never())
                .verifyToken(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("rejects an invalid Google token")
    void rejectsInvalidGoogleToken() {
        when(googleAuthService.verifyToken("invalid-token"))
                .thenReturn(null);

        ResponseEntity<ErrorDTO> unauthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO("Invalid Google Token"));

        when(authService.unauthorized("Invalid Google Token"))
                .thenReturn(unauthorized);

        ResponseEntity<?> response = authController.loginWithGoogle(
                new GoogleLoginRequestDTO("invalid-token"));

        assertThat(response).isSameAs(unauthorized);

        verify(databaseService, never())
                .findOrCreateUser(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("verifies a valid JWT")
    void verifiesValidJwt() {
        User user = new User();

        AuthDTO auth = new AuthDTO("refreshed-token", user);

        when(authService.verifyAuthHeader("Bearer token"))
                .thenReturn(auth);

        ResponseEntity<?> response = authController.verifyToken("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(auth);
    }

    @Test
    @DisplayName("rejects a missing authorization header")
    void rejectsMissingAuthorizationHeader() {
        when(authService.verifyAuthHeader(null))
                .thenThrow(new AuthService.MissingAuthHeaderException());

        ResponseEntity<ErrorDTO> unauthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO("Missing or invalid Authorization header"));

        when(authService.unauthorized("Missing or invalid Authorization header"))
                .thenReturn(unauthorized);

        ResponseEntity<?> response = authController.verifyToken(null);

        assertThat(response).isSameAs(unauthorized);
    }

    @Test
    @DisplayName("reports an expired token")
    void reportsExpiredToken() {
        when(authService.verifyAuthHeader("Bearer expired"))
                .thenThrow(new JwtService.TokenExpiredException(
                        "JWT expired",
                        new RuntimeException()));

        ResponseEntity<?> response = authController.verifyToken("Bearer expired");

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        assertThat(response.getBody())
                .isEqualTo(new TokenExpiredDTO(
                        false,
                        "Token expired",
                        "relogin_with_google"));
    }

    @Test
    @DisplayName("returns not found when the authenticated user does not exist")
    void returnsNotFoundWhenUserDoesNotExist() {
        when(authService.verifyAuthHeader("Bearer token"))
                .thenThrow(new DatabaseService.UserNotFoundException());

        ResponseEntity<?> response = authController.verifyToken("Bearer token");

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(response.getBody())
                .isEqualTo(new ErrorDTO("User not found"));
    }

    @Test
    @DisplayName("rejects an invalid JWT")
    void rejectsInvalidJwt() {
        when(authService.verifyAuthHeader("Bearer invalid"))
                .thenThrow(new JwtService.InvalidTokenException(
                        "Invalid JWT",
                        new RuntimeException()));

        ResponseEntity<ErrorDTO> unauthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO("Invalid token"));

        when(authService.unauthorized("Invalid token"))
                .thenReturn(unauthorized);

        ResponseEntity<?> response = authController.verifyToken("Bearer invalid");

        assertThat(response).isSameAs(unauthorized);
    }
}