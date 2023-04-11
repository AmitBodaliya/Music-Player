package com.abapp.soundplay.Room.Songs;


import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.Room.Fav.TableFav;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SongsRepository {
    private final SongsDao myDao;
    private final Executor executor;

    public SongsRepository(MyDatabaseSongs myDatabase) {
        myDao = myDatabase.myDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public int itemCount(){
       return myDao.getItemCount();
    }

    public void insert(TableSongs tableSongs) {
        executor.execute(() -> myDao.insert(tableSongs));
    }


    public void insertAll(ArrayList<SongsInfo> arrayList) {

        for (SongsInfo songsInfo: arrayList){
            TableSongs tableSongs = new TableSongs();
            tableSongs.Song_Path = songsInfo.getPath().toString();
            tableSongs.SONGS_TITLE = songsInfo.getTitle1();
            tableSongs.SONGS_ARTISTS = songsInfo.getArtist();
            tableSongs.SONGS_ALBUM = songsInfo.getAlbum();
            tableSongs.SONGS_LENGTH = songsInfo.getSongLength();

            executor.execute(() -> myDao.insert(tableSongs));
        }


    }
}
