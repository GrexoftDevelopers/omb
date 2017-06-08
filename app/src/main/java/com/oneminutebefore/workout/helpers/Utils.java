package com.oneminutebefore.workout.helpers;

import android.content.Context;
import android.content.Intent;

import com.oneminutebefore.workout.MainActivity;
import com.oneminutebefore.workout.WorkoutApplication;
import com.oneminutebefore.workout.models.WorkoutExercise;

/**
 * Created by tahir on 13/5/17.
 */

public class Utils {






    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void clearUserData(Context context){
        SharedPrefsUtil.deletePreference(context, Keys.KEY_TOKEN);
        SharedPrefsUtil.deletePreference(context, Keys.KEY_USER_ID);
        SharedPrefsUtil.deletePreference(context, Keys.getUserLevelKey(context));
        String hourPrefKeys[] = Keys.getHourSelectionKeys(context);
        for(String key : hourPrefKeys){
            SharedPrefsUtil.deletePreference(context, key);
        }
        String videoPrefKeys[] = Keys.getWorkoutSelectionKeys(context);
        for(String key : videoPrefKeys){
            SharedPrefsUtil.deletePreference(context, key);
        }
        WorkoutApplication application = WorkoutApplication.getmInstance();
        application.clearUserData();
        application.getDbHelper().clearData();
    }


    public static String getTimeKey(String timeMeridian){
        String time = timeMeridian.split(" ")[0];
        String meridian = timeMeridian.split(" ")[1];
        int hour = Integer.parseInt(time.split(":")[0]);
        if(meridian.equals("P.M")){
            hour += 12;
        }
        return hour + "_59";
    }

    public static Intent getSessionTimeoutIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }



}
