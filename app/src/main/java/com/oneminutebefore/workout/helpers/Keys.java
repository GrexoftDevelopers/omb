package com.oneminutebefore.workout.helpers;

import android.content.Context;

import com.oneminutebefore.workout.R;

/**
 * Created by tahir on 13/5/17.
 */

public class Keys{

    public static final String KEY_USER_ID = "user_id";

    public static final String KEY_LINKS_DOWNLOADED = "links_downloaded";
    public static final String KEY_CATEGORIES_DOWNLOADED = "categories_downloaded";
    public static final String KEY_VIDEOS_INFO = "videos_info";
    public static final String KEY_CATEGORIES_INFO = "categories_info";

    public static final String[] KEY_VIDEO_LINKS = new String[]{"omb_video_0","omb_video_1","omb_video_2","omb_video_3","omb_video_4","omb_video_5"
            ,"omb_video_6","omb_video_7","omb_video_8","omb_video_9","omb_video_10","omb_video_11"
            ,"omb_video_12","omb_video_13","omb_video_14","omb_video_15","omb_video_16","omb_video_17"
            ,"omb_video_18","omb_video_19","omb_video_20","omb_video_21","omb_video_22","omb_video_23"};

    public static final String KEY_TOKEN = "token";

    public static String[] getHourSelectionKeys(Context context){

        String[] array = new String[]{context.getString(R.string.key_00_59),context.getString(R.string.key_01_59),context.getString(R.string.key_02_59)
                ,context.getString(R.string.key_03_59),context.getString(R.string.key_04_59),context.getString(R.string.key_05_59)
                ,context.getString(R.string.key_06_59),context.getString(R.string.key_07_59),context.getString(R.string.key_08_59)
                ,context.getString(R.string.key_09_59),context.getString(R.string.key_10_59),context.getString(R.string.key_11_59)
                ,context.getString(R.string.key_12_59),context.getString(R.string.key_13_59),context.getString(R.string.key_14_59)
                ,context.getString(R.string.key_15_59),context.getString(R.string.key_16_59),context.getString(R.string.key_17_59)
                ,context.getString(R.string.key_18_59),context.getString(R.string.key_19_59),context.getString(R.string.key_20_59)
                ,context.getString(R.string.key_21_59),context.getString(R.string.key_22_59),context.getString(R.string.key_23_59)};

        return array;

    }

    public static String[] getWorkoutSelectionKeys(Context context){

        String[] array = new String[]{context.getString(R.string.list_key_00_59),context.getString(R.string.list_key_01_59),context.getString(R.string.list_key_02_59)
                ,context.getString(R.string.list_key_03_59),context.getString(R.string.list_key_04_59),context.getString(R.string.list_key_05_59)
                ,context.getString(R.string.list_key_06_59),context.getString(R.string.list_key_07_59),context.getString(R.string.list_key_08_59)
                ,context.getString(R.string.list_key_09_59),context.getString(R.string.list_key_10_59),context.getString(R.string.list_key_11_59)
                ,context.getString(R.string.list_key_12_59),context.getString(R.string.list_key_13_59),context.getString(R.string.list_key_14_59)
                ,context.getString(R.string.list_key_15_59),context.getString(R.string.list_key_16_59),context.getString(R.string.list_key_17_59)
                ,context.getString(R.string.list_key_18_59),context.getString(R.string.list_key_19_59),context.getString(R.string.list_key_20_59)
                ,context.getString(R.string.list_key_21_59),context.getString(R.string.list_key_22_59),context.getString(R.string.list_key_23_59)};

        return array;

    }

    public static String getUserLevelKey(Context context){
        return context.getString(R.string.key_list_fitness_level);
    }


}
