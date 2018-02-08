package com.oneminutebefore.workout.widgets;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.oneminutebefore.workout.BaseRequestActivity;
import com.oneminutebefore.workout.R;
import com.oneminutebefore.workout.WorkoutApplication;
import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileEditActivity extends BaseRequestActivity {

    private WorkoutApplication application;

    private EditText etFirstName, etEmail, etPhone;
    private Spinner spinnerTimeZone;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(getString(R.string.title_profile));
        }

        application = (WorkoutApplication) getApplication();

        etFirstName = (EditText) findViewById(R.id.et_first_name);
        findViewById(R.id.til_last_name).setVisibility(View.GONE);
        etEmail = (EditText) findViewById(R.id.et_email);
        findViewById(R.id.til_password).setVisibility(View.GONE);

        findViewById(R.id.til_reference_code).setVisibility(View.GONE);

        etPhone = (EditText) findViewById(R.id.et_phone);
        spinnerTimeZone = (Spinner) findViewById(R.id.spinner_time_zone);

        etFirstName.setHint(getString(R.string.name));

        final ArrayList<String> timeZones = new ArrayList<>();
        String[] array = getResources().getStringArray(R.array.time_zone_array);
        for(String str : array){
            timeZones.add(str);
        }

        ArrayAdapter<String> timezoneArrayAdapter = new ArrayAdapter<String>(ProfileEditActivity.this, android.R.layout.simple_spinner_item, timeZones);

        timezoneArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTimeZone.setAdapter(timezoneArrayAdapter);

        String url = new UrlBuilder(UrlBuilder.API_ME).build();
        HttpTask httpTask = new HttpTask(true, ProfileEditActivity.this, HttpTask.METHOD_GET);
        httpTask.setAuthorizationRequired(true, null);
        httpTask.setmCallback(new HttpTask.HttpCallback() {
            @Override
            public void onResponse(String response) throws JSONException {
                user = User.createFromJson(new JSONObject(response));
                application.setUser(user);
                application.setUserId(user.getId());
                SharedPrefsUtil.setStringPreference(ProfileEditActivity.this, Keys.KEY_USER, response);
                etFirstName.setText(user.getName());
                etPhone.setText(user.getPhone());
                etEmail.setText(user.getEmail());
                spinnerTimeZone.setSelection(timeZones.indexOf(user.getTimeZone()));
            }

            @Override
            public void onException(Exception e) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
            }
        });
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private void attemptUpdate() {
        setErrorFalse();
        if (isFeldNotEmpty()) {
            final String firstName = etFirstName.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            String timeZone = spinnerTimeZone.getSelectedItem().toString();
            final String phone = etPhone.getText().toString().trim();

            UrlBuilder builder = new UrlBuilder(UrlBuilder.API_REGISTER)
                    .addSection(user.getId())
                    .addParameters("name", firstName)
                    .addParameters("email", email)
                    .addParameters("time_zone", timeZone)
                    .addParameters("phone", phone);

            HttpTask httpTask = new HttpTask(true, ProfileEditActivity.this, HttpTask.METHOD_PUT);
            httpTask.setAuthorizationRequired(true, null);
            httpTask.setmCallback(new HttpTask.HttpCallback() {
                @Override
                public void onResponse(String response) throws JSONException {

                    Toast.makeText(ProfileEditActivity.this, getString(R.string.info_saved), Toast.LENGTH_SHORT).show();

                    user.setName(etFirstName.getText().toString().trim());
                    user.setEmail(etEmail.getText().toString().trim());
                    user.setTimeZone(spinnerTimeZone.getSelectedItem().toString());
                    user.setPhone(etPhone.getText().toString().trim());
                    SharedPrefsUtil.setStringPreference(ProfileEditActivity.this, Keys.KEY_USER, user.getJson().toString());
                    finish();

                }

                @Override
                public void onException(Exception e) {
                    try {
                        JSONObject jsonObject = new JSONObject(e.getMessage());
                        JSONObject error=jsonObject.getJSONObject("errors");
                        String message = null;
                        if(error.has("email")){
                            message = error.getJSONObject("email").optString("message");
                        }
                        String emailError = "The specified email address is already in use.";
                        if(message!=null && message.equalsIgnoreCase(emailError)){
                            ((TextInputLayout)findViewById(R.id.til_email)).setError(emailError);
                        }
                        else{
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, builder.build());

        }
    }

    private boolean isFeldNotEmpty() {
        if (TextUtils.isEmpty(etFirstName.getText().toString())) {
            ((TextInputLayout) findViewById(R.id.til_first_name)).setError(getString(R.string.error_field_required));
            etFirstName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            ((TextInputLayout) findViewById(R.id.til_email)).setError(getString(R.string.error_field_required));
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText().toString())) {
            ((TextInputLayout) findViewById(R.id.til_phone)).setError(getString(R.string.error_field_required));
            etPhone.requestFocus();
            return false;
        }
        return true;
    }

    private void setErrorFalse() {
        ((TextInputLayout) findViewById(R.id.til_first_name)).setError(null);
        ((TextInputLayout) findViewById(R.id.til_email)).setError(null);
        ((TextInputLayout) findViewById(R.id.til_phone)).setError(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_done:
                attemptUpdate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
