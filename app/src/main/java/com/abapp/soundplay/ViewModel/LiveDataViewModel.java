package com.abapp.soundplay.ViewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abapp.soundplay.Helper.FetchFileData;
import com.abapp.soundplay.Model.AlbumInfo;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.Room.Fav.FavRepository;
import com.abapp.soundplay.Room.Fav.MyDatabaseFav;
import com.abapp.soundplay.Room.Fav.TableFav;
import com.abapp.soundplay.Room.History.HistoryRepository;
import com.abapp.soundplay.Room.History.MyDatabaseHistory;
import com.abapp.soundplay.Room.History.TableHistory;
import com.abapp.soundplay.Room.Songs.MyDatabaseSongs;
import com.abapp.soundplay.Room.Songs.SongsRepository;
import com.abapp.soundplay.Room.Songs.TableSongs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LiveDataViewModel extends ViewModel {


    @SuppressLint("StaticFieldLeak")
    Context context;

    //list db
    MyDatabaseSongs myDatabaseSongs;
    SongsRepository songsRepository;

    MyDatabaseFav myDatabaseFav;
    FavRepository favRepository;

    MyDatabaseHistory myDatabaseHistory;
    HistoryRepository historyRepository;


    //live data
    public MutableLiveData<ArrayList<SongsInfo>> allSongLivaData;
    public MutableLiveData<ArrayList<SongsInfo>> favSongLivaData;
    public MutableLiveData<ArrayList<SongsInfo>> recentSongLivaData;


    //artist and album live data
    public MutableLiveData<ArrayList<AlbumInfo>> artistLivaData;
    public MutableLiveData<ArrayList<AlbumInfo>> albumLivaData;

    public MutableLiveData<Boolean> updatingList = new MutableLiveData<>(false);



    public void initData(Context context) {
        this.context = context;
        Log.i("TAG", "LiveDataViewModel: ");
        allSongLivaData = new MutableLiveData<>();
        favSongLivaData = new MutableLiveData<>();
        recentSongLivaData = new MutableLiveData<>();

        artistLivaData = new MutableLiveData<>();
        albumLivaData = new MutableLiveData<>();


        //database
        myDatabaseSongs = MyDatabaseSongs.getDatabase(context);
        songsRepository = new SongsRepository(myDatabaseSongs);

        //fav list
        myDatabaseFav = MyDatabaseFav.getDatabase(context);
        favRepository = new FavRepository(myDatabaseFav);

        //recent list
        myDatabaseHistory = MyDatabaseHistory.getDatabase(context);
        historyRepository = new HistoryRepository(myDatabaseHistory);


        initList();
    }




    private void initList(){

        //get list live update
        myDatabaseSongs.myDao().getAllEntities().observeForever(tableSongs -> {
            ArrayList<SongsInfo> arrayList1 = new ArrayList<>();
            for (TableSongs tableSongs1 : tableSongs) {
                SongsInfo songsInfo = new SongsInfo(
                        tableSongs1.SONGS_TITLE,
                        tableSongs1.SONGS_ARTISTS,
                        tableSongs1.SONGS_ALBUM,
                        tableSongs1.SONGS_LENGTH,
                        new File(tableSongs1.Song_Path));
                        if(isSongFilePresent(tableSongs1.Song_Path)) arrayList1.add(songsInfo);
            }

            allSongLivaData.setValue(arrayList1);

            extractAlbumList(arrayList1);
            extractArtisList(arrayList1);
        });


        //set fav list
        myDatabaseFav.myDao().getAllEntities().observeForever(tableFavs -> {
            ArrayList<SongsInfo> arrayList1 = new ArrayList<>();
            for (TableFav tableFav: tableFavs){
                SongsInfo songsInfo = new SongsInfo(
                        tableFav.SONGS_TITLE,
                        tableFav.SONGS_ARTISTS,
                        tableFav.SONGS_ALBUM,
                        tableFav.SONGS_LENGTH,
                        new File(tableFav.Song_Path));
                if(isSongFilePresent(tableFav.Song_Path)) arrayList1.add(songsInfo);
            }
            favSongLivaData.postValue(arrayList1);
        });


        //set list history
        myDatabaseHistory.myDao().getAllEntities().observeForever(tableHistory -> {
            ArrayList<SongsInfo> arrayList1 = new ArrayList<>();
            for (TableHistory tableHistory1 : tableHistory){
                SongsInfo songsInfo = new SongsInfo(
                        tableHistory1.SONGS_TITLE,
                        tableHistory1.SONGS_ARTISTS,
                        tableHistory1.SONGS_ALBUM,
                        tableHistory1.SONGS_LENGTH,
                        new File(tableHistory1.Song_Path));
                if(isSongFilePresent(tableHistory1.Song_Path)) arrayList1.add(songsInfo);
            }
            recentSongLivaData.postValue(arrayList1);
        });

    }


    public boolean isSongFilePresent(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        try {
            File songFile = new File(filePath);
            return songFile.exists() && songFile.isFile();
        } catch (Exception e) {
            return false;
        }
    }



    public void insertFav(SongsInfo songsInfo){
        favRepository.insert(songsInfo);
    }

    public ArrayList<SongsInfo> getFavList(){ return favSongLivaData.getValue(); }
    public ArrayList<SongsInfo> getRecentList(){ return recentSongLivaData.getValue(); }




     public void getAllNewSongList(){
        FetchFileData fetchFileData = new FetchFileData(context);
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            updatingList.postValue(true);
            ArrayList<SongsInfo> arrayList =  new ArrayList<>(fetchFileData.fetchFile(Environment.getExternalStorageDirectory() , false , false , true));
            songsRepository.insertAll(arrayList);
            updatingList.postValue(false);
        });
    }






    public void extractArtisList(ArrayList<SongsInfo> arrayList){
        Log.d("TAG", "extractArtisList() called with: arrayList = [" + arrayList.size() + "]");

        Map<String, AlbumInfo> artistMap = new HashMap<>();

        for (SongsInfo songsInfo : arrayList) {
            String[] artists = songsInfo.getArtist().split(",");

            for (String artist : artists) {
                String artistName = artist.trim();

                if (!artistMap.containsKey(artistName)) {
                    AlbumInfo newAlbum = new AlbumInfo(artistName, new ArrayList<>());
                    artistMap.put(artistName, newAlbum);
                }

                AlbumInfo albumInfo = artistMap.get(artistName);
                if (albumInfo != null) albumInfo.addItem(songsInfo);
            }
        }

        artistLivaData.postValue(new ArrayList<>(artistMap.values()));
    }



    public void extractAlbumList(ArrayList<SongsInfo> arrayList){
        Log.d("TAG", "extractAlbumList() called with: arrayList = [" + arrayList.size() + "]");


        Map<String, AlbumInfo> artistMap = new HashMap<>();

        for (SongsInfo songsInfo : arrayList) {
            String[] artists = songsInfo.getAlbum().split(",");

            for (String artist : artists) {
                String artistName = artist.trim();

                if (!artistMap.containsKey(artistName)) {
                    AlbumInfo newAlbum = new AlbumInfo(artistName, new ArrayList<>());
                    artistMap.put(artistName, newAlbum);
                }

                AlbumInfo albumInfo = artistMap.get(artistName);
                if (albumInfo != null) albumInfo.addItem(songsInfo);
            }
        }

        albumLivaData.postValue(new ArrayList<>(artistMap.values()));
    }



}
