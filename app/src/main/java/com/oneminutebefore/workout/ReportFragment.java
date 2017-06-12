package com.oneminutebefore.workout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
    private int reportFilter;

    public static final int REPORT_WEEKLY = 1;
    public static final int REPORT_MONTHLY = 2;
    public static final int REPORT_CUSTOM = 3;


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
    public static ReportFragment newInstance(int reportFilter) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REPORT_FILTER, reportFilter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportFilter = getArguments().getInt(ARG_REPORT_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_report, container, false);

        TextView txtTitle = (TextView) fragmentView.findViewById(R.id.txt_title);
        TextView txtNoWorkout = (TextView) fragmentView.findViewById(R.id.txt_no_workout);

        switch (reportFilter){
            case REPORT_WEEKLY :
                txtTitle.setText("This week");
                txtNoWorkout.setText("No workouts in this week");
                fragmentView.findViewById(R.id.card_filter).setVisibility(View.GONE);
                break;

            case REPORT_MONTHLY:
                txtTitle.setText("This month");
                txtNoWorkout.setText("No workouts in this month");
                fragmentView.findViewById(R.id.card_filter).setVisibility(View.GONE);
                break;

            case REPORT_CUSTOM:
                txtTitle.setText("Custom search");
                txtNoWorkout.setText("No results found");
                break;
        }

        return fragmentView;
    }

}
