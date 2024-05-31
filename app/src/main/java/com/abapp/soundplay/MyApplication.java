package com.abapp.soundplay;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.abapp.soundplay.params.Prefs;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int applyCustomTheme(Context context) {
        Prefs prefs = new Prefs(context);
        String customTheme = prefs.getPrefs(Prefs.customTheme, "null").trim();

        Log.i("TAG", "checkTheme: " + customTheme);
        if((customTheme).equals("1")) return R.style.AppTheme_CustomAccent1;
        if((customTheme).equals("2"))return R.style.AppTheme_CustomAccent2;
        if((customTheme).equals("3"))return R.style.AppTheme_CustomAccent3;
        if((customTheme).equals("4"))return R.style.AppTheme_CustomAccent4;
        if((customTheme).equals("5"))return R.style.AppTheme_CustomAccent5;
        if((customTheme).equals("6"))return R.style.AppTheme_CustomAccent6;
        if((customTheme).equals("7"))return R.style.AppTheme_CustomAccent7;
        if((customTheme).equals("8"))return R.style.AppTheme_CustomAccent8;
        if((customTheme).equals("9"))return R.style.AppTheme_CustomAccent9;
        if((customTheme).equals("10")) return R.style.AppTheme_CustomAccent10;
        if((customTheme).equals("11")) return R.style.AppTheme_CustomAccent11;
        if((customTheme).equals("12")) return R.style.AppTheme_CustomAccent12;
        if((customTheme).equals("13")) return R.style.AppTheme_CustomAccent13;
        if((customTheme).equals("14")) return R.style.AppTheme_CustomAccent14;
        if((customTheme).equals("15")) return R.style.AppTheme_CustomAccent15;
        if((customTheme).equals("16")) return R.style.AppTheme_CustomAccent16;
        return R.style.Theme_SoundApp;
    }


}
