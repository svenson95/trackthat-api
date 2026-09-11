package com.svenson95.track_e_backend.runtime;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IdleShutdownScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdleShutdownScheduler.class);

  private static final Duration IDLE_TIMEOUT = Duration.ofMinutes(30);
  private static final long CHECK_INTERVAL_MINUTES = 1;

  private final ApiActivityTracker activityTracker;
  private final ConfigurableApplicationContext applicationContext;

  private final AtomicBoolean shutdownTriggered = new AtomicBoolean();

  public IdleShutdownScheduler(
      ApiActivityTracker activityTracker, ConfigurableApplicationContext applicationContext) {
    this.activityTracker = activityTracker;
    this.applicationContext = applicationContext;
  }

  @Scheduled(fixedDelay = CHECK_INTERVAL_MINUTES, timeUnit = TimeUnit.MINUTES)
  public void checkIdleTimeout() {
    if (shutdownTriggered.get() || activityTracker.hasActiveRequests()) {
      return;
    }

    Duration idleDuration = activityTracker.getIdleDuration();

    if (idleDuration.compareTo(IDLE_TIMEOUT) < 0) {
      return;
    }

    if (!shutdownTriggered.compareAndSet(false, true)) {
      return;
    }

    LOGGER.info(
        "No API activity for {} minutes. Shutting down application.", idleDuration.toMinutes());

    int exitCode = SpringApplication.exit(applicationContext);

    System.exit(exitCode);
  }
}
