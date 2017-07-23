package com.oneminutebefore.workout;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.WorkoutCategory;
import com.oneminutebefore.workout.models.WorkoutExercise;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutSelectionFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutSelectionFormFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_HOUR_KEYS = "hour_keys";
    private static final String ARG_SELECTED_WORKOUTS = "selectedWorkouts";
    private static final String ARG_EDITED_WORKOUT = "selectedWorkout";

    // TODO: Rename and change types of parameters
    private String[] hourKeys;
    private ArrayList<SelectedWorkout> selectedWorkouts;
    private SelectedWorkout selectedWorkout;
    private View dialogView;

    private WorkoutSelectionFragment parentFragment;
    private ArrayList<String> selectableHours;
    private ArrayList<WorkoutExercise> workoutExercises;
    private Spinner spLevel, spHour, spWorkout;
    private ArrayList<WorkoutCategory> categories;
    private WorkoutApplication application;


    public WorkoutSelectionFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param hourKeys Parameter 1.
     * @param selectedWorkouts Parameter 2.
     * @param  selectedWorkout {@link SelectedWorkout}.
     * @return A new instance of fragment WorkoutSelectionFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutSelectionFormFragment newInstance(String[] hourKeys, ArrayList<SelectedWorkout> selectedWorkouts, SelectedWorkout selectedWorkout) {
        WorkoutSelectionFormFragment fragment = new WorkoutSelectionFormFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_HOUR_KEYS, hourKeys);
        args.putSerializable(ARG_SELECTED_WORKOUTS, selectedWorkouts);
        args.putSerializable(ARG_EDITED_WORKOUT, selectedWorkout);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hourKeys = getArguments().getStringArray(ARG_HOUR_KEYS);
            selectedWorkouts = (ArrayList<SelectedWorkout>) getArguments().getSerializable(ARG_SELECTED_WORKOUTS);
            selectedWorkout = (SelectedWorkout) getArguments().getSerializable(ARG_EDITED_WORKOUT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView =  inflater.inflate(R.layout.fragment_wrokout_selection_form, container, false);

        application = WorkoutApplication.getmInstance();
        spHour = (Spinner) dialogView.findViewById(R.id.sp_hour);
        spWorkout = (Spinner) dialogView.findViewById(R.id.sp_workout);
        spLevel = (Spinner) dialogView.findViewById(R.id.sp_level);

        categories = new ArrayList<>();
        int selectedIndex = 0;
        for(Map.Entry entry : application.getWorkoutCategories().entrySet()){
            categories.add((WorkoutCategory) entry.getValue());
        }

        if(selectedWorkout != null){
            selectedIndex = categories.indexOf(selectedWorkout.getCategory());
        }

        ArrayAdapter<WorkoutCategory> categoryAdapter = new ArrayAdapter<WorkoutCategory>(getActivity(), android.R.layout.simple_spinner_item, categories){

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView == null){
                    convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item,parent,false);
                }
                ((TextView)convertView).setText(categories.get(position).getName());
                return convertView;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView == null){
                    convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_item,parent,false);
                }
                ((TextView)convertView).setText(categories.get(position).getName());
                return convertView;
            }
        };

        spLevel.setAdapter(categoryAdapter);

        spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setWorkoutsAdapter(position);
                refreshButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spLevel.setSelection(selectedIndex);

        selectableHours = new ArrayList<>();
        for(String hour : hourKeys){
            boolean exists = false;
            for(SelectedWorkout selectedWorkout1 : selectedWorkouts){
                if(selectedWorkout1.getTimeKey().equals(hour) && selectedWorkout1 != selectedWorkout){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                selectableHours.add(SelectedWorkout.getTimeMeridian(hour));
            }
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,selectableHours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHour.setAdapter(hourAdapter);
        if(selectedWorkout != null && selectableHours.contains(selectedWorkout.getTimeMeridian())){
            spHour.setSelection(selectableHours.indexOf(selectedWorkout.getTimeMeridian()));
        }

        return dialogView;
    }

    private void setWorkoutsAdapter(int selectedIndex) {
        workoutExercises = new ArrayList<>();
        for(Map.Entry entry : application.getWorkouts().entrySet()){
            WorkoutExercise workoutExercise = (WorkoutExercise) entry.getValue();
            if(!selectedWorkouts.contains(workoutExercise) || (selectedWorkout != null && selectedWorkout.equals(workoutExercise))){
                if(workoutExercise.getCategory().equals(categories.get(selectedIndex))){
                    workoutExercises.add(workoutExercise);
                }
            }
        }
        ArrayAdapter<WorkoutExercise> arrayAdapter = new ArrayAdapter<WorkoutExercise>(getActivity()
                ,android.R.layout.simple_spinner_item,workoutExercises){

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView == null){
                    convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_item,parent,false);
                }
                ((TextView)convertView).setText(workoutExercises.get(position).getName());
                return convertView;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView == null){
                    convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item,parent,false);
                }
                ((TextView)convertView).setText(workoutExercises.get(position).getName());
                return convertView;
            }
        };
        spWorkout.setAdapter(arrayAdapter);
        if(selectedWorkout != null){
            for(int i = 0 ; i < workoutExercises.size() ; i++){
                if(selectedWorkout.equals(workoutExercises.get(i))){
                    spWorkout.setSelection(i);
                    break;
                }
            }
        }
    }

    public void refreshButton(){
        if(selectableHours.isEmpty() || workoutExercises.isEmpty()){
            parentFragment.setOkButtonVisibility(View.GONE);
        }else{
            parentFragment.setOkButtonVisibility(View.VISIBLE);
        }
    }

    public void setParentFragment(WorkoutSelectionFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void save(final WorkoutSaveCallback callback){
        WorkoutExercise workoutExercise = (WorkoutExercise) spWorkout.getSelectedItem();
        if(workoutExercise != null){
            String hour = ((String) spHour.getSelectedItem());
            if(hour != null){
                hour = SelectedWorkout.getTimeKey(hour);
            }
            if(selectedWorkout != null){
                int index = selectedWorkouts.lastIndexOf(selectedWorkout);
                selectedWorkout = new SelectedWorkout(workoutExercise,hour);
                selectedWorkouts.set(index,selectedWorkout);
            }else{
                if(workoutExercise != null){
                    selectedWorkout = new SelectedWorkout(workoutExercise,hour);
                    selectedWorkouts.add(selectedWorkout);
                }
            }
            SharedPrefsUtil.setBooleanPreference(getActivity(), selectedWorkout.getTimeKey(), true);
            SharedPrefsUtil.setStringPreference(getActivity(), "list_" + selectedWorkout.getTimeKey(), selectedWorkout.getId());

            SelectedWorkout savedWorkout = application.getDbHelper().getSelectedWorkoutByTime(selectedWorkout.getTimeMeridian());
            if(savedWorkout == null || !savedWorkout.getId().equals(selectedWorkout.getId())){
                String url = new UrlBuilder(UrlBuilder.API_PRODUCTS)
                        .addParameters("workout_time", selectedWorkout.getTimeMeridian())
                        .addParameters("category", selectedWorkout.getCategory().getId())
                        .addParameters("workout", selectedWorkout.getId())
                        .addParameters("user_id", application.getUserId())
                        .build();

                HttpTask httpTask = new HttpTask(true,getActivity(),HttpTask.METHOD_POST);
                httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
                    @Override
                    public void onSessionTimeout() {
                        startActivity(Utils.getSessionTimeoutIntent(getActivity()));
                    }
                });
                httpTask.setmCallback(new HttpTask.HttpCallback() {
                    @Override
                    public void onResponse(String response) throws JSONException {
                        JSONObject jsonObject = new JSONObject(response);
                        application.getDbHelper().insertSelectedWorkout(jsonObject, false, true);
                        if(callback != null){
                            callback.onSave();
                        }
                    }

                    @Override
                    public void onException(Exception e) {

                    }
                });
                httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
            }else{
                if(callback != null){
                    callback.onSave();
                }
            }
        }
    }

    public interface WorkoutSaveCallback{

        public void onSave();

    }

}
