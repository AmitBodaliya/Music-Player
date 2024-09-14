package com.abapp.soundplay.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.abapp.soundplay.Model.SongsInfo;

import java.io.File;
import java.util.HashMap;

public class MusicArt {

    HashMap<File, Bitmap> hashMap;

    private static MusicArt instance;

    private MusicArt() {
        hashMap = new HashMap<>();
    }

    public static synchronized MusicArt getInstance() {
        if (instance == null) {
            instance = new MusicArt();
        }
        return instance;
    }


    public Bitmap getAlbumArt(SongsInfo songsInfo){
        Bitmap bitmap = hashMap.get(songsInfo.getPath());
        if (bitmap == null){

            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(songsInfo.getPath().toString());
            try {
                byte[] art = metaRetriever.getEmbeddedPicture();
                Bitmap bitmap1 =  BitmapFactory.decodeByteArray(art, 0, art.length);
                hashMap.put(songsInfo.getPath() , bitmap1);
                return bitmap1;
            } catch (Exception e) {
                return null;
            }

        }else return bitmap;
    }

}
