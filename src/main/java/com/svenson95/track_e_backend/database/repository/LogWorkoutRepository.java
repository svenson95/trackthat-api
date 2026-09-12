package com.svenson95.track_e_backend.database.repository;

import com.svenson95.track_e_backend.database.model.LogWorkout;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogWorkoutRepository extends MongoRepository<LogWorkout, String> {

  Optional<LogWorkout> findByDate(String date);

  List<LogWorkout> findAllByUserId(String userId);

  Optional<LogWorkout> findFirstByUserIdAndDateBetweenOrderByDateDesc(
      String userId, long start, long end);

  List<LogWorkout> findByUserIdAndSetsExerciseAndDateLessThanOrderByDateDesc(
      String userId, String exercise, long before, Pageable pageable);

  Optional<LogWorkout> findByLogId(Long logId);

  Optional<LogWorkout> findTopByUserIdOrderByLogIdDesc(String userId);
}
