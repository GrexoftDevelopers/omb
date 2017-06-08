package com.oneminutebefore.workout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.oneminutebefore.workout.helpers.HttpConnectException;
import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;
import com.oneminutebefore.workout.helpers.VolleyHelper;
import com.oneminutebefore.workout.models.User;

import org.json.JSONException;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etCurrentPassword;
    private Button btnSubmit;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(getTitle());
        }

        etPassword = (EditText)findViewById(R.id.et_password);
        etConfirmPassword = (EditText)findViewById(R.id.et_confirm_password);
        etCurrentPassword = (EditText)findViewById(R.id.et_current_password);
        btnSubmit = (Button)findViewById(R.id.btn_submit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        showProgress(false);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atteptSubmit();
            }
        });

    }

    private void atteptSubmit() {
        showProgress(true);
        if(validateForm()){
            String password = etPassword.getText().toString();
            String currentPassword = etCurrentPassword.getText().toString();
            User user = WorkoutApplication.getmInstance().getUser();
            if(user != null){
                String url = new UrlBuilder(UrlBuilder.API_REGISTER)
                        .addSection(user.getId())
                        .addSection("password")
                        .addParameters("oldPassword", currentPassword)
                        .addParameters("newPassword", password)
                        .build();

                final HttpTask httpTask = new HttpTask(false, ChangePasswordActivity.this, HttpTask.METHOD_PUT);
                httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
                    @Override
                    public void onSessionTimeout() {
                        startActivity(Utils.getSessionTimeoutIntent(ChangePasswordActivity.this));
                    }
                });
                httpTask.setmCallback(new HttpTask.HttpCallback() {
                    @Override
                    public void onResponse(String response) throws JSONException {
                        showProgress(false);
                        if(httpTask.getInterceptedCode() >= 200 && httpTask.getInterceptedCode() < 300){
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.msg_password_changed), Snackbar.LENGTH_SHORT).show();
                            etConfirmPassword.setText(null);
                            etPassword.setText(null);
                            etCurrentPassword.setText(null);
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        showProgress(false);
                        if(e instanceof HttpConnectException){
                            if(((HttpConnectException)e).getStatusCode() == 403){
                                ((TextInputLayout)findViewById(R.id.til_current_password)).setError(getString(R.string.error_invalid_current_password));
                            }else if(!e.getMessage().equals(HttpConnectException.MSG_NO_INTERNET)){
                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
                            }
                        }else{
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
                httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

//                VolleyHelper volleyHelper = new VolleyHelper(ChangePasswordActivity.this, false);
//                volleyHelper.callApi(Request.Method.PUT, url, null, new VolleyHelper.VolleyCallback() {
//                    @Override
//                    public void onSuccess(String result) throws JSONException {
//                        showProgress(false);
//                        Snackbar
//                                .make(findViewById(android.R.id.content)
//                                        , getString(R.string.msg_password_changed)
//                                        , Snackbar.LENGTH_SHORT)
//                                .show();
//                        etConfirmPassword.setText(null);
//                        etPassword.setText(null);
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        showProgress(false);
//                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
//                    }
//                });
            }
        }else{
            showProgress(false);
        }
    }

    private boolean validateForm(){
        TextInputLayout tilPassword = ((TextInputLayout)findViewById(R.id.til_password));
        tilPassword.setError(null);
        TextInputLayout tilConfirmPassword = ((TextInputLayout)findViewById(R.id.til_confirm_password));
        tilConfirmPassword.setError(null);
        TextInputLayout tilCurrentPassword = ((TextInputLayout)findViewById(R.id.til_current_password));
        tilCurrentPassword.setError(null);

        String currentPassword = etCurrentPassword.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(currentPassword)){
            tilCurrentPassword.setError(getString(R.string.error_no_current_password));
            return false;
        }
        if(TextUtils.isEmpty(password)){
            tilPassword.setError(getString(R.string.error_no_password));
            return false;
        }
        if(password.length() < 4){
            tilPassword.setError(getString(R.string.error_invalid_password));
            return false;
        }
        if(TextUtils.isEmpty(confirmPassword) || !confirmPassword.equals(password)){
            tilConfirmPassword.setError(getString(R.string.error_confirm_password));
            return false;
        }
        return true;
    }

    private void showProgress(boolean b) {
        btnSubmit.setEnabled(!b);
        progressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
