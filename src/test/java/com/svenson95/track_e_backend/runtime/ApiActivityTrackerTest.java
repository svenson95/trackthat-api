package com.svenson95.track_e_backend.runtime;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiActivityTrackerTest {

  private AtomicLong nanoTime;
  private ApiActivityTracker activityTracker;

  @BeforeEach
  void setUp() {
    nanoTime = new AtomicLong();
    activityTracker = new ApiActivityTracker(nanoTime::get);
  }

  @Test
  void shouldTrackActiveRequest() {
    activityTracker.requestStarted();

    assertThat(activityTracker.hasActiveRequests()).isTrue();

    activityTracker.requestFinished();

    assertThat(activityTracker.hasActiveRequests()).isFalse();
  }

  @Test
  void shouldTrackMultipleActiveRequests() {
    activityTracker.requestStarted();
    activityTracker.requestStarted();

    activityTracker.requestFinished();

    assertThat(activityTracker.hasActiveRequests()).isTrue();

    activityTracker.requestFinished();

    assertThat(activityTracker.hasActiveRequests()).isFalse();
  }

  @Test
  void shouldReturnIdleDurationSinceLastActivity() {
    nanoTime.set(Duration.ofSeconds(5).toNanos());

    assertThat(activityTracker.getIdleDuration()).isEqualTo(Duration.ofSeconds(5));
  }

  @Test
  void shouldResetIdleDurationWhenRequestStarts() {
    nanoTime.set(Duration.ofSeconds(5).toNanos());

    activityTracker.requestStarted();

    nanoTime.set(Duration.ofSeconds(8).toNanos());

    assertThat(activityTracker.getIdleDuration()).isEqualTo(Duration.ofSeconds(3));
  }

  @Test
  void shouldResetIdleDurationWhenRequestFinishes() {
    activityTracker.requestStarted();

    nanoTime.set(Duration.ofSeconds(5).toNanos());

    activityTracker.requestFinished();

    nanoTime.set(Duration.ofSeconds(7).toNanos());

    assertThat(activityTracker.getIdleDuration()).isEqualTo(Duration.ofSeconds(2));
  }
}
