package com.oneminutebefore.workout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class RegisterFragment extends Fragment {
    private EditText etFirstName, etLastName, etMobileNo, etTimeZone, etLevel;
    private Button btnRegister,btnSignIn;


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
        View fragmentView = inflater.inflate(R.layout.fragment_register, container, false);
        etFirstName = (EditText) fragmentView.findViewById(R.id.et_first_name);
        etLastName = (EditText) fragmentView.findViewById(R.id.et_last_name);
        etMobileNo = (EditText) fragmentView.findViewById(R.id.et_mobile_no);
        etTimeZone = (EditText) fragmentView.findViewById(R.id.et_time_zone);
        etLevel = (EditText) fragmentView.findViewById(R.id.et_level);
        btnRegister = (Button) fragmentView.findViewById(R.id.btn_register);
        btnSignIn = (Button) fragmentView.findViewById(R.id.btn_login);
        return fragmentView;
    }

}



