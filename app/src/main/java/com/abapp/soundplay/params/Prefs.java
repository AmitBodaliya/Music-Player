package com.abapp.soundplay.params;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;



public class Prefs {
    private static  Context context;
    private static final String projectName = "project";

    public static final String customTheme = "custom_theme";

    public static final String splashSongLoad = "splash_song_load";
    public static final String directSong = "direct_song";



    public Prefs(Context context) {
        Prefs.context = context.getApplicationContext();
    }


    public void setPrefs(String id, File data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(projectName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (data.exists()) {
            editor.putString(id, data.toString());
            editor.apply();
        }
    }


    public void setPrefs(String id, Boolean data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(projectName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(id, data);
        editor.apply();
    }

    public boolean setPrefs(String id, String data) {
        Log.d("TAG", "setPrefs() called with: id = [" + id + "], data = [" + data + "]");
        SharedPreferences sharedPreferences = context.getSharedPreferences(projectName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(id, data);
        editor.apply();
        return true;
    }

    public int getPrefs(String id, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(projectName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(id, defaultValue);
    }

    public String getPrefs(String id, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(projectName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(id, defaultValue);
    }

    public boolean getPrefs(String id, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(projectName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(id, defaultValue);
    }
}
