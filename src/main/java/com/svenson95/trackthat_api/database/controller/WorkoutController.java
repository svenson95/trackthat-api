package com.svenson95.trackthat_api.database.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.svenson95.trackthat_api.database.dto.WorkoutDTO;
import com.svenson95.trackthat_api.database.service.WorkoutService;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

  @Autowired
  private WorkoutService workoutService;

  @GetMapping("/get/{userId}")
  public List<WorkoutDTO> getWorkouts(@PathVariable String userId) {
    return workoutService.findUserWorkouts(userId);
  }

  @PostMapping("/add")
  public WorkoutDTO addWorkout(@RequestBody WorkoutDTO workout) {
    return workoutService.createWorkout(workout);
  }

  @PostMapping("/change-name")
  public WorkoutDTO changeWorkoutName(@RequestBody WorkoutDTO workout) {
    return workoutService.changeWorkoutName(workout);
  }

  @PostMapping("/change-list")
  public WorkoutDTO changeWorkoutList(@RequestBody WorkoutDTO workout) {
    return workoutService.changeWorkoutList(workout);
  }

  @PostMapping("/update-all/{userId}")
  public List<WorkoutDTO> updateAllWorkouts(
      @PathVariable String userId, @RequestBody List<WorkoutDTO> workouts) {
    return workoutService.updateAllWorkouts(userId, workouts);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteWorkout(@PathVariable String id) {
    boolean deleted = workoutService.deleteById(id);
    return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
