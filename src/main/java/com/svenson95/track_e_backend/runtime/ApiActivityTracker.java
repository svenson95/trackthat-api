package com.svenson95.track_e_backend.runtime;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import org.springframework.stereotype.Component;

@Component
public class ApiActivityTracker {

  private final LongSupplier nanoTimeSupplier;

  private final AtomicLong lastActivityNanos;
  private final AtomicInteger activeRequests = new AtomicInteger();

  public ApiActivityTracker() {
    this(System::nanoTime);
  }

  ApiActivityTracker(LongSupplier nanoTimeSupplier) {
    this.nanoTimeSupplier = nanoTimeSupplier;
    this.lastActivityNanos = new AtomicLong(nanoTimeSupplier.getAsLong());
  }

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
    long idleNanos = nanoTimeSupplier.getAsLong() - lastActivityNanos.get();

    return Duration.ofNanos(idleNanos);
  }

  private void markActivity() {
    lastActivityNanos.set(nanoTimeSupplier.getAsLong());
  }
}
