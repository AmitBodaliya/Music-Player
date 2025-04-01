package com.abapp.soundplay.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RAHHorizontal;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentMain extends Fragment {

    Context context;

    LinearLayout historyIconLayout, favIconLayout;
    LinearLayout historyLayout, favLayout, allSongLayout;

    //view
    RecyclerView recyclerViewAllSongs;
    RAHHorizontal adapterAllSongs;

    RecyclerView recyclerViewFav;
    RAHHorizontal adapterAllFav;

    RecyclerView recyclerViewHistory;
    RAHHorizontal adapterAllHistory;

    ProgressBar isUpdatingList;

    //live data
    LiveDataViewModel liveDataViewModel;

    public FragmentMain() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        context = requireContext();
        context = requireActivity();


//        ints
        favIconLayout = v.findViewById(R.id.favIconLayout);
        historyIconLayout = v.findViewById(R.id.historyIconLayout);
        historyLayout = v.findViewById(R.id.historyLayout);
        favLayout = v.findViewById(R.id.favLayout);
        allSongLayout = v.findViewById(R.id.allSongLayout);
        isUpdatingList = v.findViewById(R.id.isUpdatingList);

        //set on click
        favIconLayout.setOnClickListener(v1 -> ((MainActivity) requireContext()).showAllFavSOng());
        historyIconLayout.setOnClickListener(v1 -> ((MainActivity) requireContext()).showHistory());


        // recycler view
        recyclerViewHistory = v.findViewById(R.id.recyclerViewHistory);
        recyclerViewAllSongs = v.findViewById(R.id.recyclerViewAllSongs);
        recyclerViewFav = v.findViewById(R.id.recyclerViewFav);


        //get list
        liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);
        liveDataViewModel.allSongLivaData.observe(getViewLifecycleOwner(), this::setRecyclerViewAllSOng);
        liveDataViewModel.favSongLivaData.observe(getViewLifecycleOwner(), this::setRecyclerViewFav);
        liveDataViewModel.recentSongLivaData.observe(getViewLifecycleOwner(), this::setRecyclerViewHistory);

        //list update observer
        liveDataViewModel.updatingList.observe(getViewLifecycleOwner(), aBoolean -> {
            isUpdatingList.setVisibility(aBoolean ? View.VISIBLE: View.GONE);
        });

        return v;
    }

    public void setRecyclerViewAllSOng(ArrayList<SongsInfo> arrayList){
        Log.i("TAG", "setRecyclerViewAllSOng: " + arrayList.size());

        //set recycler view;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAllSongs.setLayoutManager(linearLayoutManager);
        adapterAllSongs = new RAHHorizontal(requireContext(), recyclerViewAllSongs, arrayList);
        recyclerViewAllSongs.setAdapter(adapterAllSongs);

        adapterAllSongs.setClickListener(new RAHHorizontal.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemClick(view, songsInfo ,position, arrayList );
            }

            @Override
            public void onItemLongClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemLongClick(view, songsInfo ,position, arrayList );
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
        adapterAllFav = new RAHHorizontal(requireContext(), recyclerViewFav, arrayList);
        recyclerViewFav.setAdapter(adapterAllFav);

        adapterAllFav.setClickListener(new RAHHorizontal.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemClick(view, songsInfo ,position, arrayList );
            }

            @Override
            public void onItemLongClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemLongClick(view, songsInfo ,position, arrayList );
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
        adapterAllHistory = new RAHHorizontal(requireContext(), recyclerViewHistory, arrayList);
        recyclerViewHistory.setAdapter(adapterAllHistory);

        adapterAllHistory.setClickListener(new RAHHorizontal.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemClick(view, songsInfo ,position, arrayList );
            }

            @Override
            public void onItemLongClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemLongClick(view, songsInfo ,position, arrayList );
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onMenuClick(view, songsInfo ,position, arrayList);
            }
        });
    }

}