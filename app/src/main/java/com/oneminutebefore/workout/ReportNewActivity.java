package com.oneminutebefore.workout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.oneminutebefore.workout.ReportFragment.REPORT_CUSTOM;

public class ReportNewActivity extends BaseRequestActivity {

    private ImageButton btnPrevious;
    private ImageButton btnNext;

    private TextView tvMonthName;

    private RecyclerView rclCalendar;

    private ArrayList<Integer> repsCount;
    private Calendar calendar;
    private int month;
    private int year;
    private int padding;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat requestDateFormat;
    private HttpTask httpTask;

    private HashMap<Integer, HashMap<String, Integer>> workoutsBreakupMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        rclCalendar = (RecyclerView) findViewById(R.id.rcl_days);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnPrevious = (ImageButton) findViewById(R.id.btn_prev);
        tvMonthName = (TextView) findViewById(R.id.tv_month_name);

        dateFormat = new SimpleDateFormat("MMMM, yyyy");
        requestDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        calendar = Calendar.getInstance();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                fetchData();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                fetchData();
            }
        });

        ((TextView)findViewById(R.id.txt_title)).setText(getTitle());
        ((TextView)findViewById(R.id.txt_subtitle)).setText(getString(R.string.reps_count));


        fetchData();


    }

    private void fetchData() {
        repsCount = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        Date startDate = calendar.getTime();
        String monthName = dateFormat.format(calendar.getTimeInMillis());
        tvMonthName.setText(monthName);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date endDate = calendar.getTime();
        if(httpTask != null){
            httpTask.cancel(true);
        }
        httpTask = new HttpTask(true,ReportNewActivity.this, HttpTask.METHOD_POST);
        UrlBuilder builder = new UrlBuilder(UrlBuilder.API_GET_RECORD).addParameters("type", REPORT_CUSTOM);
        builder.addParameters("from", requestDateFormat.format(startDate));
        builder.addParameters("to", requestDateFormat.format(endDate));
        httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
            @Override
            public void onSessionTimeout() {
                startActivity(Utils.getSessionTimeoutIntent(ReportNewActivity.this));
            }
        });
        httpTask.setmCallback(new HttpTask.HttpCallback() {
            @Override
            public void onResponse(String response) throws JSONException {
                rclCalendar.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(response)){
                    JSONObject responseJson = new JSONObject(response);
                    JSONArray dataArray = responseJson.getJSONArray("date_array");
                    while(calendar.get(Calendar.MONTH) == month){
                        repsCount.add(0);
                        calendar.add(Calendar.DAY_OF_YEAR,1);
                    }
                    if(dataArray != null && dataArray.length() > 0){
                        workoutsBreakupMap = new HashMap<>();
                        for(int i = 0 ; i < dataArray.length(); i++){
                            JSONObject dateObject = dataArray.getJSONObject(i);
                            String dateString = dateObject.optString("d_date");
                            int day = 1;
                            dateString = dateString.substring(0, dateString.length()-1).replace("T"," ");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            try {
                                day = dateFormat.parse(dateString).getDate();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            int count = dateObject.getInt("count");
                            repsCount.set(day - 1,repsCount.get(day - 1) + count);
                            JSONArray breakUpArray = dateObject.getJSONArray("data");
                            if(breakUpArray != null && breakUpArray.length() > 0){
                                if(!workoutsBreakupMap.containsKey(day)){
                                    workoutsBreakupMap.put(day, new HashMap<String, Integer>());
                                }
                                HashMap<String, Integer> dayBreakUpMap = workoutsBreakupMap.get(day);
                                for(int j = 0 ; j < breakUpArray.length() ; j++){
                                    JSONObject dayBreakUpItem = breakUpArray.getJSONObject(j);
                                    dayBreakUpMap.put(dayBreakUpItem.getString("name"), dayBreakUpItem.getInt("count"));
                                }
                            }
                        }
                    }

                    setData();
                }
            }

            @Override
            public void onException(Exception e) {
                rclCalendar.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT).show();
            }
        });
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, builder.build());
        setData();
    }

    private void setData() {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        if(repsCount != null && !repsCount.isEmpty()){
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            for(int i = dayOfWeek - 1 ; i >= 1 ; i--){
                repsCount.add(0,0);
            }
            padding = dayOfWeek - 1;
        }
        rclCalendar.setAdapter(new RepsAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class RepsAdapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_calendar_day, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            if(position < padding){
                holder.itemView.setVisibility(View.INVISIBLE);
                holder.itemView.setOnClickListener(null);
            }else{
                holder.itemView.setVisibility(View.VISIBLE);
                holder.tvDate.setText(String.valueOf(position - padding + 1));
                holder.tvCount.setText(String.valueOf(repsCount.get(position)));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(workoutsBreakupMap != null && !workoutsBreakupMap.isEmpty()){
                            Intent intent = new Intent(ReportNewActivity.this, RepsBreakUpActivity.class);
                            HashMap<String, Integer> mapBreakUp = workoutsBreakupMap.get(holder.getAdapterPosition() - padding + 1);
                            if(mapBreakUp != null){
                                intent.putExtra(RepsBreakUpActivity.KEY_BREAKUP_MAP, mapBreakUp);
                                Calendar tempCalendar = Calendar.getInstance();
                                tempCalendar.set(Calendar.YEAR, year);
                                tempCalendar.set(Calendar.MONTH, month);
                                tempCalendar.set(Calendar.DAY_OF_MONTH, holder.getAdapterPosition() - padding + 1);
                                intent.putExtra(RepsBreakUpActivity.KEY_DATE, tempCalendar.getTimeInMillis());
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return repsCount != null ? repsCount.size() : 0;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{


        private TextView tvDate;
        private TextView tvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
        }
    }

}
