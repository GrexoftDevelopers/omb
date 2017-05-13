package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.oneminutebefore.workout.widgets.SwipeDisabledViewPager;

import java.util.ArrayList;

public class MainActivity extends BaseRequestActivity implements LoginFragment.LoginInteractionListener,RegisterFragment.RegisterInteractionListener{

    private ViewPager vpLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        ViewPager vpSlide = (ViewPager)findViewById(R.id.vp_slide_show);
//        ViewPagerAdapter sliderAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        sliderAdapter.addItem(ImageFragment.newInstance(R.drawable.slide_1));
//        sliderAdapter.addItem(ImageFragment.newInstance(R.drawable.slide_2));
//        sliderAdapter.addItem(ImageFragment.newInstance(R.drawable.slide_3));
//        vpSlide.setAdapter(sliderAdapter);

//        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtube_player);
//        youTubePlayerView.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                youTubePlayer.setShowFullscreenButton(true);
//                youTubePlayer.cueVideo("https://youtu.be/WngOnjCVcqU");
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });

        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player_layout);
        youTubePlayerFragment.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.setShowFullscreenButton(true);
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                youTubePlayer.cueVideo("WngOnjCVcqU");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.player_layout, YoutubePlayerFragment.newInstance("https://youtu.be/WngOnjCVcqU"))
//                .commit();

        vpLogin = (ViewPager) findViewById(R.id.vp_forms);
        ViewPagerAdapter formsAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        formsAdapter.addItem(new LoginFragment());
        formsAdapter.addItem(new RegisterFragment());
        vpLogin.setAdapter(formsAdapter);
        ((SwipeDisabledViewPager)vpLogin).setPagingEnabled(false);

    }

    @Override
    public void onLoginSuccessFul() {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.login_successful), Snackbar.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onSignUpClicked() {
        vpLogin.setCurrentItem(1);
    }

    @Override
    public void onRegisterSuccessFul() {
        Toast.makeText(MainActivity.this, "Registered", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onSignInClicked() {
        vpLogin.setCurrentItem(0);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter{


        private ArrayList<Fragment> fragments = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private void addItem(Fragment fragment){
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments != null && fragments.size() > position ? fragments.get(position) : null;
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }
    }

}
