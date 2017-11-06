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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;
import com.oneminutebefore.workout.models.CompletedWorkout;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.User;
import com.oneminutebefore.workout.models.UserTrack;
import com.oneminutebefore.workout.models.WorkoutExercise;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VideoPlayerActivity extends BaseRequestActivity {

    public static final String KEY_URL = "url";
    public static final String KEY_WORKOUT = "workout";
    public static final String KEY_TIME_MILLIS = "time_millis";
    public static final String KEY_SHOW_TIMER = "show_timer";
    public static final String KEY_USER_TRACK_ID = "completed_workout_id";

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
    private boolean showTimer;
    private int completedWorkoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent mIntent = getIntent();
        selectedWorkoutExercise = (SelectedWorkout) mIntent.getSerializableExtra(KEY_WORKOUT);
        showTimer = mIntent.getBooleanExtra(KEY_SHOW_TIMER, true);
        long timeMillis = mIntent.getLongExtra(KEY_TIME_MILLIS, 0);
        completedWorkoutId = mIntent.getIntExtra(KEY_USER_TRACK_ID, -1);
        workoutDate = new Date(timeMillis);

        application = WorkoutApplication.getmInstance();

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
            System.out.println("video link : " + selectedWorkoutExercise.getVideoLink());
            int substringIndex = selectedWorkoutExercise.getVideoLink().lastIndexOf("=") + 1;
            if(substringIndex <= 0){
                substringIndex = selectedWorkoutExercise.getVideoLink().lastIndexOf("/") + 1;
            }
            final String url = selectedWorkoutExercise.getVideoLink().substring(substringIndex);
            System.out.println("url : " + url);
            youTubePlayerFragment.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.setShowFullscreenButton(true);
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                    youTubePlayer.cueVideo(url);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });

            if(showTimer){
                String session = application.getSessionToken();
                if(TextUtils.isEmpty(session)){
                    session = SharedPrefsUtil.getStringPreference(this, Keys.KEY_TOKEN);
                    if(!TextUtils.isEmpty(session)){
                        application.setSessionToken(session);
                    }
                }
                if(!TextUtils.isEmpty(session)){
                    if(application.getUser() == null){
                        String userJson = SharedPrefsUtil.getStringPreference(VideoPlayerActivity.this, Keys.KEY_USER, "");
                        if (!TextUtils.isEmpty(userJson)) {
                            try {
                                initUser(userJson);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
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
            }else{
                findViewById(R.id.timer_box).setVisibility(View.GONE);
                findViewById(R.id.card_workout_count).setVisibility(View.VISIBLE);
                final TextView tvNoWorkout = (TextView) findViewById(R.id.txt_no_workout);
                tvNoWorkout.setVisibility(View.GONE);
                final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_workout_count);
                recyclerView.setVisibility(View.GONE);
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.history_progress);
                progressBar.setVisibility(View.VISIBLE);
                HttpTask httpTask = new HttpTask(false,VideoPlayerActivity.this, HttpTask.METHOD_GET);
                UrlBuilder builder = new UrlBuilder(UrlBuilder.API_PRODUCTS).addSection(selectedWorkoutExercise.getSelectedWorkoutId());
                httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
                    @Override
                    public void onSessionTimeout() {
                        startActivity(Utils.getSessionTimeoutIntent(VideoPlayerActivity.this));
                    }
                });
                httpTask.setmCallback(new HttpTask.HttpCallback() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        progressBar.setVisibility(View.GONE);

                        CompletedWorkout completedWorkout = CompletedWorkout.createFromJson(new JSONObject(response));
                        recyclerView.setAdapter(new UserTrackAdapter(completedWorkout.getUserTracks()));
                        if(completedWorkout != null && completedWorkout.getUserTracks() != null && !completedWorkout.getUserTracks().isEmpty()){
                            recyclerView.setVisibility(View.VISIBLE);
                            tvNoWorkout.setVisibility(View.GONE);
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            tvNoWorkout.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onException(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        tvNoWorkout.setVisibility(View.VISIBLE);
                    }
                });
                httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, builder.build());
            }

            fetchUserInfo();


        } else {
            findViewById(R.id.youtube_content).setVisibility(View.GONE);
            findViewById(R.id.layout_no_content).setVisibility(View.VISIBLE);
        }
    }

    private class UserTrackAdapter extends RecyclerView.Adapter<UserTrackViewHolder>{

        private ArrayList<UserTrack> userTracks;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        UserTrackAdapter(ArrayList<UserTrack> userTracks){
            this.userTracks = userTracks;
        }

        @Override
        public UserTrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserTrackViewHolder(
                    getLayoutInflater().inflate(
                            R.layout.item_workout_count
                            ,parent
                            ,false
                    )
            );
        }

        @Override
        public void onBindViewHolder(UserTrackViewHolder holder, int position) {

            UserTrack userTrack = userTracks.get(position);
            long millis = SelectedWorkout.getDateTimeLong(userTrack.getDate());
            holder.tvDate.setText(dateFormat.format(new Date(millis)));
            holder.tvCount.setText(String.valueOf(userTrack.getReps()));
            if(position == userTracks.size() - 1){
                holder.itemView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return userTracks == null ? 0 : userTracks.size();
        }
    }

    private static class UserTrackViewHolder extends RecyclerView.ViewHolder{


        private TextView tvCount;
        private TextView tvDate;

        public UserTrackViewHolder(View itemView) {
            super(itemView);
            tvCount = (TextView)itemView.findViewById(R.id.tv_count);
            tvDate = (TextView)itemView.findViewById(R.id.tv_workout_name);
            itemView.findViewById(R.id.tv_workout_time).setVisibility(View.GONE);
        }
    }

    private void initUser(String json) throws JSONException {
        User user = User.createFromJson(new JSONObject(json));
        WorkoutApplication application = ((WorkoutApplication) getApplication());
        application.setUser(user);
        application.setUserId(user.getId());
//        if(application.getDbHelper() == null){
//            application.setDbHelper(new DBHelper(VideoPlayerActivity.this));
//        }
//        SharedPrefsUtil.setStringPreference(VideoPlayerActivity.this, Keys.getUserLevelKey(VideoPlayerActivity.this), user.getUserLevel());
    }

    private void fetchUserInfo(){

//        String userJson = SharedPrefsUtil.getStringPreference(VideoPlayerActivity.this, Keys.KEY_USER, "");
//        if (false){ // && !TextUtils.isEmpty(userJson)) {
//            try {
////                initUser(userJson);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        else{
//            application.setSessionToken(null);
            String url = new UrlBuilder(UrlBuilder.API_ME).build();
            HttpTask httpTask = new HttpTask(false,VideoPlayerActivity.this,HttpTask.METHOD_GET);
            httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
                @Override
                public void onSessionTimeout() {
                    startActivity(getSessionTimeoutIntent());
                }
            });
            httpTask.setmCallback(new HttpTask.HttpCallback() {
                @Override
                public void onResponse(String response) throws JSONException {
                    initUser(response);
                    SharedPrefsUtil.setStringPreference(VideoPlayerActivity.this, Keys.KEY_USER, response);
//                    startActivity(getSessionTimeoutIntent());
                }

                @Override
                public void onException(Exception e) {

                }
            });
            httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
//        }
    }

    private Intent getSessionTimeoutIntent() {
        Intent intent = Utils.getSessionTimeoutIntent(VideoPlayerActivity.this);
        intent.putExtra(MainActivity.KEY_REDIRECTION_ACTIVITY, MainActivity.REDIRECTION_VIDEO_ACTIVITY);
        intent.putExtra(MainActivity.KEY_PREVIOUS_USER_ID, application.getUserId());
        Bundle redirectionExtra = new Bundle();
        redirectionExtra.putBoolean(VideoPlayerActivity.KEY_SHOW_TIMER, showTimer);
        redirectionExtra.putLong(VideoPlayerActivity.KEY_TIME_MILLIS, getIntent().getLongExtra(KEY_TIME_MILLIS, 0));
        redirectionExtra.putInt(KEY_USER_TRACK_ID, completedWorkoutId);
        redirectionExtra.putSerializable(VideoPlayerActivity.KEY_WORKOUT, selectedWorkoutExercise);
        intent.putExtra(MainActivity.KEY_REDIRECTION_EXTRA, redirectionExtra);
        return intent;
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
                                    v.setEnabled(false);
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
                                    dialog.dismiss();
                                    finish();
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

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void saveReps(double count) {
//        VolleyHelper volleyHelper = new VolleyHelper(VideoPlayerActivity.this, true);

        String url = new UrlBuilder(UrlBuilder.API_SAVE_REPS)
                .addParameters("date",new SimpleDateFormat("MM-dd-yyyy").format(workoutDate))
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

        HttpTask httpTask = new HttpTask(true,VideoPlayerActivity.this, HttpTask.METHOD_POST);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
            }
        });
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        CompletedWorkout completedWorkout = new CompletedWorkout(selectedWorkoutExercise,(int)count,workoutDate.getTime(), true);
        completedWorkout.setSelectedWorkoutId(selectedWorkoutExercise.getSelectedWorkoutId());
        completedWorkout.setCompletedWorkoutId(completedWorkoutId);
//      application.getDbHelper().insertUserTrack(completedWorkout);
        application.getDbHelper().completeUserTrack(completedWorkout);
    }
}
