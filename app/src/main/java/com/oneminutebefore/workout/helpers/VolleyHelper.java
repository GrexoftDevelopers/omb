package com.oneminutebefore.workout.helpers;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oneminutebefore.workout.BaseRequestActivity;
import com.oneminutebefore.workout.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class VolleyHelper {

    private final Context activity;
    private final boolean showProgress;

    private boolean isInProgress;
    private boolean isCancelled;
    private RequestQueue queue;

    private static final int mConnectionTimeout = 20000;

    private static final int mSocketTimeout = 60000;

    public VolleyHelper(Context activity) {
        this(activity, true);
    }

    public VolleyHelper(Context context, boolean showProgress){
        this.activity = context;
        this.showProgress = showProgress;
        if(activity instanceof BaseRequestActivity){
            ((BaseRequestActivity)activity).addHelper(this);
        }
    }

//    public RequestQueue getQueue() {
//        return queue;
//    }


    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public void callApi(int method, final String urlWithParams, final JSONObject obj, final VolleyCallback callback) {
        final ProgressDialog pd = new ProgressDialog(activity);
        if(showProgress)
        {
            pd.setCancelable(false);
            pd.setMessage(activity.getString(R.string.please_wait));
            pd.show();
        }
        System.out.println("api url: " + urlWithParams);
        System.out.println("api obj: " + obj);
        queue = Volley.newRequestQueue(activity);
        final int paramsIndex = urlWithParams.indexOf('?');
        String url = paramsIndex > 0 ? urlWithParams.substring(0,paramsIndex) : urlWithParams;
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(showProgress && pd.isShowing()){
                    pd.dismiss();
                }
                System.out.println("api response: " + response.toString());
                if(!isCancelled && callback != null){
                    try {
                        callback.onSuccess(response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError(activity.getString(R.string.some_error_occured));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(showProgress && pd.isShowing()){
                    pd.dismiss();
                }
                if(!isCancelled && callback != null){
                    callback.onError("error: " + error.toString());
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if(paramsIndex > 0){
                    HashMap<String, String> params = new HashMap<>();
                    String paramsArray[] = urlWithParams.substring(paramsIndex + 1).split("&");
                    for(String param : paramsArray){
                        String nameValuePair[] = param.split("=");
                        params.put(nameValuePair[0],nameValuePair[1]);
                    }
                    return params;
                }

                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(method, url, obj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        if(showProgress && pd.isShowing()){
//                            pd.dismiss();
//                        }
//                        System.out.println("api response: " + response.toString());
//                        if(!isCancelled && callback != null){
//                            try {
//                                callback.onSuccess(response.toString());
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                callback.onError(activity.getString(R.string.some_error_occured));
//                            }
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if(showProgress && pd.isShowing()){
//                    pd.dismiss();
//                }
//                if(!isCancelled && callback != null){
//                    callback.onError("error: " + error.toString());
//                }
//            }
//        });
        stringRequest.setShouldCache(false);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }

    public interface VolleyCallback {
        void onSuccess(String result) throws JSONException;
        void onError(String error);
    }

}