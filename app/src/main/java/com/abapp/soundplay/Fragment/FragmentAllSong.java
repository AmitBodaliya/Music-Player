package com.abapp.soundplay.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FragmentAllSong extends Fragment {

    //view
    FastScrollRecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    //model
    LiveDataViewModel liveDataViewModel;

    public FragmentAllSong() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_allsong, container, false);

        //recycler view
        recyclerView = v.findViewById(R.id.recyclerViewAllSOng);


        //progress bar
        ProgressBar progressBar = v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //init recycler view
        setRecyclerView();


        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            // Update the UI on the main thread
            requireActivity().runOnUiThread(() -> {

                //get list
                liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);
                liveDataViewModel.allSongLivaData.observe(getViewLifecycleOwner(), this::setListData);
                progressBar.setVisibility(View.GONE);

            });
        });

        return v;
    }


    void setListData(ArrayList<SongsInfo> list){
        recyclerViewAdapter.updateList(list);
    }


    void setRecyclerView(){
        recyclerViewAdapter = new RecyclerViewAdapter(recyclerView, requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
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