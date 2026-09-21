package com.svenson95.track_e_backend.database.service;

import com.svenson95.track_e_backend.database.dto.ExerciseWorkoutHistoryDTO;
import com.svenson95.track_e_backend.database.dto.LogWorkoutDTO;
import com.svenson95.track_e_backend.database.mapper.LogWorkoutMapper;
import com.svenson95.track_e_backend.database.model.LogWorkout;
import com.svenson95.track_e_backend.database.repository.LogWorkoutRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LogWorkoutService {

  private final LogWorkoutRepository logWorkoutRepository;
  private final LogWorkoutMapper logWorkoutMapper;

  private static final ZoneId USER_ZONE = ZoneId.of("Europe/Berlin");
  private static final Duration WORKOUT_DURATION = Duration.ofHours(6);

  private static final int MAX_HISTORY_LIMIT = 10;

  public LogWorkoutService(
      LogWorkoutRepository logWorkoutRepository, LogWorkoutMapper logWorkoutMapper) {
    this.logWorkoutRepository = logWorkoutRepository;
    this.logWorkoutMapper = logWorkoutMapper;
  }

  public ResponseEntity<LogWorkoutDTO> findLatestWorkoutByDate(Long date, String userId) {
    Instant setTime = Instant.ofEpochSecond(date);
    Instant earliestPossibleWorkoutStart = setTime.minus(WORKOUT_DURATION);

    long start = earliestPossibleWorkoutStart.getEpochSecond();
    long end = setTime.getEpochSecond();

    return logWorkoutRepository
        .findFirstByUserIdAndDateBetweenOrderByDateDesc(userId, start, end)
        .filter(log -> belongsToSameWorkout(log.getDate(), date))
        .map(logWorkoutMapper::toDto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.noContent().build());
  }

  public ResponseEntity<List<LogWorkoutDTO>> findLogWorkoutsForUser(String userId) {
    List<LogWorkoutDTO> logs =
        logWorkoutRepository.findAllByUserId(userId).stream().map(logWorkoutMapper::toDto).toList();

    return logs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(logs);
  }

  public ExerciseWorkoutHistoryDTO findWorkoutHistoryForExercise(
      String exercise, String userId, Long before, int limit) {

    int normalizedLimit = Math.max(1, Math.min(limit, MAX_HISTORY_LIMIT));

    long beforeExclusive =
        before != null ? before : Instant.now().minus(WORKOUT_DURATION).getEpochSecond() + 1;

    List<LogWorkout> logs =
        logWorkoutRepository.findByUserIdAndSetsExerciseAndDateLessThanOrderByDateDesc(
            userId, exercise, beforeExclusive, PageRequest.of(0, normalizedLimit + 1));

    boolean hasMore = logs.size() > normalizedLimit;

    List<LogWorkoutDTO> workouts =
        logs.stream()
            .limit(normalizedLimit)
            .map(log -> toExerciseWorkoutDto(log, exercise))
            .toList();

    return new ExerciseWorkoutHistoryDTO(workouts, hasMore);
  }

  private LogWorkoutDTO toExerciseWorkoutDto(LogWorkout log, String exercise) {
    LogWorkoutDTO dto = logWorkoutMapper.toDto(log);

    List<LogWorkoutDTO.SetItemDTO> sets =
        Optional.ofNullable(log.getSets()).orElseGet(List::of).stream()
            .filter(set -> exercise.equals(set.getExercise()))
            .map(logWorkoutMapper::toDto)
            .toList();

    dto.setSets(sets);

    return dto;
  }

  public LogWorkoutDTO updateOrCreateLog(
      Long setDate, LogWorkoutDTO.SetItemDTO setDto, String userId) {

    LogWorkout log =
        logWorkoutRepository.findAll().stream()
            .filter(existingLog -> userId.equals(existingLog.getUserId()))
            .filter(existingLog -> belongsToSameWorkout(existingLog.getDate(), setDate))
            .findFirst()
            .orElseGet(
                () -> new LogWorkout(userId, createLogId(userId), setDate, new ArrayList<>()));

    if (log.getSets() == null) {
      log.setSets(new ArrayList<>());
    }

    log.getSets().add(logWorkoutMapper.toEntity(setDto));
    log.normalizeSetIds();
    LogWorkout saved = logWorkoutRepository.save(log);

    return logWorkoutMapper.toDto(saved);
  }

  private Long createLogId(String userId) {
    return logWorkoutRepository
        .findTopByUserIdOrderByLogIdDesc(userId)
        .map(LogWorkout::getLogId)
        .map(id -> id + 1)
        .orElse(1L);
  }

  public ResponseEntity<LogWorkoutDTO> updateSetsInLog(
      String logId, List<LogWorkoutDTO.SetItemDTO> setDtos) {

    LogWorkout log =
        logWorkoutRepository
            .findByLogId(Long.valueOf(logId))
            .orElseThrow(() -> new RuntimeException("Log not found: " + logId));

    if (setDtos == null || setDtos.isEmpty()) {
      logWorkoutRepository.delete(log);

      return ResponseEntity.noContent().build();
    }

    log.setSets(
        setDtos.stream()
            .map(logWorkoutMapper::toEntity)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));

    log.normalizeSetIds();

    LogWorkout saved = logWorkoutRepository.save(log);

    return ResponseEntity.ok(logWorkoutMapper.toDto(saved));
  }

  private boolean belongsToSameWorkout(Long logDate, Long targetDate) {
    LocalDate logLocalDate = Instant.ofEpochSecond(logDate).atZone(USER_ZONE).toLocalDate();
    LocalDate targetLocalDate = Instant.ofEpochSecond(targetDate).atZone(USER_ZONE).toLocalDate();
    return logLocalDate.equals(targetLocalDate);
  }
}
