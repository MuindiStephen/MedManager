package com.android.benhaxe.medmanager.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.benhaxe.medmanager.BaseActivity;
import com.android.benhaxe.medmanager.R;
import com.android.benhaxe.medmanager.sync.ReminderUtilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewMedicine extends BaseActivity
        implements View.OnClickListener {

    private static final String TAG = NewMedicine.class.getSimpleName();
    public static BaseActivity baseActivity;
    ProgressDialog progressDialog;

    //[Declaration] of views
    private AutoCompleteTextView drugsName, drugsDosage;
    private static TextView startDate, endDate;

    private Spinner timeInterval;
    private String drugs_interval;
    int thisMedInterval;

    long id = 1;

    //[Fire base]
    private DatabaseReference medicineRef;
    private FirebaseAuth mAuth;

    private DatePickerDialogFragment mDatePickerDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medicine);

        baseActivity = new BaseActivity();
        progressDialog = new ProgressDialog(this);

        // Setting up custom actionbar using toolbar
        Toolbar toolbar;
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //[Initialize] views
        drugsName = findViewById(R.id.et_med_name);
        drugsDosage = findViewById(R.id.et_med_dosage);

        startDate = findViewById(R.id.tv_start_date);
        endDate = findViewById(R.id.tv_end_date);

        startDate.setText(getCurrentDate());

        timeInterval = findViewById(R.id.spinner_interval);

        //[set on click listener]
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);

        // Fire base
        medicineRef = FirebaseDatabase.getInstance().getReference(MEDICINE);
        mAuth = FirebaseAuth.getInstance();

        mDatePickerDialogFragment = new DatePickerDialogFragment();
        setUpSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_medicine, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_start_date:
                mDatePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_START_DATE);
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.tv_end_date:
                mDatePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_END_DATE);
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                break;
        }
    }


    public static class DatePickerDialogFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        public static final int FLAG_START_DATE = 0;
        public static final int FLAG_END_DATE = 1;

        private int flag = 0;

        public void setFlag(int i) {
            flag = i;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat(baseActivity.DATE_FORMAT, Locale.US);

            if (flag == FLAG_START_DATE) {
                startDate.setText(sdf.format(calendar.getTime()));
            } else if (flag == FLAG_END_DATE) {
                endDate.setText(sdf.format(calendar.getTime()));
            }
        }
    }

    void setUpSpinner() {
        timeInterval = findViewById(R.id.spinner_interval);

        // Spinner for Local government area
        ArrayAdapter<CharSequence> lgaSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.drugs_interval,
                android.R.layout.simple_spinner_dropdown_item);

        /*Set drop down view for each spinner*/
        lgaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Apply adapter to spinner
        timeInterval.setAdapter(lgaSpinnerAdapter);
        setUpSpinnerSelectedListener();
    }

    void setUpSpinnerSelectedListener() {
        timeInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);

                drugs_interval = adapterView.getItemAtPosition(position).toString();
                String[] interval = drugs_interval.split(" ");

                String getNextTime = interval[0];
                thisMedInterval = Integer.valueOf(getNextTime);

                Log.d(TAG, "Next Time: " + thisMedInterval);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void makeNewMedication() {
        progressDialog.setMessage("Creating new medication...");
        progressDialog.show();
        String medName, medDosage, medStartDate, medStopDate;

        Log.w(TAG, "Gotten here");

        Log.d(TAG, "Old Drugs ID: " + id);
        id = id++;
        Log.d(TAG, "New Drugs ID: " + id);

        medName = drugsName.getText().toString();
        medDosage = drugsDosage.getText().toString();

        medStartDate = startDate.getText().toString();
        medStopDate = endDate.getText().toString();

        Log.w(TAG, "Medicine details: " + medName + " " + medDosage + " " + medStartDate + " " + medStopDate);

        if (!TextUtils.isEmpty(medName) && !TextUtils.isEmpty(medDosage) && !TextUtils.isEmpty(medStartDate) && !TextUtils.isEmpty(medStopDate)) {

            /*App.putEachMedication(medName, medDosage, thisMedInterval);*/

            Bundle bundle = bundleDrugs(id, medName, medDosage);

            //schedules the notification using fire base job scheduler
            ReminderUtilities.scheduleMedicationReminder(bundle, this);

            DatabaseReference newMedicine = medicineRef.push();

            newMedicine.child(DRUGS_NAME).setValue(medName);
            newMedicine.child(DRUGS_DOSAGE).setValue(medDosage);
            newMedicine.child(DRUG_START_DATE).setValue(medStartDate);
            newMedicine.child(DRUGS_END_DATE).setValue(medStopDate);

            newMedicine.child(DRUGS_INTERVAL).setValue(String.valueOf(thisMedInterval));
            try {
                newMedicine.child(DRUGS_MONTHS_ELAPSED).setValue(setDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newMedicine.child(DRUG_USER).setValue(mAuth.getCurrentUser().getUid());

            progressDialog.dismiss();
            Intent intent = new Intent(NewMedicine.this, AllMed.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            progressDialog.dismiss();
            drugsName.setError(REQUIRED);
            drugsDosage.setError(REQUIRED);
        }
    }

    private Bundle bundleDrugs(long id,  String medName, String medDosage){
        Bundle bundle = new Bundle();

        bundle.putLong(DRUGS_ID, id);

        bundle.putString(DRUGS_NAME, medName);
        bundle.putString(DRUGS_DOSAGE, medDosage);

        bundle.putInt(DRUGS_INTERVAL, thisMedInterval);

        return bundle;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_save:
                makeNewMedication();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String getCurrentDate(){
        Date date = new Date();

        Log.d(TAG, "Current Date: " + new SimpleDateFormat(baseActivity.DATE_FORMAT, Locale.US).format(date));
        return new SimpleDateFormat(baseActivity.DATE_FORMAT, Locale.US).format(date);
    }

    private static List<String> getMonths(Date startDate, Date endDate){
        List<String> date = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(startDate);
        while (calendar.getTimeInMillis() < endDate.getTime()) {
            calendar.add(Calendar.MONTH, 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.US);
            date.add(simpleDateFormat.format(calendar.getTime()));
        }
        Log.d(TAG, "Months between dates: " + date);
        return date;
    }

    String setDate() throws ParseException{
        String drugsStartString, drugsEndString;
        Date drugsStartDate, drugsEndDate;

        /**
         * Get the string value of the [start] & [end] date selected by the user
         * And then initialize it to the date
         * */
        drugsStartString = startDate.getText().toString();
        drugsEndString = endDate.getText().toString();

        // Set Format for the start and end date
        DateFormat df = new SimpleDateFormat(baseActivity.DATE_FORMAT, Locale.US);

        drugsStartDate = df.parse(drugsStartString);
        drugsEndDate = df.parse(drugsEndString);
        return String.valueOf(getMonths(drugsStartDate, drugsEndDate));
    }
}
