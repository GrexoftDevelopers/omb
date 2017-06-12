package com.oneminutebefore.workout.models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.util.HashMap;

/**
 * Created by tahir on 23/5/17.
 */

public class WorkoutCategory implements Serializable{


    private String id;
    private String name;
    private String slug;
    private int v;
    private HashMap<String, WorkoutExercise> workouts;
    private String updateTime;
    private boolean isActive;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public HashMap<String, WorkoutExercise> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(HashMap<String, WorkoutExercise> workouts) {
        this.workouts = workouts;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public static HashMap<String, WorkoutCategory> createMapFromJson(String data) {

        if (!TextUtils.isEmpty(data)) {
            try {
                JSONArray dataArray = new JSONArray(data);
                if (dataArray != null && dataArray.length() > 0) {
                    HashMap<String, WorkoutCategory> map = new HashMap<>();
                    WorkoutCategory workoutCategory;
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject jsonObject = dataArray.getJSONObject(i);
                        workoutCategory = createFromJson(jsonObject);
                        map.put(workoutCategory.id, workoutCategory);
                    }
                    return map;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    public static WorkoutCategory createFromJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            WorkoutCategory workoutCategory = new WorkoutCategory();
            workoutCategory.id = jsonObject.optString("_id");
            workoutCategory.name = jsonObject.optString("name");
            workoutCategory.slug = jsonObject.optString("slug");
            workoutCategory.updateTime = jsonObject.optString("updated");
            workoutCategory.isActive = jsonObject.optBoolean("active", true);
            workoutCategory.v = jsonObject.optInt("__v", 0);
//                        JSONArray workoutsArray = jsonObject.optJSONArray("products");
//                        if(workoutsArray != null && workoutsArray.length() > 0){
//
//                            for(int j = 0 ; j < workoutsArray.length() ; j++){
//
//                            }
//                        }
            return workoutCategory;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof WorkoutCategory)
        return id.equals(((WorkoutCategory) obj).getId());
        return false;
    }
}
