package com.oneminutebefore.workout.helpers;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oneminutebefore.workout.BaseRequestActivity;
import com.oneminutebefore.workout.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class VolleyHelper {

    private final Context activity;
    private final boolean showProgress;

    private boolean isInProgress;
    private boolean isCancelled;
    private RequestQueue queue;

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

    public void callApi(int method, String url, final JSONObject obj, final VolleyCallback callback) {
        final ProgressDialog pd = new ProgressDialog(activity);
        if(showProgress)
        {
            pd.setCancelable(false);
            pd.setMessage(activity.getString(R.string.please_wait));
            pd.show();
        }
        System.out.println("api url: " + url);
        System.out.println("api obj: " + obj);
        queue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(method, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
        });
        jsObjRequest.setShouldCache(false);

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);

    }

    public void callApiGet(String url, final VolleyCallback callback) {
        System.out.println("api url: " + url);
        final ProgressDialog pd = new ProgressDialog(activity);
        if(showProgress)
        {
            pd.setCancelable(false);
            pd.setMessage(activity.getString(R.string.please_wait));
            pd.show();
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(showProgress && pd.isShowing()){
                    pd.dismiss();
                }
                System.out.println("api response: " +response);
                if(!isCancelled && callback != null){
                    try {
                        callback.onSuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError(activity.getString(R.string.some_error_occured));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error: " +error.toString());
                if(showProgress && pd.isShowing()){
                    pd.dismiss();
                }
                if(!isCancelled && callback != null){
                    callback.onError(error.toString());
                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    public void CallApiGetArray(String url, final VolleyCallback callback) {

        RequestQueue queue = Volley.newRequestQueue(activity);
        JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    JSONObject json = new JSONObject();
                    json.put("array",response);
                    System.out.println(json.toString());
                    callback.onSuccess(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                System.out.println(error);
                callback.onError(error.toString());

            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);

    }


    public interface VolleyCallback {
        void onSuccess(String result) throws JSONException;
        void onError(String error);
    }

}