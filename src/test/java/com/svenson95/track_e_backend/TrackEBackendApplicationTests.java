package com.svenson95.track_e_backend;

import com.svenson95.track_e_backend.config.MongoTestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestcontainersConfiguration.class)
class TrackThatBackendApplicationTests {

  @Test
  void contextLoads() {}
}
