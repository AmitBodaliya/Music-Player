package com.abapp.soundplay.Music;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

public class MusicPlayer_2 {

    Context context;
    MediaPlayer mediaPlayer;
    MediaPlayer.OnCompletionListener onCompletionListener;

    public MusicPlayer_2(Context context) {
        this.context = context;
    }


    public void setMusic(File file){
        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file));
        mediaPlayer.setVolume(100 , 100);
    }

    public void startMusic(){
        if (mediaPlayer != null) mediaPlayer.start();
    }

    public void pauseMusic(){
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void stopMusic(){
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    public void setVolume(int v){
        mediaPlayer.setVolume(v , v);
    }

    public void muteMusic(){
        setVolume(0);
    }

    public void unMuteMusic(){
        setVolume(100);
    }


    public boolean isMusicPlaying(){
        if (mediaPlayer != null) return mediaPlayer.isPlaying();
        else return false;
    }


    public int getDuration(){
        if (mediaPlayer != null) return mediaPlayer.getDuration();
        else return 0;
    }

    public void setSeekTo(int progress){
        if (mediaPlayer != null) mediaPlayer.seekTo(progress);
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition();
        else return 0;
    }


    public boolean isNull(){
        return mediaPlayer == null;
    }

    public void destroyPlayer(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }


    // allows clicks events to be caught
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onMusicComplete() ;
    }







}
