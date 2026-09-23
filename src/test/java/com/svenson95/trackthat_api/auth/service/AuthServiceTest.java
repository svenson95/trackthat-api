package com.svenson95.trackthat_api.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.svenson95.trackthat_api.auth.dto.AuthDTO;
import com.svenson95.trackthat_api.database.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth service")
class AuthServiceTest {

    @Mock
    private DatabaseService databaseService;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(databaseService, jwtService);
    }

    @Test
    @DisplayName("verifies an authorization header and refreshes the token")
    void verifiesAuthorizationHeaderAndRefreshesToken() {
        Claims claims = Jwts.claims()
                .add("userId", "google-123")
                .build();

        User user = new User();
        user.setGoogleId("google-123");

        when(jwtService.validateToken("old-token"))
                .thenReturn(claims);

        when(databaseService.findByUserId("google-123"))
                .thenReturn(Optional.of(user));

        when(jwtService.refreshToken(claims))
                .thenReturn("refreshed-token");

        AuthDTO result = authService.verifyAuthHeader("Bearer old-token");

        assertThat(result.token()).isEqualTo("refreshed-token");
        assertThat(result.user()).isSameAs(user);

        verify(jwtService).validateToken("old-token");
        verify(databaseService).findByUserId("google-123");
        verify(jwtService).refreshToken(claims);
    }

    @Test
    @DisplayName("rejects a missing authorization header")
    void rejectsMissingAuthorizationHeader() {
        assertThatThrownBy(() -> authService.verifyAuthHeader(null))
                .isInstanceOf(AuthService.MissingAuthHeaderException.class);

        verify(jwtService, never()).validateToken(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("rejects an authorization header without bearer scheme")
    void rejectsAuthorizationHeaderWithoutBearerScheme() {
        assertThatThrownBy(() -> authService.verifyAuthHeader("old-token"))
                .isInstanceOf(AuthService.MissingAuthHeaderException.class);
    }

    @Test
    @DisplayName("rejects an empty bearer token")
    void rejectsEmptyBearerToken() {
        assertThatThrownBy(() -> authService.verifyAuthHeader("Bearer "))
                .isInstanceOf(AuthService.MissingAuthHeaderException.class);
    }

    @Test
    @DisplayName("rejects a token without user ID")
    void rejectsTokenWithoutUserId() {
        Claims claims = Jwts.claims().build();

        when(jwtService.validateToken("token")).thenReturn(claims);

        assertThatThrownBy(() -> authService.verifyAuthHeader("Bearer token"))
                .isInstanceOf(AuthService.UserIdNotDefinedException.class);

        verify(databaseService, never())
                .findByUserId(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("rejects a token with blank user ID")
    void rejectsTokenWithBlankUserId() {
        Claims claims = Jwts.claims()
                .add("userId", " ")
                .build();

        when(jwtService.validateToken("token")).thenReturn(claims);

        assertThatThrownBy(() -> authService.verifyAuthHeader("Bearer token"))
                .isInstanceOf(AuthService.UserIdNotDefinedException.class);
    }

    @Test
    @DisplayName("throws when the authenticated user does not exist")
    void throwsWhenUserDoesNotExist() {
        Claims claims = Jwts.claims()
                .add("userId", "google-123")
                .build();

        when(jwtService.validateToken("token")).thenReturn(claims);
        when(databaseService.findByUserId("google-123"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyAuthHeader("Bearer token"))
                .isInstanceOf(DatabaseService.UserNotFoundException.class);

        verify(jwtService, never())
                .refreshToken(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("creates an unauthorized response")
    void createsUnauthorizedResponse() {
        var response = authService.unauthorized("Invalid token");

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Invalid token");
    }
}