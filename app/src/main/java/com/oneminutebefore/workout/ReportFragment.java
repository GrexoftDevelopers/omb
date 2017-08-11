package com.oneminutebefore.workout;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private ImageButton btnNext;
    private ImageButton btnPrev;

    private ArrayList<CompletedWorkout> completedWorkouts;

    private RecyclerView recyclerView;
    private ProgressBar proogressBar;
    private HttpTask httpTask;


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
        btnNext = (ImageButton) fragmentView.findViewById(R.id.btn_next);
        btnPrev = (ImageButton) fragmentView.findViewById(R.id.btn_prev);
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

        Calendar calendar = Calendar.getInstance();
        switch (reportFilter){
            case REPORT_WEEKLY :
                unavailabilityText = "No workouts in this week";
//                fragmentView.findViewById(R.id.card_filter).setVisibility(View.GONE);
                btnDateRange.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
//                btnNext.setEnabled(false);
                btnPrev.setVisibility(View.VISIBLE);
                endDate = new Date(calendar.getTimeInMillis());
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                startDate = new Date(calendar.getTimeInMillis());
                break;

            case REPORT_MONTHLY:
                unavailabilityText = "No workouts in this month";
//                fragmentView.findViewById(R.id.card_filter).setVisibility(View.GONE);
                btnDateRange.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
//                btnNext.setEnabled(false);
                btnPrev.setVisibility(View.VISIBLE);
                endDate = new Date(calendar.getTimeInMillis());
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                startDate = new Date(calendar.getTimeInMillis());
                break;

            case REPORT_CUSTOM:
                unavailabilityText = "No results found";
                startDate = new Date(calendar.getTimeInMillis());
                endDate = startDate;
                btnDateRange.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
                btnPrev.setVisibility(View.GONE);
                break;
        }
        updateDateField();
        tvNoWorkout.setText(unavailabilityText);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                if(reportFilter == REPORT_WEEKLY){
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    int firstDayOfWeek = calendar.getFirstDayOfWeek();
                    if(day != firstDayOfWeek){
                        calendar.add(calendar.DAY_OF_YEAR,1);
                    }
                    calendar.add(Calendar.DAY_OF_YEAR, 7);
                    startDate = calendar.getTime();
                    calendar.add(Calendar.DAY_OF_YEAR, 6);
                    endDate = calendar.getTime();
                }else if(reportFilter == REPORT_MONTHLY){
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    calendar.add(Calendar.MONTH, 1);
                    calendar.set(Calendar.DAY_OF_MONTH,1);
                    startDate = calendar.getTime();
                    calendar.add(Calendar.MONTH, 1);
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    endDate = calendar.getTime();
                }
                updateDateField();
                setData();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                if(reportFilter == REPORT_WEEKLY){
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    endDate = calendar.getTime();
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    startDate = calendar.getTime();
                }else if(reportFilter == REPORT_MONTHLY){
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
//                    if(day != 1){
//                    }else{
//                    }
                    calendar.set(Calendar.DAY_OF_MONTH,1);
                    calendar.add(Calendar.DAY_OF_YEAR,-1);
                    endDate = calendar.getTime();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    startDate = calendar.getTime();
                }
                updateDateField();
                setData();
            }
        });
        return fragmentView;
    }

    private void updateDateField() {
        etdateRange.setText(dateFormat.format(startDate) + " to " + dateFormat.format(endDate));
    }

    public void setData(){

        tvNoWorkout.setVisibility(View.GONE);
        proogressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if(httpTask != null){
            httpTask.cancel(true);
        }
        httpTask = new HttpTask(false,getActivity(), HttpTask.METHOD_POST);
//        UrlBuilder builder = new UrlBuilder(UrlBuilder.API_GET_RECORD).addParameters("type", reportFilter);
        UrlBuilder builder = new UrlBuilder(UrlBuilder.API_GET_RECORD).addParameters("type", REPORT_CUSTOM);
        if(true || reportFilter.equals(REPORT_CUSTOM)){
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
            int repCountTotal = 0;
            if(completedWorkout.getUserTracks() != null && !completedWorkout.getUserTracks().isEmpty()){
                for(UserTrack userTrack : completedWorkout.getUserTracks()){
                    repCountTotal += userTrack.getReps();
                }
            }
            holder.tvTitle.setText(completedWorkout.getName() + " (" + repCountTotal + ")");
//            if(completedWorkout.getUserTracks() != null && !completedWorkout.getUserTracks().isEmpty()){
//                int i = 0;
//                for(UserTrack userTrack : completedWorkout.getUserTracks()){
//                    View view = getActivity().getLayoutInflater().inflate(R.layout.item_workout_count
//                            ,holder.rclUserTrack
//                            ,true);
//                    view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dpToPx(72)));
//                    long millis = SelectedWorkout.getDateTimeLong(userTrack.getDate());
//                    ((TextView)view.findViewById(R.id.tv_workout_name)).setText(dateFormat.format(new Date(millis)));
//                    ((TextView)view.findViewById(R.id.tv_count)).setText(String.valueOf(userTrack.getReps()));
//                    if(i == completedWorkout.getUserTracks().size() - 1){
//                        view.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
//                    }
//                    view.findViewById(R.id.tv_workout_time).setVisibility(View.GONE);
////                    holder.rclUserTrack.addView(view);
//                }
//            }
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
//        private LinearLayout rclUserTrack;
        private RecyclerView rclUserTrack;

        public CompletedWorkoutViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
//            rclUserTrack = (LinearLayout)itemView.findViewById(R.id.list_user_track);
            rclUserTrack = (RecyclerView) itemView.findViewById(R.id.list_user_track);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
