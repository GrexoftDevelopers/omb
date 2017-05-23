package com.oneminutebefore.workout.models;

import java.util.HashMap;

/**
 * Created by tahir on 23/5/17.
 */

public class WorkoutCategory {






    private String id;
    private String name;
    private String slug;
    private int v;
    private HashMap<String, WorkoutExercise> workouts;
    private String updateTime;
    private boolean isActive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public HashMap<String, WorkoutExercise> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(HashMap<String, WorkoutExercise> workouts) {
        this.workouts = workouts;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
