package com.svenson95.trackthat_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrackThatApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(TrackThatApiApplication.class, args);
  }
}
