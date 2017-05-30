package com.oneminutebefore.workout.models;

/**
 * Created by tahir on 31/5/17.
 */

public class CompletedWorkout extends SelectedWorkout {

    private int repsCount;

    public CompletedWorkout(String id, String name, String description, String videoLink) {
        super(id, name, description, videoLink);
    }

    public CompletedWorkout(WorkoutExercise workoutExercise, String timeKey, int repsCount) {
        super(workoutExercise, timeKey);
        this.repsCount = repsCount;
    }

    public int getRepsCount() {
        return repsCount;
    }
}
