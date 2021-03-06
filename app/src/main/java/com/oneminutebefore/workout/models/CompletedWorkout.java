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

    private boolean isCompleted;

    private int completedWorkoutId;

    public CompletedWorkout(String id, String name, String description, String videoLink) {
        super(id, name, description, videoLink);
    }

    public CompletedWorkout(WorkoutExercise workoutExercise, String timeKey, int repsCount, boolean isCompleted) {
        super(workoutExercise, timeKey);
        this.repsCount = repsCount;
        this.isCompleted = isCompleted;
    }

    public CompletedWorkout(SelectedWorkout selectedWorkout, int repsCount, long date, boolean isCompleted) {
        super(selectedWorkout, selectedWorkout.getTimeKey());
        this.setSelectedWorkoutId(selectedWorkout.getSelectedWorkoutId());
        this.repsCount = repsCount;
        this.date = date;
        this.isCompleted = isCompleted;
    }

    public CompletedWorkout(SelectedWorkout selectedWorkout) {
        super(selectedWorkout, selectedWorkout.getTimeKey());
        this.isCompleted = true;
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
                    CompletedWorkout completedWorkout = createFromJson(jsonObject);
                    completedWorkouts.add(completedWorkout);
                }
            }
            return completedWorkouts;
        }

        return null;
    }

    public static CompletedWorkout createFromJson(JSONObject jsonObject){
        try{
            JSONObject workoutObject = jsonObject.optJSONObject("workout");
            workoutObject.put("brand", jsonObject.optJSONObject("category"));
            WorkoutExercise workoutExercise = WorkoutExercise.createFromJson(workoutObject);
            String id = jsonObject.optString("_id");
            CompletedWorkout completedWorkout = new CompletedWorkout(new SelectedWorkout(workoutExercise,"00_00",id));
            completedWorkout.userTracks = UserTrack.createListFromJson(jsonObject.getJSONArray("user_track"));
            return completedWorkout;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompletedWorkoutId(int completedWorkoutId) {
        this.completedWorkoutId = completedWorkoutId;
    }

    public int getCompletedWorkoutId() {
        return completedWorkoutId;
    }
}
