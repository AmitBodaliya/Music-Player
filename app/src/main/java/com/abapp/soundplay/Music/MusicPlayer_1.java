package com.abapp.soundplay.Music;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.abapp.soundplay.Model.SongsInfo;

public class MusicPlayer_1 {

    Context context;
    SongsInfo songsInfo;
    MediaPlayer mediaPlayer;
    OnTaskCompletionListener onTaskCompletionListener;


    public MusicPlayer_1(Context context) {
        this.context = context;
    }

     public void setMusic(SongsInfo songsInfo){
        this.songsInfo = songsInfo;

        stopMusic();

        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(songsInfo.getPath()));
        mediaPlayer.setVolume(1.0f , 1.0f);

        mediaPlayer.setOnCompletionListener(mp -> {
            if (onTaskCompletionListener != null) onTaskCompletionListener.onMusicComplete(mediaPlayer);
        });

        if (onTaskCompletionListener != null) onTaskCompletionListener.onMusicChange(mediaPlayer , songsInfo);

    }


     public void setMusicAndStart(SongsInfo songsInfo){
        this.songsInfo = songsInfo;

        stopMusic();

        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(songsInfo.getPath()));
        mediaPlayer.setVolume(1.0f , 1.0f);
        mediaPlayer.start();


        mediaPlayer.setOnCompletionListener(mp -> {
            if (onTaskCompletionListener != null) onTaskCompletionListener.onMusicComplete(mediaPlayer);
        });

        if (onTaskCompletionListener != null) onTaskCompletionListener.onMusicChange(mediaPlayer , songsInfo);
        if (onTaskCompletionListener != null) onTaskCompletionListener.onMusicSet(mediaPlayer , songsInfo);

    }

    public void startMusic(){
        if (mediaPlayer != null) mediaPlayer.start();
        if (onTaskCompletionListener != null) onTaskCompletionListener.onMusicChange(mediaPlayer , songsInfo);
    }

    public void pauseMusic(){
        if (mediaPlayer != null) mediaPlayer.pause();
        if (onTaskCompletionListener != null) onTaskCompletionListener.onMusicChange(mediaPlayer , songsInfo);
    }

    public void stopMusic(){
        if (mediaPlayer != null) mediaPlayer.stop();
        if (onTaskCompletionListener != null) onTaskCompletionListener.onMusicChange(mediaPlayer , songsInfo);
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

    public SongsInfo getSongsInfo(){return  songsInfo;}

    public boolean isNull(){
        return mediaPlayer == null;
    }

    public void destroyPlayer(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }




    // allows clicks events to be caught
    public void setOnTaskCompletionListener(OnTaskCompletionListener onTaskCompletionListener) {
        this.onTaskCompletionListener = onTaskCompletionListener;
    }


    // parent activity will implement this method to respond to click events
    public interface OnTaskCompletionListener {
        void onMusicComplete(MediaPlayer mediaPlayer) ;
        void onMusicChange(MediaPlayer mediaPlayer, SongsInfo songsInfo) ;
        void onMusicSet(MediaPlayer mediaPlayer, SongsInfo songsInfo) ;
    }






}
