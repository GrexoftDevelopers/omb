package com.oneminutebefore.workout;

import android.app.Application;

import com.oneminutebefore.workout.models.User;
import com.oneminutebefore.workout.models.WorkoutCategory;
import com.oneminutebefore.workout.models.WorkoutExercise;

import java.util.HashMap;

/**
 * Created by tahir on 13/5/17.
 */

public class WorkoutApplication extends Application {

    private String sessionToken;
    private String userId;

    private static WorkoutApplication mInstance;

    private HashMap<String, WorkoutExercise> workouts;

    private HashMap<String, WorkoutCategory> workoutCategories;

    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static WorkoutApplication getmInstance() {
        return mInstance;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public HashMap<String, WorkoutExercise> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(HashMap<String, WorkoutExercise> workouts) {
        this.workouts = workouts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
