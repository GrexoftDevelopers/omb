package com.oneminutebefore.workout;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.WorkoutExercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class WorkoutSettingsActivity extends AppCompatActivity {

    private ArrayList<SelectedWorkout> selectedWorkouts;
    private WorkoutApplication application;
    private String categoryName;
    private String[] hourKeys;
    private String[] workoutKeys;
    private RecyclerView rclWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWorkoutDialog(null);
            }
        });
        application = (WorkoutApplication) getApplication();
        selectedWorkouts = new ArrayList<>();
        hourKeys = Keys.getHourSelectionKeys(this);
        workoutKeys = Keys.getWorkoutSelectionKeys(this);
        HashMap<String, WorkoutExercise> workouts = application.getWorkouts();
        categoryName = SharedPrefsUtil.getStringPreference(this,Keys.getUserLevelKey(this),"Intermediate");
        for(int i  = 0 ; i < hourKeys.length ; i++){
            boolean isHourSelected = SharedPrefsUtil.getBooleanPreference(this, hourKeys[i], false);
            if(isHourSelected){
                String hourWorkout = SharedPrefsUtil.getStringPreference(this, workoutKeys[i]);
                if(workouts.containsKey(hourWorkout) && workouts.get(hourWorkout).getCategory().getName().equals(categoryName)){
                    selectedWorkouts.add(new SelectedWorkout(workouts.get(hourWorkout),hourKeys[i]));
                }
            }
        }

        rclWorkouts = (RecyclerView) findViewById(R.id.list_workouts);
        rclWorkouts.setAdapter(new WorkoutsAdapter());

        refreshUI();

    }

    private void refreshUI() {
        if(selectedWorkouts != null && !selectedWorkouts.isEmpty()){
            rclWorkouts.setVisibility(View.VISIBLE);
            findViewById(R.id.tv_no_workout).setVisibility(View.GONE);
        }else{
            rclWorkouts.setVisibility(View.GONE);
            findViewById(R.id.tv_no_workout).setVisibility(View.VISIBLE);
        }
    }

    private void showAddWorkoutDialog(final SelectedWorkout selectedWorkout){

        AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutSettingsActivity.this);
        builder.setNegativeButton(getString(R.string.cancel),null);
        builder.setPositiveButton(getString(R.string.ok),null);

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_workout,null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(dialogView);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Spinner spHour = (Spinner) dialogView.findViewById(R.id.sp_hour);
                final Spinner spWorkout = (Spinner) dialogView.findViewById(R.id.sp_workout);
                final ArrayList<WorkoutExercise> workoutExercises = new ArrayList<>();
                for(Map.Entry entry : application.getWorkouts().entrySet()){
                    WorkoutExercise workoutExercise = (WorkoutExercise) entry.getValue();
                    if(workoutExercise.getCategory().getName().equals(categoryName)){
                        workoutExercises.add(workoutExercise);
                    }
                }
                ArrayAdapter<WorkoutExercise> arrayAdapter = new ArrayAdapter<WorkoutExercise>(WorkoutSettingsActivity.this
                        ,android.R.layout.simple_spinner_item,workoutExercises){

                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        if(convertView == null){
                            convertView = getLayoutInflater().inflate(android.R.layout.simple_spinner_item,parent,false);
                        }
                        ((TextView)convertView).setText(workoutExercises.get(position).getName());
//                        return super.getView(position, convertView, parent);
                        return convertView;
                    }

                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        if(convertView == null){
                            convertView = getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item,parent,false);
                        }
                        ((TextView)convertView).setText(workoutExercises.get(position).getName());
                        return convertView;
                    }
                };
                spWorkout.setAdapter(arrayAdapter);
                if(selectedWorkout != null){
                    for(int i = 0 ; i < workoutExercises.size() ; i++){
                        if(selectedWorkout.getId().equals(workoutExercises.get(i).getId())){
                            spWorkout.setSelection(i);
                            break;
                        }
                    }
                }
//
                final ArrayList<String> selectableHours = new ArrayList<>();
                for(String hour : hourKeys){
                    boolean exists = false;
                    for(SelectedWorkout selectedWorkout1 : selectedWorkouts){
                        if(selectedWorkout1.getTimeKey().equals(hour) && selectedWorkout1 != selectedWorkout){
                            exists = true;
                            break;
                        }
                    }
                    if(!exists){
                        selectableHours.add(hour.replace('_',':'));
                    }
                }
                if(selectableHours.isEmpty()){
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                }
                ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(WorkoutSettingsActivity.this,android.R.layout.simple_spinner_item,selectableHours);
                hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spHour.setAdapter(hourAdapter);
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkoutExercise workoutExercise = (WorkoutExercise) spWorkout.getSelectedItem();
                        String hour = ((String) spHour.getSelectedItem()).replace(':','_');
                        if(selectedWorkout != null){
                            int index = selectedWorkouts.lastIndexOf(selectedWorkout);
                            selectedWorkouts.set(index,new SelectedWorkout(workoutExercise,hour));
                            rclWorkouts.getAdapter().notifyItemChanged(index);
                        }else{
                            selectedWorkouts.add(new SelectedWorkout(workoutExercise,hour));
                            rclWorkouts.getAdapter().notifyItemInserted(selectedWorkouts.size()-1);
                        }
                        refreshUI();
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_done:
                saveSelectedWorkouts();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveSelectedWorkouts() {

        // Remove old data from preferences
        for(String key : hourKeys){
            SharedPrefsUtil.deletePreference(WorkoutSettingsActivity.this, key);
        }
        for(String key : workoutKeys){
            SharedPrefsUtil.deletePreference(WorkoutSettingsActivity.this, key);
        }
        if(selectedWorkouts != null && !selectedWorkouts.isEmpty()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            for(SelectedWorkout selectedWorkout : selectedWorkouts){
                SharedPrefsUtil.setBooleanPreference(WorkoutSettingsActivity.this, selectedWorkout.getTimeKey(), true);
                SharedPrefsUtil.setStringPreference(WorkoutSettingsActivity.this, "list_" + selectedWorkout.getTimeKey(), selectedWorkout.getId());
                Calendar calendar = Calendar.getInstance();
                String workoutTime = dateFormat.format(calendar.getTime());
                String createdTime = dateTimeFormat.format(calendar.getTime()).replace(" ","T") + "Z";
                String url = new UrlBuilder(UrlBuilder.API_PRODUCTS)
                        .addParameters("workout_time", workoutTime)
                        .addParameters("category", selectedWorkout.getCategory().getId())
                        .addParameters("workout", selectedWorkout.getId())
                        .addParameters("update_at", createdTime)
                        .addParameters("created_at", createdTime)
                        .addParameters("user_id", application.getUserId())
                        .build();

                HttpTask httpTask = new HttpTask(false,WorkoutSettingsActivity.this);
                httpTask.setAuthorizationRequired(true);
                httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
            }
        }
    }

    private class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.ViewHolder>{


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_workout_count, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {

            final SelectedWorkout selectedWorkout = selectedWorkouts.get(i);

            viewHolder.tvWorkoutName.setText(selectedWorkout.getName());
            viewHolder.tvHour.setText(selectedWorkout.getTime());
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedWorkouts.remove(selectedWorkout);
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                    refreshUI();
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddWorkoutDialog(selectedWorkout);
                }
            });
        }

        @Override
        public int getItemCount() {
            return selectedWorkouts != null ? selectedWorkouts.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private TextView tvHour, tvWorkoutName;
            private ImageView ivDelete;

            public ViewHolder(View itemView) {
                super(itemView);

                tvHour = (TextView)itemView.findViewById(R.id.tv_workout_time);
                tvWorkoutName = (TextView)itemView.findViewById(R.id.tv_workout_name);
                ivDelete = (ImageView)itemView.findViewById(R.id.iv_delete);
                ivDelete.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.tv_count).setVisibility(View.GONE);
            }
        }
    }

}
