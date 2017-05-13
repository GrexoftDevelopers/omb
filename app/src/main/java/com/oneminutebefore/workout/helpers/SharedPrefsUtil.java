package com.oneminutebefore.workout.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;


public class SharedPrefsUtil {

    public static String getStringPreference(Context context, String key) {
        return getStringPreference(context, key, null);
    }

    public static String getStringPreference(Context context, String key, String defaultValue){
        String value = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(key, defaultValue);
        }
        return value;
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue){
        boolean value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getBoolean(key, defaultValue);
        }
        return value;
    }

    public static boolean setStringPreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean deletePreference(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(key);
            return editor.commit();
        }
        return false;
    }

    public static boolean hasPreference(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            if(preferences.contains(key))
            return true;
        }
        return false;
    }
}



