package com.abapp.soundplay;

import android.app.Application;
import android.os.Build;

import com.google.android.material.color.DynamicColors;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DynamicColors.applyToActivitiesIfAvailable(this);

    }

    public int applyCustomTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) return R.style.Theme_SoundApp_M3;
        else return R.style.Theme_SoundApp_MC;
    }

}