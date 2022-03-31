package com.android.benhaxe.medmanager.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.benhaxe.medmanager.BaseActivity;
import com.android.benhaxe.medmanager.MonthsUI.AprilFragment;
import com.android.benhaxe.medmanager.MonthsUI.AugustFragment;
import com.android.benhaxe.medmanager.MonthsUI.DecemberFragment;
import com.android.benhaxe.medmanager.MonthsUI.FebruaryFragment;
import com.android.benhaxe.medmanager.MonthsUI.JanuaryFragment;
import com.android.benhaxe.medmanager.MonthsUI.JulyFragment;
import com.android.benhaxe.medmanager.MonthsUI.JuneFragment;
import com.android.benhaxe.medmanager.MonthsUI.MarchFragment;
import com.android.benhaxe.medmanager.MonthsUI.MayFragment;
import com.android.benhaxe.medmanager.MonthsUI.NovemberFragment;
import com.android.benhaxe.medmanager.MonthsUI.OctoberFragment;
import com.android.benhaxe.medmanager.MonthsUI.SeptemberFragment;
import com.android.benhaxe.medmanager.R;
import com.android.benhaxe.medmanager.adapter.MedicinePojo;
import com.android.benhaxe.medmanager.adapter.MonthlyAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicineByMonths extends BaseActivity {

    private static final String TAG = MedicineByMonths.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tablayout;
    private ViewPager viewPager;
    /**/

    public static DatabaseReference dbRef;
    public static FirebaseAuth mAuth;
    public static FirebaseUser user;

    public String currentMonths;
    public MonthlyAdapter myAdapters;

    /**/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_by_months);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tablayout = findViewById(R.id.tabs);
        // Assign the viewpager to tab
        tablayout.setupWithViewPager(viewPager);

        // What to query for
        dbRef = FirebaseDatabase.getInstance().getReference(MEDICINE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        useBottomNavigation();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new JanuaryFragment(), "January");
        viewPagerAdapter.addFragment(new FebruaryFragment(), "February");
        viewPagerAdapter.addFragment(new MarchFragment(), "March");
        viewPagerAdapter.addFragment(new AprilFragment(), "April");
        viewPagerAdapter.addFragment(new MayFragment(), "May");
        viewPagerAdapter.addFragment(new JuneFragment(), "June");
        viewPagerAdapter.addFragment(new JulyFragment(), "July");
        viewPagerAdapter.addFragment(new AugustFragment(), "August");
        viewPagerAdapter.addFragment(new SeptemberFragment(), "September");
        viewPagerAdapter.addFragment(new OctoberFragment(), "October");
        viewPagerAdapter.addFragment(new NovemberFragment(), "November");
        viewPagerAdapter.addFragment(new DecemberFragment(), "December");

        viewPager.setAdapter(viewPagerAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setEnabled(false);
        menuItem.setVisible(false);

        return true;
    }


    public void fetchPerMonth(String currentMonth, MonthlyAdapter myAdapter) {
        this.currentMonths = currentMonth;
        this.myAdapters = myAdapter;

        dbRef.orderByChild(DRUG_USER)
                .equalTo(user.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MedicinePojo medicinePojo = dataSnapshot.getValue(MedicinePojo.class);

                        if (medicinePojo != null && medicinePojo.isForMonth(currentMonths)) {
                            Log.d(TAG, "adapter: " + medicinePojo.toString());
                            myAdapters.add(medicinePojo);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        myAdapters.clear();
    }
}
