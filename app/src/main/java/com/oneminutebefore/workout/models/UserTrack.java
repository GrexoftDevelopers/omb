package com.oneminutebefore.workout.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by husain707 on 15/06/17.
 */

public class UserTrack {

    private String id;
    private int reps;
    private String date;

    public int getReps() {
        return reps;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public static ArrayList<UserTrack> createListFromJson(JSONArray jsonArray){

        if(jsonArray != null && jsonArray.length() > 0){
            ArrayList<UserTrack> tracks = new ArrayList<>();
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject trackJson = jsonArray.optJSONObject(i);
                if(trackJson != null){
                    UserTrack userTrack = new UserTrack();
                    userTrack.id = trackJson.optString("_id");
                    userTrack.reps = trackJson.optInt("rep");
                    userTrack.date = trackJson.optString("date");
                    tracks.add(userTrack);
                }
            }
            return tracks;
        }

        return null;
    }

}
