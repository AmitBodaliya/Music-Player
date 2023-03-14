package com.abapp.soundplay.params;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class Prefs {
    Context context;
    String projectName = "project";

    public static final String upNextListPath = "upNextListPath";
    public static final String upNextListPathFrom = "upNextListPathFrom";
    public static final String song1URI = "infoSong1";
    public static final String song2URI = "infoSong2";
    public static final String djMode = "djMode";
    public static final String pauseOn = "pause_on";
    public static final String appwidgetIdSingle = "APPWIDGET_ID_SINGLE";
    public static final String appwidgetIdDeck = "APPWIDGET_ID_DECK";

    public static final String allSongs= "allSongs";
    public static final String folderSongs =  "folderSongs";


    public Prefs(Context context){
        this.context = context;
    }

    public void setPrefs(String id , File data){
        SharedPreferences sharedPreferences = context.getSharedPreferences(projectName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (data.exists()) {
            editor.putString("" + id, data.toString());
            editor.apply();
        }
    }

    public void setPrefs(String id , String data){
        SharedPreferences sharedPreferences = context.getSharedPreferences(projectName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("" + id, data.toString());
        editor.apply();
    }

    public int getPrefs(String id , int defaultValue){
        SharedPreferences getShared = context.getSharedPreferences(projectName, MODE_PRIVATE);
        return getShared.getInt("" + id, defaultValue);
    }

    public String getPrefs(String id , String defaultValue){
        SharedPreferences getShared = context.getSharedPreferences(projectName, MODE_PRIVATE);
        return getShared.getString("" + id, defaultValue);
    }

    public boolean getPrefs(String id , boolean defaultValue){
        SharedPreferences getShared = context.getSharedPreferences(projectName, MODE_PRIVATE);
        return getShared.getBoolean("" + id, false);
    }




}
