package com.abapp.soundplay.Helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


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


    public SongsInfo createSongInfo(File file) {
        return new SongsInfo(
                file.getName().substring(0, file.getName().lastIndexOf(".")),
                mediaMetaData.getSongArtist(file),
                mediaMetaData.getSongAlbum(file),
                mediaMetaData.getSongLength(file),
                file
        );
    }

    public SongsInfo createSongInfoFromUri(Uri uri) {
        // Convert URI to file path
        String filePath = getFilePathFromUri(uri);
        if (filePath == null) {
            return null;
        }

        // Create File object from file path
        File file = new File(filePath);

        // Create and return SongsInfo object
        return new SongsInfo(
                file.getName().substring(0, file.getName().lastIndexOf(".")),
                mediaMetaData.getSongArtist(file),
                mediaMetaData.getSongAlbum(file),
                mediaMetaData.getSongLength(file),
                file
        );
    }


    private String getFilePathFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }

        String scheme = uri.getScheme();
        if ("content".equals(scheme)) {
            return getFilePathFromContentUri(uri);
        } else if ("file".equals(scheme)) {
            return uri.getPath();
        }
        return null;
    }

    private String getFilePathFromContentUri(Uri uri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            cursor.close();
            return filePath;
        }
        return null;
    }



}
