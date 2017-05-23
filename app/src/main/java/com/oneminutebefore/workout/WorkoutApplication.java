package com.oneminutebefore.workout;

import android.app.Application;

import com.oneminutebefore.workout.models.WorkoutCategory;
import com.oneminutebefore.workout.models.WorkoutExercise;

import java.util.HashMap;

/**
 * Created by tahir on 13/5/17.
 */

public class WorkoutApplication extends Application {

    private String userId;
    private String sessionToken;

    private static WorkoutApplication mInstance;

    private HashMap<String, WorkoutExercise> workouts;

    private HashMap<String, WorkoutCategory> workoutCategories;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static WorkoutApplication getmInstance() {
        return mInstance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HashMap<String, WorkoutExercise> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(HashMap<String, WorkoutExercise> workouts) {
        this.workouts = workouts;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
