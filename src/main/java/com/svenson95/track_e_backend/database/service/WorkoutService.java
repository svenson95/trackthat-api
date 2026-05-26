package com.svenson95.track_e_backend.database.service;

import com.svenson95.track_e_backend.database.dto.WorkoutDTO;
import com.svenson95.track_e_backend.database.mapper.WorkoutMapper;
import com.svenson95.track_e_backend.database.model.Workout;
import com.svenson95.track_e_backend.database.model.Workout.ListItem;
import com.svenson95.track_e_backend.database.repository.WorkoutRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkoutService {

  private final WorkoutRepository workoutRepository;
  private final WorkoutMapper workoutMapper;

  public WorkoutService(WorkoutRepository workoutRepository, WorkoutMapper workoutMapper) {
    this.workoutRepository = workoutRepository;
    this.workoutMapper = workoutMapper;
  }

  public List<WorkoutDTO> findUserWorkouts(String userId) {
    return workoutRepository.findByUserId(userId).orElse(List.of()).stream()
        .map(workoutMapper::toDto)
        .toList();
  }

  public WorkoutDTO createWorkout(WorkoutDTO dto) {
    if (workoutRepository.existsByUserIdAndName(dto.getUserId(), dto.getName())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Workout already exists for this user");
    }

    Workout workout = workoutMapper.toEntity(dto);
    Workout saved = workoutRepository.save(workout);

    return workoutMapper.toDto(saved);
  }

  public WorkoutDTO changeWorkoutName(WorkoutDTO dto) {
    Workout workout =
        workoutRepository
            .findByWorkoutId(dto.getWorkoutId())
            .orElseThrow(() -> new RuntimeException("Workout not found"));

    if (workoutRepository.existsByUserIdAndName(dto.getUserId(), dto.getName())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Workout already exists for this user");
    }

    workout.setName(dto.getName());
    Workout updatedWorkout = workoutRepository.save(workout);
    return workoutMapper.toDto(updatedWorkout);
  }

  public WorkoutDTO changeWorkoutList(WorkoutDTO dto) {
    Workout workout =
        workoutRepository
            .findByWorkoutId(dto.getWorkoutId())
            .orElseThrow(() -> new RuntimeException("Workout not found"));
    Workout newList = workoutMapper.toEntity(dto);
    workout.setList(newList.getList());
    Workout updatedWorkout = workoutRepository.save(workout);
    return workoutMapper.toDto(updatedWorkout);
  }

  public List<WorkoutDTO> updateAllWorkouts(String userId, List<WorkoutDTO> workoutsDto) {

    List<Workout> existingWorkouts =
        workoutRepository
            .findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Workout not found"));
    List<Workout> updatedWorkouts = new ArrayList<>();

    for (int i = 0; i < existingWorkouts.size(); i++) {
      Workout existingWorkout = existingWorkouts.get(i);
      WorkoutDTO dto = workoutsDto.get(i);

      existingWorkout.setListId(dto.getListId());
      existingWorkout.setName(dto.getName());

      List<ListItem> items = dto.getList().stream().map(ListItem::fromDto).toList();

      existingWorkout.setList(items);
      updatedWorkouts.add(existingWorkout);
    }

    List<Workout> savedWorkouts = workoutRepository.saveAll(updatedWorkouts);

    return workoutMapper.toDtoList(savedWorkouts);
  }

  public boolean deleteById(String id) {
    return workoutRepository
        .findById(id)
        .map(
            workout -> {
              workoutRepository.deleteById(id);
              return true;
            })
        .orElse(false);
  }
}
