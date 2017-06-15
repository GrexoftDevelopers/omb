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

    protected String id;
    protected String name;
    protected String description;
    protected String videoLink;
    protected int v;
    protected boolean orders;
    protected WorkoutCategory category;
    protected String categoryId;

    public WorkoutExercise(String id, String name, String description, String videoLink) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.videoLink = videoLink;
    }

    public WorkoutExercise(){}

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
                        workoutExercise = createFromJson(jsonObject);
                        map.put(workoutExercise.id, workoutExercise);
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

    public static WorkoutExercise createFromJson(JSONObject jsonObject){
        String id = jsonObject.optString("_id");
        String name = jsonObject.optString("name");
        String link = jsonObject.optString("info");
        boolean orders = jsonObject.optBoolean("orders",true);
        int v = jsonObject.optInt("__v",0);
        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise = new WorkoutExercise(id,name,null,link);
        workoutExercise.orders = orders;
        workoutExercise.v = v;
        workoutExercise.categoryId = jsonObject.optString("brand_id");
        workoutExercise.category = WorkoutCategory.createFromJson(jsonObject.optJSONObject("brand"));
        return workoutExercise;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((WorkoutExercise)obj).getId());
    }
}
