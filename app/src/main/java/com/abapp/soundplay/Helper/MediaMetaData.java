package com.abapp.soundplay.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;

public class MediaMetaData {

    Context context;


    public MediaMetaData(Context context) {
        this.context = context;
    }



    public String getSongTitle(File path){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path.toString());
        return  mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }


    public String getSongArtist(File path) {
        if (!path.exists()) return "";

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path.toString());
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        if (artist == null) {
            return "";
        } else return artist;
    }

    public String getSongAlbum(File path) {
        if (!path.exists()) return "";

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path.toString());
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

        if (artist == null) {
            return "";
        } else return artist;
    }


    //get album art of song
    public Bitmap getSongBitmap(File uri) {

        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(String.valueOf(uri));
        try {
            byte[] art = mediaMetadata.getEmbeddedPicture();
            return BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (Exception e) {
            return null;
        }
    }



    public String getSongLength(File path) {
        if (!path.exists()) return "";

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path.toString());
        String during = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        Long dur = Long.parseLong(during);

        String Sec = String.valueOf((dur % 60000) / 1000);
        String min = String.valueOf(dur / 60000);

        if (Sec.length() == 1) {
            return min + ":0" + Sec;
        } else {
            return min + ":" + Sec;
        }
    }

}
