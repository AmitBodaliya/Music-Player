package com.abapp.soundplay.Room.Fav;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TableFav.class}, version = 1)
public abstract class MyDatabaseFav extends RoomDatabase {
    public abstract FavDao myDao();

    private static volatile MyDatabaseFav INSTANCE;

    public static MyDatabaseFav getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabaseFav.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MyDatabaseFav.class, "my_database_fav")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

