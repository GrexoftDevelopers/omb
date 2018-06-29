package com.oneminutebefore.workout.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oneminutebefore.workout.helpers.IntentUtils;

public class OmbReceiver extends BroadcastReceiver {

    public static final String ACTION_CHECKER = "com.oneminutebefore.workout.ACTOIN_CHECKER";
    public static final int REQUEST_CODE = 100001;

    @Override
    public void onReceive(Context context, Intent intent) {
        IntentUtils.scheduleWorkoutNotifications(context);
    }
}
