package com.oneminutebefore.workout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RepsBreakUpActivity extends AppCompatActivity {

    public static final String KEY_BREAKUP_MAP = "breakup_map";
    public static final String KEY_DATE = "date";
    private ArrayList<Integer> breakUpCount;
    private ArrayList<String> workoutNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reps_break_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        TextView tvTitle = (TextView) findViewById(R.id.txt_title);
        TextView tvSubTitle = (TextView) findViewById(R.id.txt_subtitle);

        RecyclerView rclBreakup = (RecyclerView) findViewById(R.id.rcl_breakup);

        HashMap<String, Integer> breakUpMap = (HashMap<String, Integer>) getIntent().getSerializableExtra(KEY_BREAKUP_MAP);
        if(breakUpMap != null && !breakUpMap.isEmpty()){
            breakUpCount = new ArrayList<>();
            workoutNames = new ArrayList<>();
            for(Map.Entry entry : breakUpMap.entrySet()){
                breakUpCount.add(Integer.parseInt(entry.getValue().toString()));
                workoutNames.add(entry.getKey().toString());
            }
        }
        long date = getIntent().getLongExtra(KEY_DATE, System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMM, yyyy");
        tvTitle.setText(getTitle());
        tvSubTitle.setText(dateFormat.format(new Date(date)));

        rclBreakup.setAdapter(new BreakUpAdapter());
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

    private class BreakUpAdapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_workout_count, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.tvCount.setText(String.valueOf(breakUpCount.get(position)));
            holder.tvDate.setText(workoutNames.get(position));

        }

        @Override
        public int getItemCount() {
            return workoutNames != null ? workoutNames.size() : 0;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvCount;
        private TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCount = (TextView)itemView.findViewById(R.id.tv_count);
            tvDate = (TextView)itemView.findViewById(R.id.tv_workout_name);
            itemView.findViewById(R.id.tv_workout_time).setVisibility(View.GONE);
        }
    }
}
