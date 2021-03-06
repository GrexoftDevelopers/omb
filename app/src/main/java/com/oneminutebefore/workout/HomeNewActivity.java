package com.oneminutebefore.workout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.oneminutebefore.workout.helpers.IntentUtils;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.Utils;
import com.oneminutebefore.workout.models.CompletedWorkout;
import com.oneminutebefore.workout.models.WorkoutExercise;
import com.oneminutebefore.workout.widgets.ProfileEditActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HomeNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGNUP = 1001;
    private NavigationView navigationView;

    private TextView tvTimer;
    private static TimerTask timerTask;

    @SuppressLint("SimpleDateFormat")
    private TextView tvTimerMinutes;
    private View layoutUpcomingTask, layoutScheduleTask;
    private Button btnScheduleTask;

    private ArrayList<CompletedWorkout> workoutsDone;
    private TextView tvRepCountHead;

    private Button btnPause;
    private boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.one_minute_before));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvTimer = (TextView) findViewById(R.id.tv_timer_hour);
        tvTimerMinutes = (TextView) findViewById(R.id.tv_timer_minutes);
        layoutScheduleTask = findViewById(R.id.layout_schedule_task);
        layoutUpcomingTask = findViewById(R.id.layout_upcoming_task);
        btnScheduleTask = (Button) findViewById(R.id.btn_schedule_workout);
        tvRepCountHead = (TextView) findViewById(R.id.tv_rep_count_head);

        btnScheduleTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(HomeNewActivity.this, SettingsActivityNew.class);
                startActivity(mIntent);
            }
        });

        isPaused = SharedPrefsUtil.getBooleanPreference(HomeNewActivity.this, Keys.KEY_IS_PAUSED, false);

        btnPause = (Button) findViewById(R.id.btn_pause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = !isPaused;
                SharedPrefsUtil.setBooleanPreference(HomeNewActivity.this, Keys.KEY_IS_PAUSED, isPaused);
//                if(isPaused){
//                    IntentUtils.cancelWorkoutNotifications(HomeNewActivity.this);
//                }else{
//                    IntentUtils.scheduleWorkoutNotifications(HomeNewActivity.this);
//                }
                initNavigationItems();
                resetTimer();
                if(!isPaused){
                    int missedWorkoutCount = WorkoutApplication.getmInstance().getDbHelper().getMissedWorkoutsCount();
                    if(missedWorkoutCount > 0){
                        Utils.showConfirmDialog(HomeNewActivity.this, R.string.message, R.string.msg_missed_workouts, null, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(HomeNewActivity.this, MissedWorkoutsActivity.class));
                            }
                        });
                    }
                }
//                else{
//                    WorkoutApplication.getmInstance().getDbHelper().deleteOldMissedWorkouts();
//                }
            }
        });
        initNavigationItems();
        IntentUtils.scheduleWorkoutNotifications(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initNavigationItems();
    }

    @Override
    protected void onDestroy() {
        if (timerTask != null) {
            timerTask.cancel(true);
            timerTask = null;
        }
        super.onDestroy();
    }

    private void initNavigationItems() {
        Menu menu = navigationView.getMenu();
        WorkoutApplication application = WorkoutApplication.getmInstance();
        String userId = application.getUserId();
        if (userId != null && userId.equals("-1")) {
            menu.findItem(R.id.action_sign_up).setVisible(true);
            menu.findItem(R.id.action_setting).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(false);
            findViewById(R.id.btn_register).setVisibility(View.VISIBLE);
            findViewById(R.id.videos_box).setVisibility(View.VISIBLE);

            YouTubeThumbnailView youTubeThumbnailView = (YouTubeThumbnailView) findViewById(R.id.yt_sample_1);
            youTubeThumbnailView.initialize(Constants.DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo("s7CNC9irjt0");
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
            youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeNewActivity.this, YoutubePlayerActivity.class);
                    intent.putExtra(YoutubePlayerActivity.KEY_LINK, "s7CNC9irjt0");
                    startActivity(intent);
                }
            });

            YouTubeThumbnailView youTubeThumbnailView2 = (YouTubeThumbnailView) findViewById(R.id.yt_sample_2);
            youTubeThumbnailView2.initialize(Constants.DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo("s7CNC9irjt0");
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
            youTubeThumbnailView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeNewActivity.this, YoutubePlayerActivity.class);
                    intent.putExtra(YoutubePlayerActivity.KEY_LINK, "s7CNC9irjt0");
                    startActivity(intent);
                }
            });


        } else {
            findViewById(R.id.btn_register).setVisibility(View.GONE);
            menu.findItem(R.id.action_sign_up).setVisible(false);
            menu.findItem(R.id.action_setting).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(true);
            findViewById(R.id.btn_register).setVisibility(View.GONE);
            findViewById(R.id.videos_box).setVisibility(View.GONE);
        }


    }


    private void resetTimer() {

        WorkoutApplication application = WorkoutApplication.getmInstance();
        String userId = application.getUserId();
        if (userId != null && userId.equals("-1")) {
            findViewById(R.id.card_timer).setVisibility(View.GONE);
            findViewById(R.id.card_workout_count).setVisibility(View.GONE);
        } else {
            findViewById(R.id.card_workout_count).setVisibility(View.VISIBLE);
            findViewById(R.id.card_timer).setVisibility(View.VISIBLE);

            boolean restarted = false;
            boolean timerSet = false;

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            String hours[] = Keys.getHourSelectionKeys(getApplicationContext());

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeNewActivity.this);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek == 1 || dayOfWeek == 7){
                if(dayOfWeek == 1) {
                    calendar.add(Calendar.DATE, 1);
                }
                else{
                    calendar.add(Calendar.DATE,2);
                }
                hour = 0;
            }

            if(!isPaused){
                for (int i = hour; ; i++) {

                    if (i == hours.length) {
                        restarted = true;
                        i = 0;
                        calendar.add(Calendar.DATE, 1);
                        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        if(dayOfWeek == 7){
                            calendar.add(Calendar.DATE,2);
                        }
                    }
                    if (preferences.getBoolean(hours[i], false)) {
                        String workoutId = SharedPrefsUtil.getStringPreference(getApplicationContext(), Keys.getWorkoutSelectionKeys(getApplicationContext())[i], "");
//                    String categoryId = SharedPrefsUtil.getStringPreference(getApplicationContext(), Keys.getUserLevelKey(HomeNewActivity.this), "");
                        HashMap<String, WorkoutExercise> exerciseHashMap = application.getWorkouts();
                        if (!TextUtils.isEmpty(workoutId)
                                && exerciseHashMap != null && !exerciseHashMap.isEmpty()){
//                            && application.getWorkouts().get(workoutId).getCategory().getId().equals(categoryId)) {
                            WorkoutExercise workoutExercise = exerciseHashMap.get(workoutId);
                            calendar.set(Calendar.HOUR_OF_DAY, i);
                            calendar.set(Calendar.MINUTE, 59);
                            if (timerTask != null) {
                                timerTask.cancel(true);
                            }
                            ((TextView) findViewById(R.id.tv_workout_name)).setText(workoutExercise.getName());
                            if(timerTask != null){
                                timerTask.cancel(true);
                            }
                            timerTask = new TimerTask(calendar.getTimeInMillis());
                            timerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            timerSet = true;
                            layoutUpcomingTask.setVisibility(View.VISIBLE);
                            layoutScheduleTask.setVisibility(View.GONE);
                            break;
                        }else{
                            if(timerTask != null){
                                timerTask.cancel(true);
                            }
                            timerSet = false;
                        }
                    }
                    if (i == hour && restarted) {
                        break;
                    }
                }
            }
            layoutUpcomingTask.setVisibility(!isPaused && timerSet? View.VISIBLE : View.GONE);
            layoutScheduleTask.setVisibility(!isPaused && !timerSet? View.VISIBLE : View.GONE);
            btnPause.setVisibility(!isPaused && !timerSet ? View.GONE : View.VISIBLE);
            if(!isPaused){
//                if(timerSet){
//                    IntentUtils.scheduleWorkoutNotifications(HomeNewActivity.this);
//                }else{
//                    IntentUtils.cancelWorkoutNotifications(HomeNewActivity.this);
//                }
                btnPause.setText(getString(R.string.pause));
            }else{
//                IntentUtils.cancelWorkoutNotifications(HomeNewActivity.this);
                btnPause.setText(getString(R.string.resume));
            }

            workoutsDone = application.getDbHelper().getTodayCompletedWorkouts();

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_workout_count);
            recyclerView.setNestedScrollingEnabled(false);
            if(workoutsDone != null && !workoutsDone.isEmpty()){
                findViewById(R.id.txt_no_workout).setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                int repCountTotal = 0;
                for(CompletedWorkout workout : workoutsDone){
                    repCountTotal += workout.getRepsCount();
                }
                tvRepCountHead.setText(getString(R.string.today_count) + " (" + repCountTotal + ")");
                recyclerView.setAdapter(new WorkoutCountAdapter());
            }else{
                recyclerView.setVisibility(View.GONE);
                tvRepCountHead.setText(getString(R.string.today_count));
                findViewById(R.id.txt_no_workout).setVisibility(View.VISIBLE);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        resetTimer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home_new, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_setting) {
            startActivity(new Intent(this, SettingsActivityNew.class));
        } else if (id == R.id.action_sign_up) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(intent, RC_SIGNUP);
        } else if (id == R.id.action_sample) {
            startActivity(new Intent(this, SampleWorkoutActivity.class));
        } else if (id == R.id.action_missed) {
            startActivity(new Intent(this, MissedWorkoutsActivity.class));
        } else if (id == R.id.action_contact) {
            startActivity(new Intent(this, ContactActivity.class));
        } else if (id == R.id.action_report) {
//            startActivity(new Intent(this, ReportsActivity.class));
            startActivity(new Intent(this, ReportNewActivity.class));
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle(getString(R.string.confirm_logout))
                    .setMessage(getString(R.string.msg_confirm_logout))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.clearUserData(HomeNewActivity.this);
                            Intent intent = new Intent(HomeNewActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else if(id == R.id.action_profile){
            startActivity(new Intent(HomeNewActivity.this, ProfileEditActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class TimerTask extends AsyncTask<Void, Long, Boolean> {

        private long deadLine;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm:ss");

        public TimerTask(long deadLine) {
            this.deadLine = deadLine;
            System.out.println("dead line : " + dateFormat.format(new Date(deadLine)));
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            long currentTime;
            boolean flag = true;
            boolean resetTimer = false;
            while (flag) {
                currentTime = System.currentTimeMillis();
                if (currentTime < deadLine) {
                    publishProgress(deadLine - currentTime);
                } else {
                    flag = false;
                    resetTimer = true;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    flag = false;
                }
            }

            return resetTimer;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            values[0] /= 1000;
            int hoursInt = (int) (values[0] / 3600);
            String hours = String.valueOf(hoursInt);
            values[0] %= 3600;
            String minutes = String.valueOf(values[0] / 60);
            values[0] %= 60;
            if (!hours.equals("0")) {
                if(hoursInt >= 24){
                    int days = hoursInt / 24;
                    hoursInt = hoursInt % 24;
                    tvTimer.setText(getResources().getQuantityString(R.plurals.days_hours,days,days) + (hoursInt > 0 ? " " + String.format(getString(R.string.time_left_hour), String.valueOf(hoursInt)) : ""));
                }else{
                    tvTimer.setText(String.format(getString(R.string.time_left_hour), hours));
                }
                tvTimer.setVisibility(View.VISIBLE);
            } else {
                tvTimer.setVisibility(View.GONE);
            }
            tvTimerMinutes.setText(String.format(getString(R.string.time_left_minutes), minutes, String.valueOf(values[0])));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                resetTimer();
            }
        }
    }

    private class WorkoutCountAdapter extends RecyclerView.Adapter<WorkoutCountAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_workout_count, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            CompletedWorkout workoutExercise = workoutsDone.get(i);
            viewHolder.tvName.setText(workoutExercise.getName());
            viewHolder.tvCount.setText(String.valueOf(workoutExercise.getRepsCount()));
            viewHolder.tvTime.setText(workoutExercise.getTimeMeridian());
            switch (workoutExercise.getRepsCount()) {
                case 7:
//                    ((GradientDrawable)viewHolder.tvCount.getBackground()).setColor(ContextCompat.getColor(HomeNewActivity.this,R.color.blue_grey));
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_blue_grey);
                    break;
                case 8:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_brown);
                    break;
                case 9:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_amber);
                    break;
                case 10:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_green);
                    break;
                case 11:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_teel);
                    break;
                case 12:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_blue);
                    break;
                case 1:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_deep_purple);
                    break;
                case 2:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_purple);
                    break;
                case 3:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_lime);
                    break;
                case 4:
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_red);
                    break;
            }

            if (i == getItemCount() - 1) {
                viewHolder.itemView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
            } else {
                viewHolder.itemView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeNewActivity.this, ReportNewActivity.class));
                }
            });

        }

        @Override
        public int getItemCount() {
            return workoutsDone != null ? workoutsDone.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvCount;
            private TextView tvName;
            private TextView tvTime;

            public ViewHolder(View itemView) {
                super(itemView);

                tvTime = (TextView) itemView.findViewById(R.id.tv_workout_time);
                tvName = (TextView) itemView.findViewById(R.id.tv_workout_name);
                tvCount = (TextView) itemView.findViewById(R.id.tv_count);

            }
        }

    }
}
