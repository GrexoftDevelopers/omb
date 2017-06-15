package com.oneminutebefore.workout;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateSelectionFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATE_TYPE = "date_type";
    private static final String ARG_INPUT_DATE = "input_date";

    public static final String DATE_TYPE_START = "Start";
    public static final String DATE_TYPE_END = "End";

    // TODO: Rename and change types of parameters
    private String dateType;
    private Date inputDate;

    private OnDateSelectedListener mCallback;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

    public DateSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dateType Parameter 1.
     * @param inputDate Parameter 1.
     * @return A new instance of fragment DateSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DateSelectionFragment newInstance(String dateType, String inputDate) {
        DateSelectionFragment fragment = new DateSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE_TYPE, dateType);
        args.putString(ARG_INPUT_DATE, inputDate);
        fragment.setArguments(args);
        return fragment;
    }

    public void setmCallback(OnDateSelectedListener mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dateType = getArguments().getString(ARG_DATE_TYPE);
            try {
                inputDate = dateFormat.parse(getArguments().getString(ARG_INPUT_DATE));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.ok), null);
        builder.setNegativeButton(getString(R.string.cancel),null);
        final View fragmentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_date_selection, null);
        builder.setView(fragmentView);

        TextView tvTitle = (TextView) fragmentView.findViewById(R.id.tv_title);
        tvTitle.setText(dateType + " date");

        final DatePicker datePicker = (DatePicker) fragmentView.findViewById(R.id.date_picker);
        datePicker.setCalendarViewShown(false);
        datePicker.updateDate(inputDate.getYear() + 1900,inputDate.getMonth(),inputDate.getDate());
//                datePicker.getCalendarView().setDate(inputDate.getTime());
        datePicker.setMaxDate(System.currentTimeMillis());

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String dateStr = (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth() + "-" + datePicker.getYear();
                        try {
                            Date date = dateFormat.parse(dateStr);
                            alertDialog.dismiss();
                            if(mCallback != null){
                                mCallback.onDateSelected(date, dateType);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

//        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                String dateStr = (month + 1) + "-" + dayOfMonth + "-" + year;
//                try {
//                    Date date = dateFormat.parse(dateStr);
//                    dismiss();
//                    if(mCallback != null){
//                        mCallback.onDateSelected(date, dateType);
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        },inputDate.getYear(),inputDate.getMonth(),inputDate.getDate());
//
//        datePickerDialog.setTitle(dateType + " date");

        return alertDialog;
    }

    public interface OnDateSelectedListener{

        public void onDateSelected(Date date, String dateType);

    }
}
