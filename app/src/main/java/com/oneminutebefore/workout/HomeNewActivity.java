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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;

import java.text.SimpleDateFormat;
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

//        YouTubeThumbnailView youTubeThumbnailView = (YouTubeThumbnailView)findViewById(R.id.yt_sample_1);
//        youTubeThumbnailView.initialize(Constants.DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
//                youTubeThumbnailLoader.setVideo("s7CNC9irjt0");
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
//        youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeNewActivity.this, YoutubePlayerActivity.class);
//                intent.putExtra(YoutubePlayerActivity.KEY_LINK,"s7CNC9irjt0");
//                startActivity(intent);
//            }
//        });
//
//        YouTubeThumbnailView youTubeThumbnailView2 = (YouTubeThumbnailView)findViewById(R.id.yt_sample_2);
//        youTubeThumbnailView2.initialize(Constants.DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
//                youTubeThumbnailLoader.setVideo("s7CNC9irjt0");
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
//        youTubeThumbnailView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeNewActivity.this, YoutubePlayerActivity.class);
//                intent.putExtra(YoutubePlayerActivity.KEY_LINK,"s7CNC9irjt0");
//                startActivity(intent);
//            }
//        });

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
        }else{
            menu.findItem(R.id.action_sign_up).setVisible(false);
            menu.findItem(R.id.action_setting).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(true);
        }


    }


    private void resetTimer(){
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
}
