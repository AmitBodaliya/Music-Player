package com.abapp.soundplay.Helper;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    public Bitmap getAlbumArt(SongsInfo songsInfo , ImageView imageView){
        Bitmap bitmap = hashMap.get(songsInfo.getPath());

        if (bitmap == null){
            if (imageView != null) new loadAlbumArt(imageView, songsInfo).execute();
            return null;
        }else return bitmap;
    }





    // download thumbnail of video
    @SuppressLint("StaticFieldLeak")
    public class loadAlbumArt extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        SongsInfo songsInfo;

        public loadAlbumArt(ImageView imageView, SongsInfo songsInfo) {
            this.imageView = imageView;
            this.songsInfo = songsInfo;
        }

        @Override
        protected void onPreExecute() {
            this.imageView.setImageResource(R.drawable.baseline_music_note_24);
            super.onPreExecute();
        }

        @Override
        public Bitmap doInBackground(String[] strings) {

            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(songsInfo.getPath().toString());

            try {
                byte[] art = metaRetriever.getEmbeddedPicture();
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            } catch (Exception e) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                this.imageView.setImageBitmap(bitmap);
                hashMap.put(songsInfo.getPath() , bitmap);
            }
            else this.imageView.setImageResource(R.drawable.baseline_music_note_24);
        }
    }


}
