package com.svenson95.track_e_backend.runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.svenson95.track_e_backend.runtime.IdleShutdownScheduler.ShutdownHandler;

@Component
public class SpringApplicationShutdownHandler implements ShutdownHandler {

  private final ConfigurableApplicationContext applicationContext;

  public SpringApplicationShutdownHandler(ConfigurableApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void shutdown() {
    int exitCode = SpringApplication.exit(applicationContext);

    System.exit(exitCode);
  }
}
