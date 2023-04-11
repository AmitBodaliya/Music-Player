package com.abapp.soundplay.Room.Songs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TableSongs entity);

    @Delete
    void delete(TableSongs tableSongs);

    @Query("SELECT * FROM table_songs")
    LiveData<List<TableSongs>> getAllEntities();


    @Query("SELECT COUNT(*) FROM table_songs")
    int getItemCount();
}

