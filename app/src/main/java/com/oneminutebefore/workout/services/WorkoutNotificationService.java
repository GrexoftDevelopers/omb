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
import android.text.TextUtils;

import com.oneminutebefore.workout.R;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;

import java.util.Calendar;


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
            checkHourSelection();
        }catch (Exception e ){
            e.getMessage();
        }
    }

    private void checkHourSelection() {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String hourSelectionKey = Keys.getHourSelectionKeys(getApplicationContext())[hour];
        boolean isHourSelected = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(hourSelectionKey,false);

        if(isHourSelected){

            String workoutLink = SharedPrefsUtil.getStringPreference(getApplicationContext(), Keys.KEY_VIDEO_LINKS[hour], "");

            if(!TextUtils.isEmpty(workoutLink)){
                addNotification(workoutLink);
            }

        }

    }

    private void addNotification(String link) {

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setTicker("OMB")
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setContentTitle(getString(R.string.one_minute_workout))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(link))
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}
