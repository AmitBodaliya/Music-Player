package com.abapp.soundplay.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RAHHorizontal;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.Room.Fav.MyDatabaseFav;
import com.abapp.soundplay.Room.Fav.TableFav;
import com.abapp.soundplay.Room.History.MyDatabaseHistory;
import com.abapp.soundplay.Room.History.TableHistory;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FragmentMain extends Fragment {

    Context context;

    LinearLayout historyIconLayout, favIconLayout;
    LinearLayout historyLayout, favLayout, allSongLayout;

    //database
    MyDatabaseFav myDatabaseFav;
    MyDatabaseHistory myDatabaseHistory;

    RecyclerView recyclerViewAllSongs;
    RAHHorizontal adapterAllSongs;

    RecyclerView recyclerViewFav;
    RAHHorizontal adapterAllFav;

    RecyclerView recyclerViewHistory;
    RAHHorizontal adapterAllHistory;

    ArrayList<SongsInfo> arrayListFav;
    ArrayList<SongsInfo> arrayListHistory;

    //live data
    LiveDataViewModel liveDataViewModel;

    public FragmentMain() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        context = requireContext();
        context = requireActivity();

        //database
        myDatabaseFav = MyDatabaseFav.getDatabase(context);
        myDatabaseHistory = MyDatabaseHistory.getDatabase(context);


//        ints
        favIconLayout = v.findViewById(R.id.favIconLayout);
        historyIconLayout = v.findViewById(R.id.historyIconLayout);
        historyLayout = v.findViewById(R.id.historyLayout);
        favLayout = v.findViewById(R.id.favLayout);
        allSongLayout = v.findViewById(R.id.allSongLayout);

        //set on click
        favIconLayout.setOnClickListener(v1 -> ((MainActivity) requireContext()).showAllFavSOng(arrayListFav));
        historyIconLayout.setOnClickListener(v1 -> ((MainActivity) requireContext()).showHistory(arrayListHistory));


        // recycler view
        recyclerViewHistory = v.findViewById(R.id.recyclerViewHistory);
        recyclerViewAllSongs = v.findViewById(R.id.recyclerViewAllSongs);
        recyclerViewFav = v.findViewById(R.id.recyclerViewFav);


        //get list
        liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);
        liveDataViewModel.getLiveList().observe(getViewLifecycleOwner(), this::setRecyclerViewAllSOng);

        //set fav list
        myDatabaseFav.myDao().getAllEntities().observe(getViewLifecycleOwner(), tableFavs -> {
            Log.i("TAG", "onCreateView: " + tableFavs.size());
            ArrayList<SongsInfo> arrayList1 = new ArrayList<>();
            for (TableFav tableFav: tableFavs){
                SongsInfo songsInfo = new SongsInfo(
                        tableFav.SONGS_TITLE,
                        tableFav.SONGS_ARTISTS,
                        tableFav.SONGS_ALBUM,
                        tableFav.SONGS_LENGTH,
                        new File(tableFav.Song_Path));
                arrayList1.add(songsInfo);
            }
            arrayListFav = arrayList1;
            setRecyclerViewFav(arrayList1);
        });


        //set list history
        myDatabaseHistory.myDao().getAllEntities().observe(getViewLifecycleOwner(), tableHistory -> {
            ArrayList<SongsInfo> arrayList1 = new ArrayList<>();
            for (TableHistory tableHistory1 : tableHistory){
                SongsInfo songsInfo = new SongsInfo(
                        tableHistory1.SONGS_TITLE,
                        tableHistory1.SONGS_ARTISTS,
                        tableHistory1.SONGS_ALBUM,
                        tableHistory1.SONGS_LENGTH,
                        new File(tableHistory1.Song_Path));
                arrayList1.add(songsInfo);
            }
            arrayListHistory = arrayList1;
            setRecyclerViewHistory(arrayList1);
        });



        return v;
    }

    public void setRecyclerViewAllSOng(ArrayList<SongsInfo> arrayList){
        Log.i("TAG", "setRecyclerViewAllSOng: " + arrayList.size());

        //set recycler view;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAllSongs.setLayoutManager(linearLayoutManager);
        adapterAllSongs = new RAHHorizontal(requireContext() , arrayList);
        recyclerViewAllSongs.setAdapter(adapterAllSongs);

        adapterAllSongs.setClickListener(new RAHHorizontal.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemClick(view, songsInfo ,position, arrayList );
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onMenuClick(view, songsInfo ,position, arrayList);
            }
        });
    }


    public void setRecyclerViewFav(ArrayList<SongsInfo> arrayList){
        Collections.reverse(arrayList);
        favLayout.setVisibility((arrayList.isEmpty())? View.GONE : View.VISIBLE);

        //set recycler view;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFav.setLayoutManager(linearLayoutManager);
        adapterAllFav = new RAHHorizontal(requireContext() , arrayList);
        recyclerViewFav.setAdapter(adapterAllFav);

        adapterAllFav.setClickListener(new RAHHorizontal.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemClick(view, songsInfo ,position, arrayList );
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onMenuClick(view, songsInfo ,position, arrayList);
            }
        });
    }


    public void setRecyclerViewHistory(ArrayList<SongsInfo> arrayList){
        Collections.reverse(arrayList);
        historyLayout.setVisibility((arrayList.isEmpty())? View.GONE : View.VISIBLE);

        //set recycler view;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHistory.setLayoutManager(linearLayoutManager);
        adapterAllHistory = new RAHHorizontal(requireContext() , arrayList);
        recyclerViewHistory.setAdapter(adapterAllHistory);

        adapterAllHistory.setClickListener(new RAHHorizontal.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemClick(view, songsInfo ,position, arrayList );
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onMenuClick(view, songsInfo ,position, arrayList);
            }
        });
    }

}