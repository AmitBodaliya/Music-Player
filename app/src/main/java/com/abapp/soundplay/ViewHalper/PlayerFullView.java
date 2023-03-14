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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.ItemMoveCallback;
import com.abapp.soundplay.Adapter.RVAUpNext;
import com.abapp.soundplay.Gesture.OnSwipeGesture;
import com.abapp.soundplay.Helper.FavSong;
import com.abapp.soundplay.Helper.MediaMetaData;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.Music.MusicPlayer_1;
import com.abapp.soundplay.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class PlayerFullView {

    Context context;
    Activity activity;
    MainActivity mainActivity;
    MusicPlayer_1 musicPlayer_1;
    FavSong favSong;

    //music helper
    MediaMetadataRetriever mediaMetadataRetriever;
    MediaMetaData mediaMetaData;

    boolean djMode = false;


    //ints
    SlidingUpPanelLayout slidingPaneLayout;
    ImageView songInfoScreen;
    ImageView imageViewF;
    ImageView cancelFullScreenAlert;
    ImageView albumArtFullScreenAlert, playPauseFullScreenAlert, nextFullScreenAlert, previousFullScreenAlert;
    TextView titleTextFullScreenAlert, timeDuringFullScreenAlert, titleArtistFullScreenAlert, timeCurrentFullScreenAlert;
    SeekBar seekBarArtScreenFullScreenAlert;
    LinearLayout upNextListLayoutFullScreenAlert;


    //current song.
    SongsInfo currentSongInfo;

    //up next list
    ArrayList<SongsInfo> upNextList;
    RecyclerView recyclerViewUpNextList;
    RVAUpNext recyclerViewAdapter;




    AlertDialog alertDialog;


    public PlayerFullView(Context context, MainActivity mainActivity, MusicPlayer_1 musicPlayer_1, ArrayList<SongsInfo> upNextList) {
        this.context = context;
        this.activity = (Activity) context;
        this.mainActivity = mainActivity;
        this.musicPlayer_1 = musicPlayer_1;
        this.currentSongInfo = musicPlayer_1.getSongsInfo();
        this.upNextList = upNextList;



        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(String.valueOf(currentSongInfo.getPath()));
        this.mediaMetaData = new MediaMetaData(context);

        favSong = new FavSong(context);
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


        recyclerViewUpNextList = mView.findViewById(R.id.upNextListRecyclerView);
        upNextListLayoutFullScreenAlert = mView.findViewById(R.id.upNextLayout);


        alert.setView(mView);
        alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        }


        //on slide down in the view
        mView.setOnTouchListener(new OnSwipeGesture(context) {
            @Override
            public void onSwipeBottom() {
                alertDialog.dismiss();
            }
        });


        upNextListLayoutFullScreenAlert.setOnTouchListener(new OnSwipeGesture(context) {
            @Override
            public void onClick() {
                slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }

            @Override
            public void onSwipeTop() {
            }
        });


        //set up next list visibility
        if (djMode) {
            upNextListLayoutFullScreenAlert.setVisibility(View.GONE);
        } else {
            upNextListLayoutFullScreenAlert.setVisibility(View.VISIBLE);
        }


        //implement slide
        slidingPaneLayout = mView.findViewById(R.id.slidePanelLayout);
        if (!djMode) {
            slidingPaneLayout.setPanelHeight(170);
            slidingPaneLayout.setShadowHeight(0);
            slidingPaneLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    mView.findViewById(R.id.bottomUpLayoutSlidePanel).setAlpha(slideOffset);
                    mView.findViewById(R.id.bottomBottomUpLayoutSlidePanel).setAlpha(1 - slideOffset);
                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                }
            });
            mView.findViewById(R.id.bottomUpLayoutSlidePanel).setAlpha(0);
            mView.findViewById(R.id.bottomBottomUpLayoutSlidePanel).setAlpha(1);
        } else slidingPaneLayout.setPanelHeight(0);


        slidingPaneLayout.setScrollableView(recyclerViewUpNextList);


        //ready list show in recycler view view
        recyclerViewUpNextList.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewAdapter = new RVAUpNext(context, upNextList);

        for (int i = 0; i < upNextList.size(); i++) {
            if (upNextList.get(i).getUniqueID().equals(currentSongInfo.getUniqueID())) {
                recyclerViewUpNextList.scrollToPosition(i);
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
        nextFullScreenAlert.setOnClickListener(v -> mainActivity.nextSong());

        //previous song
        previousFullScreenAlert.setOnClickListener(v -> mainActivity.previousSong());


        //favourite song icon on click
        imageViewF.setOnClickListener(v -> {
            if (favSong.checkSongIsPresentInFav(currentSongInfo.getPath())) {
                imageViewF.setImageResource(R.drawable.baseline_favorite_border_24);
                Toast.makeText(context, "Remove from Favourite", Toast.LENGTH_SHORT).show();
                favSong.removeFavouriteSong(currentSongInfo.getPath());
            } else {
                favSong.addToFavourite(currentSongInfo.getPath());
                Toast.makeText(context, "Added to Favourite", Toast.LENGTH_SHORT).show();
                imageViewF.setImageResource(R.drawable.baseline_favorite_24);
            }
        });


        //on play pause click
        playPauseFullScreenAlert.setOnClickListener(v -> {
            if (musicPlayer_1.isMusicPlaying()) {
                musicPlayer_1.pauseMusic();
                playPauseFullScreenAlert.setImageResource(R.drawable.play_drawable);
            } else {
                musicPlayer_1.startMusic();
                playPauseFullScreenAlert.setImageResource(R.drawable.baseline_pause_24);
            }
        });


        refreshView(true);

    }



    public void updateData(MusicPlayer_1 musicPlayer_1, SongsInfo currentSongInfo){
        this.musicPlayer_1 = musicPlayer_1;
        this.currentSongInfo = currentSongInfo;
    }



    public void updateUpNextList(ArrayList<SongsInfo> upNextList){
        this.upNextList = upNextList;
    }


    public void dismissDialog(){
        alertDialog.dismiss();
    }



    public void refreshView(boolean showDialog){

        //set values ///////////////////////////////////////////////////////////////////////////////


        //set album art
        Bitmap bitmap = mediaMetaData.getSongBitmap(currentSongInfo.getPath());
        if (bitmap != null) {
            albumArtFullScreenAlert.setImageBitmap(bitmap);
        } else {
            albumArtFullScreenAlert.setImageResource(R.drawable.baseline_music_note_24);
        }


        if (musicPlayer_1.isMusicPlaying()) {
            playPauseFullScreenAlert.setImageResource(R.drawable.baseline_pause_24);
        } else {
            playPauseFullScreenAlert.setImageResource(R.drawable.play_drawable);
        }

        //set favourite song icon
        if (favSong.checkSongIsPresentInFav(currentSongInfo.getPath())) {
            imageViewF.setImageResource(R.drawable.baseline_favorite_24);
        }else imageViewF.setImageResource(R.drawable.baseline_favorite_border_24);


        //set next prev enable or not
        if (djMode) {
            nextFullScreenAlert.setImageResource(R.drawable.du_skip_next);
            previousFullScreenAlert.setImageResource(R.drawable.du_skip_prev);
        } else {
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
        }

        //set title and artist
        titleTextFullScreenAlert.setText(currentSongInfo.getTitle().substring(0, currentSongInfo.getTitle().lastIndexOf(".")));
        titleArtistFullScreenAlert.setText(currentSongInfo.getArtist());
        timeDuringFullScreenAlert.setText(currentSongInfo.getSongLength());






//        set seek Bar max
        seekBarArtScreenFullScreenAlert.setMax(musicPlayer_1.getDuration());


        //set seek change
        seekBarArtScreenFullScreenAlert.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayer_1.setSeekTo(progress);

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
                int mCurrentPosition = musicPlayer_1.getCurrentPosition();

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
