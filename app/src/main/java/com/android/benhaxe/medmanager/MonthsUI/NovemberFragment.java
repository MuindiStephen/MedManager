package com.android.benhaxe.medmanager.MonthsUI;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.benhaxe.medmanager.R;
import com.android.benhaxe.medmanager.adapter.MedicinePojo;
import com.android.benhaxe.medmanager.adapter.MonthlyAdapter;
import com.android.benhaxe.medmanager.ui.MedicineByMonths;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NovemberFragment extends Fragment {
    View rootView;


    public NovemberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_months, container, false);

        RecyclerView recyclerView;

        List<MedicinePojo> thisMedicinePojos;
        MonthlyAdapter thisAdapter;

        // Set up the recycler view here
        recyclerView = rootView.findViewById(R.id.rv_medicine);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        thisMedicinePojos = new ArrayList<>();
        thisAdapter = new MonthlyAdapter(thisMedicinePojos, getActivity());

        new MedicineByMonths().fetchPerMonth("november", thisAdapter);

        thisAdapter.clear();
        recyclerView.setAdapter(thisAdapter);
        return rootView;
    }

}
