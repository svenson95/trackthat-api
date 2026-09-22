package com.svenson95.trackthat_api.database.dto;

import java.util.List;

public record ExerciseWorkoutHistoryDTO(List<LogWorkoutDTO> workouts, boolean hasMore) {
}
