package com.svenson95.track_e_backend.database.service;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LogWorkoutService {

  private final LogWorkoutRepository logWorkoutRepository;
  private final LogWorkoutMapper logWorkoutMapper;

  private static final ZoneId USER_ZONE = ZoneId.of("Europe/Berlin");
  private static final Duration WORKOUT_DURATION = Duration.ofHours(6);

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

  public ResponseEntity<List<LogWorkoutDTO>> findLogWorkoutsByDate(Long date, String userId) {
    ZoneId zone = ZoneId.of("Europe/Berlin");
    LocalDate targetDate = Instant.ofEpochSecond(date).atZone(zone).toLocalDate();

    long startOfDay = targetDate.atStartOfDay(zone).toEpochSecond();
    long endOfDay = targetDate.plusDays(1).atStartOfDay(zone).toEpochSecond() - 1;

    List<LogWorkoutDTO> logs =
        logWorkoutRepository
            .findAllByUserIdAndDateBetweenOrderByDateDesc(userId, startOfDay, endOfDay)
            .stream()
            .map(logWorkoutMapper::toDto)
            .toList();

    return logs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(logs);
  }

  public Optional<LogWorkoutDTO> findLatestLogForExercise(String exercise, String userId) {
    long latestAllowedDate = Instant.now().minus(WORKOUT_DURATION).getEpochSecond();

    return logWorkoutRepository
        .findTopByUserIdAndSetsExerciseAndDateLessThanEqualOrderByDateDesc(
            userId, exercise, latestAllowedDate)
        .map(
            log -> {
              List<LogWorkoutDTO.SetItemDTO> filteredSets =
                  log.getSets().stream()
                      .filter(set -> exercise.equals(set.getExercise()))
                      .map(logWorkoutMapper::toDto)
                      .toList();

              LogWorkoutDTO dto = logWorkoutMapper.toDto(log);
              dto.setSets(filteredSets);

              return dto;
            });
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

  public LogWorkoutDTO updateSetInLog(
      String logId, String setIndex, LogWorkoutDTO.SetItemDTO setDto) {
    LogWorkout log =
        logWorkoutRepository
            .findByLogId(Long.valueOf(logId))
            .orElseThrow(() -> new RuntimeException("Log not found: " + logId));
    int index = Integer.parseInt(setIndex);

    if (log.getSets() == null || index < 0 || index >= log.getSets().size()) {
      throw new RuntimeException("Set index out of bounds: " + setIndex);
    }

    log.getSets().set(index, logWorkoutMapper.toEntity(setDto));
    log.normalizeSetIds();

    LogWorkout saved = logWorkoutRepository.save(log);
    return logWorkoutMapper.toDto(saved);
  }

  public ResponseEntity<?> deleteSetInLog(String logId, String itemId) {
    LogWorkout log =
        logWorkoutRepository
            .findByLogId(Long.valueOf(logId))
            .orElseThrow(() -> new RuntimeException("Log not found"));
    long parsedItemId = Long.parseLong(itemId);
    boolean removed = log.getSets().removeIf(set -> set.getItemId() == parsedItemId);

    if (!removed) {
      return ResponseEntity.notFound().build();
    }

    if (log.getSets().isEmpty()) {
      logWorkoutRepository.delete(log);
      return ResponseEntity.noContent().build();
    }

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
