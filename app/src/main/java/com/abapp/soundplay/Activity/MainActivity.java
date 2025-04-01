package com.abapp.soundplay.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abapp.soundplay.Gesture.OnSwipeGesture;
import com.abapp.soundplay.Helper.FetchFileData;
import com.abapp.soundplay.Helper.MediaMetaData;
import com.abapp.soundplay.Helper.UniqueIdGen;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.Music.MusicPlayer_1;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewHalper.PlayerFullView;
import com.abapp.soundplay.ViewHalper.ShowListView;
import com.abapp.soundplay.ViewHalper.SongInfoView;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;
import com.abapp.soundplay.databinding.ActivityMainBinding;
import com.abapp.soundplay.databinding.ContentDockMasterBinding;
import com.abapp.soundplay.params.Prefs;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    PlayerFullView playerFullView;

    //Media Data
    MediaMetaData mediaMetaData;

    FetchFileData fetchFileData;


    //array list
    SongsInfo currentSong;

    ArrayList<SongsInfo> upNextList = new ArrayList<>();


    Prefs prefs;
    UniqueIdGen uniqueIdGen;


    //view model
    LiveDataViewModel liveDataViewModel;




    private MusicPlayer_1.MusicBinder musicBinder;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("TAG", "onServiceConnected() called with: componentName = [" + componentName + "], iBinder = [" + iBinder + "]");
            musicBinder = (MusicPlayer_1.MusicBinder) iBinder;
            if (!musicBinder.isNull()) uiUpdate();
            MediaControllerCompat mediaController = musicBinder.getMediaController();
            MediaControllerCompat.setMediaController(MainActivity.this, mediaController);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("TAG", "onServiceDisconnected() called with: componentName = [" + componentName + "]");
            musicBinder = null;
            MediaControllerCompat.setMediaController(MainActivity.this, null);
        }
    };



    private ActivityMainBinding binding;
    private ContentDockMasterBinding dockBinding;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Access views in the included layout
        dockBinding = ContentDockMasterBinding.bind(binding.dockLayout.getRoot());


        setupCrashHandler();



        //class
        prefs = new Prefs(this);
        uniqueIdGen = UniqueIdGen.getInstance();


        // Create an instance of the ViewModel
        liveDataViewModel = new ViewModelProvider(this).get(LiveDataViewModel.class);
        liveDataViewModel.initData(this);


        //media
        mediaMetaData = new MediaMetaData(this);

        fetchFileData = new FetchFileData(this);


        // init view
        initView();

        //refresh list of song if applied
        if (prefs.getPrefs(Prefs.reloadOnOpen, true)) refreshList();


        new Handler().postDelayed(() -> {
            if (musicBinder == null) return;
            handleIncomingIntent(getIntent());
        }, 2000);
    }




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }


    public void handleIncomingIntent(Intent intent){
        Uri audioUri = intent.getData();

        if(audioUri != null){
            ArrayList<SongsInfo> newList = new ArrayList<>();
            SongsInfo songsInfo = fetchFileData.createSongInfoFromUri(audioUri);
            newList.add(songsInfo);
            onItemClick(null, songsInfo, 0, newList);
        }
    }





    @SuppressLint("ClickableViewAccessibility")
    private void initView(){

        //set default navigation view
        NavigationUI.setupWithNavController(binding.bottomNavigation, Navigation.findNavController(this, R.id.nav_host_fragment_activity_main));
        setDefaultNav();


        //update ui broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverUpdateUi, new IntentFilter(getPackageName() + ".PLAYBACK_STATE_CHANGED"));

    }




    public void setDefaultNav(){
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_main);
    }


    public void refreshList(){ liveDataViewModel.getAllNewSongList(); }












    //Dialog    ////////////////////////////////////////////////////////////////////////////////////

    //show full screen view
    public void openPlayingSong() {
        if (!musicBinder.isNull()) {
            playerFullView = new PlayerFullView(this, this, musicBinder, upNextList);
            playerFullView.showDialog();
        }
    }



    //show songInfo in dialog
    public void songInfoDialog(File uri) {
        new SongInfoView(this, uri , mediaMetaData);
    }

    public void showSongListDialog(String title, ArrayList<SongsInfo> list) {
        if (title.isEmpty()) title = "<unknown>";
        new ShowListView(this , this, title, list);
    }


    public void showAllFavSOng() {
        ArrayList<SongsInfo> arrayList = liveDataViewModel.getFavList();
        if (arrayList.isEmpty()) Toast.makeText(MainActivity.this, "No Favourite Song", Toast.LENGTH_SHORT).show();
        else new ShowListView(MainActivity.this, MainActivity.this, "Favourite Songs", arrayList);
    }


    public void showHistory() {
        ArrayList<SongsInfo> arrayList = liveDataViewModel.getRecentList();
        if (arrayList.isEmpty()) Toast.makeText(MainActivity.this, "No Recent Song", Toast.LENGTH_SHORT).show();
        else new ShowListView(MainActivity.this, MainActivity.this, "Recent", arrayList);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////








    //on click
    public void onItemClick(View v, SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
//        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale));

        if(prefs.getPrefs(Prefs.directSong, false)){
            showSeekTODialog(v, songsInfo, position, arrayList);
        }else {
            ArrayList<SongsInfo> finalLIst = new ArrayList<>();
            assert arrayList != null;
            for (SongsInfo songINfo : arrayList) {
                songINfo.setUniqueID(uniqueIdGen.generateUniqueId());
                finalLIst.add(songINfo);
            }

            currentSong = finalLIst.get(position);
            upNextList = finalLIst;

            musicBinder.playMedia(songsInfo, arrayList, 0);


            if (playerFullView != null) playerFullView.dismissDialog();

            playerFullView = new PlayerFullView(this, this, musicBinder, upNextList);
            if (playerFullView.isShowing()) playerFullView.showDialog();
        }

    }



    //on click
    public void onItemLongClick(View v, SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
//        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale));
        if(liveDataViewModel.isSongFilePresent(songsInfo.getPath().toString())){
            showSeekTODialog(v, songsInfo, position, arrayList);
        }
    }


    public void onItemLongClickUtil(View v, SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList, int SeekTo) {

        ArrayList<SongsInfo> finalLIst = new ArrayList<>();
        for (SongsInfo songINfo: arrayList) {
            songINfo.setUniqueID(uniqueIdGen.generateUniqueId());
            finalLIst.add(songINfo);
        }

        currentSong = finalLIst.get(position);
        upNextList = finalLIst;

        musicBinder.playMedia(songsInfo, arrayList, SeekTo);


        if (playerFullView != null) playerFullView.dismissDialog();

        playerFullView = new PlayerFullView(this, this, musicBinder, upNextList);
        if (playerFullView.isShowing()) playerFullView.showDialog();
    }



    //on click
    public void nextSong(SongsInfo songsInfo) {
        songsInfo.setUniqueID(uniqueIdGen.generateUniqueId());

        for (int i = 0; i < upNextList.size() ; i++) {
            if (upNextList.get(i).getUniqueID().equals(currentSong.getUniqueID())) {
                if(upNextList.size() == i + 1) upNextList.add(songsInfo);
                else upNextList.add(i + 1, songsInfo);
            }
        }
    }




















    ////////////////////////////////////////////////////////////////////////////////////////////////


    // song menu
    @SuppressLint("NonConstantResourceId")
    public void onMenuClick(View view, SongsInfo songsInfo , int position, ArrayList<SongsInfo> listOnClick) {
        File file = songsInfo.getPath();

        if (!file.exists()) return;

        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu );
        PopupMenu popup = new PopupMenu(wrapper, view ,  Gravity.END);
        popup.getMenuInflater().inflate(R.menu.popup_menu , popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {

            if(item.getItemId() == R.id.item1){
                onItemClick(view,songsInfo , position, listOnClick);
            }
            else if(item.getItemId() == R.id.item2){
                nextSong(songsInfo);
            }
            else if(item.getItemId() == R.id.item4){
                upNextList.add(songsInfo);
            }
            else if(item.getItemId() == R.id.item5){
                liveDataViewModel.insertFav(songsInfo);
                Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show();
            }
            else if(item.getItemId() == R.id.item6){
                songInfoDialog(file);
            }else if(item.getItemId() == R.id.item7){
                try {
                    String sharePath = file.toString();
                    Intent share = new Intent(Intent.ACTION_SEND)
                            .setType("audio/*")
                            .putExtra(Intent.EXTRA_STREAM, Uri.parse(sharePath));

                    startActivity(Intent.createChooser(share, "Share Sound File"));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }else {
                return super.onOptionsItemSelected(item);
            }
            return true;
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





    ////////////////////////////////////////////////////////////////////////////////////////////////


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void showSeekTODialog(View v, SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this , R.style.BottomSheetDialogStyle );
        bottomSheetDialog.setContentView(R.layout.dialog_seek_to);

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(songsInfo.getPath().toString());

        ImageView albumArt = bottomSheetDialog.findViewById(R.id.songInfoAlbumArt);

        TextView title = bottomSheetDialog.findViewById(R.id.titleSongInfo);
        TextView artist = bottomSheetDialog.findViewById(R.id.artistSongInfo);
        TextView album = bottomSheetDialog.findViewById(R.id.albumSongInfo);
        TextView songDuration = bottomSheetDialog.findViewById(R.id.timeDuringScreen);
        TextView timeCurrentScreen = bottomSheetDialog.findViewById(R.id.timeCurrentScreen);
        SeekBar seekBarScreenTO = bottomSheetDialog.findViewById(R.id.seekBarScreenTO);
        Button btnPlay = bottomSheetDialog.findViewById(R.id.btnPlaySong);


        //set bitmap on songInfo
        File uri = songsInfo.getPath();
        assert albumArt != null;
        if (uri != null) {
            Bitmap songBitmap = mediaMetaData.getSongBitmap(songsInfo.getPath());
            if (songBitmap != null) albumArt.setImageBitmap(songBitmap);
            else albumArt.setImageResource(R.drawable.baseline_music_note_24);
        } else {
            albumArt.setImageResource(R.drawable.baseline_music_note_24);
        }



        //title.setText(uri.getName());
        assert title != null;
        title.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        assert artist != null;
        artist.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        assert album != null;
        album.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));


        //set Duration song
        long dur = Long.parseLong(Objects.requireNonNull(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        String Sec = String.valueOf((dur % 60000) / 1000);
        String min = String.valueOf(dur / 60000);

        assert songDuration != null;
        songDuration.setText((Sec.length() == 1)? min + ":0" + Sec : min + ":" + Sec);


        //set seek Bar max
        assert seekBarScreenTO != null;
        seekBarScreenTO.setMax((int) dur);

        seekBarScreenTO.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String Sec = String.valueOf((progress % 60000) / 1000);
                String min = String.valueOf(progress / 60000);

                assert timeCurrentScreen != null;
                timeCurrentScreen.setText((Sec.length() == 1)? min + ":0" + Sec : min + ":" + Sec);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        assert btnPlay != null;
        btnPlay.setOnClickListener(view -> {
            onItemLongClickUtil(v, songsInfo, position, arrayList, seekBarScreenTO.getProgress());
            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.show();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////










    //ui update from service
    BroadcastReceiver broadcastReceiverUpdateUi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            uiUpdate();
        }
    };






    @SuppressLint("ClickableViewAccessibility")
    public void uiUpdate(){
        if (musicBinder == null) return;

        SongsInfo songsInfo = musicBinder.getCurrentSongInfo();
        upNextList = musicBinder.getAllSong();

        if (songsInfo == null || upNextList == null) return;


        if (playerFullView == null) {
            playerFullView = new PlayerFullView(this, this, musicBinder, upNextList);
        }
        else {
            playerFullView.updateData(musicBinder);
            playerFullView.updateUpNextList(upNextList);
            playerFullView.refreshView(false);
        }


        dockBinding.contentDockMaster.setVisibility(View.VISIBLE);
        dockBinding.contentDockMaster.setOnTouchListener(new OnSwipeGesture(this) {
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
        if (bitmap != null) dockBinding.albumImagePlayer.setImageBitmap(bitmap);
        else dockBinding.albumImagePlayer.setImageResource(R.drawable.baseline_music_note_24);

        //on play pause click
        dockBinding.btnNextPlayer.setOnClickListener(v -> musicBinder.nextSong());


        //on play pause click
        dockBinding.playPausePlayer.setOnClickListener(v -> {
            if (musicBinder.isMusicPlaying()) {
                musicBinder.pauseMusic();
                dockBinding.playPausePlayer.setImageResource(R.drawable.play_drawable);
            } else {
                musicBinder.playMusic();
                dockBinding.playPausePlayer.setImageResource(R.drawable.baseline_pause_24);
            }
        });


        //set title
        dockBinding.titleTextPlayer.setText(songsInfo.getTitle1());

        //set play pause
        dockBinding.playPausePlayer.setImageResource((musicBinder.isMusicPlaying())? R.drawable.baseline_pause_24 : R.drawable.play_drawable);


//        set seek Bar
        dockBinding.seekBarPlayer.setMax(musicBinder.getDuration());
        dockBinding.seekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicBinder.setSeekTo(progress);
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
                int mCurrentPosition = musicBinder.getCurrentPosition();
                dockBinding.seekBarPlayer.setProgress(mCurrentPosition);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mRunnable,0);

    }











    @Override
    protected void onStart() {
        super.onStart();
        Intent musicIntent = new Intent(this, MusicPlayer_1.class);
        startService(musicIntent);
        bindService(musicIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service if it's bound
//        unbindService(serviceConnection);
//        unregisterReceiver(broadcastReceiverUpdateUi);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public void restartApp() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }




    private void setupCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            Log.d("TAG", "setupCrashHandler() called " + exception.getMessage());
            exception.printStackTrace();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        });
    }



}