package com.oneminutebefore.workout;

import android.app.Application;

/**
 * Created by tahir on 13/5/17.
 */

public class WorkoutApplication extends Application {

    private String userId;

    private static WorkoutApplication mInstance;

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
}
