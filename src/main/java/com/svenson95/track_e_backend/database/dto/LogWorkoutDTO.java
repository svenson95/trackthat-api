package com.svenson95.track_e_backend.database.dto;

import java.util.List;

public class LogWorkoutDTO {
  private String id; // MongoDB doc id
  private String userId;
  private Long logId;
  private Long date; // unix timestamp

  private List<SetItemDTO> sets;

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

  public Long getLogId() {
    return logId;
  }

  public void setLogId(Long logId) {
    this.logId = logId;
  }

  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  public List<SetItemDTO> getSets() {
    return sets;
  }

  public void setSets(List<SetItemDTO> sets) {
    this.sets = sets;
  }

  public static class UpdateSetsDTO {
    private List<SetItemDTO> sets;

    public List<SetItemDTO> getSets() {
      return sets;
    }

    public void setSets(List<SetItemDTO> sets) {
      this.sets = sets;
    }
  }

  public static class SetItemDTO {
    private String exercise;
    private Double load;
    private Long reps;
    private Long itemId;
    private String time;
    private String note;

    public String getExercise() {
      return exercise;
    }

    public void setExercise(String exercise) {
      this.exercise = exercise;
    }

    public Double getLoad() {
      return load;
    }

    public void setLoad(Double load) {
      this.load = load;
    }

    public Long getReps() {
      return reps;
    }

    public void setReps(Long reps) {
      this.reps = reps;
    }

    public Long getItemId() {
      return itemId;
    }

    public void setItemId(Long itemId) {
      this.itemId = itemId;
    }

    public String getTime() {
      return time;
    }

    public void setTime(String time) {
      this.time = time;
    }

    public String getNote() {
      return note;
    }

    public void setNote(String note) {
      this.note = note;
    }
  }
}
