package com.svenson95.trackthat_api.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.svenson95.trackthat_api.database.model.Workout;

public interface WorkoutRepository extends MongoRepository<Workout, String> {
  Optional<List<Workout>> findByUserId(String userId);

  Optional<Workout> findByWorkoutId(Long workoutId);

  boolean existsByUserIdAndName(String userId, String name);
}
