package com.oneminutebefore.workout.helpers;

import android.content.Context;

/**
 * Created by tahir on 13/5/17.
 */

public class Utils {






    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void clearData(Context context){
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
    }



}
