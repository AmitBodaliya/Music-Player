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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.abapp.soundplay.Room.Fav.FavRepository;
import com.abapp.soundplay.Room.Fav.MyDatabaseFav;
import com.abapp.soundplay.Room.History.HistoryRepository;
import com.abapp.soundplay.Room.History.MyDatabaseHistory;
import com.abapp.soundplay.Room.Songs.MyDatabaseSongs;
import com.abapp.soundplay.Room.Songs.SongsRepository;
import com.abapp.soundplay.Room.Songs.TableSongs;
import com.abapp.soundplay.ViewHalper.PlayerFullView;
import com.abapp.soundplay.ViewHalper.ShowListView;
import com.abapp.soundplay.ViewHalper.SongInfoView;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //music payer view
    LinearLayout content_dock_master;
    ImageView albumImagePlayer;
    SeekBar seekBarPlayer;
    TextView titleTextPlayer;
    ImageView playPausePlayer;


    //full screen view
    BottomNavigationView bottomNavigationView;
    PlayerFullView playerFullView;


    //Media Data
    MediaMetaData mediaMetaData;

    FetchFileData fetchFileData;


    //array list
    SongsInfo currentSong;
    ArrayList<SongsInfo> upNextList = new ArrayList<>();


    UniqueIdGen uniqueIdGen;

    MyDatabaseSongs myDatabaseSongs;
    SongsRepository songsRepository;

    MyDatabaseFav myDatabaseFav;
    FavRepository favRepository;

    MyDatabaseHistory myDatabaseHistory;
    HistoryRepository historyRepository;

    LiveDataViewModel liveDataViewModel;



    private MusicPlayer_1.MusicBinder musicBinder;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicBinder = (MusicPlayer_1.MusicBinder) iBinder;

            if (!musicBinder.isNull()) uiUpdate();

            MediaControllerCompat mediaController = musicBinder.getMediaController();
            MediaControllerCompat.setMediaController(MainActivity.this, mediaController);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBinder = null;
            MediaControllerCompat.setMediaController(MainActivity.this, null);
        }
    };



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

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        content_dock_master.setVisibility(View.GONE);


        //set music player
        Intent musicIntent = new Intent(this, MusicPlayer_1.class);
        startService(musicIntent);
        bindService(musicIntent , serviceConnection, Context.BIND_AUTO_CREATE);

        // Create an instance of the ViewModel
        liveDataViewModel = new ViewModelProvider(this).get(LiveDataViewModel.class);

        uniqueIdGen = UniqueIdGen.getInstance();

        //database
        myDatabaseSongs = MyDatabaseSongs.getDatabase(this);
        songsRepository = new SongsRepository(myDatabaseSongs);

        myDatabaseFav = MyDatabaseFav.getDatabase(this);
        favRepository = new FavRepository(myDatabaseFav);

        myDatabaseHistory = MyDatabaseHistory.getDatabase(this);
        historyRepository = new HistoryRepository(myDatabaseHistory);

        mediaMetaData = new MediaMetaData(this);
        fetchFileData = new FetchFileData(this);

        //set default navigation view
        setNavigationView();


        //get list live update
        myDatabaseSongs.myDao().getAllEntities().observe(this, tableSongs -> {
            ArrayList<SongsInfo> arrayList1 = new ArrayList<>();
            for (TableSongs tableSongs1 : tableSongs) {
                SongsInfo songsInfo = new SongsInfo(
                        tableSongs1.SONGS_TITLE,
                        tableSongs1.SONGS_ARTISTS,
                        tableSongs1.SONGS_ALBUM,
                        tableSongs1.SONGS_LENGTH,
                        new File(tableSongs1.Song_Path));
                arrayList1.add(songsInfo);
            }

            liveDataViewModel.setData(arrayList1);
        });


        //update ui broadcast
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiverUpdateUi, new IntentFilter(getPackageName() + ".PLAYBACK_STATE_CHANGED"));

    }

    public void setNavigationView(){
        //set bottom nav view
        NavigationUI.setupWithNavController(bottomNavigationView, Navigation.findNavController(this, R.id.nav_host_fragment_activity_main));

        setDefaultNav();
    }



    public void setDefaultNav(){
        bottomNavigationView.setSelectedItemId(R.id.navigation_main);
    }


    // song menu
    @SuppressLint("NonConstantResourceId")
    public void onMenuClick(View view, SongsInfo songsInfo , int position, ArrayList<SongsInfo> listOnClick) {
        File file = songsInfo.getPath();

        SongsInfo temp = new SongsInfo();
        temp.setUniqueID(uniqueIdGen.generateUniqueId());
        temp.setTitle(songsInfo.getTitle1());
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
                    favRepository.insert(songsInfo);

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
        if (title.equals("")) title = "<unknown>";
        new ShowListView(this , this, "" + title , list);
    }


    public void showAllFavSOng(ArrayList<SongsInfo> arrayList) {
        if (arrayList.isEmpty())
            Toast.makeText(MainActivity.this, "No Favourite Song", Toast.LENGTH_SHORT).show();
        else {
            new ShowListView(MainActivity.this, MainActivity.this, "Favourite Songs", arrayList);
        }
    }


    public void showHistory(ArrayList<SongsInfo> arrayList) {
        if (arrayList.isEmpty())
            Toast.makeText(MainActivity.this, "No Recent Song", Toast.LENGTH_SHORT).show();
        else {
            new ShowListView(MainActivity.this, MainActivity.this, "Recent", arrayList);
        }
    }



    //on click
    public void onItemClick(View v, SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale));

        ArrayList<SongsInfo> finalLIst = new ArrayList<>();
        for (SongsInfo songINfo: arrayList) {
            songINfo.setUniqueID(uniqueIdGen.generateUniqueId());
            finalLIst.add(songINfo);
        }

        currentSong = finalLIst.get(position);
        upNextList = finalLIst;

        musicBinder.playMedia(songsInfo, arrayList);


        if (playerFullView != null) playerFullView.dismissDialog();

        playerFullView = new PlayerFullView(this, this, musicBinder, upNextList);
        playerFullView.showDialog();

    }



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
            if (musicBinder.isMusicPlaying()) {
                musicBinder.pauseMusic();
                playPausePlayer.setImageResource(R.drawable.play_drawable);
            } else {
                musicBinder.playMusic();
                playPausePlayer.setImageResource(R.drawable.baseline_pause_24);
            }
        });


        //set title
        titleTextPlayer.setText(songsInfo.getTitle1());

        //set play pause
        playPausePlayer.setImageResource((musicBinder.isMusicPlaying())? R.drawable.baseline_pause_24 : R.drawable.play_drawable);


//        set seek Bar
        seekBarPlayer.setMax(musicBinder.getDuration());
        seekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                seekBarPlayer.setProgress(mCurrentPosition);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mRunnable,0);

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverUpdateUi);
    }

    @Override
    public void onBackPressed() {
        setDefaultNav();
    }


}