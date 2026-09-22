package com.svenson95.trackthat_api.database.dto;

import java.util.List;

public class WorkoutDTO {
  private String id; // MongoDB doc id
  private String userId;
  private Long workoutId;
  private Long listId;
  private Long lastUpdated; // unix timestamp
  private String name;
  private List<ListItemDTO> list;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public List<ListItemDTO> getList() {
    return list;
  }

  public void setList(List<ListItemDTO> list) {
    this.list = list;
  }

  public static class ListItemDTO {
    private String name; // nullable
    private ListItemType type;
    private Long itemId;
    private Long listId;

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
  }

  public static class ListItemExerciseDTO extends ListItemDTO {
    private ExerciseEquipment equipment;
    private ExerciseVariant variant; // nullable
    private String sets; // nullable
    private String reps; // nullable
    private String rest; // nullable

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

  // ---------- Enums ----------
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
    // benchpress
    flat,
    decline,
    incline,

    // legs
    normal,
    stiff_leg,

    // calves
    standing,
    seated,

    // biceps
    hammer,
    concentration,

    // back
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
    front_delta,
    middle_delta,
    rear_delta,
    traps,
    neck
  }
}
