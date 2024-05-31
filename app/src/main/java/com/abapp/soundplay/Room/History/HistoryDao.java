package com.abapp.soundplay.Room.History;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TableHistory entity);

    @Delete
    void delete(TableHistory tableHistory);

    @Query("SELECT EXISTS(SELECT * FROM table_history WHERE Song_Path = :id)")
    boolean itExists(int id);

    @Query("SELECT * FROM table_history")
    List<TableHistory> getAllEntitiesN();


    @Query("SELECT * FROM table_history")
    LiveData<List<TableHistory>> getAllEntities();

}

