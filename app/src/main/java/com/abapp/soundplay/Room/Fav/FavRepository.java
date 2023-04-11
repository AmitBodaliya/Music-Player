package com.abapp.soundplay.Room.Fav;

import com.abapp.soundplay.Model.SongsInfo;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class FavRepository {
    private final FavDao myDao;
    private final Executor executor;

    public FavRepository(MyDatabaseFav myDatabase) {
        myDao = myDatabase.myDao();
        executor = Executors.newSingleThreadExecutor();
    }



    public void insert(SongsInfo songsInfo) {
        TableFav tableFav = new TableFav();
        tableFav.Song_Path = songsInfo.getPath().toString();
        tableFav.SONGS_TITLE = songsInfo.getTitle1();
        tableFav.SONGS_ARTISTS = songsInfo.getArtist();
        tableFav.SONGS_ALBUM = songsInfo.getAlbum();
        tableFav.SONGS_LENGTH = songsInfo.getSongLength();

        executor.execute(() -> myDao.insert(tableFav));
    }

    public void delete(SongsInfo songsInfo) {
        TableFav tableFav = new TableFav();
        tableFav.Song_Path = songsInfo.getPath().toString();
        tableFav.SONGS_TITLE = songsInfo.getTitle1();
        tableFav.SONGS_ARTISTS = songsInfo.getArtist();
        tableFav.SONGS_ALBUM = songsInfo.getAlbum();
        tableFav.SONGS_LENGTH = songsInfo.getSongLength();


        executor.execute(() -> myDao.delete(tableFav));
    }

    public boolean isPresent(File filePath){
        return myDao.itExists(filePath.toString());
    }
}
