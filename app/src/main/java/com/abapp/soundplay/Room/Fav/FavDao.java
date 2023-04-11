package com.abapp.soundplay.Room.Fav;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface FavDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TableFav entity);

    @Delete
    void delete(TableFav tableFav);


    @Query("SELECT EXISTS(SELECT * FROM table_fav WHERE Song_Path = :id)")
    boolean itExists(String id);

    @Query("SELECT * FROM table_fav")
    List<TableFav> getAllEntitiesN();

    @Query("SELECT * FROM table_fav")
    LiveData<List<TableFav>> getAllEntities();

}

