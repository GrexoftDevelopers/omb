package com.oneminutebefore.workout.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.oneminutebefore.workout.receiver.OmbReceiver;
import com.oneminutebefore.workout.services.WorkoutNotificationService;

import java.util.Calendar;
import java.util.Date;

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
        System.out.println("scheduling workout notification : current time " + new Date(currentTime).toString());
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println("scheduling workout notification : adjusted time " + new Date(calendar.getTimeInMillis()).toString());
        if(currentTime > calendar.getTimeInMillis()){
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            System.out.println("scheduling workout notification after adding time: new time " + new Date(calendar.getTimeInMillis()).toString());
        }

        Intent checkerIntent = new Intent(OmbReceiver.ACTION_CHECKER);
        PendingIntent checkerPending = PendingIntent.getBroadcast(context,OmbReceiver.REQUEST_CODE,checkerIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        long halfHourMillis = 30 * 60 * 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + halfHourMillis
                    , halfHourMillis, checkerPending);

        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + halfHourMillis
                    , halfHourMillis, checkerPending);
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

        Intent checkerIntent = new Intent(OmbReceiver.ACTION_CHECKER);
        PendingIntent checkerPending = PendingIntent.getBroadcast(context,OmbReceiver.REQUEST_CODE,checkerIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(checkerPending);
    }

}
