package com.oneminutebefore.workout;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.oneminutebefore.workout.helpers.HttpConnectException;
import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends BaseRequestActivity {

    private EditText etEmail;
    private Button btnSubmit;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(getTitle());
        }

        etEmail = (EditText)findViewById(R.id.et_email);
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
            String email = etEmail.getText().toString();
            String url = new UrlBuilder(UrlBuilder.API_FORGOT_PASSWORD)
                    .addParameters("email", email)
                    .build();

            final HttpTask httpTask = new HttpTask(false, ForgotPasswordActivity.this, HttpTask.METHOD_POST);
            httpTask.setmCallback(new HttpTask.HttpCallback() {
                @Override
                public void onResponse(String response) throws JSONException {
                    showProgress(false);
                    if(httpTask.getInterceptedCode() >= 200 && httpTask.getInterceptedCode() < 300){
                        JSONObject responseJson = new JSONObject(response);
                        String message = responseJson.optString("message");
                        Utils.showAlertDialog(ForgotPasswordActivity.this, getString(R.string.success), message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onException(Exception e) {
                    showProgress(false);
                    if(e instanceof HttpConnectException){
                        if(((HttpConnectException)e).getStatusCode() == 422){
                            JSONObject responseJson = null;
                            try {
                                responseJson = new JSONObject(e.getMessage());
                                String message = responseJson.optString("message");
                                Utils.showAlertDialog(ForgotPasswordActivity.this, getString(R.string.error), message, null);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }else if(!e.getMessage().equals(HttpConnectException.MSG_NO_INTERNET)){
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
                        }
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
                    }

                }
            });
            httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }else{
            showProgress(false);
        }
    }

    private boolean validateForm(){
        TextInputLayout tilEmail = ((TextInputLayout)findViewById(R.id.til_email));
        tilEmail.setError(null);

        String email = etEmail.getText().toString();
        if(TextUtils.isEmpty(email)){
            tilEmail.setError(getString(R.string.error_field_required));
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
