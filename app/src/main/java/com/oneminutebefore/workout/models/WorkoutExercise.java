package com.oneminutebefore.workout.models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by tahir on 20/5/17.
 */

public class WorkoutExercise implements Serializable{

    private String id;
    private String name;
    private String description;
    private String videoLink;
    private int repsCount;
    private int time;
    private int v;
    private boolean orders;
    private WorkoutCategory category;
    private String categoryId;

    public WorkoutExercise(String id, String name, String description, String videoLink, int repsCount, int time) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.videoLink = videoLink;
        this.repsCount = repsCount;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public int getRepsCount() {
        return repsCount;
    }

    public void setRepsCount(int repsCount) {
        this.repsCount = repsCount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public WorkoutCategory getCategory() {
        return category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public static HashMap<String, WorkoutExercise> createMapFromJson(String data){

        if(!TextUtils.isEmpty(data)){
            try{
                JSONArray dataArray = new JSONArray(data);
                if(dataArray != null && dataArray.length() > 0){
                    HashMap<String, WorkoutExercise> map = new HashMap<>();
                    WorkoutExercise workoutExercise;
                    for(int i = 0 ; i < dataArray.length() ; i++){
                        JSONObject jsonObject = dataArray.getJSONObject(i);
                        String id = jsonObject.optString("_id");
                        String name = jsonObject.optString("name");
//                        String link = jsonObject.optString("info");
//                        String link = "http://www.youtu.be/s7CNC9irjt0";
                        String link = "https://youtu.be/pOLppIAhgr0";
                        boolean orders = jsonObject.optBoolean("orders",true);
                        int v = jsonObject.optInt("__v",0);
                        workoutExercise = new WorkoutExercise(id,name,null,link,0,0);
                        workoutExercise.orders = orders;
                        workoutExercise.v = v;
                        workoutExercise.categoryId = jsonObject.optString("brand_id");
                        workoutExercise.category = WorkoutCategory.createFromJson(jsonObject.optJSONObject("brand"));
                        map.put(id, workoutExercise);
                    }
                    return map;
                }
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }
}
