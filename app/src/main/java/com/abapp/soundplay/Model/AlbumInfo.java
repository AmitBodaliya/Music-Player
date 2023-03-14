package com.abapp.soundplay.Model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Comparator;

public class AlbumInfo {

    String title = "<unknown>";
    Bitmap titleImage;
    ArrayList<SongsInfo> arrayList = new ArrayList<>();

    public AlbumInfo(String title ){
        this.title = title;
    }

    public AlbumInfo(String title , ArrayList<SongsInfo> list){
        this.title = title;
        this.arrayList =  list;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(Bitmap titleImage) {
        this.titleImage = titleImage;
    }


    //array list
    public ArrayList<SongsInfo> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<SongsInfo> arrayListArrayList) {
        this.arrayList = arrayListArrayList;
    }

    public void addItem(SongsInfo songsInfo){
        getArrayList().add(songsInfo);
    }






    public static Comparator<AlbumInfo> AppNameComparator   = (al, al2) -> {

        String app1 = al.getTitle().toUpperCase();
        String app2 = al2.getTitle().toUpperCase();

        return app1.compareTo(app2);
    };
}
