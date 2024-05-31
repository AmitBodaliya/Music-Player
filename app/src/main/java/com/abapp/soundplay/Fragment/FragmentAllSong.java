package com.abapp.soundplay.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;

import java.util.ArrayList;

public class FragmentAllSong extends Fragment {

    LiveDataViewModel liveDataViewModel;

    public FragmentAllSong() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_allsong, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewAllSOng);


        //get list
        liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);
        liveDataViewModel.allSongLivaData.observe(getViewLifecycleOwner(), arrayList -> setRecyclerView(recyclerView, arrayList));

        return v;
    }




    void setRecyclerView(RecyclerView recyclerView, ArrayList<SongsInfo> arrayList){
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(requireContext(), arrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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