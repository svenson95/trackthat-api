package com.svenson95.trackthat_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.svenson95.trackthat_api.config.MongoTestcontainersConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestcontainersConfiguration.class)
@DisplayName("TrackThat API application")
class TrackThatApiApplicationTests {

  @Test
  @DisplayName("loads the application context")
  void contextLoads() {
  }
}
