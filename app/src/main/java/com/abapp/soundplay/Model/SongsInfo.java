package com.abapp.soundplay.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Comparator;

public class SongsInfo implements Parcelable {

    private String uniqueID;

    private String title;
    private String artist;
    private String album;
    private String songLength;
    private String path;
    private Bitmap bitmapImage ;


    public SongsInfo() {}


    //this is for dir
    public SongsInfo(String name, File myFile) {
        this.title = name;
        this.path = myFile.toString();
    }

    //master
    public SongsInfo(String title , String artist , String album , String songLength , File file){
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.songLength = songLength;
        this.path = file.toString();
    }


    //master with id
    public SongsInfo(String uniqueID, String title , String artist , String album , String songLength , File file){
        this.uniqueID = uniqueID;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.songLength = songLength;
        this.path = file.toString();
    }


    protected SongsInfo(Parcel in) {
        uniqueID = in.readString();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        songLength = in.readString();
        path = in.readString();
        bitmapImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uniqueID);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(songLength);
        dest.writeString(path);
        dest.writeParcelable(bitmapImage, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SongsInfo> CREATOR = new Creator<SongsInfo>() {
        @Override
        public SongsInfo createFromParcel(Parcel in) {
            return new SongsInfo(in);
        }

        @Override
        public SongsInfo[] newArray(int size) {
            return new SongsInfo[size];
        }
    };



    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getTitle() {
        return title;
    }
    public String getAlbum() {
        return album;
    }
    public String getArtist() {
        return artist;
    }
    public Bitmap getBitmapImage() {
        return bitmapImage;
    }
    public File getPath() {
        return new File(path);
    }
    public String getSongLength() {
        return songLength;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }
    public void setPath(File path) {
        this.path = path.toString();
    }
    public void setSongLength(String songLength) {
        this.songLength = songLength;
    }



    public static Comparator<SongsInfo> sortByTitle = (songsInfo, appInfo2) -> {

        String app1 = songsInfo.getTitle().toUpperCase();
        String app2 = appInfo2.getTitle().toUpperCase();

        return app1.compareTo(app2);
    };





}
