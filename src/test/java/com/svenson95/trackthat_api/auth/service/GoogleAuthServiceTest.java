package com.svenson95.trackthat_api.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.svenson95.trackthat_api.auth.dto.GoogleUserInfoDTO;

@ExtendWith(MockitoExtension.class)
@DisplayName("Google auth service")
class GoogleAuthServiceTest {

        @Mock
        private GoogleIdTokenVerifier verifier;

        @Mock
        private GoogleIdToken token;

        private GoogleAuthService googleAuthService;

        @BeforeEach
        void setUp() {
                googleAuthService = new GoogleAuthService(verifier);
        }

        @Test
        @DisplayName("rejects a missing token")
        void rejectsMissingToken() throws Exception {
                assertThat(googleAuthService.verifyToken(null)).isNull();

                verify(verifier, never())
                                .verify(org.mockito.ArgumentMatchers.anyString());
        }

        @Test
        @DisplayName("rejects a blank token")
        void rejectsBlankToken() throws Exception {
                assertThat(googleAuthService.verifyToken(" ")).isNull();

                verify(verifier, never())
                                .verify(org.mockito.ArgumentMatchers.anyString());
        }

        @Test
        @DisplayName("returns Google user information for a valid token")
        void returnsGoogleUserInformationForValidToken() throws Exception {
                GoogleIdToken.Payload payload = new GoogleIdToken.Payload();

                payload.setSubject("google-123");
                payload.setEmail("user@example.com");
                payload.set("name", "Test User");
                payload.set("picture", "https://example.com/picture.jpg");

                when(verifier.verify("google-token")).thenReturn(token);
                when(token.getPayload()).thenReturn(payload);

                GoogleUserInfoDTO result = googleAuthService.verifyToken("google-token");

                assertThat(result).isEqualTo(
                                new GoogleUserInfoDTO(
                                                "google-123",
                                                "user@example.com",
                                                "Test User",
                                                "https://example.com/picture.jpg"));
        }

        @Test
        @DisplayName("rejects a token not accepted by Google")
        void rejectsTokenNotAcceptedByGoogle() throws Exception {
                when(verifier.verify("invalid-token")).thenReturn(null);

                assertThat(googleAuthService.verifyToken("invalid-token"))
                                .isNull();
        }

        @Test
        @DisplayName("rejects a token when Google verification fails")
        void rejectsTokenWhenGoogleVerificationFails() throws Exception {
                when(verifier.verify("google-token"))
                                .thenThrow(new GeneralSecurityException("Verification failed"));

                assertThat(googleAuthService.verifyToken("google-token"))
                                .isNull();
        }

        @Test
        @DisplayName("rejects a token when Google verification cannot be completed")
        void rejectsTokenWhenGoogleVerificationCannotBeCompleted() throws Exception {
                when(verifier.verify("google-token"))
                                .thenThrow(new IOException("Connection failed"));

                assertThat(googleAuthService.verifyToken("google-token"))
                                .isNull();
        }
}