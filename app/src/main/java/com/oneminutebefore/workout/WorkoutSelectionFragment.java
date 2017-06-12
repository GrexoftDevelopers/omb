package com.oneminutebefore.workout;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.models.SelectedWorkout;
import com.oneminutebefore.workout.models.WorkoutExercise;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutSelectionFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_HOUR_TYPE = "hour_type";
    private static final String ARG_SELECTED_WORKOUTS = "selectedWorkouts";
    private static final String ARG_EDITED_WORKOUT = "selectedWorkout";

    // TODO: Rename and change types of parameters
    private int mHourType;
    private ArrayList<SelectedWorkout> selectedWorkouts;
    private SelectedWorkout editedWorkout;
    private View fragmentView;
    private AlertDialog alertDialog;

    private boolean mCancelled;

    private DialogInterface.OnDismissListener dismissListener;


    public WorkoutSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mHourType int.
     * @return A new instance of fragment WorkoutSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutSelectionFragment newInstance(int mHourType, ArrayList<SelectedWorkout> selectedWorkouts, SelectedWorkout selectedWorkout) {
        WorkoutSelectionFragment fragment = new WorkoutSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HOUR_TYPE, mHourType);
        args.putSerializable(ARG_SELECTED_WORKOUTS, selectedWorkouts);
        args.putSerializable(ARG_EDITED_WORKOUT, selectedWorkout);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHourType = getArguments().getInt(ARG_HOUR_TYPE);
            selectedWorkouts = (ArrayList<SelectedWorkout>) getArguments().getSerializable(ARG_SELECTED_WORKOUTS);
            editedWorkout = (SelectedWorkout) getArguments().getSerializable(ARG_EDITED_WORKOUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return fragmentView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_workout_selection, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(fragmentView);
        builder.setPositiveButton(getString(R.string.ok), null);
        builder.setNegativeButton(getString(R.string.cancel), null);

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final ViewPager viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
                ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
                String hourKeys[] = Keys.getHourSelectionKeys(getActivity());
                final ArrayList<String> workingHours = new ArrayList<>();
                ArrayList<String> nonWorkingHours = new ArrayList<>();
                for(int i = 0 ; i < hourKeys.length ; i++){
                    if(i >= 7 && i <= 16){
                        workingHours.add(hourKeys[i]);
                    }else{
                        nonWorkingHours.add(hourKeys[i]);
                    }
                }
                adapter.addFragment(WorkoutSelectionFormFragment.newInstance(workingHours.toArray(new String[10]),selectedWorkouts, editedWorkout != null ? workingHours.contains(editedWorkout.getTimeKey()) ? editedWorkout : null : null), "Working hours");
                adapter.addFragment(WorkoutSelectionFormFragment.newInstance(nonWorkingHours.toArray(new String[12]), selectedWorkouts, editedWorkout != null ? nonWorkingHours.contains(editedWorkout.getTimeKey()) ? editedWorkout : null : null), "Non Working hours");
                viewPager.setAdapter(adapter);
                TabLayout tabLayout = (TabLayout) fragmentView.findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(viewPager);

                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        ((WorkoutSelectionFormFragment)((ViewPagerAdapter)viewPager.getAdapter()).getItem(position)).refreshButton();

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int selectedIndex = 0;
                        if(editedWorkout != null){
                            if(workingHours.contains(editedWorkout.getTimeKey())){
                                selectedIndex = 0;
                            }else{
                                selectedIndex = 1;
                            }
                        }
                        viewPager.setCurrentItem(selectedIndex);
                        ((WorkoutSelectionFormFragment)((ViewPagerAdapter)viewPager.getAdapter()).getItem(selectedIndex)).refreshButton();
                    }
                }, 300);

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean result = ((WorkoutSelectionFormFragment)((ViewPagerAdapter)viewPager.getAdapter()).getItem(viewPager.getCurrentItem())).save();
                        alertDialog.dismiss();
                    }
                });

                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCancelled = true;
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!mCancelled && dismissListener != null){
                            dismissListener.onDismiss(dialog);
                        }
                    }
                });
            }
        });
        return alertDialog;
    }

    public void setOkButtonVisibility(int visibility) {

        if(alertDialog != null){
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(visibility);
        }

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> titles = new ArrayList<>();
        private ArrayList<Fragment> fragments = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            ((WorkoutSelectionFormFragment)fragment).setParentFragment(WorkoutSelectionFragment.this);
            fragments.add(fragment);
            titles.add(title);
        }


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments != null && fragments.size() > position ? fragments.get(position) : null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles != null && titles.size() > position ? titles.get(position) : null;
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }
    }

}
