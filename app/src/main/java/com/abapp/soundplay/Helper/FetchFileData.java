package com.abapp.soundplay.Helper;

import android.content.Context;


import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.params.HardCoreData;

import java.io.File;
import java.util.ArrayList;

public class FetchFileData {

    Context context;
    MediaMetaData mediaMetaData;



    public FetchFileData(Context context) {
        this.context = context;
        mediaMetaData = new MediaMetaData(context);
    }


    public ArrayList<SongsInfo> fetchFile(File file, boolean song, boolean dir, boolean all) {
        ArrayList<SongsInfo> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {

            for (File myFile : files) {

                if (!myFile.isHidden()) {
                    if (dir && myFile.isDirectory()) {
                        arrayList.add(new SongsInfo(myFile.getName(), myFile));
                    }

                    else if (song && !myFile.isDirectory()) {
                        for (String ext : HardCoreData.songExtensions) {
                            if (myFile.getName().endsWith(ext) && !myFile.getName().startsWith(".")) {
                                arrayList.add(createSongInfo(myFile));
                                break;
                            }
                        }
                    }

                    else if (all) {
                        if (myFile.isDirectory()) {
                            arrayList.addAll(fetchFile(myFile, false, false, true));
                        }
                        else {
                            for (String ext : HardCoreData.songExtensions) {
                                if (myFile.getName().endsWith(ext) && !myFile.getName().startsWith(".")) {
                                    arrayList.add(createSongInfo(myFile));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        arrayList.sort(SongsInfo.sortByTitle);
        return arrayList;
    }


    private SongsInfo createSongInfo(File file) {
        return new SongsInfo(
                file.getName().substring(0, file.getName().lastIndexOf(".")),
                mediaMetaData.getSongArtist(file),
                mediaMetaData.getSongAlbum(file),
                mediaMetaData.getSongLength(file),
                file
        );
    }





}
