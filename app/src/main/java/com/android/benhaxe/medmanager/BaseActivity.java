package com.android.benhaxe.medmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.benhaxe.medmanager.accountCreation.AccountActivity;
import com.android.benhaxe.medmanager.adapter.MedicineHolder;
import com.android.benhaxe.medmanager.adapter.MedicinePojo;
import com.android.benhaxe.medmanager.helper.BottomNavigationFragment;
import com.android.benhaxe.medmanager.ui.NewMedicine;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Benjamin Masebinu on 25-Mar-18.
 * Lead Organizer GDG FUTA
 * Android & CMS Developer at Planet Nest
 * haxeboom@gmail.com
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //[Start]
    public final static String REQUIRED = "Required";
    Fragment bottomNavigation;

    //[Fire base Nodes]
    public final static String USER = "Users";
    public final static String MEDICINE = "Medicines";

    //[Fire base values]
    public static final String DRUGS_ID = "id";
    public static final String DRUGS_NAME = "drugs";
    public static final String DRUGS_DOSAGE = "dosage";
    public final String DRUG_START_DATE = "start_date";
    public final String DRUGS_END_DATE = "stop_date";
    public final String DRUGS_MONTHS_ELAPSED = "months";

    public static final String DRUGS_INTERVAL = "interval";
    public static final String DRUG_USER = "user";

    public final String DATE_FORMAT = "dd MMM, yy";

    //Method to load  fragments concerned with account creation
    public void loadFragment(int frame_id, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frame_id, fragment);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }

    public void useBottomNavigation() {
        bottomNavigation = new BottomNavigationFragment();
        loadFragment(R.id.bottom_navigation_frame, bottomNavigation);
    }

    //[isNetworkConnected] Method to check if device  has internet connection
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void snackBar(View mView, String mBarMsg, int mDuration) {
        final Snackbar snackbar = Snackbar.make(mView, mBarMsg, mDuration);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(Color.GRAY);

        TextView snackBar_Tv = snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackBar_Tv.setTextColor(Color.WHITE);

        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        FirebaseAuth mAuth;
        switch (id) {
            case R.id.menu_logout:
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                intent = new Intent(this, AccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.menu_add:
                intent = new Intent(this, NewMedicine.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //[End]
}
