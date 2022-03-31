package com.android.benhaxe.medmanager.helper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.benhaxe.medmanager.R;
import com.android.benhaxe.medmanager.ui.AllMed;
import com.android.benhaxe.medmanager.ui.MedicineByMonths;
import com.android.benhaxe.medmanager.ui.ProfileActivity;

/**
 * Created by Benjamin Masebinu on 07-Apr-18.
 * Lead Organizer GDG FUTA
 * Android & CMS Developer at Planet Nest
 * haxeboom@gmail.com
 */

public class BottomNavigationFragment extends Fragment implements
        BottomNavigationView.OnNavigationItemSelectedListener{
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);

        BottomNavigationView navigation = rootView.findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(BottomNavigationFragment.this);

        //Attach bottom sheet behaviour hide/show on scroll using the Coordinator layout params
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void gotoProfile() {
        startActivity(new Intent(getActivity(), ProfileActivity.class));
        getActivity().finish();
    }

    public void gotoMonthView() {
        startActivity(new Intent(getActivity(), MedicineByMonths.class));
        getActivity().finish();
    }

    public void gotoMedicine() {
        startActivity(new Intent(getActivity(), AllMed.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.b_navigation_account:
                gotoProfile();
                return true;

            case R.id.b_navigation_medicine:
                gotoMedicine();
                return true;

            case R.id.b_navigation_month_view:
                gotoMonthView();
                return true;
        }
        return false;
    }
}
