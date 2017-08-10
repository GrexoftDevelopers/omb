package com.oneminutebefore.workout.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.oneminutebefore.workout.R;
import com.oneminutebefore.workout.VideoPlayerActivity;
import com.oneminutebefore.workout.WorkoutApplication;
import com.oneminutebefore.workout.helpers.DBHelper;
import com.oneminutebefore.workout.helpers.IntentUtils;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.WorkoutExercise;

import java.util.Calendar;
import java.util.HashMap;


public class WorkoutNotificationService extends IntentService {

    private static final String NAME = "workout notification";

    public static final int REQUEST_CODE = 1001;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WorkoutNotificationService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("workout notification check");
        try {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek >= 2 && dayOfWeek <= 6){
                checkHourSelection();
            }
            IntentUtils.scheduleWorkoutNotifications(getApplicationContext());
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    private void checkHourSelection() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String hourSelectionKey = Keys.getHourSelectionKeys(getApplicationContext())[hour];
        boolean isHourSelected = SharedPrefsUtil.getBooleanPreference(getApplicationContext(),hourSelectionKey,false);

        if(isHourSelected){

            String workoutsJson = SharedPrefsUtil.getStringPreference(getApplicationContext(), Keys.KEY_VIDEOS_INFO, "[]");
            HashMap<String, WorkoutExercise> workouts = WorkoutExercise.createMapFromJson(workoutsJson);

            if(workouts != null && !workouts.isEmpty()){
                String workoutId = SharedPrefsUtil.getStringPreference(getApplicationContext(), Keys.getWorkoutSelectionKeys(getApplicationContext())[hour], "");
                WorkoutExercise workoutExercise = workouts.get(workoutId);
                if(workoutExercise != null){
                    WorkoutApplication application = WorkoutApplication.getmInstance();
                    if(application.getDbHelper() == null){
                        application.setDbHelper(new DBHelper(getApplicationContext()));
                    }
                    String timeMeridian = SelectedWorkout.getTimeMeridian(hour + "_59");
//                    String selectedWorkoutId = application.getDbHelper().getSelectedWorkoutIdByTime(hour % 12 + ":59" + (hour/12==0?" am":" pm"));
                    String selectedWorkoutId = application.getDbHelper().getSelectedWorkoutIdByTime(timeMeridian);
                    addNotification(new SelectedWorkout(workoutExercise,hour + "_59",selectedWorkoutId));
                }
            }

//            String workoutLink = SharedPrefsUtil.getStringPreference(getApplicationContext(), Keys.KEY_VIDEO_LINKS[hour], "");
//
//            if(!TextUtils.isEmpty(workoutLink)){
//                addNotification(workoutLink);
//            }

        }

    }

    private void addNotification(SelectedWorkout workoutExercise) {

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setTicker("OMB")
                        .setAutoCancel(true)
                        .setContentTitle(getString(R.string.one_minute_workout))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentText(workoutExercise.getName())
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

        Intent intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.KEY_WORKOUT, workoutExercise);
        intent.putExtra(VideoPlayerActivity.KEY_TIME_MILLIS, Calendar.getInstance().getTimeInMillis());

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}
