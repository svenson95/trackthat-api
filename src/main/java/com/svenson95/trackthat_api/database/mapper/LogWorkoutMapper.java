package com.svenson95.trackthat_api.database.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.svenson95.trackthat_api.database.dto.LogWorkoutDTO;
import com.svenson95.trackthat_api.database.model.LogWorkout;

@Mapper(componentModel = "spring")
public interface LogWorkoutMapper {

  LogWorkoutDTO toDto(LogWorkout workout);

  LogWorkout toEntity(LogWorkoutDTO dto);

  List<LogWorkoutDTO> toDtoList(List<LogWorkout> workouts);

  List<LogWorkout> toEntityList(List<LogWorkoutDTO> dtos);

  LogWorkoutDTO.SetItemDTO toDto(LogWorkout.SetItem setItem);

  LogWorkout.SetItem toEntity(LogWorkoutDTO.SetItemDTO dto);

  List<LogWorkoutDTO.SetItemDTO> toSetItemDtoList(List<LogWorkout.SetItem> sets);

  List<LogWorkout.SetItem> toSetItemEntityList(List<LogWorkoutDTO.SetItemDTO> dtos);
}
