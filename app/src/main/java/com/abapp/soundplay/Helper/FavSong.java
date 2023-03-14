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


    public void addToFavourite(File file) {
        if (!checkSongIsPresentInFav(file) && !file.isDirectory()) {
            Songs songs = new Songs();
            songs.setSong(file.toString());

            myDBHandler.addFavSong(songs);
        }
    }


    public void removeFavouriteSong(File file) {
        myDBHandler.deleteFavSongById(myDBHandler.getPresentSOngId(file.toString()));
    }

    public boolean checkSongIsPresentInFav(File file) {
        return myDBHandler.checkFavIS(file.toString());
    }


    public ArrayList<SongsInfo> getAllFavouriteSOng() {
        ArrayList<SongsInfo> list = new ArrayList<>();

        ArrayList<Songs> songsArrayList = myDBHandler.getFavAllSongs();

        if (songsArrayList.size() == 0) {
            Toast.makeText(context , "No Favourite Songs", Toast.LENGTH_SHORT).show();
            return null;
        }

        for (Songs song : songsArrayList) {
            list.add(new SongsInfo(new File(song.getSong()).getName(), new File(song.getSong())));
        }

        Collections.sort(list, SongsInfo.sortByTitle);

        return list;
    }




}
