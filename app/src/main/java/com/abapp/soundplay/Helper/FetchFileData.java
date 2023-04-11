package com.abapp.soundplay.Helper;

import android.content.Context;


import com.abapp.soundplay.Model.SongsInfo;

import java.io.File;
import java.util.ArrayList;

public class FetchFileData {

    Context context;
    MediaMetaData mediaMetaData;

    public String[] songExtensions = {".mp3" , ".WAV" , ".AAC" , ".WMA"};


    public FetchFileData(Context context) {
        this.context = context;
        mediaMetaData = new MediaMetaData(context);
    }


    // getting all the dir from given location
    public ArrayList<SongsInfo> fetchFile(File file, Boolean song, Boolean dir, Boolean all) {

        ArrayList<SongsInfo> arrayList = new ArrayList<>();
        File[] songs = file.listFiles();


        if (dir) {
            if (songs != null) {
                for (File myFile : songs) {
                    if (!myFile.isHidden() && myFile.isDirectory()) {
                        arrayList.add(new SongsInfo(myFile.getName(), myFile));
                    }
                }
            }
            arrayList.sort(SongsInfo.sortByTitle);
        }


        if (song) {
            if (songs != null) {
                for (File myFile : songs) {
                    if (!myFile.isHidden() && !myFile.isDirectory()) {

                        for (String ext : songExtensions) {
                            if (myFile.getName().endsWith(ext) && !myFile.getName().startsWith(".")) {
                                arrayList.add(
                                        new SongsInfo(myFile.getName().substring(0, myFile.getName().lastIndexOf(".")),
                                                mediaMetaData.getSongArtist(myFile),
                                                mediaMetaData.getSongAlbum(myFile),
                                                mediaMetaData.getSongLength(myFile), myFile));

                            }
                        }
                    }
                }
            }

            arrayList.sort(SongsInfo.sortByTitle);

        } else if (all) {
            if (songs != null) {
                for (File myFile : songs) {

                    if (!myFile.isHidden() && myFile.isDirectory()) {
                        arrayList.addAll(fetchFile((myFile), false, false, true));
                    } else {

                        for (String ext : songExtensions) {
                            if (myFile.getName().endsWith(ext) && !myFile.getName().startsWith(".")) {
                                arrayList.add(new SongsInfo(
                                        myFile.getName().substring(0, myFile.getName().lastIndexOf(".")),
                                        mediaMetaData.getSongArtist(myFile),
                                        mediaMetaData.getSongAlbum(myFile),
                                        mediaMetaData.getSongLength(myFile), myFile));

                            }
                        }

                    }
                }

            }

            arrayList.sort(SongsInfo.sortByTitle);
        }

        return arrayList;
    }

}
