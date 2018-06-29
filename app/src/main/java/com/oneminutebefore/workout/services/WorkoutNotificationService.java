package com.oneminutebefore.workout.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.oneminutebefore.workout.R;
import com.oneminutebefore.workout.VideoPlayerActivity;
import com.oneminutebefore.workout.WorkoutApplication;
import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.IntentUtils;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.models.CompletedWorkout;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.User;
import com.oneminutebefore.workout.models.WorkoutExercise;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;


public class WorkoutNotificationService extends IntentService {

    private static final String NAME = "workout notification";

    public static final int REQUEST_CODE = 1001;
    private static final String CHANNEL_OMB = "com.oneminutebefore.workout.CHANNEL_NOTIFICATION";
    private Handler handler;
    private Runnable message;
    private WorkoutApplication application;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WorkoutNotificationService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (WorkoutApplication) getApplication();
        handler = new Handler();
        message = new Runnable() {
            @Override
            public void run() {
                String url = new UrlBuilder(UrlBuilder.API_ME).build();
                HttpTask httpTask = new HttpTask(false, WorkoutNotificationService.this, HttpTask.METHOD_GET);
                httpTask.setAuthorizationRequired(true, null);
                httpTask.setmCallback(new HttpTask.HttpCallback() {
                    @Override
                    public void onResponse(String response) throws JSONException {
                        User user = User.createFromJson(new JSONObject(response));
                        application.setUser(user);
                    }

                    @Override
                    public void onException(Exception e) {

                    }
                });
                httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            }
        };
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("workout notification check");
        String session = application.getSessionToken();
        if(TextUtils.isEmpty(session)){
            session = SharedPrefsUtil.getStringPreference(WorkoutNotificationService.this, Keys.KEY_TOKEN);
        }
        if(!TextUtils.isEmpty(session)){
            application.setSessionToken(session);
            handler.post(message);
        }
        try {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek >= 2 && dayOfWeek <= 6){
                checkHourSelection();
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
        IntentUtils.scheduleWorkoutNotifications(getApplicationContext());
    }

    private void checkHourSelection() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        int minute = calendar.get(Calendar.MINUTE);
        if(minute < 59){
            hour--;
            if(hour < 0){
                hour = 23;
                int day = Calendar.DAY_OF_WEEK - 1;
                if(day < 2){
                    return;
                }
                calendar.add(Calendar.DAY_OF_WEEK, -1);
            }
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE, 59);
        }

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
//                    if(application.getDbHelper() == null){
//                        application.setDbHelper(new DBHelper(getApplicationContext()));
//                    }
                    String timeMeridian = SelectedWorkout.getTimeMeridian(hour + "_59");
//                    String selectedWorkoutId = application.getDbHelper().getSelectedWorkoutIdByTime(hour % 12 + ":59" + (hour/12==0?" am":" pm"));
                    String selectedWorkoutId = application.getDbHelper().getSelectedWorkoutIdByTime(timeMeridian);

                    // TODO : Remove temp Code
                    //application.setSessionToken(null);

                    SelectedWorkout selectedWorkout = new SelectedWorkout(workoutExercise,hour + "_59",selectedWorkoutId);
                    long timeMillis = calendar.getTimeInMillis();
                    CompletedWorkout completedWorkout = new CompletedWorkout(selectedWorkout,0,timeMillis, false);
                    completedWorkout.setSelectedWorkoutId(selectedWorkout.getSelectedWorkoutId());
                    int id = application.getDbHelper().insertUserTrack(completedWorkout);

                    if(id > 0){
                        boolean isPaused = SharedPrefsUtil.getBooleanPreference(WorkoutNotificationService.this, Keys.KEY_IS_PAUSED, false);
                        if(!isPaused){
                            addNotification(selectedWorkout, timeMillis, id, hour);
                        }
                    }
                    return;
                }
            }

//            String workoutLink = SharedPrefsUtil.getStringPreference(getApplicationContext(), Keys.KEY_VIDEO_LINKS[hour], "");
//
//            if(!TextUtils.isEmpty(workoutLink)){
//                addNotification(workoutLink);
//            }



        }

    }

    private void addNotification(SelectedWorkout workoutExercise, long timeMillis, int id, int hour) {

        String groupName = "com.oneminutebefore.workout.NOTIFICATION_GROUP";

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_OMB)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("OMB")
                        .setAutoCancel(true)
                        .setContentTitle(getString(R.string.one_minute_workout))
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentText(workoutExercise.getName())
                        .setGroup(groupName)
//                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                ;

        Intent intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.KEY_WORKOUT, workoutExercise);
        intent.putExtra(VideoPlayerActivity.KEY_TIME_MILLIS, timeMillis);
        intent.putExtra(VideoPlayerActivity.KEY_USER_TRACK_ID, id);

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, hour + 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(getApplicationContext(),manager);
        manager.notify(hour, builder.build());
    }

    private void createNotificationChannel(Context context, NotificationManager notificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.omb_channel_name);
            String description = context.getString(R.string.omb_channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_OMB, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            notificationManager.createNotificationChannel(channel);
        }
    }

}
