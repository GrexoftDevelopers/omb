package com.oneminutebefore.workout;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.oneminutebefore.workout.helpers.DBHelper;
import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;
import com.oneminutebefore.workout.helpers.VolleyHelper;
import com.oneminutebefore.workout.models.CompletedWorkout;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.User;
import com.oneminutebefore.workout.models.WorkoutExercise;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VideoPlayerActivity extends AppCompatActivity {

    public static final String KEY_URL = "url";
    public static final String KEY_WORKOUT = "workout";
    public static final String KEY_TIME_MILLIS = "time_millis";

    private YouTubePlayerFragment youTubePlayerFragment;

    private SelectedWorkout selectedWorkoutExercise;

    private boolean isDemo;

    private ProgressBar progressBar;

    private ImageView ivPlayPause;

    private TextView txtMsgTimerAction;

    private boolean timerRunning;

    private int timerSecond;
    private TimerTask timerTask;
    private WorkoutApplication application;
    private AlertDialog alertDialog;
    private Date workoutDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent mIntent = getIntent();
        selectedWorkoutExercise = (SelectedWorkout) mIntent.getSerializableExtra(KEY_WORKOUT);
        long timeMillis = mIntent.getLongExtra(KEY_TIME_MILLIS, 0);
        workoutDate = new Date(timeMillis);

        application = WorkoutApplication.getmInstance();
        String session = application.getSessionToken();
        if(TextUtils.isEmpty(session)){
            session = SharedPrefsUtil.getStringPreference(this, Keys.KEY_TOKEN);
            if(!TextUtils.isEmpty(session)){
                application.setSessionToken(session);
            }
        }
        if(!TextUtils.isEmpty(session)){
            if(application.getUser() == null){
                fetchUserInfo();
            }
        }

        isDemo = selectedWorkoutExercise == null;
        if(isDemo){
            HashMap<String, WorkoutExercise> workouts = WorkoutApplication.getmInstance().getWorkouts();
            if(workouts != null && !workouts.isEmpty()){
                for(Map.Entry entry : workouts.entrySet()){
                    if(((WorkoutExercise)entry.getValue()).getName().equals("Desk Push ups")){
                        selectedWorkoutExercise = new SelectedWorkout((WorkoutExercise)entry.getValue(),"");
                        break;
                    }
                }
            }
        }

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            if(isDemo){
                mActionBar.setTitle("Demo");
            }else{
                mActionBar.setTitle(selectedWorkoutExercise.getName());
            }
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player_layout);

        if (!TextUtils.isEmpty(selectedWorkoutExercise.getVideoLink())) {
            findViewById(R.id.youtube_content).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_no_content).setVisibility(View.GONE);
            youTubePlayerFragment.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.setShowFullscreenButton(true);
                    int substringIndex = selectedWorkoutExercise.getVideoLink().lastIndexOf("=") + 1;
                    if(substringIndex < 0){
                        substringIndex = selectedWorkoutExercise.getVideoLink().lastIndexOf("/") + 1;
                    }
                    String url = selectedWorkoutExercise.getVideoLink().substring(substringIndex);
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                    youTubePlayer.cueVideo(url);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });

            timerSecond = 0;
            ivPlayPause = (ImageView)findViewById(R.id.iv_play_pause);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            txtMsgTimerAction = (TextView)findViewById(R.id.txt_timer_action_msg);

            progressBar.setMax(60);
            progressBar.setProgress(0);
            progressBar.setMax(60);
            ((CircleProgressBar)progressBar).setProgressBackgroundColor(getResources().getColor(R.color.divider_color));
            ivPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleTimer();
                }
            });


        } else {
            findViewById(R.id.youtube_content).setVisibility(View.GONE);
            findViewById(R.id.layout_no_content).setVisibility(View.VISIBLE);
        }
    }

    private void initUser(String json) throws JSONException {
        User user = User.createFromJson(new JSONObject(json));
        WorkoutApplication application = ((WorkoutApplication) getApplication());
        application.setUser(user);
        application.setUserId(user.getId());
        if(application.getDbHelper() == null){
            application.setDbHelper(new DBHelper(VideoPlayerActivity.this));
        }
//        SharedPrefsUtil.setStringPreference(VideoPlayerActivity.this, Keys.getUserLevelKey(VideoPlayerActivity.this), user.getUserLevel());
    }

    private void fetchUserInfo(){

        String userJson = SharedPrefsUtil.getStringPreference(VideoPlayerActivity.this, Keys.KEY_USER, "");
        if (!TextUtils.isEmpty(userJson)) {
            try {
                initUser(userJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            String url = new UrlBuilder(UrlBuilder.API_ME).build();
            HttpTask httpTask = new HttpTask(false,VideoPlayerActivity.this,HttpTask.METHOD_GET);
            httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
                @Override
                public void onSessionTimeout() {
                    startActivity(Utils.getSessionTimeoutIntent(VideoPlayerActivity.this));
                }
            });
            httpTask.setmCallback(new HttpTask.HttpCallback() {
                @Override
                public void onResponse(String response) throws JSONException {
                    initUser(response);
                    SharedPrefsUtil.setStringPreference(VideoPlayerActivity.this, Keys.KEY_USER, response);
                }

                @Override
                public void onException(Exception e) {

                }
            });
            httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
    }

    private void toggleTimer(){

        if(timerSecond >= 60){
            return;
        }
        timerRunning = !timerRunning;

        if(timerRunning){
            ivPlayPause.setImageResource(R.drawable.ic_pause_48dp);
            timerTask = new TimerTask();
            timerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            txtMsgTimerAction.setText(getString(R.string.pause));
        }else{
            txtMsgTimerAction.setText(getString(R.string.start));
            ivPlayPause.setImageResource(R.drawable.ic_play_arrow_48dp);
            if(timerTask != null){
                timerTask.cancel(true);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        if(timerTask !=null){
            timerTask.cancel(true);
        }
//        youTubePlayerFragment.onDestroy();
        super.onDestroy();
    }

    private class TimerTask extends AsyncTask<Void, Void, Void> {

        MediaPlayer mp;

        @Override
        protected Void doInBackground(Void... params) {

            boolean flag = true;
            while (flag && timerSecond < 60) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    flag = false;
                }
                timerSecond++;
                publishProgress();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(timerSecond);
            if(timerSecond == 30 || timerSecond == 50 || timerSecond > 55){
                mp = MediaPlayer.create(VideoPlayerActivity.this,R.raw.beep);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        mp.prepareAsync();
//                    }
//                });
//                mp.reset();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!isCancelled()){
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_reps_input,null);
                final EditText etRepsCount = (EditText)dialogView.findViewById(R.id.et_reps_count);
                builder.setView(dialogView);
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.setPositiveButton(getString(R.string.ok), null);
                alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isDemo){
                                    String repsCount = etRepsCount.getText().toString().trim();
                                    if(TextUtils.isEmpty(repsCount)){
                                        ((TextInputLayout)dialogView.findViewById(R.id.til_reps_count)).setError("Please enter reps");
                                    }else{
                                        if(repsCount.startsWith(".")){
                                            repsCount = "0" + repsCount;
                                        }
                                        if(repsCount.equals("-")){
                                            repsCount = "0";
                                        }
                                        double count = Double.valueOf(repsCount);
                                        if(application.getUser() != null){
                                            saveReps(count);
                                        }else{
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }
                                }else{
                                    Toast.makeText(VideoPlayerActivity.this, getString(R.string.msg_thanks), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
                alertDialog.show();
                ivPlayPause.setOnClickListener(null);
                ivPlayPause.setVisibility(View.INVISIBLE);
                txtMsgTimerAction.setVisibility(View.GONE);
            }
        }
    }

    private void saveReps(double count) {
//        VolleyHelper volleyHelper = new VolleyHelper(VideoPlayerActivity.this, true);

        String url = new UrlBuilder(UrlBuilder.API_SAVE_REPS)
                .addParameters("date",new SimpleDateFormat("yyyy-MM-dd").format(workoutDate))
                .addParameters("rep",String.valueOf((int)count))
                .addParameters("id", selectedWorkoutExercise.getSelectedWorkoutId())
                .addParameters("user_id", application.getUser().getId())
                .build();
//        volleyHelper.setAuthorizationRequired(true);
//        volleyHelper.callApi(Request.Method.POST, url, null, new VolleyHelper.VolleyCallback() {
//            @Override
//            public void onSuccess(String result) throws JSONException {
//                Toast.makeText(VideoPlayerActivity.this, getString(R.string.msg_thanks), Toast.LENGTH_SHORT).show();
//                alertDialog.dismiss();
//                finish();
//            }
//
//            @Override
//            public void onError(String error) {
//                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
//            }
//        });

        HttpTask httpTask = new HttpTask(false,VideoPlayerActivity.this, HttpTask.METHOD_POST);
        httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
            @Override
            public void onSessionTimeout() {
                startActivity(Utils.getSessionTimeoutIntent(VideoPlayerActivity.this));
            }
        });
        httpTask.setmCallback(new HttpTask.HttpCallback() {
            @Override
            public void onResponse(String response) throws JSONException {
                Toast.makeText(VideoPlayerActivity.this, getString(R.string.msg_thanks), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                finish();
            }

            @Override
            public void onException(Exception e) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
            }
        });
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        CompletedWorkout completedWorkout = new CompletedWorkout(selectedWorkoutExercise,(int)count,workoutDate.getTime());
        completedWorkout.setSelectedWorkoutId(selectedWorkoutExercise.getSelectedWorkoutId());
        application.getDbHelper().insertUserTrack(completedWorkout);
    }
}
