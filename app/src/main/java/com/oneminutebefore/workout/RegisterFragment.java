package com.oneminutebefore.workout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.models.User;
import com.oneminutebefore.workout.models.WorkoutCategory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RegisterFragment extends Fragment {
    private EditText etFirstName, etEmail, etPassword, etReferenceCode, etPhone, etLastName;
    private Button btnRegister, btnSignIn;
    private Spinner spinnerLevel, spinnerTimeZone;
    private RegisterInteractionListener mListener;
    private String level,timeZone;

    private ProgressBar progressBar;
    private View fragmentView;
    private WorkoutApplication application;

    public RegisterFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance() {
        return new RegisterFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_register, container, false);
        etFirstName = (EditText) fragmentView.findViewById(R.id.et_first_name);
        etLastName = (EditText) fragmentView.findViewById(R.id.et_last_name);
        etEmail = (EditText) fragmentView.findViewById(R.id.et_email);
        etPassword = (EditText) fragmentView.findViewById(R.id.et_password);
        etReferenceCode = (EditText) fragmentView.findViewById(R.id.et_reference_code);
        etPhone = (EditText) fragmentView.findViewById(R.id.et_phone);
        btnRegister = (Button) fragmentView.findViewById(R.id.btn_register);
        spinnerLevel = (Spinner) fragmentView.findViewById(R.id.spinner_level);
        spinnerTimeZone = (Spinner) fragmentView.findViewById(R.id.spinner_time_zone);
        btnSignIn = (Button) fragmentView.findViewById(R.id.btn_login);
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        application = WorkoutApplication.getmInstance();

        ArrayList<WorkoutCategory> categories = new ArrayList<>();
        for(Map.Entry entry : application.getWorkoutCategories().entrySet()){
            categories.add((WorkoutCategory) entry.getValue());
        }

        ArrayAdapter<WorkoutCategory> userLevelAdapter = new UserLevelAdapter(getActivity(), android.R.layout.simple_spinner_item, categories);

        spinnerLevel.setAdapter(userLevelAdapter);

        // Creating adapter for spinner
        ArrayAdapter<CharSequence> timezoneArrayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.time_zone_array, android.R.layout.simple_spinner_item);

        timezoneArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTimeZone.setAdapter(timezoneArrayAdapter);

        spinnerTimeZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeZone=spinnerTimeZone.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSignInClicked();
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        return fragmentView;
    }

    private void attemptRegister() {
        setErrorFalse();
        if (isFeldNotEmpty()) {
            final String firstName = etFirstName.getText().toString().trim();
            final String lastName = etLastName.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            String mobileNo = etPassword.getText().toString().trim();
            timeZone = spinnerTimeZone.getSelectedItem().toString();
            level = ((WorkoutCategory)spinnerLevel.getSelectedItem()).getId();
            String referenceCode = etReferenceCode.getText().toString().trim();
            final String phone = etPhone.getText().toString().trim();

            btnRegister.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            UrlBuilder builder = new UrlBuilder(UrlBuilder.API_REGISTER)
                    .addParameters("name", firstName + (!TextUtils.isEmpty(lastName) ? " " + lastName : ""))
                    .addParameters("email", email)
                    .addParameters("password", mobileNo)
                    .addParameters("time_zone", timeZone)
                    .addParameters("user_level", level)
                    .addParameters("phone", phone);

            if (!TextUtils.isEmpty(referenceCode)) {
                builder.addParameters("company", referenceCode);
            }


            HttpTask httpTask = new HttpTask(false, getActivity());
            httpTask.setmCallback(new HttpTask.HttpCallback() {
                @Override
                public void onResponse(String response) throws JSONException {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    JSONObject responseJson = new JSONObject(response);
                    String token = responseJson.optString("token");
////                    if(TextUtils.isEmpty(token)){
////                        String message = responseJson.optString("message");
////                        if(!TextUtils.isEmpty(message)){
////                            String emailError = "This email is not registered.";
////                            String passwordError = "This password is not correct.";
////                            if(message.equals(emailError)){
////                                ((TextInputLayout)fragmentView.findViewById(R.id.til_email)).setError(emailError);
////                            }else if(message.equals(passwordError)){
////                                ((TextInputLayout)fragmentView.findViewById(R.id.til_password)).setError(passwordError);
////                            }
////                        }
////                    }else{

                    if(!TextUtils.isEmpty(token)){
                        SharedPrefsUtil.setStringPreference(getActivity(), Keys.KEY_TOKEN, token);
                        WorkoutApplication.getmInstance().setSessionToken(token);
                        if (mListener != null) {
                            mListener.onRegisterSuccessFul();
                        }

                        User user=new User(email,firstName,phone,level,"","","",timeZone);
                        WorkoutApplication.getmInstance().setUser(user);

                    }
//                    }
                }

                @Override
                public void onException(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    try {
                        JSONObject jsonObject = new JSONObject(e.getMessage());
                        JSONObject error=jsonObject.getJSONObject("errors");

                        String message = null;
                        if(error.has("email")){
                             message = error.getJSONObject("email").optString("message");

                        }
                        String emailError = "The specified email address is already in use.";
//                        String emailError = "The specified email address is already in use.";
//                        String emailError = "The specified email address is already in use.";
                        if(message!=null && message.equalsIgnoreCase(emailError)){
                            ((TextInputLayout)fragmentView.findViewById(R.id.til_email)).setError(emailError);
                        }
//                        else if(message.equals(passwordError)){
//                            ((TextInputLayout)fragmentView.findViewById(R.id.til_password)).setError(passwordError);
//                        }
                        else{
                            Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.some_error_occured), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, builder.build());


//            VolleyHelper volleyHelper = new VolleyHelper(getActivity(), false);
//            volleyHelper.callApi(Request.Method.POST, builder.build(), null, new VolleyHelper.VolleyCallback() {
//                @Override
//                public void onSuccess(String result) throws JSONException {
//                    progressBar.setVisibility(View.GONE);
//                    btnRegister.setEnabled(true);
//                    JSONObject responseJson = new JSONObject(result);
//                    String userId = responseJson.optString("status", "-1");
//                    if (!userId.equals("-1")) {
//                        WorkoutApplication.getmInstance().setSessionToken(userId);
//                        SharedPrefsUtil.setStringPreference(getActivity(), Keys.KEY_USER_ID, userId);
//                        if (mListener != null) {
//                            mListener.onRegisterSuccessFul();
//                        }
//                    }
//                }
//
//                @Override
//                public void onError(String error) {
//                    progressBar.setVisibility(View.GONE);
//                    btnRegister.setEnabled(true);
//                    WorkoutApplication.getmInstance().setSessionToken("1234");
//                    SharedPrefsUtil.setStringPreference(getActivity(), Keys.KEY_USER_ID, "1234");
//                    if (mListener != null) {
//                        mListener.onRegisterSuccessFul();
//                    }
//                }
//            });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterInteractionListener) {
            mListener = (RegisterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface RegisterInteractionListener {
        void onRegisterSuccessFul();

        void onSignInClicked();
    }

    private boolean isFeldNotEmpty() {

        // Reset errors
        ((TextInputLayout) fragmentView.findViewById(R.id.til_first_name)).setError(null);
        ((TextInputLayout) fragmentView.findViewById(R.id.til_email)).setError(null);
        ((TextInputLayout) fragmentView.findViewById(R.id.til_password)).setError(null);

        if (TextUtils.isEmpty(etFirstName.getText().toString())) {
            ((TextInputLayout) fragmentView.findViewById(R.id.til_first_name)).setError(getString(R.string.error_field_required));
            etFirstName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            ((TextInputLayout) fragmentView.findViewById(R.id.til_email)).setError(getString(R.string.error_field_required));
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText().toString())) {
            ((TextInputLayout) fragmentView.findViewById(R.id.til_phone)).setError(getString(R.string.error_field_required));
            etPhone.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            ((TextInputLayout) fragmentView.findViewById(R.id.til_password)).setError(getString(R.string.error_field_required));
            etPassword.requestFocus();
            return false;
        }
//        if (TextUtils.isEmpty(etTimeZone.getText().toString())) {
//            ((TextInputLayout) fragmentView.findViewById(R.id.til_time_zone)).setError(getString(R.string.error_field_required));
//            etTimeZone.requestFocus();
//            return false;
//
//        }
//        if (TextUtils.isEmpty(etLevel.getText().toString())) {
//            ((TextInputLayout) fragmentView.findViewById(R.id.til_level)).setError(getString(R.string.error_field_required));
//            etLevel.requestFocus();
//            return false;
//        }
        return true;
    }

    private void setErrorFalse() {
        ((TextInputLayout) fragmentView.findViewById(R.id.til_first_name)).setError(null);
        ((TextInputLayout) fragmentView.findViewById(R.id.til_email)).setError(null);
        ((TextInputLayout) fragmentView.findViewById(R.id.til_password)).setError(null);
//        ((TextInputLayout) fragmentView.findViewById(R.id.til_level)).setError(null);
//        ((TextInputLayout) fragmentView.findViewById(R.id.til_time_zone)).setError(null);
        ((TextInputLayout) fragmentView.findViewById(R.id.til_phone)).setError(null);
    }

    private class UserLevelAdapter extends ArrayAdapter<WorkoutCategory>{


        public UserLevelAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<WorkoutCategory> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            WorkoutCategory category = getItem(position);

            ((TextView)convertView).setText(category.getName());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            WorkoutCategory category = getItem(position);

            ((TextView)convertView).setText(category.getName());

            return convertView;
        }
    }

}



