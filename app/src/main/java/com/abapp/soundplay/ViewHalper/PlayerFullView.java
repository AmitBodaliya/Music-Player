package com.abapp.soundplay.ViewHalper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.ItemMoveCallback;
import com.abapp.soundplay.Adapter.RVAUpNext;
import com.abapp.soundplay.Helper.MediaMetaData;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.Music.MusicPlayer_1;
import com.abapp.soundplay.R;
import com.abapp.soundplay.Room.Fav.FavRepository;
import com.abapp.soundplay.Room.Fav.MyDatabaseFav;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerFullView {

    Context context;
    Activity activity;
    MainActivity mainActivity;

    MusicPlayer_1.MusicBinder musicBinder;
    FavRepository favRepository;

    //music helper
    MediaMetadataRetriever mediaMetadataRetriever;
    MediaMetaData mediaMetaData;

//    boolean djMode = false;


    //ints
    MotionLayout motionLayout;
    ImageView expandUpNextList;

    ImageView songInfoScreen;
    ImageView imageViewF;
    ImageView cancelFullScreenAlert;
    ImageView albumArtFullScreenAlert, playPauseFullScreenAlert, nextFullScreenAlert, previousFullScreenAlert;
    TextView titleTextFullScreenAlert, timeDuringFullScreenAlert, titleArtistFullScreenAlert, timeCurrentFullScreenAlert;
    SeekBar seekBarArtScreenFullScreenAlert;


    //current song.
    SongsInfo currentSongInfo;

    //up next list
    TextView subTitleUpNext;
    ArrayList<SongsInfo> upNextList;
    RecyclerView recyclerViewUpNextList;
    RVAUpNext recyclerViewAdapter;




    AlertDialog alertDialog;


    public PlayerFullView(Context context, MainActivity mainActivity, MusicPlayer_1.MusicBinder musicBinder, ArrayList<SongsInfo> upNextList) {
        this.context = context;
        this.activity = (Activity) context;
        this.mainActivity = mainActivity;
        this.musicBinder = musicBinder;
        this.currentSongInfo = musicBinder.getCurrentSongInfo();
        this.upNextList = upNextList;



        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(String.valueOf(currentSongInfo.getPath()));
        this.mediaMetaData = new MediaMetaData(context);

        favRepository = new FavRepository(MyDatabaseFav.getDatabase(context));
    }


    public boolean isShowing(){
        if (alertDialog == null) return false;
        return alertDialog.isShowing();
    }




    //searchFunction
    @SuppressLint({"ResourceAsColor", "SetTextI18n", "DefaultLocale", "ClickableViewAccessibility"})
    public void showDialog() {
        if (!currentSongInfo.getPath().exists()) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            return;
        }


        Rect displayRectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);


        final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.FullScreenAlertDialogStyle);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialog_fullscreen_song, null);

        mView.setMinimumWidth((int) (displayRectangle.width() * 1f));
        mView.setMinimumHeight((int) (displayRectangle.height() * 1f));


//        ints
        motionLayout = mView.findViewById(R.id.motionLayout);
        expandUpNextList = mView.findViewById(R.id.imageViewShowList);

        songInfoScreen = mView.findViewById(R.id.songInfoScreen);
        imageViewF = mView.findViewById(R.id.favouriteScreen);
        cancelFullScreenAlert = mView.findViewById(R.id.cancelScreen);
        albumArtFullScreenAlert = mView.findViewById(R.id.albumArtScreen);
        playPauseFullScreenAlert = mView.findViewById(R.id.playPauseArtScreen);
        nextFullScreenAlert = mView.findViewById(R.id.nexArtScreen);
        previousFullScreenAlert = mView.findViewById(R.id.prevArtScreen);
        titleTextFullScreenAlert = mView.findViewById(R.id.titleScreen);
        titleArtistFullScreenAlert = mView.findViewById(R.id.titleArtistScreen);
        timeDuringFullScreenAlert = mView.findViewById(R.id.timeDuringScreen);
        timeCurrentFullScreenAlert = mView.findViewById(R.id.timeCurrentScreen);
        seekBarArtScreenFullScreenAlert = mView.findViewById(R.id.seekBarScreen);

        subTitleUpNext = mView.findViewById(R.id.subTitleUpNextList);
        recyclerViewUpNextList = mView.findViewById(R.id.upNextListRecyclerView);


        alert.setView(mView);
        alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);



        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        }




        //on click expand to end
        expandUpNextList.setOnClickListener(view -> motionLayout.transitionToEnd());
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                expandUpNextList.setRotation(progress * 180);
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                if (currentId == motionLayout.getStartState()){
                    expandUpNextList.setOnClickListener(view -> motionLayout.transitionToEnd());
                }else {
                    expandUpNextList.setOnClickListener(view -> motionLayout.transitionToStart());
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });



        //ready list show in recycler view view
        recyclerViewUpNextList.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewAdapter = new RVAUpNext(context, upNextList);

        for (int i = 0; i < upNextList.size(); i++) {
            if (upNextList.get(i).getUniqueID().equals(currentSongInfo.getUniqueID())) {
                recyclerViewUpNextList.scrollToPosition(i);
                recyclerViewAdapter.setCurrentSOng(i);
                break;
            }
        }

        recyclerViewAdapter.setClickListener(new RVAUpNext.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                mainActivity.onItemClick(view, songsInfo, position, list);
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                mainActivity.onMenuClick(view, songsInfo, position, list);
            }

            @Override
            public void onItemMoved() {
                onListItemChange();
            }
        });


        ItemTouchHelper.Callback callback = new ItemMoveCallback(recyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerViewUpNextList);

        recyclerViewUpNextList.setAdapter(recyclerViewAdapter);




        //onclick song info
        songInfoScreen.setOnClickListener(v -> mainActivity.songInfoDialog(currentSongInfo.getPath()));

        //cancel dialog
        cancelFullScreenAlert.setOnClickListener(v -> alertDialog.dismiss());

        //next song
        nextFullScreenAlert.setOnClickListener(v -> musicBinder.nextSong());

        //previous song
        previousFullScreenAlert.setOnClickListener(v -> musicBinder.previousSong());


        //favourite song icon on click
        imageViewF.setOnClickListener(v -> {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                if (favRepository.isPresent(currentSongInfo.getPath())){
                    imageViewF.setImageResource(R.drawable.baseline_favorite_border_24);
                    favRepository.delete(currentSongInfo);
                } else {
                    favRepository.insert(currentSongInfo);
                    imageViewF.setImageResource(R.drawable.baseline_favorite_24);
                }
            });

        });


        //on play pause click
        playPauseFullScreenAlert.setOnClickListener(v -> {
            if (musicBinder.isMusicPlaying()) {
                musicBinder.pauseMusic();
                playPauseFullScreenAlert.setImageResource(R.drawable.play_drawable);
            } else {
                musicBinder.playMusic();
                playPauseFullScreenAlert.setImageResource(R.drawable.baseline_pause_24);
            }
        });


        refreshView(true);

    }



    public void updateData(MusicPlayer_1.MusicBinder musicBinder){
        this.musicBinder = musicBinder;
        this.currentSongInfo = musicBinder.getCurrentSongInfo();
    }



    public void updateUpNextList(ArrayList<SongsInfo> upNextList){
        this.upNextList = upNextList;
    }


    public void dismissDialog(){
        if (alertDialog != null) alertDialog.dismiss();
    }


    @SuppressLint("SetTextI18n")
    public void onListItemChange(){
        boolean b = false;

        for (int i = 0; i < upNextList.size(); i++) {
            if (upNextList.get(i).getUniqueID().equals(currentSongInfo.getUniqueID())) {
                recyclerViewUpNextList.scrollToPosition(i);
                recyclerViewAdapter.setCurrentSOng(i);

                subTitleUpNext.setVisibility(View.VISIBLE);
                subTitleUpNext.setText((i + 1) + " of " + upNextList.size());


                if (i + 1 < upNextList.size()) {
                    nextFullScreenAlert.setImageResource(R.drawable.baseline_skip_next_24);
                } else nextFullScreenAlert.setImageResource(R.drawable.du_skip_next);

                if (0 <= i - 1) {
                    previousFullScreenAlert.setImageResource(R.drawable.baseline_skip_previous_24);
                } else previousFullScreenAlert.setImageResource(R.drawable.du_skip_prev);

                b = true;
                break;
            }
        }
        if (!b) {
            recyclerViewAdapter.setCurrentSOng(-1);
            subTitleUpNext.setVisibility(View.GONE);
        }
    }


    @SuppressLint("SetTextI18n")
    public void refreshView(boolean showDialog){
        if (alertDialog == null) return;

        //set values ///////////////////////////////////////////////////////////////////////////////

        boolean b = false;
        for (int i = 0; i < upNextList.size(); i++) {
            if (upNextList.get(i).getUniqueID().equals(currentSongInfo.getUniqueID())) {
                recyclerViewUpNextList.scrollToPosition(i);
                recyclerViewAdapter.setCurrentSOng(i);

                subTitleUpNext.setVisibility(View.VISIBLE);
                subTitleUpNext.setText((i + 1) + " of " + upNextList.size());

                b = true;
                break;
            }
        }
        if (!b) {
            recyclerViewAdapter.setCurrentSOng(-1);
            subTitleUpNext.setVisibility(View.GONE);
        }


        //set album art
        Bitmap bitmap = mediaMetaData.getSongBitmap(currentSongInfo.getPath());
        if (bitmap != null) {
            albumArtFullScreenAlert.setImageBitmap(bitmap);
        } else {
            albumArtFullScreenAlert.setImageResource(R.drawable.baseline_music_note_24);
        }


        if (musicBinder.isMusicPlaying()) {
            playPauseFullScreenAlert.setImageResource(R.drawable.baseline_pause_24);
        } else {
            playPauseFullScreenAlert.setImageResource(R.drawable.play_drawable);
        }

        //set favourite song icon
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (favRepository.isPresent(currentSongInfo.getPath())){
                imageViewF.setImageResource(R.drawable.baseline_favorite_24);
            }else imageViewF.setImageResource(R.drawable.baseline_favorite_border_24);
        });


        //set next prev enable or not
        for (int i = 0; i < upNextList.size(); i++) {
            if (currentSongInfo.getUniqueID().equals(upNextList.get(i).getUniqueID())) {

                if (i + 1 < upNextList.size()) {
                    nextFullScreenAlert.setImageResource(R.drawable.baseline_skip_next_24);
                } else nextFullScreenAlert.setImageResource(R.drawable.du_skip_next);

                if (0 <= i - 1) {
                    previousFullScreenAlert.setImageResource(R.drawable.baseline_skip_previous_24);
                } else previousFullScreenAlert.setImageResource(R.drawable.du_skip_prev);
            }
        }

        //set title and artist
        titleTextFullScreenAlert.setText(currentSongInfo.getTitle1());
        titleArtistFullScreenAlert.setText(currentSongInfo.getArtist());
        timeDuringFullScreenAlert.setText(currentSongInfo.getSongLength());






//        set seek Bar max
        seekBarArtScreenFullScreenAlert.setMax(musicBinder.getDuration());


        //set seek change
        seekBarArtScreenFullScreenAlert.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicBinder.setSeekTo(progress);

                    //set time update
                    String finalTime;
                    String Sec = String.valueOf((progress % 60000) / 1000);
                    String min = String.valueOf(progress / 60000);
                    if (Sec.length() == 1) {
                        finalTime = min + ":0" + Sec;
                    } else {
                        finalTime = min + ":" + Sec;
                    }

                    timeCurrentFullScreenAlert.setText(finalTime);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        final Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {

            @Override
            public void run() {
                int mCurrentPosition = musicBinder.getCurrentPosition();

                //set time update
                String finalTime;
                String Sec = String.valueOf((mCurrentPosition % 60000) / 1000);
                String min = String.valueOf(mCurrentPosition / 60000);
                if (Sec.length() == 1) {
                    finalTime = min + ":0" + Sec;
                } else {
                    finalTime = min + ":" + Sec;
                }


                seekBarArtScreenFullScreenAlert.setProgress(mCurrentPosition);
                timeCurrentFullScreenAlert.setText(finalTime);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mRunnable,0);





        //show dialog
        if (showDialog) alertDialog.show();


    }

}
