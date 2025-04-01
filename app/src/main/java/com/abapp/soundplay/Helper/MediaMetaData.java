package com.abapp.soundplay.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.io.IOException;

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


    public Bitmap getSongBitmap(File uri) {
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        try {
            if (uri == null || !uri.exists() || !uri.isFile() || !uri.canRead()) {
                Log.e("MediaMetaData", "Invalid file: " + (uri != null ? uri.getAbsolutePath() : "null"));
                return null;
            }

            mediaMetadata.setDataSource(uri.getAbsolutePath());
            byte[] art = mediaMetadata.getEmbeddedPicture();

            if (art != null && art.length > 0) {
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            } else {
                Log.w("MediaMetaData", "No album art found for: " + uri.getAbsolutePath());
                return null; // Return null if no album art is found
            }

        } catch (Exception e) {
            Log.e("MediaMetaData", "Error retrieving album art", e);
            return null;
        } finally {
            try {
                mediaMetadata.release(); // Release resources to avoid memory leaks
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public String getSongLength(File path) {
        if (!path.exists()) return "";

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path.toString());
        String during = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        assert during != null;
        long dur = Long.parseLong(during);

        String Sec = String.valueOf((dur % 60000) / 1000);
        String min = String.valueOf(dur / 60000);

        return (Sec.length() == 1)? min + ":0" + Sec :  min + ":" + Sec;
    }

}
