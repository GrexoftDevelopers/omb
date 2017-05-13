package com.oneminutebefore.workout;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oneminutebefore.workout.helpers.Keys;
import com.oneminutebefore.workout.helpers.SharedPrefsUtil;
import com.oneminutebefore.workout.helpers.UrlBuilder;
import com.oneminutebefore.workout.helpers.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterFragment extends Fragment {
    private EditText etName, etEmail, etPassword, etTimeZone, etLevel;
    private Button btnRegister, btnSignIn;
    private RegisterInteractionListener mListener;

    private ProgressBar progressBar;
    private View fragmentView;

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
        etName = (EditText) fragmentView.findViewById(R.id.et_first_name);
        etEmail = (EditText) fragmentView.findViewById(R.id.et_email);
        etPassword = (EditText) fragmentView.findViewById(R.id.et_password);
        etTimeZone = (EditText) fragmentView.findViewById(R.id.et_time_zone);
        etLevel = (EditText) fragmentView.findViewById(R.id.et_level);
        btnRegister = (Button) fragmentView.findViewById(R.id.btn_register);
        btnSignIn = (Button) fragmentView.findViewById(R.id.btn_login);
        progressBar = (ProgressBar)fragmentView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        etLevel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    Toast.makeText(getActivity(), "Registered", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
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
        if (isFeldNotEmpty()) {
            String firstName= etName.getText().toString().trim();
            String lastName= etEmail.getText().toString().trim();
            String mobileNo= etPassword.getText().toString().trim();
            String timeZone=etTimeZone.getText().toString().trim();
            String level=etLevel.getText().toString().trim();

            setErrorFalse();
            btnRegister.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            String url = new UrlBuilder(UrlBuilder.API_REGISTER)
                    .addParameters("name", firstName)
                    .addParameters("email", lastName)
                    .addParameters("password",mobileNo)
                    .build();

            VolleyHelper volleyHelper = new VolleyHelper(getActivity(), false);
            volleyHelper.callApiGet(url, new VolleyHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    JSONObject responseJson = new JSONObject(result);
                    String userId = responseJson.optString("status","-1");
                    if(!userId.equals("-1")){
                        WorkoutApplication.getmInstance().setUserId(userId);
                        SharedPrefsUtil.setStringPreference(getActivity(), Keys.KEY_USER_ID, userId);
                        if (mListener != null) {
                            mListener.onRegisterSuccessFul();
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    if (mListener != null) {
                        mListener.onRegisterSuccessFul();
                    }
                }
            });

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
        ((TextInputLayout)fragmentView.findViewById(R.id.til_first_name)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_email)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_password)).setError(null);

        if (TextUtils.isEmpty(etName.getText().toString())) {
            ((TextInputLayout)fragmentView.findViewById(R.id.til_first_name)).setError(getString(R.string.error_field_required));
            etName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            ((TextInputLayout)fragmentView.findViewById(R.id.til_email)).setError(getString(R.string.error_field_required));
            etEmail.requestFocus();
            return false;

        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            ((TextInputLayout)fragmentView.findViewById(R.id.til_password)).setError(getString(R.string.error_field_required));
            etPassword.requestFocus();
            return false;
        }
//        if (TextUtils.isEmpty(etTimeZone.getText().toString())) {
//            ((TextInputLayout)fragmentView.findViewById(R.id.til_time_zone)).setError(getString(R.string.error_field_required));
//            etTimeZone.requestFocus();
//            return false;
//
//        }
//        if (TextUtils.isEmpty(etLevel.getText().toString())) {
//            ((TextInputLayout)fragmentView.findViewById(R.id.til_level)).setError(getString(R.string.error_field_required));
//            etLevel.requestFocus();
//            return false;
//        }
        return true;
    }
    private void setErrorFalse(){
        ((TextInputLayout)fragmentView.findViewById(R.id.til_first_name)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_email)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_password)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_level)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_time_zone)).setError(null);
    }

}



