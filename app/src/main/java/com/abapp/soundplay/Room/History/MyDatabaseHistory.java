package com.abapp.soundplay.Room.History;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.abapp.soundplay.Room.Fav.FavDao;
import com.abapp.soundplay.Room.Fav.TableFav;

@Database(entities = {TableHistory.class}, version = 1)
public abstract class MyDatabaseHistory extends RoomDatabase {
    public abstract HistoryDao myDao();

    private static volatile MyDatabaseHistory INSTANCE;

    public static MyDatabaseHistory getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabaseHistory.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MyDatabaseHistory.class, "my_database_history")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

