package com.oneminutebefore.workout;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.oneminutebefore.workout.helpers.HttpConnectException;
import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;
import com.oneminutebefore.workout.helpers.VolleyHelper;
import com.oneminutebefore.workout.models.User;
import com.oneminutebefore.workout.models.WorkoutCategory;
import com.oneminutebefore.workout.models.WorkoutExercise;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends BaseRequestActivity {

    private boolean videosSaved;
    private boolean categoriesSaved;
    private WorkoutApplication application;
    private String referralCode;
    private boolean doSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Uri data = getIntent().getData();
        if(data != null && data.getScheme().equals("http")){
            String signupUrl = data.toString();
            if(!TextUtils.isEmpty(signupUrl)){
                doSignup = true;
                String codeVariable = "code=";
                if(signupUrl.contains(codeVariable)){
                    int beginIndex = signupUrl.lastIndexOf(codeVariable) + codeVariable.length();
                    referralCode = signupUrl.substring(beginIndex);
                    Toast.makeText(getApplicationContext(), "referral code : " + referralCode, Toast.LENGTH_SHORT).show();
                }
            }
        }

        boolean areLinksDownloaded = false; // SharedPrefsUtil.getBooleanPreference(this, Keys.KEY_LINKS_DOWNLOADED, false);
        boolean areCategoriesDownloaded = false; // SharedPrefsUtil.getBooleanPreference(this, Keys.KEY_CATEGORIES_DOWNLOADED, false);

        application = WorkoutApplication.getmInstance();


        if (!areLinksDownloaded) {
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
        } else {
            String workoutsJson = SharedPrefsUtil.getStringPreference(SplashActivity.this, Keys.KEY_VIDEOS_INFO, "[]");
            application.setWorkouts(WorkoutExercise.createMapFromJson(workoutsJson));
            videosSaved = true;
            checkLoginAndRedirect();
        }
        if (!areCategoriesDownloaded) {
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
        } else {
            String categoriesJson = SharedPrefsUtil.getStringPreference(SplashActivity.this, Keys.KEY_CATEGORIES_INFO, "[]");
            application.setWorkoutCategories(WorkoutCategory.createMapFromJson(categoriesJson));
            categoriesSaved = true;
            checkLoginAndRedirect();
        }
    }

    private void checkLoginAndRedirect() {

        // Ensure that the required data is fetched
        if (!videosSaved || !categoriesSaved) return;

        String token = SharedPrefsUtil.getStringPreference(SplashActivity.this, Keys.KEY_TOKEN, "");
        WorkoutApplication application = WorkoutApplication.getmInstance();
        application.setSessionToken(token);
        if (!token.equals("")) {
            WorkoutApplication.getmInstance().setSessionToken(token);
            fetchUserInfo();
        } else {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            mainIntent.putExtra(MainActivity.KEY_REFERRAL_CODE, referralCode);
            mainIntent.putExtra(MainActivity.KEY_DO_SIGNUP, doSignup);
            startActivity(mainIntent);
        }
    }

    private void initUser(String json) throws JSONException {
        User user = User.createFromJson(new JSONObject(json));
        WorkoutApplication application = ((WorkoutApplication) getApplication());
        application.setUser(user);
        application.setUserId(user.getId());
//        SharedPrefsUtil.setStringPreference(SplashActivity.this, Keys.getUserLevelKey(SplashActivity.this), user.getUserLevel());
//        application.setDbHelper(new DBHelper(SplashActivity.this));
        Intent intent = new Intent(SplashActivity.this, HomeNewActivity.class);
        startActivity(intent);
    }

    private void fetchUserInfo() {

        String userJson = SharedPrefsUtil.getStringPreference(SplashActivity.this, Keys.KEY_USER, "");
        if (!TextUtils.isEmpty(userJson)) {
            try {
                initUser(userJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String url = new UrlBuilder(UrlBuilder.API_ME).build();
            HttpTask httpTask = new HttpTask(true, SplashActivity.this, HttpTask.METHOD_GET);
            httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
                @Override
                public void onSessionTimeout() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            httpTask.setmCallback(new HttpTask.HttpCallback() {
                @Override
                public void onResponse(String response) throws JSONException {
                    initUser(response);
                    SharedPrefsUtil.setStringPreference(SplashActivity.this, Keys.KEY_USER, response);
                }

                @Override
                public void onException(Exception e) {
                    if (!(e instanceof HttpConnectException && e.getMessage().equals(HttpConnectException.MSG_NO_INTERNET))) {
                        Toast.makeText(SplashActivity.this, getString(R.string.some_error_occured), Toast.LENGTH_SHORT).show();
                        Utils.clearUserData(SplashActivity.this);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                }
            });
            httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
    }

    private void saveLinks(String linksData) {

        HashMap<String, WorkoutExercise> map = WorkoutExercise.createMapFromJson(linksData);
        if (map != null && !map.isEmpty()) {
            application.setWorkouts(map);
            SharedPrefsUtil.setStringPreference(SplashActivity.this, Keys.KEY_VIDEOS_INFO, linksData);
            SharedPrefsUtil.setBooleanPreference(SplashActivity.this, Keys.KEY_LINKS_DOWNLOADED, true);
        }
        videosSaved = true;
        checkLoginAndRedirect();
    }

    private void saveCategories(String linksData) {

        HashMap<String, WorkoutCategory> map = WorkoutCategory.createMapFromJson(linksData);
        if (map != null && !map.isEmpty()) {
            application.setWorkoutCategories(map);
            SharedPrefsUtil.setStringPreference(SplashActivity.this, Keys.KEY_CATEGORIES_INFO, linksData);
            SharedPrefsUtil.setBooleanPreference(SplashActivity.this, Keys.KEY_CATEGORIES_DOWNLOADED, true);
        }
        categoriesSaved = true;
        checkLoginAndRedirect();
    }
}
