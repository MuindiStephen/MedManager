package com.android.benhaxe.medmanager.sync;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.android.benhaxe.medmanager.BaseActivity;
import com.android.benhaxe.medmanager.R;
import com.android.benhaxe.medmanager.ui.AllMed;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import static com.android.benhaxe.medmanager.BaseActivity.DRUGS_INTERVAL;
import static com.android.benhaxe.medmanager.sync.MedicationReminderJobService.NotificationUtils.alarmSound;
import static com.android.benhaxe.medmanager.sync.MedicationReminderJobService.NotificationUtils.largeIcon;
import static com.android.benhaxe.medmanager.sync.MedicationReminderJobService.NotificationUtils.pendingIntent;

/**
 * Created by Benjamin Masebinu on 31-Mar-18.
 * Lead Organizer GDG FUTA
 * Android & CMS Developer at Planet Nest
 * haxeboom@gmail.com
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MedicationReminderJobService extends JobService{

    /**
     * By default JobService runs on the main thread
     *
     * */
    private static final String TAG = MedicationReminderJobService.class.getSimpleName();

    private static AsyncTask mBackgroundTask;

    //onStartJob - Execute the task that send out the notification that we have made
    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        final Bundle bundle = jobParameters.getExtras();
        // Make it run in the background
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = MedicationReminderJobService.this;
                    // Notification builder to describe a notification
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_notification_small)
                            .setLargeIcon(largeIcon(context))
                            .setContentTitle("Med Manager")
                            .setContentText(bundle.getString(BaseActivity.DRUGS_NAME))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(BaseActivity.DRUGS_NAME))
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setSound(alarmSound)
                            .setExtras(bundle)
                            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            .setContentIntent(pendingIntent(context))
                            .setAutoCancel(true);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
                    }

                    //Notification Manager to displays a notification
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify((int)bundle.getLong(DRUGS_INTERVAL), notificationBuilder.build());
                return null;
            }

            // Job services tells the system when the job is done by calling jobFinished
            @Override
            protected void onPostExecute(Object o) {
                jobFinished(jobParameters, false);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    //onStop - It get called when the requirement is no more met
    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null)mBackgroundTask.cancel(true);
        //returning true here - means as soon as the condition/requirement of the job is met it should retry iy
        return true;
    }


    //Inner class for notification utils
    public static class NotificationUtils {
        private static final int DRUGS_REMINDER_PENDING_INTENT_ID = 100;
        public static  Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Helper method
        public static PendingIntent pendingIntent(Context context){

            //This method returns the instance of a pending intent - that will be passed to another app (Notification manager)

            Intent startActivityIntent = new Intent(context, AllMed.class);

            /**
             * context: The context in which the pending intent should start the activity
             * requestCode - int:Private request code for the sender
             * intent - Intent of the activity to be launched
             * flag
             * */

            return PendingIntent.getActivity(
                    context,
                    DRUGS_REMINDER_PENDING_INTENT_ID,
                    startActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        public static Bitmap largeIcon(Context context){
            Resources res = context.getResources();

            return BitmapFactory.decodeResource(res, R.drawable.ic_time);
        }
    }
}
