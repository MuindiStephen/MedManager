package com.android.benhaxe.medmanager;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import net.alexandroid.shpref.ShPref;

/**
 * Created by Benjamin Masebinu on 01-Apr-18.
 * Lead Organizer GDG FUTA
 * Android & CMS Developer at Planet Nest
 * haxeboom@gmail.com
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ShPref.init(this, ShPref.APPLY);

        if (!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));

        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
/*
    public static void putEachMedication(String name, String dosage, int interval){
        ShPref.put(BaseActivity.DRUGS_NAME, name);
        ShPref.put(BaseActivity.DRUGS_DOSAGE, dosage);
        ShPref.put(BaseActivity.DRUGS_INTERVAL, interval);
    }*/
}
