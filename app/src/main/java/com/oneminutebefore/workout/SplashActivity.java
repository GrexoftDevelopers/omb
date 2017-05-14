package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.VolleyHelper;

import org.json.JSONException;

public class SplashActivity extends BaseRequestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        boolean areLinksDownloaded = SharedPrefsUtil.getBooleanPreference(this, Keys.KEY_LINKS_DOWNLOADED, false);

        if(!areLinksDownloaded){
            VolleyHelper volleyHelper = new VolleyHelper(this, false);
            volleyHelper.callApiGet("www.google.com", new VolleyHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    saveLinks(result);
                }

                @Override
                public void onError(String error) {
                    saveLinks(null);
                }
            });
        }else{
            checkLoginAndRedirect();
        }
    }

    private void checkLoginAndRedirect(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                String userId = SharedPrefsUtil.getStringPreference(SplashActivity.this, Keys.KEY_USER_ID, "-1");
//                if(!userId.equals("-1")){
//                    WorkoutApplication.getmInstance().setUserId(userId);
//                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
//                }else{
//                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                }
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
        },2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               finish();
            }
        }, 3000);

    }

    private void saveLinks(String linksData){

        // TODO : replace this with parsing logic to save original links
        for(String key : Keys.KEY_VIDEO_LINKS){
            SharedPrefsUtil.setStringPreference(SplashActivity.this, key, "zlsZYXeydas");
        }

        checkLoginAndRedirect();

    }
}
