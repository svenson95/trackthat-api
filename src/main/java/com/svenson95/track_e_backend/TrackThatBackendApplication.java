package com.svenson95.track_e_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrackThatBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(TrackThatBackendApplication.class, args);
  }
}
