package com.svenson95.track_e_backend.runtime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class IdleShutdownSchedulerTest {

  @Mock private ApiActivityTracker activityTracker;

  @Mock private ShutdownHandler shutdownHandler;

  private IdleShutdownScheduler scheduler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    scheduler = new IdleShutdownScheduler(activityTracker, shutdownHandler);
  }

  @Test
  void shouldNotShutdownWhileRequestIsActive() {
    when(activityTracker.hasActiveRequests()).thenReturn(true);

    scheduler.checkIdleTimeout();

    verify(activityTracker, never()).getIdleDuration();
    verifyNoInteractions(shutdownHandler);
  }

  @Test
  void shouldNotShutdownBeforeIdleTimeout() {
    when(activityTracker.hasActiveRequests()).thenReturn(false);
    when(activityTracker.getIdleDuration()).thenReturn(Duration.ofMinutes(14));

    scheduler.checkIdleTimeout();

    verifyNoInteractions(shutdownHandler);
  }

  @Test
  void shouldShutdownAfterIdleTimeout() {
    when(activityTracker.hasActiveRequests()).thenReturn(false);
    when(activityTracker.getIdleDuration()).thenReturn(Duration.ofMinutes(15));

    scheduler.checkIdleTimeout();

    verify(shutdownHandler).shutdown();
  }

  @Test
  void shouldOnlyTriggerShutdownOnce() {
    when(activityTracker.hasActiveRequests()).thenReturn(false);
    when(activityTracker.getIdleDuration()).thenReturn(Duration.ofMinutes(15));

    scheduler.checkIdleTimeout();
    scheduler.checkIdleTimeout();

    verify(shutdownHandler).shutdown();
  }
}
