package com.android.benhaxe.medmanager.adapter;

/**
 * Created by Benjamin Masebinu on 30-Mar-18.
 * Lead Organizer GDG FUTA
 * Android & CMS Developer at Planet Nest
 * haxeboom@gmail.com
 */

public class MedicinePojo {
    String drugs, dosage, stop_date, months;

    public MedicinePojo() {
    }

    public MedicinePojo(String drugs, String dosage, String stop_date, String months) {
        this.drugs = drugs;
        this.dosage = dosage;
        this.stop_date = stop_date;
        this.months = months;

    }

    public String getDrugs() {
        return drugs;
    }

    public void setDrugs(String drugs) {
        this.drugs = drugs;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getStop_date() {
        return stop_date;
    }

    public void setStop_date(String stop_date) {
        this.stop_date = stop_date;
    }

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public boolean isForMonth(String month) {
        if (months == null) return false;
        return months.toLowerCase().contains(month);
    }
}
