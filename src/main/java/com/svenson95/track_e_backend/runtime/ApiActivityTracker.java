package com.svenson95.track_e_backend.runtime;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class ApiActivityTracker {

  private final AtomicLong lastActivityNanos = new AtomicLong(System.nanoTime());

  private final AtomicInteger activeRequests = new AtomicInteger();

  public void requestStarted() {
    activeRequests.incrementAndGet();
    markActivity();
  }

  public void requestFinished() {
    markActivity();
    activeRequests.decrementAndGet();
  }

  public boolean hasActiveRequests() {
    return activeRequests.get() > 0;
  }

  public Duration getIdleDuration() {
    long idleNanos = System.nanoTime() - lastActivityNanos.get();

    return Duration.ofNanos(idleNanos);
  }

  private void markActivity() {
    lastActivityNanos.set(System.nanoTime());
  }
}
