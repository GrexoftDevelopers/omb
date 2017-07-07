package com.oneminutebefore.workout.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tahir on 31/5/17.
 */

public class SelectedWorkout extends WorkoutExercise {

    private String selectedWorkoutId;
    private long updated;
    private long createdAt;
    private long updatedAt;
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
//            String timeUnit = Integer.valueOf(timeParts[0]) / 12 == 0 ? " am" : " pm";
//            return hour + ":" + timeParts[1] + timeUnit;
//        }
        return timeKey.replace('_',':');
    }

    public String getTimeMeridian(){
        return getTimeMeridian(getTimeKey());
//        int workoutHour = Integer.parseInt(getTimeKey().split("_")[0]);
//        String meridian = workoutHour / 12 == 0 ? "A.M" : "P.M";
//        workoutHour = workoutHour % 12;
//        String workoutTime = workoutHour + ":59 " + meridian;
//        return workoutTime;
    }

    public long getUpdated() {
        return updated;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }

    public static long getDateTimeLong(String dateString){

        dateString = dateString.substring(0, dateString.length()-1).replace("T"," ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            return dateFormat.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getFormattedDateTime(long millis){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS_").format(new Date(millis)).replace(" ","T").replace("_","Z");
    }

    public static String getTimeMeridian(String timeKey){
        int workoutHour = Integer.parseInt(timeKey.split("_")[0]);
        String meridian = workoutHour / 12 == 0 ? "A.M" : "P.M";
        workoutHour = workoutHour % 12;
        if(workoutHour == 0){
            workoutHour = 12;
        }
        String workoutTime = workoutHour + ":59 " + meridian;
        return workoutTime;
    }

    public static String getTimeKey(String timeMeridian){
        int hour = Integer.parseInt(timeMeridian.split(":")[0]);
        if(hour == 12){
            hour = 0;
        }
        String meridian = timeMeridian.split(" ")[1];
        if(meridian.equals("A.M")){
            hour += 12;
        }
        return hour + "_59";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SelectedWorkout){
            return id.equals(((SelectedWorkout) obj).getId()) && timeKey.equals(((SelectedWorkout) obj).getTimeKey());
        }
        return super.equals(obj);
    }
}
