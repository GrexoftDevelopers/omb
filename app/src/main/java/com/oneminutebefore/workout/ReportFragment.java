package com.oneminutebefore.workout;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.Utils;

import org.json.JSONException;

import java.text.SimpleDateFormat;
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
    public static final String REPORT_MONTHLY = "monthly";
    public static final String REPORT_CUSTOM = "advanced";

    private Date startDate;
    private Date endDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    private String unavailabilityText;
    private String title;
    private TextView tvTitle,tvNoWorkout;
    private EditText etdateRange;
    private ImageButton btnDateRange;


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

        tvTitle = (TextView) fragmentView.findViewById(R.id.txt_title);
        tvNoWorkout = (TextView) fragmentView.findViewById(R.id.txt_no_workout);
        etdateRange = (EditText)fragmentView.findViewById(R.id.et_date);
        btnDateRange = (ImageButton) fragmentView.findViewById(R.id.btn_date_range);

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
                title = "This week";
                unavailabilityText = "No workouts in this week";
                fragmentView.findViewById(R.id.card_filter).setVisibility(View.GONE);
                break;

            case REPORT_MONTHLY:
                title = "This month";
                unavailabilityText = "No workouts in this month";
                fragmentView.findViewById(R.id.card_filter).setVisibility(View.GONE);
                break;

            case REPORT_CUSTOM:
                title = "Custom search";
                unavailabilityText = "No results found";
                startDate = new Date(Calendar.getInstance().getTimeInMillis());
                endDate = startDate;
                etdateRange.setText(dateFormat.format(startDate) + " to " + dateFormat.format(endDate));
                break;
        }

        tvTitle.setText(title);
        tvNoWorkout.setText(unavailabilityText);

        return fragmentView;
    }



    private void setData(){

        HttpTask httpTask = new HttpTask(true,getActivity(), HttpTask.METHOD_POST);
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

            }

            @Override
            public void onException(Exception e) {

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

}
