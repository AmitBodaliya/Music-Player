package com.abapp.soundplay.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RVAAlbumArtists;
import com.abapp.soundplay.R;
import com.abapp.soundplay.Model.AlbumInfo;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;

import java.util.ArrayList;

public class FragmentArtists extends Fragment {

    Context context;

    RecyclerView recyclerView;
    RVAAlbumArtists adapter;

    LiveDataViewModel liveDataViewModel;

    public FragmentArtists() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_artists, container, false);

        context = requireActivity();
        recyclerView = v.findViewById(R.id.recyclerViewArtists);


        //get list
        liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);
        liveDataViewModel.artistLivaData.observe(getViewLifecycleOwner(), this::setList);

        return v;
    }





    public void setList(ArrayList<AlbumInfo> artistList ){
        artistList.sort(AlbumInfo.AppNameComparator);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RVAAlbumArtists(context , artistList );
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3 ));
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new RVAAlbumArtists.ItemClickListener() {
            @Override
            public void onItemClick(String artistName , int position, ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).showSongListDialog("" +  artistList.get(position).getTitle() , list);

        }

            @Override
            public void onMenuClick(View view, int position, String artistName , ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).albumMenu(view , list);

            }
        });
    }

}