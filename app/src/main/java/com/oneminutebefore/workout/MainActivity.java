package com.oneminutebefore.workout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.oneminutebefore.workout.helpers.DBHelper;
import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.User;
import com.oneminutebefore.workout.widgets.SwipeDisabledViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseRequestActivity implements LoginFragment.LoginInteractionListener, RegisterFragment.RegisterInteractionListener {

    private ViewPager vpLogin;
    private WorkoutApplication application;
    private int workoutsFetchStatus;
    private int userInfoFetchStatus;

    private static final int INFO_FETCH_STATUS_SUCCESS = 1;
    private static final int INFO_FETCH_STATUS_FAILURE = -1;
    private static final int INFO_FETCH_STATUS_INCOMPLETE = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = ((WorkoutApplication) getApplication());
        if (application.getDbHelper() == null) {
            application.setDbHelper(new DBHelper(MainActivity.this));
        }
        vpLogin = (ViewPager) findViewById(R.id.vp_forms);
        ViewPagerAdapter formsAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        formsAdapter.addItem(new LoginFragment());
        formsAdapter.addItem(new RegisterFragment());
        vpLogin.setAdapter(formsAdapter);
        ((SwipeDisabledViewPager) vpLogin).setPagingEnabled(false);

    }

    private void fetchSelectedWorkoutsInfo() {

        String url = new UrlBuilder(UrlBuilder.API_GET_WORKOUTS).build();
        HttpTask httpTask = new HttpTask(false, MainActivity.this, HttpTask.METHOD_GET);
        httpTask.setAuthorizationRequired(true, null);
        httpTask.setmCallback(new HttpTask.HttpCallback() {
            @Override
            public void onResponse(String response) throws JSONException {
                JSONArray workoutsArray = new JSONArray(response);
                if (workoutsArray != null && workoutsArray.length() > 0) {
                    for (int i = 0; i < workoutsArray.length(); i++) {
                        application.getDbHelper().insertSelectedWorkout(workoutsArray.getJSONObject(i), true,true);
                    }
                }
                HashMap<String, SelectedWorkout> selectedWorkoutHashMap = application.getDbHelper().getSelectedWorkouts();
                if (selectedWorkoutHashMap != null && !selectedWorkoutHashMap.isEmpty()) {
                    for (Map.Entry entry : selectedWorkoutHashMap.entrySet()) {
                        SharedPrefsUtil.setBooleanPreference(MainActivity.this, entry.getKey().toString(), true);
                        SharedPrefsUtil.setStringPreference(MainActivity.this, "list_" + entry.getKey().toString(), ((SelectedWorkout) entry.getValue()).getId());
                    }
                }
                workoutsFetchStatus = INFO_FETCH_STATUS_SUCCESS;
                if (userInfoFetchStatus != INFO_FETCH_STATUS_INCOMPLETE) {
                    showProgress(false);
                    switchToHomeActivity();
                }
            }

            @Override
            public void onException(Exception e) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
                workoutsFetchStatus = INFO_FETCH_STATUS_FAILURE;
                if (userInfoFetchStatus != INFO_FETCH_STATUS_INCOMPLETE) {
                    showProgress(false);
                    if (userInfoFetchStatus == INFO_FETCH_STATUS_SUCCESS) {
                        switchToHomeActivity();
                    }
                }
            }
        });
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private void showProgress(boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void fetchUserInfo() {

        String url = new UrlBuilder(UrlBuilder.API_ME).build();
        HttpTask httpTask = new HttpTask(false, MainActivity.this, HttpTask.METHOD_GET);
        httpTask.setAuthorizationRequired(true, null);
        httpTask.setmCallback(new HttpTask.HttpCallback() {
            @Override
            public void onResponse(String response) throws JSONException {
                User user = User.createFromJson(new JSONObject(response));
                application.setUser(user);
                application.setUserId(user.getId());
                SharedPrefsUtil.setStringPreference(MainActivity.this, Keys.KEY_USER, response);
//                SharedPrefsUtil.setStringPreference(MainActivity.this, Keys.getUserLevelKey(MainActivity.this), user.getUserLevel());
                userInfoFetchStatus = INFO_FETCH_STATUS_SUCCESS;
                if (workoutsFetchStatus != INFO_FETCH_STATUS_INCOMPLETE) {
                    showProgress(false);
                    switchToHomeActivity();
                }
            }

            @Override
            public void onException(Exception e) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
                userInfoFetchStatus = INFO_FETCH_STATUS_FAILURE;
                if (workoutsFetchStatus != INFO_FETCH_STATUS_INCOMPLETE) {
                    showProgress(false);
                    if (workoutsFetchStatus == INFO_FETCH_STATUS_SUCCESS) {
                        switchToHomeActivity();
                    }
                }
            }
        });
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    @Override
    public void onLoginSuccessFul() {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.login_successful), Snackbar.LENGTH_SHORT).show();
        fetchInfo();
    }

    @Override
    public void onSignUpClicked() {
        vpLogin.setCurrentItem(1);
    }

    @Override
    public void onRegisterSuccessFul() {
        Toast.makeText(MainActivity.this, "Registered", Toast.LENGTH_LONG).show();
        fetchInfo();
    }

    private void fetchInfo() {
        showProgress(true);
        fetchSelectedWorkoutsInfo();
        fetchUserInfo();
    }

    private void switchToHomeActivity() {
        Intent intent = new Intent(this, HomeNewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSignInClicked() {
        vpLogin.setCurrentItem(0);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private void addItem(Fragment fragment) {
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
