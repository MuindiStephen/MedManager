package com.android.benhaxe.medmanager.accountCreation;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.android.benhaxe.medmanager.BaseActivity;
import com.android.benhaxe.medmanager.R;
import com.android.benhaxe.medmanager.ui.AllMed;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    public static final String TAG = LoginFragment.class.getSimpleName();
    View rootView;
    private ProgressDialog mProgressDialog;

    //[Declaration]
    BaseActivity baseActivity;

    //[Fire base]
    FirebaseAuth mAuth;
    DatabaseReference mUserReference;

    //[Views]
    private AutoCompleteTextView mName, mEmail, mPassword, mConfirmPass;
    TextView goto_login;

    String name, email, password, confirmPass;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        baseActivity = new BaseActivity();
        mProgressDialog = new ProgressDialog(getContext());

        //[Fire base]
        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference(baseActivity.USER);

        mUserReference.keepSynced(true);

        //[Initialize  views]
        mName = rootView.findViewById(R.id.et_Name);
        mEmail = rootView.findViewById(R.id.et_email);
        mPassword = rootView.findViewById(R.id.et_password);
        mConfirmPass = rootView.findViewById(R.id.et_re_password);

        // Start Sign up from here
        rootView.findViewById(R.id.btn_sign_up)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignUp();
            }
        });

        // Go to login screen from here
        goto_login = rootView.findViewById(R.id.tv_goto_login);
        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountActivity)getActivity()).gotoLogin();
            }
        });
        return rootView;
    }

    private boolean isFieldValid(){
        name = mName.getText().toString().trim();
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();
        confirmPass = mConfirmPass.getText().toString().trim();

        if (name.isEmpty() || name.length() < 3) {
            mName.setError("At least 3 characters");
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Enter a valid email address");
            return false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPassword.setError("Between 4 and 10 alphanumeric characters");
            return false;
        }

        if (confirmPass.isEmpty() || confirmPass.length() < 4 || confirmPass.length() > 10 || !(confirmPass.equals(password))) {
            mConfirmPass.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void doSignUp(){
        final String region = "San Francisco, CA";
        if (!isFieldValid()) {
            return;
        }
        mProgressDialog.setMessage("Creating User...");
        mProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDetails = mUserReference.child(user_id);

                            currentUserDetails.child("name").setValue(name);
                            currentUserDetails.child("email").setValue(email);
                            currentUserDetails.child("password").setValue(password);

                            currentUserDetails.child("region").setValue(region);

                            Intent intent = new Intent(getContext(), AllMed.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mProgressDialog.dismiss();
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        baseActivity.snackBar(rootView.findViewById(R.id.sign_up_snack_bar), e.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                });
    }
}
