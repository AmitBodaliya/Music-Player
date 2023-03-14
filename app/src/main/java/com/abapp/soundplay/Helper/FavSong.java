package com.abapp.soundplay.Helper;

import android.content.Context;
import android.widget.Toast;

import com.abapp.soundplay.Model.Songs;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.database.MyDBHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FavSong {

    Context context;
    MyDBHandler myDBHandler;

    public FavSong(Context context) {
        this.context = context;
        myDBHandler = new MyDBHandler(context);
    }


    public void addToFavourite(SongsInfo songsInfo) {
        if (!checkSongIsPresentInFav(songsInfo) && !songsInfo.getPath().isDirectory()) {
            myDBHandler.addFavSong(songsInfo);
        }
    }


    public void removeFavouriteSong(File file) {
        myDBHandler.deleteFavSongById(file);
    }

    public boolean checkSongIsPresentInFav(SongsInfo songsInfo) {
        return myDBHandler.songIsFav(songsInfo.getPath());
    }

    public boolean isListEmpty(){
        ArrayList<SongsInfo> songsInfos = myDBHandler.getAllFavSongs();
        return songsInfos.isEmpty();
    }


    public ArrayList<SongsInfo> getAllFavouriteSOng() {
        return myDBHandler.getAllFavSongs();
    }

}
