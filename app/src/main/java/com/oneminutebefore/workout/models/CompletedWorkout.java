package com.oneminutebefore.workout.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by tahir on 31/5/17.
 */

public class CompletedWorkout extends SelectedWorkout {

    private int repsCount;

    private long date;

    private ArrayList<UserTrack> userTracks;

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

    public CompletedWorkout(SelectedWorkout selectedWorkout) {
        super(selectedWorkout, selectedWorkout.getTimeKey());
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

    public static long getDateLong(String dateString){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<UserTrack> getUserTracks() {
        return userTracks;
    }

    public static ArrayList<CompletedWorkout> createListFromJson(JSONArray jsonArray){

        if(jsonArray != null && jsonArray.length() > 0){
            ArrayList<CompletedWorkout> completedWorkouts = new ArrayList<>();
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if(jsonObject != null){
                    try {
                        JSONObject workoutObject = jsonObject.optJSONObject("workout");
                        workoutObject.put("brand", jsonObject.optJSONObject("category"));
                        WorkoutExercise workoutExercise = WorkoutExercise.createFromJson(workoutObject);
                        String id = jsonObject.optString("_id");
                        CompletedWorkout completedWorkout = new CompletedWorkout(new SelectedWorkout(workoutExercise,"00_00",id));
                        completedWorkout.userTracks = UserTrack.createListFromJson(jsonObject.getJSONArray("user_track"));
                        completedWorkouts.add(completedWorkout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return completedWorkouts;
        }

        return null;
    }

}
