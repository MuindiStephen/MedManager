package com.android.benhaxe.medmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.benhaxe.medmanager.R;

/**
 * Created by Benjamin Masebinu on 31-Mar-18.
 * Lead Organizer GDG FUTA
 * Android & CMS Developer at Planet Nest
 * haxeboom@gmail.com
 */

public class MedicineHolder extends RecyclerView.ViewHolder {

    //This holder works directly with the [item view]
    private TextView medName, medDosage, medEndDate;
    View mView;

    public MedicineHolder(View itemView) {
        super(itemView);

        mView = itemView;
    }

    public void setName(String drugs) {
        medName = itemView.findViewById(R.id.tv_drugs_name);
        medName.setText(drugs);
    }

    public void setDosage(String dosage) {
        medDosage = itemView.findViewById(R.id.tv_drugs_dosage);
        medDosage.setText(dosage);
    }

    public void setEndDate(String stop_date) {
        medEndDate = itemView.findViewById(R.id.tv_end_date);
        medEndDate.setText(stop_date);
    }
}
