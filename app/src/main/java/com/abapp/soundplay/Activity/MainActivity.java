package com.abapp.soundplay.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abapp.soundplay.Gesture.OnSwipeGesture;
import com.abapp.soundplay.Helper.FavSong;
import com.abapp.soundplay.Helper.FetchFileData;
import com.abapp.soundplay.Helper.HistorySong;
import com.abapp.soundplay.Helper.MediaMetaData;
import com.abapp.soundplay.Helper.UniqueIdGen;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.Music.MusicPlayer_1;
import com.abapp.soundplay.Notification.CreateNotification;
import com.abapp.soundplay.Receiver.Action;
import com.abapp.soundplay.Receiver.OnClearFromRecentService;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewHalper.PlayerFullView;
import com.abapp.soundplay.ViewHalper.ShowListView;
import com.abapp.soundplay.ViewHalper.SongInfoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicPlayer_1.OnTaskCompletionListener {

    //music payer view
    LinearLayout content_dock_master;
    ImageView albumImagePlayer;
    SeekBar seekBarPlayer;
    TextView titleTextPlayer;
    ImageView playPausePlayer;


    //full screen view
    BottomNavigationView bottomNavigationView;
    PlayerFullView playerFullView;


    //music payer
    MusicPlayer_1 musicPlayer_1;

    //Media Data
    MediaMetaData mediaMetaData;
    MediaSessionCompat mediaSession;

    FetchFileData fetchFileData;


    //array list
    SongsInfo currentSong;
    ArrayList<SongsInfo> upNextList = null;
    ArrayList<SongsInfo> arrayListBackground = null;


    UniqueIdGen uniqueIdGen;
    FavSong favSong;
    HistorySong historySong;

    //notification
    NotificationManager notificationManager;


    //focus change
    boolean isFocusChangeByUser = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("TAG", "onCreate()");

        //music payer view
        content_dock_master = findViewById(R.id.content_dock_master);
        albumImagePlayer = findViewById(R.id.albumImagePlayer);
        seekBarPlayer = findViewById(R.id.seekBarPlayer);
        titleTextPlayer = findViewById(R.id.titleTextPlayer);
        playPausePlayer = findViewById(R.id.playPausePlayer);

        content_dock_master.setVisibility(View.GONE);


        //set music player
        musicPlayer_1 = new MusicPlayer_1(this);
        musicPlayer_1.setOnTaskCompletionListener(this);


        uniqueIdGen = UniqueIdGen.getInstance();
        favSong = new FavSong(this);
        historySong = new HistorySong(this);
        mediaMetaData = new MediaMetaData(this);
        fetchFileData = new FetchFileData(this);


        setMediaSession();
        registerReceiver();


        //set list and set view pager
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();


        if(extras == null){
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
        } else {

            arrayListBackground = intent.getParcelableArrayListExtra("arrayList");
            arrayListBackground.sort(SongsInfo.sortByTitle);

            //set bottom nav view
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            setDefaultNav();

        }

    }

    public ArrayList<SongsInfo> getArrayList(){
        return arrayListBackground;
    }


    public void setDefaultNav(){
        bottomNavigationView.setSelectedItemId(R.id.navigation_main);
    }


    public void setMediaSession(){

        //media session
//        ComponentName receiver = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
//        mediaSession = new MediaSessionCompat(this, "PlayerService", receiver, null);
        mediaSession = new MediaSessionCompat(this, "PlayerService");

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(mediaSessionCallBack);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(focusChange -> {
            Log.d("TAG", "onAudioFocusChange : " + focusChange);
            if (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT == focusChange){
                if (musicPlayer_1.isMusicPlaying()){
                    isFocusChangeByUser = true;
                    musicPlayer_1.pauseMusic();
                }
            }else if (AudioManager.AUDIOFOCUS_GAIN == focusChange){
                if (isFocusChangeByUser){
                    isFocusChangeByUser = false;
                    musicPlayer_1.startMusic();
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


        mediaSession.setActive(true);
    }


    public void registerReceiver(){
        //notificationReceiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

    }


    //menu/////////////////////////////////////////////////////////////////////////////////////////////
    public void MainMenu(View view){
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu );
        PopupMenu popup = new PopupMenu(wrapper, view ,  Gravity.END);
        popup.getMenuInflater().inflate(R.menu.popup_menu_main , popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {

            //refresh list
            if (item.getItemId() == R.id.refreshList) {
                startActivity(new Intent(this, SplashActivity.class));
                finish();
                return true;
            }

            //play
            if (item.getItemId() == R.id.itemBothPlay) {
                return true;
            }


            if (item.getItemId() == R.id.idFavourite) {
                showAllFavSOng();
                return true;
            }

            if (item.getItemId() == R.id.item_setting) {
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            }

            if (item.getItemId() == R.id.item_main_about) {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }

            return super.onOptionsItemSelected(item);

        });
        popup.show();
    }


    // song menu
    @SuppressLint("NonConstantResourceId")
    public void onMenuClick(View view, SongsInfo songsInfo , int position, ArrayList<SongsInfo> listOnClick) {
        File file = songsInfo.getPath();

        SongsInfo temp = new SongsInfo();
        temp.setUniqueID(uniqueIdGen.generateUniqueId());
        temp.setTitle(songsInfo.getTitle());
        temp.setArtist(songsInfo.getArtist());
        temp.setAlbum(songsInfo.getAlbum());
        temp.setPath(songsInfo.getPath());
        temp.setSongLength(songsInfo.getSongLength());
        temp.setBitmapImage(songsInfo.getBitmapImage());

        if (!file.exists()) return;

        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu );
        PopupMenu popup = new PopupMenu(wrapper, view ,  Gravity.END);
        popup.getMenuInflater().inflate(R.menu.popup_menu , popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                //play
                case R.id.item1: {
                    onItemClick(view,songsInfo , position, listOnClick);
                    return true;
                }

                //play next
                case R.id.item2: {
                    for (int i = 0; i < upNextList.size(); i++) {
                        if (upNextList.get(i).getUniqueID().equals(currentSong.getUniqueID())){
                            upNextList.add(i + 1 , temp);
                            playerFullView.updateUpNextList(upNextList);
                            break;
                        }
                    }
                    return true;
                }

                //add to queue song
                case R.id.item4:
                    upNextList.add(temp);
                    return true;

                //add to favourite song
                case R.id.item5:
                    favSong.addToFavourite(songsInfo);
                    Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show();
                    return true;

                //set song info
                case R.id.item6:
                    songInfoDialog(file);
                    return true;

                //share song
                case R.id.item7:
                    try {
                        String sharePath = file.toString();
                        Intent share = new Intent(Intent.ACTION_SEND)
                                .setType("audio/*")
                                .putExtra(Intent.EXTRA_STREAM, Uri.parse(sharePath));

                        startActivity(Intent.createChooser(share, "Share Sound File"));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }

                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        });

        popup.show();
    }


    @SuppressLint("NonConstantResourceId")
    public void albumMenu(View view, ArrayList<SongsInfo> list) {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu );
        PopupMenu popup = new PopupMenu(wrapper, view ,  Gravity.START);
        popup.getMenuInflater().inflate(R.menu.album_menu , popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {

            //play
            if (item.getItemId() == R.id.item_play) {
                onItemClick(view, list.get(0), 0, list);
                return true;
            }
            return super.onOptionsItemSelected(item);

        });

        popup.show();
    }








    //Dialog    ////////////////////////////////////////////////////////////////////////////////////

    //show full screen view
    public void openPlayingSong() {
        if (!musicPlayer_1.isNull()) {
            playerFullView = new PlayerFullView(this, this, musicPlayer_1, upNextList);
            playerFullView.showDialog();
        }
    }



    //show songInfo in dialog
    public void songInfoDialog(File uri) {
        new SongInfoView(this, uri , mediaMetaData);
    }

    public void showSongListDialog(String title, ArrayList<SongsInfo> list) {
        if (title.equals("")) title = "<unknown>";
        new ShowListView(this , this, "" + title , list);
    }

    public void showAllFavSOng() {
        if (favSong.isListEmpty())
            Toast.makeText(this, "No Favourite Song", Toast.LENGTH_SHORT).show();
        else new ShowListView(this , this, "Favourite Songs" , favSong.getAllFavouriteSOng());
    }


    public void showHistory() {
        if (favSong.isListEmpty())
            Toast.makeText(this, "No History", Toast.LENGTH_SHORT).show();
        else new ShowListView(this , this, "History" , historySong.getAllHistory());
    }










    //on click
    public void onItemClick(View view , SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
        ArrayList<SongsInfo> finalLIst = new ArrayList<>();
        for (SongsInfo songINfo: arrayList) {
            songINfo.setUniqueID(uniqueIdGen.generateUniqueId());
            finalLIst.add(songINfo);
        }

        currentSong = finalLIst.get(position);
        upNextList = finalLIst;

        musicPlayer_1.setMusicAndStart(songsInfo);

        if (playerFullView != null) playerFullView.dismissDialog();

        playerFullView = new PlayerFullView(this, this, musicPlayer_1, upNextList);
//        playerFullView.showDialog();



        //set media session queue
//        int id = 1;
//        List<MediaSessionCompat.QueueItem> queueItemS = new ArrayList<>();
//
//        for (SongsInfo songsInfo1: arrayList){
//            MediaDescriptionCompat mediaDescription = new MediaDescriptionCompat.Builder()
//                    .setTitle("" + songsInfo1.getTitle())
//                    .setSubtitle("" + songsInfo1.getArtist())
//                    .setIconBitmap(songsInfo1.getBitmapImage())
//                    .setMediaUri(Uri.fromFile(songsInfo1.getPath()))
//                    .build();
//
//            queueItemS.add(new MediaSessionCompat.QueueItem(mediaDescription , id++));
//        }
//        mediaSession.setQueue(queueItemS);


    }



    //music player functions

    @Override
    public void onMusicComplete(MediaPlayer mediaPlayer) {
        nextSong();
    }

    @Override
    public void onMusicSet(MediaPlayer mediaPlayer, SongsInfo songsInfo) {
        historySong.addToHistory(songsInfo);
    }


    @Override
    public void onMusicChange(MediaPlayer mediaPlayer, SongsInfo songsInfo) {
        changeView(songsInfo);

        int position = 0;
        for (int i = 0; i < upNextList.size(); i++) {
            if (songsInfo.getUniqueID().equals(upNextList.get(i).getUniqueID())){
                position = i;
                break;
            }
        }

        if (musicPlayer_1.isMusicPlaying()){
            CreateNotification.createNotification(MainActivity.this, songsInfo,
                    R.drawable.baseline_pause_black_24, position, upNextList.size() - 1);
        }else {
            CreateNotification.createNotification(MainActivity.this, songsInfo,
                    R.drawable.baseline_play_arrow_black_24, position, upNextList.size() - 1);
        }


        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "" + songsInfo.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "" + songsInfo.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "" + songsInfo.getAlbum())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, songsInfo.getBitmapImage())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicPlayer_1.getDuration());

        mediaSession.setMetadata(metadataBuilder.build());



        if (musicPlayer_1.isMusicPlaying()){
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE).build());
        }else {
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE).build());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void changeView(SongsInfo songsInfo){
        content_dock_master.setVisibility(View.VISIBLE);
        content_dock_master.setOnTouchListener(new OnSwipeGesture(this) {
            @Override
            public void onClick() {
                openPlayingSong();
            }

            @Override
            public void onSwipeTop() {
                openPlayingSong();
            }
        });


        //set album art
        Bitmap bitmap = mediaMetaData.getSongBitmap(songsInfo.getPath());
        if (bitmap != null) {
            albumImagePlayer.setImageBitmap(bitmap);
        } else {
            albumImagePlayer.setImageResource(R.drawable.baseline_music_note_24);
        }

        //on play pause click
        playPausePlayer.setOnClickListener(v -> {
            if (musicPlayer_1.isMusicPlaying()) {
                musicPlayer_1.pauseMusic();
                playPausePlayer.setImageResource(R.drawable.play_drawable);
            } else {
                musicPlayer_1.startMusic();
                playPausePlayer.setImageResource(R.drawable.baseline_pause_24);
            }
        });


        //set title
        titleTextPlayer.setText(songsInfo.getTitle().substring(0, songsInfo.getTitle().lastIndexOf(".")));

        //set play pause
        playPausePlayer.setImageResource((musicPlayer_1.isMusicPlaying())? R.drawable.baseline_pause_24 : R.drawable.play_drawable);


//        set seek Bar
        seekBarPlayer.setMax(musicPlayer_1.getDuration());
        seekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayer_1.setSeekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        //set seek bar
        final Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                int mCurrentPosition = musicPlayer_1.getCurrentPosition();
                seekBarPlayer.setProgress(mCurrentPosition);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mRunnable,0);

    }




    public void nextSong(){
        for (int i = 0; i < upNextList.size(); i++) {
            if (upNextList.get(i).getUniqueID().equals(currentSong.getUniqueID())){
                if (i < upNextList.size() - 1) {
                    currentSong = upNextList.get(i + 1);

                    musicPlayer_1.stopMusic();
                    musicPlayer_1.setMusicAndStart(currentSong);

                    playerFullView.updateData(musicPlayer_1, currentSong);
                    playerFullView.updateUpNextList(upNextList);
                    playerFullView.refreshView(false);
                }else changeView(currentSong);
                break;
            }
        }
    }

    public void previousSong(){
        for (int i = 0; i < upNextList.size(); i++) {
            if (upNextList.get(i).getUniqueID().equals(currentSong.getUniqueID())){
                if (i > 0) {
                    currentSong = upNextList.get(i - 1);

                    musicPlayer_1.stopMusic();
                    musicPlayer_1.setMusicAndStart(currentSong);

                    playerFullView.updateData(musicPlayer_1, currentSong);
                    playerFullView.updateUpNextList(upNextList);
                    playerFullView.refreshView(false);
                }
                break;
            }
        }
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



    BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

                    Log.i("TAG", "onReceive: STATE_DISCONNECTED" + action);

            if (action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);

                if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    Log.i("TAG", "onReceive: STATE_DISCONNECTED");
                    // Bluetooth headset is disconnected, pause playback
                    // Pause your music playback here
                }
            }
        }
    };

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
                    if (musicPlayer_1.isMusicPlaying()){
                        musicPlayer_1.pauseMusic();
                    }else musicPlayer_1.startMusic();
                    break;
                case Action.ACTION_NEXT:
                    nextSong();
                    break;
                case Action.ACTION_PLAY:
                    musicPlayer_1.startMusic();
                    break;
                case Action.ACTION_PAUSE:
                    musicPlayer_1.pauseMusic();
                    break;
            }
        }
    };


    MediaSessionCompat.Callback mediaSessionCallBack = new MediaSessionCompat.Callback(){
        @Override
        public void onPlay() {
            Log.d("TAG", "onPlay(a) called");
            musicPlayer_1.startMusic();
        }

        @Override
        public void onPause() {
            Log.d("TAG", "onPause(a) called");
            musicPlayer_1.pauseMusic();
        }

        @Override
        public void onSkipToNext() {
            Log.d("TAG", "onSkipToNext(a) called");
            nextSong();
        }

        @Override
        public void onSkipToPrevious() {
            Log.d("TAG", "onSkipToPrevious(a) called");
            previousSong();
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
                            if (musicPlayer_1.isMusicPlaying()){
                                musicPlayer_1.pauseMusic();
                            }else musicPlayer_1.startMusic();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            musicPlayer_1.pauseMusic();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            musicPlayer_1.startMusic();
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
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }

        musicPlayer_1.destroyPlayer();


        unregisterReceiver(broadcastReceiver);
//        unregisterReceiver(mHeadsetReceiver);
        mediaSession.release();

    }

    @Override
    public void onBackPressed() {
        setDefaultNav();
    }
}