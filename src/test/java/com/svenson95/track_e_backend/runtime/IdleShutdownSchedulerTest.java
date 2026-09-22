package com.svenson95.track_e_backend.runtime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Idle shutdown scheduler")
class IdleShutdownSchedulerTest {

  @Mock
  private ApiActivityTracker activityTracker;

  @Mock
  private ShutdownHandler shutdownHandler;

  private IdleShutdownScheduler scheduler;

  @BeforeEach
  void setUp() {
    scheduler = new IdleShutdownScheduler(activityTracker, shutdownHandler);
  }

  @Test
  @DisplayName("does not shut down while a request is active")
  void doesNotShutdownWhileRequestIsActive() {
    when(activityTracker.hasActiveRequests()).thenReturn(true);

    scheduler.checkIdleTimeout();

    verify(activityTracker, never()).getIdleDuration();
    verifyNoInteractions(shutdownHandler);
  }

  @Test
  @DisplayName("does not shut down before the idle timeout")
  void doesNotShutdownBeforeIdleTimeout() {
    when(activityTracker.hasActiveRequests()).thenReturn(false);
    when(activityTracker.getIdleDuration()).thenReturn(Duration.ofMinutes(14));

    scheduler.checkIdleTimeout();

    verifyNoInteractions(shutdownHandler);
  }

  @Test
  @DisplayName("shuts down when the idle timeout is reached")
  void shutsDownWhenIdleTimeoutIsReached() {
    when(activityTracker.hasActiveRequests()).thenReturn(false);
    when(activityTracker.getIdleDuration()).thenReturn(Duration.ofMinutes(15));

    scheduler.checkIdleTimeout();

    verify(shutdownHandler).shutdown();
  }

  @Test
  @DisplayName("triggers shutdown only once")
  void triggersShutdownOnlyOnce() {
    when(activityTracker.hasActiveRequests()).thenReturn(false);
    when(activityTracker.getIdleDuration()).thenReturn(Duration.ofMinutes(15));

    scheduler.checkIdleTimeout();
    scheduler.checkIdleTimeout();

    verify(shutdownHandler).shutdown();
  }
}
