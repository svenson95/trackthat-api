package com.svenson95.trackthat_api.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.svenson95.trackthat_api.auth.dto.GoogleUserInfoDTO;

import io.jsonwebtoken.Claims;

@DisplayName("JWT service")
class JwtServiceTest {

    private static final String SECRET = "test-secret-key-with-at-least-32-bytes";

    private static final Instant NOW = Instant.parse("2026-09-23T12:00:00Z");

    @Test
    @DisplayName("generates a valid token with user claims")
    void generatesValidTokenWithUserClaims() {
        JwtService jwtService = createService(NOW);

        GoogleUserInfoDTO userInfo = new GoogleUserInfoDTO(
                "google-123",
                "user@example.com",
                "Test User",
                "https://example.com/picture.jpg");

        String token = jwtService.generateToken(userInfo);

        Claims claims = jwtService.validateToken(token);

        assertThat(claims.get("userId", String.class)).isEqualTo("google-123");
        assertThat(claims.get("email", String.class)).isEqualTo("user@example.com");
        assertThat(claims.get("name", String.class)).isEqualTo("Test User");
        assertThat(claims.get("picture", String.class))
                .isEqualTo("https://example.com/picture.jpg");
        assertThat(claims.getSubject()).isEqualTo("user@example.com");

        assertThat(claims.getIssuedAt()).isEqualTo(Date.from(NOW));
        assertThat(claims.getExpiration())
                .isEqualTo(Date.from(NOW.plus(JwtService.TOKEN_EXPIRATION)));
    }

    @Test
    @DisplayName("refreshes a token with a new expiration")
    void refreshesTokenWithNewExpiration() {
        JwtService initialService = createService(NOW);

        GoogleUserInfoDTO userInfo = new GoogleUserInfoDTO(
                "google-123",
                "user@example.com",
                "Test User",
                "picture");

        String originalToken = initialService.generateToken(userInfo);
        Claims originalClaims = initialService.validateToken(originalToken);

        Instant refreshTime = NOW.plusSeconds(60);

        JwtService refreshedService = createService(refreshTime);

        String refreshedToken = refreshedService.refreshToken(originalClaims);
        Claims refreshedClaims = refreshedService.validateToken(refreshedToken);

        assertThat(refreshedClaims.getIssuedAt())
                .isEqualTo(Date.from(refreshTime));

        assertThat(refreshedClaims.getExpiration())
                .isEqualTo(Date.from(refreshTime.plus(JwtService.TOKEN_EXPIRATION)));

        assertThat(refreshedClaims.get("userId", String.class))
                .isEqualTo("google-123");
        assertThat(refreshedClaims.get("email", String.class))
                .isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("rejects an expired token")
    void rejectsExpiredToken() {
        JwtService tokenService = createService(NOW);

        String token = tokenService.generateToken(
                new GoogleUserInfoDTO(
                        "google-123",
                        "user@example.com",
                        "Test User",
                        "picture"));

        JwtService futureService = createService(NOW.plus(JwtService.TOKEN_EXPIRATION).plusSeconds(1));

        assertThatThrownBy(() -> futureService.validateToken(token))
                .isInstanceOf(JwtService.TokenExpiredException.class);
    }

    @Test
    @DisplayName("rejects an invalid token")
    void rejectsInvalidToken() {
        JwtService jwtService = createService(NOW);

        assertThatThrownBy(() -> jwtService.validateToken("invalid-token"))
                .isInstanceOf(JwtService.InvalidTokenException.class);
    }

    @Test
    @DisplayName("rejects a token signed with another secret")
    void rejectsTokenSignedWithAnotherSecret() {
        JwtService firstService = createService(NOW);

        JwtService secondService = new JwtService(
                "another-test-secret-with-at-least-32-bytes",
                Clock.fixed(NOW, ZoneOffset.UTC));

        String token = firstService.generateToken(
                new GoogleUserInfoDTO(
                        "google-123",
                        "user@example.com",
                        "Test User",
                        "picture"));

        assertThatThrownBy(() -> secondService.validateToken(token))
                .isInstanceOf(JwtService.InvalidTokenException.class);
    }

    @Test
    @DisplayName("requires a configured secret")
    void requiresConfiguredSecret() {
        assertThatThrownBy(() -> new JwtService("", Clock.systemUTC()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT secret must be configured");
    }

    private JwtService createService(Instant instant) {
        return new JwtService(
                SECRET,
                Clock.fixed(instant, ZoneOffset.UTC));
    }
}