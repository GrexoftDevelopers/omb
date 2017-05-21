package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.oneminutebefore.workout.models.WorkoutExercise;

public class VideoPlayerActivity extends AppCompatActivity {

    public static final String KEY_URL = "url";
    public static final String KEY_WORKOUT = "workout";

    private YouTubePlayerFragment youTubePlayerFragment;

    private WorkoutExercise workoutExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent mIntent = getIntent();
//        final String url = mIntent.getStringExtra(KEY_URL);
        workoutExercise = (WorkoutExercise) mIntent.getSerializableExtra(KEY_WORKOUT);
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player_layout);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle(workoutExercise.getName());
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (!TextUtils.isEmpty(workoutExercise.getVideoLink())) {
            findViewById(R.id.youtube_content).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_no_content).setVisibility(View.GONE);
            youTubePlayerFragment.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.setShowFullscreenButton(true);
                    String url = workoutExercise.getVideoLink();
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                    youTubePlayer.cueVideo(url.substring(url.lastIndexOf("/") + 1));
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
        } else {
            findViewById(R.id.youtube_content).setVisibility(View.GONE);
            findViewById(R.id.layout_no_content).setVisibility(View.VISIBLE);
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
//        youTubePlayerFragment.onDestroy();
        super.onDestroy();
    }
}
