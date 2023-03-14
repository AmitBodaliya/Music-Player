package com.abapp.soundplay.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.abapp.soundplay.Model.Songs;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.params.Params;

import java.io.File;
import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {

    public MyDBHandler(Context context){
        super(context , Params.DB_NAME , null ,Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String favSOng = "CREATE TABLE " + Params.TABLE_NAME + " (" + Params.SONG_PATH +   " TEXT PRIMARY KEY," + Params.SONGS_TITLE + " TEXT," + Params.SONGS_ARTISTS + " TEXT," + Params.SONGS_ALBUM + " TEXT," + Params.SONGS_LENGTH + " TEXT)" ;
        db.execSQL(favSOng);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // aad data using Song class in sql data base
    public void addFavSong(SongsInfo songsInfo){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.SONG_PATH , songsInfo.getPath().toString());
        values.put(Params.SONGS_TITLE , songsInfo.getTitle());
        values.put(Params.SONGS_ARTISTS , songsInfo.getArtist());
        values.put(Params.SONGS_ALBUM , songsInfo.getAlbum());
        values.put(Params.SONGS_LENGTH , songsInfo.getSongLength());


        sqLiteDatabase.insert(Params.TABLE_NAME , null , values);
        sqLiteDatabase.close();
    }

    public ArrayList<SongsInfo> getAllFavSongs(){
        ArrayList<SongsInfo> finalList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String SELECT = "SELECT * FROM " + Params.TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(SELECT , null);

        if(cursor.moveToFirst()){
            do{
                SongsInfo songsInfo = new SongsInfo();

                songsInfo.setPath(new File(cursor.getString(0)));
                songsInfo.setTitle(cursor.getString(1));
                songsInfo.setArtist(cursor.getString(2));
                songsInfo.setAlbum(cursor.getString(3));
                songsInfo.setSongLength(cursor.getString(4));

                if(songsInfo.getPath().exists()) finalList.add(songsInfo);
                else deleteFavSongById(songsInfo.getPath());

            }while (cursor.moveToNext());
        }
        return finalList;
    }


    public boolean songIsFav(File path){

        for(SongsInfo songsInfo : getAllFavSongs()){
            if(songsInfo.getPath().toString().equals(path.toString())){
                return true;
            }
        }
        return false;
    }


    public void deleteFavSongById(File file){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Params.TABLE_NAME, Params.SONG_PATH +"=?", new String[]{file.toString()});
        db.close();
    }


    public int getCountByTableName(String tableName){
        String query = "SELECT  * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount();

    }
}
