package com.abapp.soundplay.Room.Fav;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "table_fav")
public class TableFav {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Song_Path")
    public String Song_Path;

    @ColumnInfo(name = "SONGS_TITLE")
    public String SONGS_TITLE;

    @ColumnInfo(name = "SONGS_ARTISTS")
    public String SONGS_ARTISTS;

    @ColumnInfo(name = "SONGS_ALBUM")
    public String SONGS_ALBUM;

    @ColumnInfo(name = "SONGS_LENGTH")
    public String SONGS_LENGTH;
}