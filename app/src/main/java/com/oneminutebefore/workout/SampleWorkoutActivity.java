package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.oneminutebefore.workout.models.WorkoutExercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SampleWorkoutActivity extends AppCompatActivity {

    private WorkoutApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_workout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getTitle());
        }

        RecyclerView rclSample = (RecyclerView) findViewById(R.id.sample_list);
        application = WorkoutApplication.getmInstance();
        if(application.getWorkouts() != null && !application.getWorkouts().isEmpty()){
            rclSample.setLayoutManager(new GridLayoutManager(this,2));
            rclSample.setAdapter(new SampleAdapter(application.getWorkouts()));
        }
    }

    private class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder>{

        private ArrayList<WorkoutExercise> workouts;

        SampleAdapter(HashMap<String, WorkoutExercise> workoutExerciseHashMap){

            workouts = new ArrayList<>();
            for(Map.Entry entry : workoutExerciseHashMap.entrySet()){
                workouts.add((WorkoutExercise) entry.getValue());
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_sample_exercise, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            final WorkoutExercise workoutExercise = workouts.get(position);
            int substringIndex = workoutExercise.getVideoLink().lastIndexOf("=") + 1;
            if(substringIndex < 0){
                substringIndex = workoutExercise.getVideoLink().lastIndexOf("/") + 1;
            }
            final String link = workoutExercise.getVideoLink().substring(substringIndex);
            holder.tvName.setText(workoutExercise.getName());

            holder.youTubeThumbnailView.initialize(Constants.DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo(link);
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    System.out.println("init failure");
                }
            });
            holder.youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SampleWorkoutActivity.this, YoutubePlayerActivity.class);
                    final WorkoutExercise workoutExercise = workouts.get(holder.getAdapterPosition());
                    int substringIndex = workoutExercise.getVideoLink().lastIndexOf("=") + 1;
                    if(substringIndex < 0){
                        substringIndex = workoutExercise.getVideoLink().lastIndexOf("/") + 1;
                    }
                    final String link = workoutExercise.getVideoLink().substring(substringIndex);
                    intent.putExtra(YoutubePlayerActivity.KEY_LINK, link);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return workouts != null ? workouts.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private YouTubeThumbnailView youTubeThumbnailView;
            private TextView tvName;

            public ViewHolder(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.txt_name);
                youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.thumb_video);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
