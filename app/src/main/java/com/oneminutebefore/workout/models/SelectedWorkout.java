package com.oneminutebefore.workout.models;

/**
 * Created by tahir on 31/5/17.
 */

public class SelectedWorkout extends WorkoutExercise {





    private String timeKey;

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
}
