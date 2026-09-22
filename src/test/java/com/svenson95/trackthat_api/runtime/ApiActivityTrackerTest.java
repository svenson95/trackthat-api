package com.svenson95.trackthat_api.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("API activity tracker")
class ApiActivityTrackerTest {

  private AtomicLong nanoTime;
  private ApiActivityTracker activityTracker;

  @BeforeEach
  void setUp() {
    nanoTime = new AtomicLong();
    // The constructor ApiActivityTracker(LongSupplier) is not visible
    activityTracker = new ApiActivityTracker(nanoTime::get);
  }

  @Test
  @DisplayName("tracks an active request")
  void tracksActiveRequest() {
    activityTracker.requestStarted();

    assertThat(activityTracker.hasActiveRequests()).isTrue();

    activityTracker.requestFinished();

    assertThat(activityTracker.hasActiveRequests()).isFalse();
  }

  @Test
  @DisplayName("tracks multiple active requests")
  void tracksMultipleActiveRequests() {
    activityTracker.requestStarted();
    activityTracker.requestStarted();

    activityTracker.requestFinished();

    assertThat(activityTracker.hasActiveRequests()).isTrue();

    activityTracker.requestFinished();

    assertThat(activityTracker.hasActiveRequests()).isFalse();
  }

  @Test
  @DisplayName("returns the idle duration since the last activity")
  void returnsIdleDurationSinceLastActivity() {
    nanoTime.set(Duration.ofSeconds(5).toNanos());

    assertThat(activityTracker.getIdleDuration()).isEqualTo(Duration.ofSeconds(5));
  }

  @Test
  @DisplayName("resets the idle duration when a request starts")
  void resetsIdleDurationWhenRequestStarts() {
    nanoTime.set(Duration.ofSeconds(5).toNanos());

    activityTracker.requestStarted();

    nanoTime.set(Duration.ofSeconds(8).toNanos());

    assertThat(activityTracker.getIdleDuration()).isEqualTo(Duration.ofSeconds(3));
  }

  @Test
  @DisplayName("resets the idle duration when a request finishes")
  void resetsIdleDurationWhenRequestFinishes() {
    activityTracker.requestStarted();

    nanoTime.set(Duration.ofSeconds(5).toNanos());

    activityTracker.requestFinished();

    nanoTime.set(Duration.ofSeconds(7).toNanos());

    assertThat(activityTracker.getIdleDuration()).isEqualTo(Duration.ofSeconds(2));
  }
}
