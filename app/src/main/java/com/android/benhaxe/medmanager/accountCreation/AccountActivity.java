package com.android.benhaxe.medmanager.accountCreation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.android.benhaxe.medmanager.BaseActivity;
import com.android.benhaxe.medmanager.R;

public class AccountActivity extends BaseActivity {

    Fragment loginFragment, signUpFragment;
    int accountFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account);

        accountFrame = R.id.account_frame;
        //Create instance of the Fragment
        loginFragment = new LoginFragment();
        signUpFragment = new SignUpFragment();

        gotoLogin();
    }

    public void gotoLogin() {
        loadFragment(accountFrame, loginFragment);
    }

    public void gotoSignup() {
        loadFragment(accountFrame, signUpFragment);
    }
}
