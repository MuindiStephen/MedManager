package com.android.benhaxe.medmanager.sync;

import android.content.Context;
import android.os.Bundle;

import com.android.benhaxe.medmanager.BaseActivity;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Benjamin Masebinu on 06-Apr-18.
 * Lead Organizer GDG FUTA
 * Android & CMS Developer at Planet Nest
 * haxeboom@gmail.com
 */

public class ReminderUtilities {

    // This class holds a method which schedules the job

    //TODO: the minutes interval should come from interval a user is setting on a drug
    private static final int REMINDER_INTERVAL_MINUTES = 120;
    private static final int REMINDER_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES);
    private static final int SYNC_FLEX_TIME_SECONDS = REMINDER_INTERVAL_SECONDS;

    //Unique tag to identify the job

    //To keep track if the job is started or not
    private static boolean sInitialized;

    //Synchronized means the method should not be executed more than once
    synchronized public static void scheduleMedicationReminder(Bundle bundle, Context context){
        if (sInitialized) return;

        String drugsEntryId = String.valueOf(bundle.getLong(BaseActivity.DRUGS_ID));

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);

        Job job = firebaseJobDispatcher.newJobBuilder()
                .setService(MedicationReminderJobService.class)
                .setTag(drugsEntryId)
                .setExtras(bundle)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEX_TIME_SECONDS))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        firebaseJobDispatcher.schedule(job);
        sInitialized = true;
    }
}
