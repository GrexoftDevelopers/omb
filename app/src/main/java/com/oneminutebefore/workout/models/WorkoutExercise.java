package com.oneminutebefore.workout.models;

/**
 * Created by tahir on 20/5/17.
 */

public class WorkoutExercise {





    private String id;
    private String name;
    private String description;
    private String videoLink;
    private int repsCount;
    private int time;

    public WorkoutExercise(String id, String name, String description, String videoLink, int repsCount, int time) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.videoLink = videoLink;
        this.repsCount = repsCount;
        this.time = time;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public int getRepsCount() {
        return repsCount;
    }

    public void setRepsCount(int repsCount) {
        this.repsCount = repsCount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
