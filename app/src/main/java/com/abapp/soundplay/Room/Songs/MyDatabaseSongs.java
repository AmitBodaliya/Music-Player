package com.abapp.soundplay.Room.Songs;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.abapp.soundplay.Room.Fav.FavDao;
import com.abapp.soundplay.Room.Fav.TableFav;

@Database(entities = {TableSongs.class}, version = 1)
public abstract class MyDatabaseSongs extends RoomDatabase {
    public abstract SongsDao myDao();

    private static volatile MyDatabaseSongs INSTANCE;

    public static MyDatabaseSongs getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabaseSongs.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MyDatabaseSongs.class, "my_database_songs")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

