package com.oneminutebefore.workout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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


public class RegisterFragment extends Fragment {
    private EditText etFirstName, etLastName, etMobileNo, etTimeZone, etLevel;
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
        etFirstName = (EditText) fragmentView.findViewById(R.id.et_first_name);
        etLastName = (EditText) fragmentView.findViewById(R.id.et_last_name);
        etMobileNo = (EditText) fragmentView.findViewById(R.id.et_mobile_no);
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
            String firstName=etFirstName.getText().toString().trim();
            String lastName=etLastName.getText().toString().trim();
            String mobileNo=etMobileNo.getText().toString().trim();
            String timeZone=etTimeZone.getText().toString().trim();
            String level=etLevel.getText().toString().trim();

            setErrorFalse();
            btnRegister.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    if (mListener != null) {
                        mListener.onRegisterSuccessFul();
                    }
                }
            }, 2000);
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
        // TODO: Update argument type and name
        void onRegisterSuccessFul();

        void onSignInClicked();
    }

    private boolean isFeldNotEmpty() {
        if (TextUtils.isEmpty(etFirstName.getText().toString())) {
            ((TextInputLayout)fragmentView.findViewById(R.id.til_first_name)).setError(getString(R.string.error_field_required));
            etFirstName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etLastName.getText().toString())) {
            ((TextInputLayout)fragmentView.findViewById(R.id.til_last_name)).setError(getString(R.string.error_field_required));
            etLastName.requestFocus();
            return false;

        }
        if (TextUtils.isEmpty(etMobileNo.getText().toString())) {
            ((TextInputLayout)fragmentView.findViewById(R.id.til_mobile_no)).setError(getString(R.string.error_field_required));
            etMobileNo.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etTimeZone.getText().toString())) {
            ((TextInputLayout)fragmentView.findViewById(R.id.til_time_zone)).setError(getString(R.string.error_field_required));
            etTimeZone.requestFocus();
            return false;

        }
        if (TextUtils.isEmpty(etLevel.getText().toString())) {
            ((TextInputLayout)fragmentView.findViewById(R.id.til_level)).setError(getString(R.string.error_field_required));
            etLevel.requestFocus();
            return false;
        }
        return true;
    }
    private void setErrorFalse(){
        ((TextInputLayout)fragmentView.findViewById(R.id.til_first_name)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_last_name)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_mobile_no)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_level)).setError(null);
        ((TextInputLayout)fragmentView.findViewById(R.id.til_time_zone)).setError(null);
    }

}



