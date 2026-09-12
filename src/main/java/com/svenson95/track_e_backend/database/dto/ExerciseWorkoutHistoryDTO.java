package com.svenson95.track_e_backend.database.dto;

import java.util.List;

public record ExerciseWorkoutHistoryDTO(List<LogWorkoutDTO> workouts, boolean hasMore) {}
