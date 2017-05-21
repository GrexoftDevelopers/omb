package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class VideoPlayerActivity extends AppCompatActivity {

    private YouTubePlayerFragment youTubePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle("OMB");
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent mIntent = getIntent();
        final String url = mIntent.getStringExtra("URL");
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        if (!TextUtils.isEmpty(url)) {
            findViewById(R.id.youtube_content).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_no_content).setVisibility(View.GONE);
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
        youTubePlayerFragment.onDestroy();
        super.onDestroy();
    }
}
