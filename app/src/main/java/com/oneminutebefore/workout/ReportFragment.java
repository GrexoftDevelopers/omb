package com.oneminutebefore.workout;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;
import com.oneminutebefore.workout.models.CompletedWorkout;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.UserTrack;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REPORT_FILTER = "report_filter";

    // TODO: Rename and change types of parameters
    private String reportFilter;

    public static final String REPORT_WEEKLY = "week";
    public static final String REPORT_MONTHLY = "month";
    public static final String REPORT_CUSTOM = "advanced";

    private Date startDate;
    private Date endDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    private String unavailabilityText;
    private String title;
    private TextView tvNoWorkout;
    private EditText etdateRange;
    private ImageButton btnDateRange;

    private ArrayList<CompletedWorkout> completedWorkouts;

    private RecyclerView recyclerView;
    private ProgressBar proogressBar;


    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param reportFilter int.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String reportFilter) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REPORT_FILTER, reportFilter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportFilter = getArguments().getString(ARG_REPORT_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_report, container, false);

        tvNoWorkout = (TextView) fragmentView.findViewById(R.id.txt_no_workout);
        etdateRange = (EditText)fragmentView.findViewById(R.id.et_date);
        btnDateRange = (ImageButton) fragmentView.findViewById(R.id.btn_date_range);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.list_workout_count);
        proogressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar);


        btnDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionFragment dateSelectionFragment = DateSelectionFragment.newInstance(DateSelectionFragment.DATE_TYPE_START, dateFormat.format(startDate));
                dateSelectionFragment.setmCallback(dateSelectedListener);
                dateSelectionFragment.show(getActivity().getSupportFragmentManager(), "Date");
            }
        });

        switch (reportFilter){
            case REPORT_WEEKLY :
                unavailabilityText = "No workouts in this week";
                fragmentView.findViewById(R.id.card_filter).setVisibility(View.GONE);
                break;

            case REPORT_MONTHLY:
                unavailabilityText = "No workouts in this month";
                fragmentView.findViewById(R.id.card_filter).setVisibility(View.GONE);
                break;

            case REPORT_CUSTOM:
                unavailabilityText = "No results found";
                startDate = new Date(Calendar.getInstance().getTimeInMillis());
                endDate = startDate;
                etdateRange.setText(dateFormat.format(startDate) + " to " + dateFormat.format(endDate));
                break;
        }

        tvNoWorkout.setText(unavailabilityText);

        return fragmentView;
    }

    public void setData(){

        tvNoWorkout.setVisibility(View.GONE);
        proogressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        HttpTask httpTask = new HttpTask(false,getActivity(), HttpTask.METHOD_POST);
        UrlBuilder builder = new UrlBuilder(UrlBuilder.API_GET_RECORD).addParameters("type", reportFilter);
        if(reportFilter.equals(REPORT_CUSTOM)){
            builder.addParameters("from", dateFormat.format(startDate));
            builder.addParameters("to", dateFormat.format(endDate));
        }
        httpTask.setAuthorizationRequired(true, new HttpTask.SessionTimeOutListener() {
            @Override
            public void onSessionTimeout() {
                startActivity(Utils.getSessionTimeoutIntent(getActivity()));
            }
        });
        httpTask.setmCallback(new HttpTask.HttpCallback() {
            @Override
            public void onResponse(String response) throws JSONException {

                proogressBar.setVisibility(View.GONE);

                completedWorkouts = CompletedWorkout.createListFromJson(new JSONArray(response));
                recyclerView.setAdapter(new CompletedWorkoutsAdapter());
                if(completedWorkouts != null && !completedWorkouts.isEmpty()){
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoWorkout.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    tvNoWorkout.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onException(Exception e) {
                proogressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                tvNoWorkout.setVisibility(View.VISIBLE);
            }
        });
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, builder.build());

    }

    private DateSelectionFragment.OnDateSelectedListener dateSelectedListener = new DateSelectionFragment.OnDateSelectedListener() {
        @Override
        public void onDateSelected(Date date, String dateType) {
            if(dateType.equals(DateSelectionFragment.DATE_TYPE_START)){
                startDate = date;
                DateSelectionFragment dateSelectionFragment = DateSelectionFragment.newInstance(DateSelectionFragment.DATE_TYPE_END, dateFormat.format(endDate));
                dateSelectionFragment.setmCallback(this);
                dateSelectionFragment.show(getActivity().getSupportFragmentManager(), "Date");
            }else{
                endDate = date;
                etdateRange.setText(dateFormat.format(startDate) + " to " + dateFormat.format(endDate));
                setData();
            }
        }
    };

    private class CompletedWorkoutsAdapter extends RecyclerView.Adapter<CompletedWorkoutViewHolder>{


        @Override
        public CompletedWorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CompletedWorkoutViewHolder(
                    getActivity().getLayoutInflater().inflate(
                            R.layout.item_completed_workout
                            ,parent
                            ,false)
            );
        }

        @Override
        public void onBindViewHolder(CompletedWorkoutViewHolder holder, int position) {

            final CompletedWorkout completedWorkout = completedWorkouts.get(position);
            holder.tvTitle.setText(completedWorkout.getName());
            holder.rclUserTrack.setAdapter(new UserTrackAdapter(completedWorkout.getUserTracks()));

        }

        @Override
        public int getItemCount() {
            return completedWorkouts != null ? completedWorkouts.size() : 0;
        }
    }

    private class UserTrackAdapter extends RecyclerView.Adapter<UserTrackViewHolder>{

        private ArrayList<UserTrack> userTracks;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        UserTrackAdapter(ArrayList<UserTrack> userTracks){
            this.userTracks = userTracks;
        }

        @Override
        public UserTrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserTrackViewHolder(
                    getActivity().getLayoutInflater().inflate(
                            R.layout.item_workout_count
                            ,parent
                            ,false
                    )
            );
        }

        @Override
        public void onBindViewHolder(UserTrackViewHolder holder, int position) {

            UserTrack userTrack = userTracks.get(position);
            long millis = SelectedWorkout.getDateTimeLong(userTrack.getDate());
            holder.tvDate.setText(dateFormat.format(new Date(millis)));
            holder.tvCount.setText(String.valueOf(userTrack.getReps()));
            if(position == userTracks.size() - 1){
                holder.itemView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return userTracks == null ? 0 : userTracks.size();
        }
    }

    private static class UserTrackViewHolder extends RecyclerView.ViewHolder{


        private TextView tvCount;
        private TextView tvDate;

        public UserTrackViewHolder(View itemView) {
            super(itemView);
            tvCount = (TextView)itemView.findViewById(R.id.tv_count);
            tvDate = (TextView)itemView.findViewById(R.id.tv_workout_name);
            itemView.findViewById(R.id.tv_workout_time).setVisibility(View.GONE);
        }
    }

    private static class CompletedWorkoutViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle;
        private RecyclerView rclUserTrack;

        public CompletedWorkoutViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            rclUserTrack = (RecyclerView)itemView.findViewById(R.id.list_user_track);
        }
    }

}
