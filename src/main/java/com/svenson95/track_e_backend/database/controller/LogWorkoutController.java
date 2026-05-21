package com.svenson95.track_e_backend.database.controller;

import com.svenson95.track_e_backend.database.dto.LogWorkoutDTO;
import com.svenson95.track_e_backend.database.service.LogWorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs-workout")
public class LogWorkoutController {

  @Autowired private LogWorkoutService logWorkoutService;

  @GetMapping("/get/{date}/{userId}")
  public ResponseEntity<?> getLogWorkouts(@PathVariable Long date, @PathVariable String userId) {
    return logWorkoutService.findLogWorkout(date, userId);
  }

  @GetMapping("/get/latest-log/{exercise}/{userId}")
  public ResponseEntity<LogWorkoutDTO> getLatestLogForExercise(
      @PathVariable String exercise, @PathVariable String userId) {
    return logWorkoutService
        .findLatestLogForExercise(exercise, userId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.noContent().build());
  }

  @PostMapping("/add/set/{date}/{userId}")
  public LogWorkoutDTO addSetToLog(
      @PathVariable Long date,
      @PathVariable String userId,
      @RequestBody LogWorkoutDTO.SetItemDTO setDto) {
    return logWorkoutService.updateOrCreateLog(date, setDto, userId);
  }

  @PutMapping("/update/{logId}/{setIndex}")
  public LogWorkoutDTO updateSetInLog(
      @PathVariable String logId,
      @PathVariable String setIndex,
      @RequestBody LogWorkoutDTO.SetItemDTO setDto) {
    return logWorkoutService.updateSetInLog(logId, setIndex, setDto);
  }

  @DeleteMapping("/delete/{logId}/{itemId}")
  public ResponseEntity<?> deleteSetInLog(@PathVariable String logId, @PathVariable String itemId) {
    return logWorkoutService.deleteSetInLog(logId, itemId);
  }
}
