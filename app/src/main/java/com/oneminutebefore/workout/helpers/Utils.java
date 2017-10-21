package com.oneminutebefore.workout.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.oneminutebefore.workout.MainActivity;
import com.oneminutebefore.workout.R;
import com.oneminutebefore.workout.WorkoutApplication;

/**
 * Created by tahir on 13/5/17.
 */

public class Utils {


    private static boolean isDialogShown;

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void clearUserData(Context context){
        SharedPrefsUtil.deletePreference(context, Keys.KEY_TOKEN);
        SharedPrefsUtil.deletePreference(context, Keys.KEY_USER_ID);
        SharedPrefsUtil.deletePreference(context, Keys.KEY_IS_PAUSED);
        SharedPrefsUtil.deletePreference(context, Keys.KEY_USER);
//        SharedPrefsUtil.deletePreference(context, Keys.getUserLevelKey(context));
        String hourPrefKeys[] = Keys.getHourSelectionKeys(context);
        for(String key : hourPrefKeys){
            SharedPrefsUtil.deletePreference(context, key);
        }
        String videoPrefKeys[] = Keys.getWorkoutSelectionKeys(context);
        for(String key : videoPrefKeys){
            SharedPrefsUtil.deletePreference(context, key);
        }
        WorkoutApplication application = WorkoutApplication.getmInstance();
        application.clearUserData();
        application.getDbHelper().clearData();
    }

    public static String getTimeKey(String timeMeridian){
        String time = timeMeridian.split(" ")[0];
        String meridian = timeMeridian.split(" ")[1];
        int hour = Integer.parseInt(time.split(":")[0]);
        if(hour == 12){
            hour = 0;
        }
        if(meridian.equals("P.M")){
            hour += 12;
        }
        return hour + "_59";
    }

    public static Intent getSessionTimeoutIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static void showAlertDialog(Context context, int titleId, int messageId, DialogInterface.OnClickListener listener) {
        showAlertDialog(context, context.getString(titleId), context.getString(messageId), listener);
    }

    public static void showAlertDialog(Context context, String title, String message, final DialogInterface.OnClickListener listener) {
        if (isDialogShown) return;
        isDialogShown = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDialogShown = false;
                        if (listener != null) {
                            listener.onClick(dialog, which);
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isDialogShown = false;
                    }
                })
                .show();
    }

    public static void showConfirmDialog(Context context, int titleResourceId, int messageResourceId, DialogInterface.OnClickListener negativeListener, DialogInterface.OnClickListener positiveListener) {
        showConfirmDialog(context, context.getString(titleResourceId), context.getString(messageResourceId), negativeListener, positiveListener);
    }

    public static void showConfirmDialog(Context context, String title, String message, final DialogInterface.OnClickListener negativeListener, final DialogInterface.OnClickListener positiveListener) {

        showConfirmDialog(context, title, message, context.getString(R.string.no), negativeListener, context.getString(R.string.yes), positiveListener);
    }

    public static void showConfirmDialog(Context context, String title, String message, String negativeButtonName, final DialogInterface.OnClickListener negativeListener, String positiveButtonName, final DialogInterface.OnClickListener positiveListener) {

        if (isDialogShown) return;
        isDialogShown = true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDialogShown = false;
                        if (negativeListener != null) negativeListener.onClick(dialog, which);
                    }
                })
                .setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDialogShown = false;
                        if (positiveListener != null) positiveListener.onClick(dialog, which);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isDialogShown = false;
                    }
                })
                .show();
    }



}
