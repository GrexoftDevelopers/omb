package com.oneminutebefore.workout.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.oneminutebefore.workout.BaseRequestActivity;
import com.oneminutebefore.workout.R;
import com.oneminutebefore.workout.WorkoutApplication;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tahir on 28/5/17.
 */

public class HttpTask extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;

    private boolean showProgress;

    private Context mContext;

    private Exception exception;

    private HttpCallback mCallback;

    private static final String TAG = "http workout";

    private String interceptedResponse;

    private boolean isAuthorizationRequired;

    private int requestMethod;

    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;
    public static final int METHOD_PUT = 3;
    private int interceptedCode;

    public void setmCallback(HttpCallback mCallback) {
        this.mCallback = mCallback;
    }

    public HttpTask(boolean showProgress, Context mContext) {
        this.showProgress = showProgress;
        this.mContext = mContext;
        requestMethod = METHOD_POST;
        if (mContext instanceof BaseRequestActivity) {
            ((BaseRequestActivity) mContext).addTask(this);
        }
    }

    public HttpTask(boolean showProgress, Context mContext, int requestMethod) {
        this.showProgress = showProgress;
        this.mContext = mContext;
        this.requestMethod = requestMethod;
        if (mContext instanceof BaseRequestActivity) {
            ((BaseRequestActivity) mContext).addTask(this);
        }
    }

    public void setAuthorizationRequired(boolean authorizationRequired) {
        isAuthorizationRequired = authorizationRequired;
    }

    private static final int mConnectionTimeout = 20000;

    private static final int mSocketTimeout = 60000;

    public int getInterceptedCode() {
        return interceptedCode;
    }

    private String postContent(Context context, String urlWithParams, String data) throws HttpConnectException {
        return postContent(context, urlWithParams, data, new DefaultHttpClient());
    }

    private String postContent(Context context, String urlWithParams, String data, DefaultHttpClient client) throws HttpConnectException {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                final int paramsIndex = urlWithParams.indexOf('?');
                String url = paramsIndex > 0 ? urlWithParams.substring(0, paramsIndex) : urlWithParams;
                Log.d(TAG, "url :  " + url);
                HttpPost postMethod = new HttpPost(url);
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, mConnectionTimeout);
                HttpConnectionParams.setSoTimeout(httpParams, mSocketTimeout);

                if (paramsIndex > 0) {
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    String paramsArray[] = urlWithParams.substring(paramsIndex + 1).split("&");
                    for (String param : paramsArray) {
                        String nameValuePair[] = param.split("=");
                        Log.d(TAG, "param : " + nameValuePair[0] + "=" + nameValuePair[1]);
                        httpParams.setParameter(nameValuePair[0], nameValuePair[1]);
                        nameValuePairs.add(new BasicNameValuePair(nameValuePair[0], nameValuePair[1]));
                    }
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    postMethod.setEntity(urlEncodedFormEntity);
                }

                ResponseHandler<String> res = new BasicResponseHandler();

                postMethod.setHeader("Content-Type", "application/x-www-form-urlencoded");

                if (isAuthorizationRequired) {
                    postMethod.setHeader("Authorization", "Bearer " + WorkoutApplication.getmInstance().getSessionToken());
                }

                client.addResponseInterceptor(new HttpResponseInterceptor() {
                    @Override
                    public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
                        interceptedCode = httpResponse.getStatusLine().getStatusCode();
                        if (interceptedCode < 200 || interceptedCode >= 400) {
                            InputStream is = httpResponse.getEntity().getContent();
                            interceptedResponse = Utils.convertStreamToString(is);
                            Log.d(TAG, "content length : " + httpResponse.getEntity().getContentLength());
                            Log.d(TAG, "response body : " + interceptedResponse);
                        }
                    }
                });

                return client.execute(postMethod, res);

            } catch (Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpResponseException) {
                    HttpConnectException e = new HttpConnectException(interceptedResponse, interceptedCode);
                    throw e;
                } else {
                    throw new HttpConnectException(t.getMessage());
                }
            }
        } else {
            throw new HttpConnectException(HttpConnectException.MSG_NO_INTERNET);
        }
    }

    private String putContent(Context context, String urlWithParams, String data, DefaultHttpClient client) throws HttpConnectException {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                final int paramsIndex = urlWithParams.indexOf('?');
                String url = paramsIndex > 0 ? urlWithParams.substring(0, paramsIndex) : urlWithParams;
                Log.d(TAG, "url :  " + url);
                HttpPut putMethod = new HttpPut(url);
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, mConnectionTimeout);
                HttpConnectionParams.setSoTimeout(httpParams, mSocketTimeout);

                if (paramsIndex > 0) {
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    String paramsArray[] = urlWithParams.substring(paramsIndex + 1).split("&");
                    for (String param : paramsArray) {
                        String nameValuePair[] = param.split("=");
                        Log.d(TAG, "param : " + nameValuePair[0] + "=" + nameValuePair[1]);
                        httpParams.setParameter(nameValuePair[0], nameValuePair[1]);
                        nameValuePairs.add(new BasicNameValuePair(nameValuePair[0], nameValuePair[1]));
                    }
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    putMethod.setEntity(urlEncodedFormEntity);
                }

                ResponseHandler<String> res = new BasicResponseHandler();

                putMethod.setHeader("Content-Type", "application/x-www-form-urlencoded");

                if (isAuthorizationRequired) {
                    putMethod.setHeader("Authorization", "Bearer " + WorkoutApplication.getmInstance().getSessionToken());
                }

                client.addResponseInterceptor(new HttpResponseInterceptor() {
                    @Override
                    public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
                        interceptedCode = httpResponse.getStatusLine().getStatusCode();
                        if (interceptedCode < 200 || interceptedCode >= 400) {
                            InputStream is = httpResponse.getEntity().getContent();
                            interceptedResponse = Utils.convertStreamToString(is);
                            Log.d(TAG, "content length : " + httpResponse.getEntity().getContentLength());
                            Log.d(TAG, "response body : " + interceptedResponse);
                        }
                    }
                });

                return client.execute(putMethod, res);

            } catch (Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpResponseException) {
                    HttpConnectException e = new HttpConnectException(interceptedResponse, interceptedCode);
                    throw e;
                } else {
                    throw new HttpConnectException(t.getMessage());
                }
            }
        } else {
            throw new HttpConnectException(HttpConnectException.MSG_NO_INTERNET);
        }
    }


    private String getContent(Context context, String urlWithParams, DefaultHttpClient client) throws HttpConnectException {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                Log.d(TAG, "url :  " + urlWithParams);
                HttpGet getMethod = new HttpGet(urlWithParams);
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, mConnectionTimeout);
                HttpConnectionParams.setSoTimeout(httpParams, mSocketTimeout);

                getMethod.setParams(httpParams);

                ResponseHandler<String> res = new BasicResponseHandler();

                getMethod.setHeader("Content-Type", "application/x-www-form-urlencoded");

                if (isAuthorizationRequired) {
                    getMethod.setHeader("Authorization", "Bearer " + WorkoutApplication.getmInstance().getSessionToken());
                }

                client.addResponseInterceptor(new HttpResponseInterceptor() {
                    @Override
                    public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
                        if (httpResponse.getStatusLine().getStatusCode() != 200) {
                            InputStream is = httpResponse.getEntity().getContent();
                            interceptedResponse = Utils.convertStreamToString(is);
                            Log.d(TAG, "content length : " + httpResponse.getEntity().getContentLength());
                            Log.d(TAG, "response body : " + interceptedResponse);
                        }
                    }
                });

                return client.execute(getMethod, res);

            } catch (Throwable t) {
                t.printStackTrace();
                throw new HttpConnectException(t instanceof HttpResponseException ? interceptedResponse : t.getMessage());
            }
        } else {
            throw new HttpConnectException(HttpConnectException.MSG_NO_INTERNET);
        }
    }

    @Override
    protected String doInBackground(String... params) {

        String result = "";
        try {
            Log.d(TAG, "request : " + params[0]);

            if (requestMethod == METHOD_POST) {
                result = postContent(mContext, params[0], null);
            } else if (requestMethod == METHOD_GET) {
                result = getContent(mContext, params[0], new DefaultHttpClient());
            } else {
                result = putContent(mContext, params[0], null, new DefaultHttpClient());
            }

            if (TextUtils.isEmpty(result) && interceptedCode >= 400) {
                this.exception = new NullPointerException("no response");
            }

        } catch (HttpConnectException e) {
            e.printStackTrace();
            this.exception = e;
        }

        Log.d(TAG, "response : " + result);

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showProgress) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(mContext.getString(R.string.please_wait));
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (showProgress && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (!isCancelled() && mCallback != null) {
            if (this.exception != null) {
                if (exception instanceof HttpConnectException && exception.getMessage().equals(HttpConnectException.MSG_NO_INTERNET)) {
                    Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mCallback.onException(this.exception);
            } else {
                try {
                    mCallback.onResponse(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mCallback.onException(e);
                }
            }
        }
    }

    public interface HttpCallback {

        public void onResponse(String response) throws JSONException;

        public void onException(Exception e);

    }
}
