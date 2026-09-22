package com.svenson95.track_e_backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.svenson95.track_e_backend.config.MongoTestcontainersConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestcontainersConfiguration.class)
@DisplayName("TrackThat backend application")
class TrackThatBackendApplicationTests {

  @Test
  @DisplayName("loads the application context")
  void contextLoads() {
  }
}
