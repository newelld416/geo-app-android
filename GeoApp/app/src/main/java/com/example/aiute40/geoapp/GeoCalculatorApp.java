package com.example.aiute40.geoapp;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by adam on 5/30/17.
 */

public class GeoCalculatorApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
