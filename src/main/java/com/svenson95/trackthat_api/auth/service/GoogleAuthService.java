package com.svenson95.trackthat_api.auth.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.svenson95.trackthat_api.auth.dto.GoogleUserInfoDTO;

@Service
public class GoogleAuthService {

  private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);

  private final GoogleIdTokenVerifier verifier;

  @Autowired
  public GoogleAuthService(@Value("${google.client-id}") String googleClientId) {
    this(createVerifier(googleClientId));
  }

  GoogleAuthService(GoogleIdTokenVerifier verifier) {
    this.verifier = verifier;
  }

  public GoogleUserInfoDTO verifyToken(String tokenString) {
    if (tokenString == null || tokenString.isBlank()) {
      return null;
    }

    try {
      GoogleIdToken token = verifier.verify(tokenString);

      if (token == null) {
        return null;
      }

      GoogleIdToken.Payload payload = token.getPayload();

      return new GoogleUserInfoDTO(
          payload.getSubject(),
          payload.getEmail(),
          (String) payload.get("name"),
          (String) payload.get("picture"));

    } catch (GeneralSecurityException | IOException e) {
      logger.warn("Google token verification failed", e);
      return null;
    }
  }

  private static GoogleIdTokenVerifier createVerifier(String googleClientId) {
    return new GoogleIdTokenVerifier.Builder(
        new NetHttpTransport(),
        new GsonFactory())
        .setAudience(List.of(googleClientId))
        .build();
  }
}