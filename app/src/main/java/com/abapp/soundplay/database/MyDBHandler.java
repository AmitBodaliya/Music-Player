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


    public String[] allExt = {".mp3" , ".WAV" , ".AAC" , ".WMA" ,  };

    public MyDBHandler(Context context){
        super(context , Params.DB_NAME , null ,Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + Params.TABLE_NAME + "(" + Params.ID +   " INTEGER PRIMARY KEY," + Params.SONG + " TEXT" + ")" ;
        String upNextListTable = "CREATE TABLE " + Params.UP_NEXT_TABLE + " (" + Params.SONG_PATH +   " TEXT PRIMARY KEY," + Params.SONGS_TITLE + " TEXT," + Params.SONGS_ARTISTS + " TEXT," + Params.SONGS_ALBUM + " TEXT," + Params.SONGS_LENGTH + " TEXT)" ;

        db.execSQL(createTable);
        db.execSQL(upNextListTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // aad data using Song class in sql data base
    public void addFavSong(Songs songs){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.SONG , songs.getSong());


        sqLiteDatabase.insert(Params.TABLE_NAME , null , values);
        sqLiteDatabase.close();
    }

    public ArrayList<Songs> getFavAllSongs(){
        ArrayList<Songs> tasksArrayList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String SELECT = "SELECT * FROM " + Params.TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(SELECT , null);

        if(cursor.moveToFirst()){
            do{
                Songs songs = new Songs();

                songs.setId(Integer.parseInt(cursor.getString(0)));
                songs.setSong(cursor.getString(1));

                if(new File(songs.getSong()).exists()){
                    tasksArrayList.add(songs);
                }
                else deleteFavSongById(songs.getId());

            }while (cursor.moveToNext());
        }
        return tasksArrayList;
    }



    public boolean addUpNextList(ArrayList<SongsInfo> list){

        Log.e("TAG" , "Up next list called to add " + list.size());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete(Params.UP_NEXT_TABLE, null, null);


        for(SongsInfo songs : list) {

                ContentValues values = new ContentValues();
                values.put(Params.SONG_PATH, songs.getPath().toString());
                values.put(Params.SONGS_TITLE, songs.getTitle());
                values.put(Params.SONGS_ARTISTS, songs.getArtist());
                values.put(Params.SONGS_ALBUM, songs.getAlbum());
                values.put(Params.SONGS_LENGTH, songs.getSongLength());

                sqLiteDatabase.insert(Params.UP_NEXT_TABLE, null, values);


        }

        Log.e("TAG" , "Up next list called to add " + getCountByTableName(Params.UP_NEXT_TABLE));

        sqLiteDatabase.close();



        return true;


    }



    public ArrayList<SongsInfo> getUpNextList(){

        ArrayList<SongsInfo> tasksArrayList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String SELECT = "SELECT * FROM " + Params.UP_NEXT_TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT , null);


        if(cursor.moveToFirst()){
            do{
                SongsInfo  songsInfo = new SongsInfo();

//                songsInfo.setId(Integer.parseInt(cursor.getString(0)));
                songsInfo.setPath(new File(cursor.getString(0)));
                songsInfo.setTitle(cursor.getString(1));
                songsInfo.setArtist(cursor.getString(2));
                songsInfo.setAlbum(cursor.getString(3));
                songsInfo.setSongLength(cursor.getString(4));

                if(songsInfo.getPath().exists()){
                    for(String ext : allExt){
                        if(songsInfo.getPath().getName().endsWith(ext)){
                            tasksArrayList.add(songsInfo);
                        }
                    }
                }
//                else deleteAllSongById(songsInfo.getId());

            }while (cursor.moveToNext());
        }
        return tasksArrayList;
    }
















    public boolean checkFavIS(String s){

        for(Songs song : getFavAllSongs()){
            if(song.getSong().equals(s)){
                return true;
            }
        }
        return false;
    }


    public int getPresentSOngId(String s){

        for(Songs song : getFavAllSongs()){
            if(song.getSong().equals(s)){
                return  song.getId();
            }
        }
        return  0;
    }


    public Songs getSong(int id){
        Songs songs = new Songs();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String SELECT = "SELECT * FROM " + Params.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT , null);

        if(cursor.move(id)) {

            songs.setId(Integer.parseInt(cursor.getString(0)));
            songs.setSong(cursor.getString(1));

        }

        return songs;
    }


    public void updateDB(Songs songs){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Params.SONG , songs.getSong());

        sqLiteDatabase.update(Params.TABLE_NAME, values, Params.ID + "=?", new String[]{String.valueOf(songs.getId())});
    }


    public void deleteFavSongById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Params.TABLE_NAME, Params.ID +"=?", new String[]{String.valueOf(id)});
        db.close();
    }


    public int getCountByTableName(String tableName){
        String query = "SELECT  * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount();

    }
}
