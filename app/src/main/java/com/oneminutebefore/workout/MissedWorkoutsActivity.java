package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oneminutebefore.workout.models.CompletedWorkout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MissedWorkoutsActivity extends AppCompatActivity {

    private TextView tvNoWorkouts;

    private RecyclerView rclWorkouts;

    private ArrayList<CompletedWorkout> missedWorkouts;

    private WorkoutApplication application;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_workouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        tvNoWorkouts = (TextView) findViewById(R.id.tv_no_workout);
        rclWorkouts = (RecyclerView) findViewById(R.id.rcl_workouts);

        application = (WorkoutApplication) getApplication();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {
        missedWorkouts = application.getDbHelper().getMissedWorkouts();
        if(missedWorkouts != null && !missedWorkouts.isEmpty()){
            rclWorkouts.setVisibility(View.VISIBLE);
            tvNoWorkouts.setVisibility(View.GONE);
            rclWorkouts.setAdapter(new MissedWorkoutAdapter());
        }else{
            rclWorkouts.setVisibility(View.GONE);
            tvNoWorkouts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MissedWorkoutAdapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_workout_count, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final CompletedWorkout completedWorkout = missedWorkouts.get(position);
            holder.tvName.setText(completedWorkout.getName());
            holder.tvDate.setText(dateFormat.format(completedWorkout.getDate()) + " " + completedWorkout.getTimeMeridian());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
                    intent.putExtra(VideoPlayerActivity.KEY_WORKOUT, completedWorkout);
                    intent.putExtra(VideoPlayerActivity.KEY_TIME_MILLIS, completedWorkout.getDate());
                    intent.putExtra(VideoPlayerActivity.KEY_USER_TRACK_ID, completedWorkout.getCompletedWorkoutId());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return missedWorkouts != null ? missedWorkouts.size() : 0;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView)itemView.findViewById(R.id.tv_workout_name);
            tvDate = (TextView)itemView.findViewById(R.id.tv_workout_time);
            itemView.findViewById(R.id.tv_count).setVisibility(View.GONE);
        }
    }
}
