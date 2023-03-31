package com.abapp.soundplay.Helper;

import android.content.Context;

import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.database.MyDBHandler;

import java.io.File;
import java.util.ArrayList;

public class HistorySong {

    Context context;
    MyDBHandler myDBHandler;

    public HistorySong(Context context) {
        this.context = context;
        myDBHandler = new MyDBHandler(context);
    }


    public void addToHistory(SongsInfo songsInfo) {
        if (myDBHandler.songIsInHistory(songsInfo.getPath())){
            removeFromHistory(songsInfo.getPath());
        }

        myDBHandler.addToHistory(songsInfo);
    }


    public void removeFromHistory(File file) {
        myDBHandler.deleteHistoryById(file);
    }


    public boolean isListEmpty(){
        return myDBHandler.getAllHistory().isEmpty();
    }


    public ArrayList<SongsInfo> getAllHistory() {
        return myDBHandler.getAllHistory();
    }

}
