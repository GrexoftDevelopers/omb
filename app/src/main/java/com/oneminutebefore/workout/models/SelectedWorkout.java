package com.oneminutebefore.workout.models;

/**
 * Created by tahir on 31/5/17.
 */

public class SelectedWorkout extends WorkoutExercise {

    private String selectedWorkoutId;
    private String updated;
    private String createdAt;
    private String updatedAt;
    private String uid;

    private String timeKey;

    public SelectedWorkout(){}

    public SelectedWorkout(String id, String name, String description, String videoLink) {
        super(id, name, description, videoLink);
    }

    public SelectedWorkout(WorkoutExercise workoutExercise, String timeKey){
        super(workoutExercise.id,workoutExercise.name,workoutExercise.description,workoutExercise.videoLink);
        this.category = workoutExercise.category;
        this.categoryId = workoutExercise.categoryId;
        this.orders = workoutExercise.orders;
        this.v = workoutExercise.v;
        this.timeKey = timeKey;
    }

    public SelectedWorkout(WorkoutExercise workoutExercise, String timeKey, String selectedWorkoutId){
        super(workoutExercise.id,workoutExercise.name,workoutExercise.description,workoutExercise.videoLink);
        this.category = workoutExercise.category;
        this.categoryId = workoutExercise.categoryId;
        this.orders = workoutExercise.orders;
        this.v = workoutExercise.v;
        this.timeKey = timeKey;
        this.selectedWorkoutId = selectedWorkoutId;
    }

    public String getSelectedWorkoutId() {
        return selectedWorkoutId;
    }

    public void setSelectedWorkoutId(String selectedWorkoutId) {
        this.selectedWorkoutId = selectedWorkoutId;
    }

    public String getTimeKey() {
        return timeKey;
    }

    public String getTime() {
//        String timeParts[] = timeKey.split("_");
//        if(timeParts.length > 1){
//            int hour = Integer.valueOf(timeParts[0]) % 12;
//            String timeUnit = Integer.valueOf(timeParts[0]) / 12 == 0 ? " A.M" : " P.M";
//            return hour + ":" + timeParts[1] + timeUnit;
//        }
        return timeKey.replace('_',':');
    }

    public String getTimeMeridian(){
        int workoutHour = Integer.parseInt(getTimeKey().split("_")[0]);
        String meridian = workoutHour / 12 == 0 ? "A.M" : "P.M";
        workoutHour = workoutHour % 12;
        String workoutTime = workoutHour + ":59 " + meridian;
        return workoutTime;
    }

    public String getUpdated() {
        return updated;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }
}
