package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.models.User;
import com.oneminutebefore.workout.widgets.SwipeDisabledViewPager;

import org.json.JSONException;
import org.json.JSONObject;

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

//        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player_layout);
//        youTubePlayerFragment.initialize(Constants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                youTubePlayer.setShowFullscreenButton(true);
//                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
//                youTubePlayer.cueVideo("WngOnjCVcqU");
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });

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

    private void fetchUserInfo(){

        String url = new UrlBuilder(UrlBuilder.API_ME).build();
        HttpTask httpTask = new HttpTask(true,MainActivity.this,HttpTask.METHOD_GET);
        httpTask.setAuthorizationRequired(true);
        httpTask.setmCallback(new HttpTask.HttpCallback() {
            @Override
            public void onResponse(String response) throws JSONException {
                User user = User.createFromJson(new JSONObject(response));
                WorkoutApplication application = ((WorkoutApplication)getApplication());
                application.setUser(user);
                application.setUserId(user.getId());
                SharedPrefsUtil.setStringPreference(MainActivity.this, Keys.KEY_USER, response);
//                SharedPrefsUtil.setStringPreference(MainActivity.this, Keys.getUserLevelKey(MainActivity.this), user.getUserLevel());
                switchToHomeActivity();
            }

            @Override
            public void onException(Exception e) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
            }
        });
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    @Override
    public void onLoginSuccessFul() {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.login_successful), Snackbar.LENGTH_SHORT).show();
        fetchUserInfo();
    }

    @Override
    public void onSignUpClicked() {
        vpLogin.setCurrentItem(1);
    }

    @Override
    public void onRegisterSuccessFul() {
        Toast.makeText(MainActivity.this, "Registered", Toast.LENGTH_LONG).show();
        fetchUserInfo();
    }

    private void switchToHomeActivity(){
        Intent intent = new Intent(this, HomeNewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
