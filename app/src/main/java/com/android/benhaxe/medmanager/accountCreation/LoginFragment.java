package com.android.benhaxe.medmanager.accountCreation;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.benhaxe.medmanager.BaseActivity;
import com.android.benhaxe.medmanager.R;
import com.android.benhaxe.medmanager.ui.AllMed;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = LoginFragment.class.getSimpleName();
    View rootView;
    private ProgressDialog mProgressDialog;

    //[Declaration]
    BaseActivity baseActivity;

    //[Fire base]
    FirebaseAuth mAuth;
    DatabaseReference mUserReference;

    //For google Sign in
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN_CODE = 1;

    //[Views]
    private AutoCompleteTextView mEmail, mPassword;
    private Button mNormLogin;
    private SignInButton mGoogleSignIn;

    private TextView mSignUpInstead, resetPassword;

    String email, password;

    public LoginFragment() {
        // Required empty public constructor
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        baseActivity = new BaseActivity();
        mProgressDialog = new ProgressDialog(getContext());

        //[Fire base]
        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference(baseActivity.USER);

        mUserReference.keepSynced(true);

        //[Initialize  views]
        mEmail = rootView.findViewById(R.id.et_email);
        mPassword = rootView.findViewById(R.id.et_password);

        mNormLogin = rootView.findViewById(R.id.btn_login);
        mGoogleSignIn = rootView.findViewById(R.id.btn_google);

        resetPassword = rootView.findViewById(R.id.tv_forgot_password);
        mSignUpInstead = rootView.findViewById(R.id.tv_goto_sign_up);

        //[set on Click listener to each buttons]
        mNormLogin.setOnClickListener(this);
        mGoogleSignIn.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        mSignUpInstead.setOnClickListener(this);

        /**
         * Crete a user [GoogleSignInOption] Object that will request the user data required for this app
         * Using the google sign in object with the [DEFAULT_SIGN_IN] parameter it request for the basic information for the user
         * [GoogleSignInOptions] Objects creates a new configuration
         * */

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail() // Not recommended because Email address might change
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        isUserExist();

        //Nothing comes after the return statement
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check if user is currently signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            //normal login
            case R.id.btn_login:
                doLogin();
                break;

            //Google sign in
            case R.id.btn_google:
                googleSignIn();
                break;

            // Goto sign up
            case R.id.tv_goto_sign_up:
                ((AccountActivity) getActivity()).gotoSignup();

            // Reset Password
            case R.id.tv_forgot_password:
                resetPassword();
        }
    }

    //Google sign method here
    private void googleSignIn() {
        mProgressDialog.setMessage("Fetching Account");
        mProgressDialog.show();
        @SuppressLint("RestrictedApi") Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_CODE);
    }

    /*Result gotten after the intent*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_CODE) {
            mProgressDialog.dismiss();
            @SuppressLint("RestrictedApi") Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Fire base
                GoogleSignInAccount account = task.getResult(ApiException.class);
                fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "fire baseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            baseActivity.snackBar(rootView.findViewById(R.id.sign_in_snack_bar), "Authentication Failed.", Snackbar.LENGTH_SHORT);
                        }
                    }
                });
    }

    private boolean isFieldValid() {
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Enter a valid email address");
            return false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPassword.setError("Between 4 and 10 alphanumeric characters");
            return false;
        }

        return true;
    }

    private void doLogin() {
        if (!isFieldValid()) {
            return;
        }
        mProgressDialog.setMessage("Authenticating...");
        mProgressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isUserExist();
                        } else {
                            mProgressDialog.dismiss();
                            baseActivity.snackBar(rootView.findViewById(R.id.sign_in_snack_bar), "Login Failed", Snackbar.LENGTH_SHORT);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        baseActivity.snackBar(rootView.findViewById(R.id.sign_in_snack_bar), e.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                });
    }

    private void resetPassword(){
        email = mEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            mEmail.setError("Enter email");
        }else if(!email.contains("@")){
            mEmail.setError("Invalid email");
        }else {
            mAuth.sendPasswordResetEmail(email).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(),
                                        "We have sent you an email to reset your password",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Failed to send email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void isUserExist() {
        Log.d(TAG, "Current User: " + mAuth.getCurrentUser());
        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        Intent intent = new Intent(getContext(), AllMed.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mProgressDialog.dismiss();
                        startActivity(intent);
                        getActivity().finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mProgressDialog.dismiss();
                    baseActivity.snackBar(rootView.findViewById(R.id.sign_in_snack_bar), databaseError.getMessage(), Snackbar.LENGTH_SHORT);
                }
            });
        }
    }

    private void updateUI(FirebaseUser user) {
        mProgressDialog.dismiss();
        if (user != null) {
            startActivity(new Intent(getActivity(), AllMed.class));
            getActivity().finish();
        }
    }
}
