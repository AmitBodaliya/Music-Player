package com.abapp.soundplay.Music;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Helper.MediaMetaData;
import com.abapp.soundplay.Helper.MusicArt;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.Receiver.Action;
import com.abapp.soundplay.Receiver.NotificationActionService;
import com.abapp.soundplay.Receiver.OnClearFromRecentService;
import com.abapp.soundplay.Room.History.HistoryRepository;
import com.abapp.soundplay.Room.History.MyDatabaseHistory;
import com.abapp.soundplay.Room.History.TableHistory;

import java.util.ArrayList;

public class MusicPlayer_1 extends Service implements MediaPlayer.OnPreparedListener ,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    // notification area).
    final int NOTIFICATION_ID = 1;


    private MediaPlayer mediaPlayer;

    private MediaSessionCompat mediaSession;
    private MediaControllerCompat mediaController;

    private MediaMetaData mediaMetaData;
    private HistoryRepository historyRepository;



    //notification
    NotificationManager notificationManager;

    private SongsInfo currentSongInfo;
    private ArrayList<SongsInfo> queue;

    AudioManager audioManager;

    private final IBinder binder = new MusicBinder();


    private boolean isFocusChangeByUser;







    private final BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.i("TAG", "onReceive: On Screen Off");
            }else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
                Log.i("TAG", "onReceive: On Screen On");
                updateUI();
            }
        }
    };



    void createMediaPlayerIfNeeded(){
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);

        }
    }


    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();

        createMediaPlayerIfNeeded();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(focusChange -> {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // Resume playback
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    if (isFocusChangeByUser){
                        isFocusChangeByUser = false;
                        playMusic();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    // Stop playback
                    pauseMusic();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Pause playback
                    if (mediaPlayer.isPlaying()) {
                        isFocusChangeByUser = true;
                        pauseMusic();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lower the volume
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    break;
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);



        mediaMetaData = new MediaMetaData(this);
        historyRepository = new HistoryRepository(MyDatabaseHistory.getDatabase(this));

//        initialize media session
        mediaSession = new MediaSessionCompat(this , "PlayerService");
        mediaController = new MediaControllerCompat(this, mediaSession.getSessionToken());


        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                playMusic();
                super.onPlay();
            }

            @Override
            public void onPause() {
                pauseMusic();
                super.onPause();
            }

            @Override
            public void onSkipToNext() {
                nextSong();
                super.onSkipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                previousSong();
                super.onSkipToPrevious();
            }

            @Override
            public void onStop() {
                stopMusic();
                super.onStop();
            }

            @Override
            public void onSeekTo(long pos) {
                mediaPlayer.seekTo((int) pos);
                updateUI();
                super.onSeekTo(pos);
            }



            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                String intentAction = mediaButtonEvent.getAction();

                if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)){
                    KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                    int action = event.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        switch (event.getKeyCode()) {
                            // Do what you want in here
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                if (mediaPlayer.isPlaying()) {
                                    pauseMusic();
                                } else {
                                    playMusic();
                                }
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                                pauseMusic();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                                playMusic();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                nextSong();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                previousSong();
                                break;
                        }
                        return true;
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });


        mediaSession.setActive(true);

//        set receiver for phone screen state
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenStateReceiver, intentFilter);


        //notificationReceiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextSong();
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }


    private void startNewMusic(SongsInfo songsInfo){
        createMediaPlayerIfNeeded();


        if (currentSongInfo.getBitmapImage() == null)
            currentSongInfo.setBitmapImage(mediaMetaData.getSongBitmap(currentSongInfo.getPath()));

        if (mediaPlayer != null) mediaPlayer.stop();


        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(songsInfo.getPath()));
        mediaPlayer.start();


        mediaPlayer.setOnCompletionListener(this);


        TableHistory tableHistory = new TableHistory();
        tableHistory.Song_Path = songsInfo.getPath().toString();
        tableHistory.SONGS_TITLE = songsInfo.getTitle1();
        tableHistory.SONGS_ARTISTS = songsInfo.getArtist();
        tableHistory.SONGS_ALBUM = songsInfo.getAlbum();
        tableHistory.SONGS_LENGTH = songsInfo.getSongLength();

        historyRepository.insert(tableHistory);

        updateUI();
    }

    private void playMusic(){
        // Start playback
        mediaPlayer.start();

        updateUI();
    }

    private void pauseMusic(){
        // Pause playback
        mediaPlayer.pause();

        updateUI();
    }

    private void stopMusic(){
        // Stop playback
        mediaPlayer.stop();
        mediaPlayer.reset();

        updateUI();
    }

    private void nextSong(){
        Log.i("TAG", "nextSong: ");
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getUniqueID().equals(currentSongInfo.getUniqueID())){
                if (i < queue.size() - 1) {
                    currentSongInfo = queue.get(i + 1);
                    startNewMusic(currentSongInfo);
                    break;
                }
                break;
            }
        }

        updateUI();
    }

    private void previousSong(){
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getUniqueID().equals(currentSongInfo.getUniqueID())){
                if (i > 0) {
                    currentSongInfo = queue.get(i - 1);
                    startNewMusic(currentSongInfo);
                    break;
                }
                break;
            }
        }

        updateUI();
    }



    private void updateUI() {
        // Update the UI with the current playback state
        Intent intent = new Intent(this.getPackageName() + ".PLAYBACK_STATE_CHANGED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);



        if (mediaPlayer.isPlaying()){
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE).build());
        }else {
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE).build());
        }


        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "" + currentSongInfo.getTitle1())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "" + currentSongInfo.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "" + currentSongInfo.getAlbum())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, currentSongInfo.getBitmapImage())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration());

        mediaSession.setMetadata(metadataBuilder.build());


        //create notification
//        //set seek bar
//        final Handler mHandler = new Handler();
//        Runnable mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                Log.i("TAG", "run: notification " + mediaPlayer.getCurrentPosition());
//
//
//                mHandler.postDelayed(this, 1000);
//            }
//        };
//        mHandler.postDelayed(mRunnable,0);

        startForeground(NOTIFICATION_ID, getNotification(mediaPlayer.isPlaying(), currentSongInfo));
    }



    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(Action.CHANNEL_ID,
                    "" + getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }



    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
            String action = intent.getExtras().getString("action_name");

            switch (action){
                case Action.ACTION_PREVIOUS:
                    previousSong();
                    break;
                case Action.ACTION_PLAY_PAUSE:
                    if (mediaPlayer.isPlaying()) pauseMusic();
                    else playMusic();
                    break;
                case Action.ACTION_NEXT:
                    nextSong();
                    break;
                case Action.ACTION_PLAY:
                    playMusic();
                    break;
                case Action.ACTION_PAUSE:
                    pauseMusic();
                    break;
            }
        }
    };


    public class MusicBinder extends Binder {

        public MediaControllerCompat getMediaController() {
            return mediaController;
        }

        public void playMedia(SongsInfo inputSongInfo, ArrayList<SongsInfo> arrayList) {
             currentSongInfo = inputSongInfo;
             queue = arrayList;
             startNewMusic(currentSongInfo);
        }


        public void playMusic(){
            MusicPlayer_1.this.playMusic();
        }

        public void pauseMusic(){
            MusicPlayer_1.this.pauseMusic();
        }

        public void nextSong(){
            MusicPlayer_1.this.nextSong();
        }

        public void previousSong(){
            MusicPlayer_1.this.previousSong();
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

        public SongsInfo getCurrentSongInfo(){return currentSongInfo;}

        public ArrayList<SongsInfo> getAllSong(){return queue;}

        public boolean isNull(){
            return mediaPlayer == null;
        }

    }




    @Override
    public void onDestroy() {
        Log.i("TAG", "onDestroy: MusicPlayer Service");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }

        unregisterReceiver(screenStateReceiver);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }







//    Notification


    /**
     * Return a notification based on the current music playback state.
     * @return Notification
     */
    public Notification getNotification(boolean musicIsPlaying ,SongsInfo songsInfo) {
        Bitmap icon = MusicArt.getInstance().getAlbumArt(songsInfo , null);

        // Create the channel if necessary
        initChannels(this);


        PendingIntent pendingIntentPrevious;
        int drw_previous;


        Intent intentPrevious = new Intent(getApplicationContext(), NotificationActionService.class).setAction(Action.ACTION_PREVIOUS);
        pendingIntentPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, intentPrevious, PendingIntent.FLAG_IMMUTABLE);
        drw_previous = R.drawable.baseline_skip_previous_24;


        Intent intentPlay = new Intent(getApplicationContext(), NotificationActionService.class).setAction(Action.ACTION_PLAY_PAUSE);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, intentPlay, PendingIntent.FLAG_IMMUTABLE);

        PendingIntent pendingIntentNext;
        int drw_next;

        Intent intentNext = new Intent(getApplicationContext(), NotificationActionService.class).setAction(Action.ACTION_NEXT);
        pendingIntentNext = PendingIntent.getBroadcast(getApplicationContext(), 0, intentNext, PendingIntent.FLAG_IMMUTABLE);
        drw_next = R.drawable.baseline_skip_next_24;

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MusicService")
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContentTitle(songsInfo.getTitle1())
                .setContentText(songsInfo.getArtist())
                .setLargeIcon((icon != null)? icon : getBitmapFromDrawable(getApplicationContext().getResources()))
                .setOngoing(true)

                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(getApplicationContext(), MainActivity.class),
                        PendingIntent.FLAG_IMMUTABLE))

                .addAction(drw_previous, "Previous", pendingIntentPrevious)
                .addAction((musicIsPlaying)? R.drawable.baseline_pause_black_24 : R.drawable.baseline_play_arrow_black_24, "Play", pendingIntentPlay)
                .addAction(drw_next, "Next", pendingIntentNext)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowCancelButton(true)
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        return builder.build();
    }

    private void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("MusicService",
                "Music Player", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Used when playing music");
        channel.setSound(null, null);
        notificationManager.createNotificationChannel(channel);
    }


    public static Bitmap getBitmapFromDrawable(Resources resources) {
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable = resources.getDrawable(R.drawable.baseline_music_note_24);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

}
