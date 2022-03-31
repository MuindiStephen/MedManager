package com.android.benhaxe.medmanager.ui;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.benhaxe.medmanager.BaseActivity;
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

public class AllMed extends BaseActivity implements SearchView.OnQueryTextListener {
    public static final String TAG = AllMed.class.getSimpleName();

    public static Toolbar toolbar;

    // Views
    private RecyclerView recyclerView;

    //Fire base
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<MedicinePojo> medicinePojos;

    private MonthlyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rv_medicine);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbRef = FirebaseDatabase.getInstance().getReference(MEDICINE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        /**/
        medicinePojos = new ArrayList<>();
        adapter = new MonthlyAdapter(medicinePojos, this);

        fetchAllMed();

        adapter.clear();
        recyclerView.setAdapter(adapter);
        /**/
        dbRef.keepSynced(true);
        useBottomNavigation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(this);
        return true;
    }

    public void fetchAllMed() {
        dbRef.orderByChild(DRUG_USER)
                .equalTo(user.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MedicinePojo medicinePojo = dataSnapshot.getValue(MedicinePojo.class);

                        if (medicinePojo != null) {
                            adapter.add(medicinePojo);
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
        adapter.clear();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();

        ArrayList<MedicinePojo> newList = new ArrayList<>();

        for (MedicinePojo medicinePojo : medicinePojos) {
            String drugName = medicinePojo.getDrugs().toLowerCase();

            if (drugName.contains(newText)) {
                newList.add(medicinePojo);
            }

            adapter.setFilter(newList);
        }
        return true;
    }
}
