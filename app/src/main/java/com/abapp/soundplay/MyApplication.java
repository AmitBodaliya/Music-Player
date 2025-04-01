package com.abapp.soundplay;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.google.android.material.color.DynamicColors;

import com.abapp.soundplay.params.Prefs;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }


}
