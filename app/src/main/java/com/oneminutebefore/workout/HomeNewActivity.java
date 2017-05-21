package com.oneminutebefore.workout;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.models.WorkoutExercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGNUP = 1001;
    private NavigationView navigationView;

    private TextView tvTimer;
    private TimerTask timerTask;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
    private TextView tvTimerMinutes;

    private ArrayList<WorkoutExercise> workoutsDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvTimer = (TextView)findViewById(R.id.tv_timer_hour);
        tvTimerMinutes = (TextView)findViewById(R.id.tv_timer_minutes);

        initNavigationItems();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initNavigationItems();
    }

    @Override
    protected void onDestroy() {
        if(timerTask != null){
            timerTask.cancel(true);
        }
        super.onDestroy();
    }

    private void initNavigationItems(){
        Menu menu = navigationView.getMenu();
        WorkoutApplication application = WorkoutApplication.getmInstance();
        String userId = application.getUserId();
        if(userId.equals("-1")){
            menu.findItem(R.id.action_sign_up).setVisible(true);
            menu.findItem(R.id.action_setting).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(false);
            findViewById(R.id.btn_register).setVisibility(View.VISIBLE);
            findViewById(R.id.videos_box).setVisibility(View.VISIBLE);

            YouTubeThumbnailView youTubeThumbnailView = (YouTubeThumbnailView)findViewById(R.id.yt_sample_1);
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
                    intent.putExtra(YoutubePlayerActivity.KEY_LINK,"s7CNC9irjt0");
                    startActivity(intent);
                }
            });

            YouTubeThumbnailView youTubeThumbnailView2 = (YouTubeThumbnailView)findViewById(R.id.yt_sample_2);
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
                    intent.putExtra(YoutubePlayerActivity.KEY_LINK,"s7CNC9irjt0");
                    startActivity(intent);
                }
            });


        }else{
            findViewById(R.id.btn_register).setVisibility(View.GONE);
            menu.findItem(R.id.action_sign_up).setVisible(false);
            menu.findItem(R.id.action_setting).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(true);
            findViewById(R.id.btn_register).setVisibility(View.GONE);
            findViewById(R.id.videos_box).setVisibility(View.GONE);
        }


    }


    private void resetTimer(){

        WorkoutApplication application = WorkoutApplication.getmInstance();
        String userId = application.getUserId();
        if(userId.equals("-1")){
            findViewById(R.id.card_timer).setVisibility(View.GONE);
            findViewById(R.id.card_workout_count).setVisibility(View.GONE);
        }else{
            findViewById(R.id.card_workout_count).setVisibility(View.VISIBLE);
            findViewById(R.id.card_timer).setVisibility(View.VISIBLE);



            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            String hours[] = Keys.getHourSelectionKeys(getApplicationContext());

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeNewActivity.this);

            boolean restarted = false;
            boolean timerSet = false;
            for(int i = hour ;; i++){

                if(i == hours.length){
                    restarted = true;
                    i = 0;
                    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
                }
                if(preferences.getBoolean(hours[i], false)){
                    calendar.set(Calendar.HOUR_OF_DAY, i);
                    calendar.set(Calendar.MINUTE,59);
                    if(timerTask != null){
                        timerTask.cancel(true);
                    }
                    timerTask = new TimerTask(calendar.getTimeInMillis());
                    timerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    timerSet = true;
                    findViewById(R.id.card_timer).setVisibility(View.VISIBLE);
                    break;
                }
                if(i==hour && restarted){
                    break;
                }
            }
            if(!timerSet){
                findViewById(R.id.card_timer).setVisibility(View.GONE);
            }

            // Dummy content showing right now
            workoutsDone = new ArrayList<>();
            workoutsDone.add(new WorkoutExercise("1","Push Ups","","",18,7));
            workoutsDone.add(new WorkoutExercise("1","Jump Rope","","",24,11));
            workoutsDone.add(new WorkoutExercise("1","Stair Run","","",56,2));
            workoutsDone.add(new WorkoutExercise("1","Squat Jump","","",24,3));

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_workout_count);
            recyclerView.setAdapter(new WorkoutCountAdapter());

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
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_sign_up) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(intent, RC_SIGNUP);
        } else if (id == R.id.action_sample) {
            startActivity(new Intent(this, SampleWorkoutActivity.class));
        } else if (id == R.id.action_contact) {
            startActivity(new Intent(this, ContactActivity.class));
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
                            WorkoutApplication.getmInstance().setUserId("-1");
                            SharedPrefsUtil.deletePreference(HomeNewActivity.this, Keys.KEY_USER_ID);
                            Intent intent = new Intent(HomeNewActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class TimerTask extends AsyncTask<Void,Long,Void>{

        private long deadLine;

        public TimerTask(long deadLine) {
            this.deadLine = deadLine;
            System.out.println("dead line : " + dateFormat.format(new Date(deadLine)));
        }

        @Override
        protected Void doInBackground(Void... params) {

            long currentTime;
            boolean flag = true;
            while(flag){
                currentTime = System.currentTimeMillis();
                System.out.println("current time : " + dateFormat.format(new Date(currentTime)));
                if(currentTime < deadLine){
                    publishProgress(deadLine - currentTime);
                }else{
                    flag = false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    flag = false;
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            values[0] /= 1000;
            String hours = String.valueOf(values[0] / 3600);
            values[0] %= 3600;
            String minutes = String.valueOf(values[0] / 60);
            values[0] %= 60;
            tvTimer.setText(String.format(getString(R.string.time_left_hour),hours));
            tvTimerMinutes.setText(String.format(getString(R.string.time_left_minutes),minutes,String.valueOf(values[0])));
        }
    }

    private class WorkoutCountAdapter extends RecyclerView.Adapter<WorkoutCountAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_workout_count, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            WorkoutExercise workoutExercise = workoutsDone.get(i);
            viewHolder.tvName.setText(workoutExercise.getName());
            viewHolder.tvCount.setText(String.valueOf(workoutExercise.getRepsCount()));
            viewHolder.tvTime.setText(workoutExercise.getTime() + ":59 " + (workoutExercise.getTime() >= 7 ? "A.M" : "P.M"));
            switch (workoutExercise.getTime()){
                case 7 :
//                    ((GradientDrawable)viewHolder.tvCount.getBackground()).setColor(ContextCompat.getColor(HomeNewActivity.this,R.color.blue_grey));
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_blue_grey);
                    break;
                case 8 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_brown);
                    break;
                case 9 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_amber);
                    break;
                case 10 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_green);
                    break;
                case 11 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_teel);
                    break;
                case 12 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_blue);
                    break;
                case 1 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_deep_purple);
                    break;
                case 2 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_purple);
                    break;
                case 3 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_lime);
                    break;
                case 4 :
                    viewHolder.tvCount.setBackgroundResource(R.drawable.count_bg_red);
                    break;
            }

            if(i == getItemCount() - 1){
                viewHolder.itemView.findViewById(R.id.divider).setVisibility(View.GONE);
            }else{
                viewHolder.itemView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return workoutsDone != null ? workoutsDone.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private TextView tvCount;
            private TextView tvName;
            private TextView tvTime;

            public ViewHolder(View itemView) {
                super(itemView);

                tvTime = (TextView)itemView.findViewById(R.id.tv_workout_time);
                tvName = (TextView)itemView.findViewById(R.id.tv_workout_name);
                tvCount = (TextView)itemView.findViewById(R.id.tv_count);

            }
        }

    }
}
