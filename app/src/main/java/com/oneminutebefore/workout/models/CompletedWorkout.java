package com.oneminutebefore.workout.models;

/**
 * Created by tahir on 31/5/17.
 */

public class CompletedWorkout extends SelectedWorkout {

    private int repsCount;

    private long date;

    public CompletedWorkout(String id, String name, String description, String videoLink) {
        super(id, name, description, videoLink);
    }

    public CompletedWorkout(WorkoutExercise workoutExercise, String timeKey, int repsCount) {
        super(workoutExercise, timeKey);
        this.repsCount = repsCount;
    }

    public CompletedWorkout(SelectedWorkout selectedWorkout, int repsCount, long date) {
        super(selectedWorkout, selectedWorkout.getTimeKey());
        this.repsCount = repsCount;
        this.date = date;
    }

    public int getRepsCount() {
        return repsCount;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDate() {
        return date;
    }

}
