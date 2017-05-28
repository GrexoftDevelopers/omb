package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.Request;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.VolleyHelper;
import com.oneminutebefore.workout.models.WorkoutCategory;
import com.oneminutebefore.workout.models.WorkoutExercise;

import org.json.JSONException;

import java.util.HashMap;

import static com.oneminutebefore.workout.WorkoutApplication.getmInstance;

public class SplashActivity extends BaseRequestActivity {

    private boolean videosSaved;
    private boolean categoriesSaved;
    private WorkoutApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        boolean areLinksDownloaded = SharedPrefsUtil.getBooleanPreference(this, Keys.KEY_LINKS_DOWNLOADED, false);
        boolean areCategoriesDownloaded = SharedPrefsUtil.getBooleanPreference(this, Keys.KEY_CATEGORIES_DOWNLOADED, false);

        application = WorkoutApplication.getmInstance();


        if(!areLinksDownloaded){
            VolleyHelper volleyHelper = new VolleyHelper(this, false);
            String url = new UrlBuilder(UrlBuilder.API_ALL_VIDEOS).build();
            volleyHelper.callApi(Request.Method.GET, url, null, new VolleyHelper.VolleyCallback() {
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
            String workoutsJson = SharedPrefsUtil.getStringPreference(SplashActivity.this, Keys.KEY_VIDEOS_INFO, "[]");
            application.setWorkouts(WorkoutExercise.createMapFromJson(workoutsJson));
            videosSaved = true;
            checkLoginAndRedirect();
        }
        if(!areCategoriesDownloaded){
            VolleyHelper volleyHelper = new VolleyHelper(this, false);
            String url = new UrlBuilder(UrlBuilder.API_ALL_CATEGORIES).build();
            volleyHelper.callApi(Request.Method.GET, url, null, new VolleyHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    saveCategories(result);
                }

                @Override
                public void onError(String error) {
                    saveCategories(null);
                }
            });
        }else{
            String categoriesJson = SharedPrefsUtil.getStringPreference(SplashActivity.this, Keys.KEY_CATEGORIES_INFO, "[]");
            application.setWorkoutCategories(WorkoutCategory.createMapFromJson(categoriesJson));
            categoriesSaved = true;
            checkLoginAndRedirect();
        }
    }

    private void checkLoginAndRedirect(){

        // Ensure that the required data is fetched
        if(!videosSaved || !categoriesSaved) return;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String token = SharedPrefsUtil.getStringPreference(SplashActivity.this, Keys.KEY_TOKEN, "");
                WorkoutApplication application = WorkoutApplication.getmInstance();
                application.setSessionToken(token);
                if(!token.equals("")){
                    getmInstance().setSessionToken(token);
                    startActivity(new Intent(SplashActivity.this, HomeNewActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
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

        HashMap<String, WorkoutExercise> map = WorkoutExercise.createMapFromJson(linksData);
        if(map != null && !map.isEmpty()){
            SharedPrefsUtil.setStringPreference(SplashActivity.this,Keys.KEY_VIDEOS_INFO, linksData);
            SharedPrefsUtil.setBooleanPreference(SplashActivity.this,Keys.KEY_LINKS_DOWNLOADED, true);
        }
        videosSaved = true;
        checkLoginAndRedirect();
    }

    private void saveCategories(String linksData){

        HashMap<String, WorkoutCategory> map = WorkoutCategory.createMapFromJson(linksData);
        if(map != null && !map.isEmpty()){
            SharedPrefsUtil.setStringPreference(SplashActivity.this,Keys.KEY_CATEGORIES_INFO, linksData);
            SharedPrefsUtil.setBooleanPreference(SplashActivity.this,Keys.KEY_CATEGORIES_DOWNLOADED, true);
        }
        categoriesSaved = true;
        checkLoginAndRedirect();
    }
}
