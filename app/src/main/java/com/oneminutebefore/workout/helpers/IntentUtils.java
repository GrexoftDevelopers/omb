package com.oneminutebefore.workout.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.oneminutebefore.workout.services.WorkoutNotificationService;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class IntentUtils {

   /* public static Intent getCallIntent(Context context, String PhoneNo) {
        Intent intentPhone = new Intent(Intent.ACTION_CALL);
        intentPhone.setData(Uri.parse("tel:" + PhoneNo));
        return intentPhone;
    }*/

    public static Intent getCallIntent(String PhoneNo) {
        Intent intentPhone = new Intent(Intent.ACTION_CALL);
        intentPhone.setData(Uri.parse("tel:" + PhoneNo));
        return intentPhone;
    }

    @SuppressWarnings("SameParameterValue")
    public static Intent getEmailIntent(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        return intent;
    }

    public static void scheduleWorkoutNotifications(Context context) {

        cancelWorkoutNotifications(context);

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, WorkoutNotificationService.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getService(context, WorkoutNotificationService.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        if(currentTime > calendar.getTimeInMillis()){
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1 % 24);
        }

        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }else{
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HOUR , pIntent);
        }

//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                120000 , pIntent);

    }

    public static void cancelWorkoutNotifications(Context context) {
        Intent intent = new Intent(context, WorkoutNotificationService.class);
        final PendingIntent pIntent = PendingIntent.getService(context, WorkoutNotificationService.REQUEST_CODE,
                intent, FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

}
