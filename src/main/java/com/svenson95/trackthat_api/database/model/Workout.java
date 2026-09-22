package com.svenson95.trackthat_api.database.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.svenson95.trackthat_api.database.dto.WorkoutDTO.ListItemDTO;

@Document(collection = "workouts")
public class Workout {
  @Id
  private String _id;

  private String userId; // MongoDB doc id
  private Long workoutId;
  private Long listId;
  private Long lastUpdated; // unix timestamp
  private String name;
  private List<ListItem> list;

  public Workout() {
  }

  public Workout(
      String userId,
      Long workoutId,
      Long listId,
      Long lastUpdated,
      String name,
      List<ListItem> list) {
    this.userId = userId;
    this.workoutId = workoutId;
    this.listId = listId;
    this.lastUpdated = lastUpdated;
    this.name = name;
    this.list = list;
  }

  public String getId() {
    return _id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Long getWorkoutId() {
    return workoutId;
  }

  public void setWorkoutId(Long workoutId) {
    this.workoutId = workoutId;
  }

  public Long getListId() {
    return listId;
  }

  public void setListId(Long listId) {
    this.listId = listId;
  }

  public Long getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Long lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ListItem> getList() {
    return list;
  }

  public void setList(List<ListItem> list) {
    this.list = list;
  }

  public static class ListItem {
    private String name;
    private ListItemType type;
    private Long itemId;
    private Long listId;

    public ListItem() {
    }

    public ListItem(String name, ListItemType type, Long itemId, Long listId) {
      this.name = name;
      this.type = type;
      this.itemId = itemId;
      this.listId = listId;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public ListItemType getType() {
      return type;
    }

    public void setType(ListItemType type) {
      this.type = type;
    }

    public Long getItemId() {
      return itemId;
    }

    public void setItemId(Long itemId) {
      this.itemId = itemId;
    }

    public Long getListId() {
      return listId;
    }

    public void setListId(Long listId) {
      this.listId = listId;
    }

    public static ListItem fromDto(ListItemDTO dto) {

      ListItem item = new ListItem();

      item.setName(dto.getName());
      item.setType(Workout.ListItemType.valueOf(dto.getType().name()));
      item.setItemId(dto.getItemId());
      item.setListId(dto.getListId());

      return item;
    }
  }

  public static class ListItemExercise extends ListItem {
    private ExerciseEquipment equipment;
    private ExerciseVariant variant;
    private String sets;
    private String reps;
    private String rest;

    public ListItemExercise() {
    }

    public ListItemExercise(
        String name,
        ListItemType type,
        Long listId,
        Long itemId,
        ExerciseEquipment equipment,
        ExerciseVariant variant,
        String sets,
        String reps,
        String rest) {
      super(name, type, listId, itemId);
      this.equipment = equipment;
      this.variant = variant;
      this.sets = sets;
      this.reps = reps;
      this.rest = rest;
    }

    public ExerciseEquipment getEquipment() {
      return equipment;
    }

    public void setEquipment(ExerciseEquipment equipment) {
      this.equipment = equipment;
    }

    public ExerciseVariant getVariant() {
      return variant;
    }

    public void setVariant(ExerciseVariant variant) {
      this.variant = variant;
    }

    public String getSets() {
      return sets;
    }

    public void setSets(String sets) {
      this.sets = sets;
    }

    public String getReps() {
      return reps;
    }

    public void setReps(String reps) {
      this.reps = reps;
    }

    public String getRest() {
      return rest;
    }

    public void setRest(String rest) {
      this.rest = rest;
    }
  }

  public enum ListItemType {
    HEADER,
    EXERCISE,
    LABEL,
    SPACER
  }

  public enum ExerciseEquipment {
    dumbbell,
    barbell,
    cable_tower,
    machine,
    bodyweight
  }

  public enum ExerciseVariant {
    flat,
    decline,
    incline,
    normal,
    stiff_leg,
    standing,
    seated,
    hammer,
    concentration,
    wide,
    close,
    one_arm,
    two_arm,
    bent_over
  }

  public enum MuscleGroup {
    calves,
    adductors,
    abductors,
    hamstrings,
    quads,
    glutes,
    forearms,
    triceps,
    biceps,
    lats,
    abs,
    core,
    chest,
    traps,
    shoulders,
    neck
  }
}
