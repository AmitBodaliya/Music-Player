package com.abapp.soundplay.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.abapp.soundplay.Adapter.ViewPagerAdapter;
import com.abapp.soundplay.Helper.FavSong;
import com.abapp.soundplay.Helper.FetchFileData;
import com.abapp.soundplay.Helper.MediaMetaData;
import com.abapp.soundplay.Helper.UniqueIdGen;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.Music.MusicPlayer_1;
import com.abapp.soundplay.Music.MusicPlayer_2;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewHalper.PlayerFullView;
import com.abapp.soundplay.ViewHalper.SearchView;
import com.abapp.soundplay.ViewHalper.ShowListView;
import com.abapp.soundplay.ViewHalper.SongInfoView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicPlayer_1.OnTaskCompletionListener {

    //layout
    String[] tabTitle = {"Home", "Songs", "Album", "Artists", "Folder"};

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    ImageView searchImageView , expanded_menu;


    //full screen view
    PlayerFullView playerFullView;


    //music payer
    MusicPlayer_1 musicPlayer_1;
    MusicPlayer_2 musicPlayer_2;

    //Media Data
    MediaMetaData mediaMetaData;
    FetchFileData fetchFileData;


    //array list
    SongsInfo currentSong;
    ArrayList<SongsInfo> upNextList = null;
    ArrayList<SongsInfo> arrayListBackground = null;


    UniqueIdGen uniqueIdGen;
    FavSong favSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //layout
        tabLayout =  findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        searchImageView = findViewById(R.id.searchImageView);
        expanded_menu = findViewById(R.id.expanded_menu);


        //onclick
        expanded_menu.setOnClickListener(this::MainMenu);
        searchImageView.setOnClickListener(view -> {
            SearchView searchView1 = new SearchView(MainActivity.this , MainActivity.this, arrayListBackground);
            searchView1.showDialog();
        });


        //set music player
        musicPlayer_1 = new MusicPlayer_1(this);
        musicPlayer_1.setOnTaskCompletionListener(this);

        musicPlayer_2 = new MusicPlayer_2(this);
//        musicPlayer_2.setOnTaskCompletionListener(this);



        uniqueIdGen = UniqueIdGen.getInstance();
        favSong = new FavSong(this);
        mediaMetaData = new MediaMetaData(this);
        fetchFileData = new FetchFileData(this);



        //set list and set view pager
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            arrayListBackground = (ArrayList<SongsInfo>) getIntent().getSerializableExtra("arrayList");
            setViewPager2(arrayListBackground);
        }

    }





    void setViewPager2(ArrayList<SongsInfo> arrayList){
        arrayList.sort(SongsInfo.sortByTitle);

        int id = viewPager2.getCurrentItem();
        viewPagerAdapter = new ViewPagerAdapter(this, this, tabTitle.length, arrayList);


        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.setCurrentItem(id, false);
        viewPager2.setOffscreenPageLimit(tabTitle.length - 1);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(tabTitle[position])).attach();
    }

    public void setViewPagerItem(int pos){ viewPager2.setCurrentItem(pos , true); }











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
//                if (!djMode) Toast.makeText(this, "On DJ Mode", Toast.LENGTH_SHORT).show();
//                else bothDeck();
                return true;
            }


            if (item.getItemId() == R.id.idFavourite) {
//                upNextListDialog("Favourite Songs", favSong.getAllFavouriteSOng());
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
//                    for (int i = 0; i < upNextList.size(); i++) {
//
//                        if (onClickSOngURI.equals(upNextList.get(i).getSongPath())) {
//
//                            ArrayList<SongsInfo> t = new ArrayList<>(upNextList.subList(0, i + 1));
//                            t.add(new SongsInfo(
//                                    mediaMetaData.getSongTitle(file) ,
//                                    mediaMetaData.getSongArtist(file),
//                                    mediaMetaData.getSongAlbum(file),
//                                    mediaMetaData.getSongLength(file),
//                                    file , "") );
//
//                            t.addAll(upNextList.subList(i + 1  , upNextList.size() ));
//
//                            upNextList = t;
//
//                            break;
//                        }
//                    }
//                    return true;
                }

                //add to favourite song
                case R.id.item5:
//                    favSong.addToFavourite(file);
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

            switch (item.getItemId()) {
                //play
                case R.id.item_play: {
                    onItemClick(view , list.get(0) ,0 , list);
                    return true;
                }

//                 play next
                case R.id.item2: {
//                    upNextList.addAll(list);
                    return true;
                }

                default:
                    return super.onOptionsItemSelected(item);
            }

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


































    //on click
    public void onItemClick(View view , SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
        ArrayList<SongsInfo> finalLIst = new ArrayList<>();
        for (SongsInfo songINfo: arrayList) {
            songINfo.setUniqueID(uniqueIdGen.generateUniqueId());
            finalLIst.add(songINfo);
        }

        currentSong = finalLIst.get(position);
        upNextList = finalLIst;

        musicPlayer_1.setMusic(songsInfo);
        musicPlayer_1.startMusic();

        if (playerFullView != null) playerFullView.dismissDialog();

        playerFullView = new PlayerFullView(this, this, musicPlayer_1, finalLIst);
        playerFullView.showDialog();

    }



    //music player functions

    @Override
    public void onMusicComplete(MediaPlayer mediaPlayer) {
        nextSong();
    }


    public void nextSong(){
        for (int i = 0; i < upNextList.size(); i++) {
            if (upNextList.get(i).getUniqueID().equals(currentSong.getUniqueID())){
                if (i < upNextList.size() - 1) {
                    currentSong = upNextList.get(i + 1);

                    musicPlayer_1.stopMusic();
                    musicPlayer_1.setMusic(currentSong);
                    musicPlayer_1.startMusic();

                    playerFullView.updateData(musicPlayer_1, currentSong);
                    playerFullView.updateUpNextList(upNextList);
                    playerFullView.refreshView(false);
                }
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
                    musicPlayer_1.setMusic(currentSong);
                    musicPlayer_1.startMusic();

                    playerFullView.updateData(musicPlayer_1, currentSong);
                    playerFullView.updateUpNextList(upNextList);
                    playerFullView.refreshView(false);
                }
                break;
            }
        }
    }





}