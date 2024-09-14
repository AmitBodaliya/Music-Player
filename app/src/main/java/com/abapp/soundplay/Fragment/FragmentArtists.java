package com.abapp.soundplay.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FragmentArtists extends Fragment {

    Context context;

    FastScrollRecyclerView recyclerView;
    RVAAlbumArtists adapter;

    LiveDataViewModel liveDataViewModel;

    public FragmentArtists() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_artists, container, false);

        //view
        context = requireActivity();
        recyclerView = v.findViewById(R.id.recyclerViewArtists);

        //progress bar
        ProgressBar progressBar = v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            // Update the UI on the main thread
            requireActivity().runOnUiThread(() -> {

                //get list
                liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);
                liveDataViewModel.artistLivaData.observe(getViewLifecycleOwner(), this::setList);
                progressBar.setVisibility(View.GONE);

            });
        });

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
                ((MainActivity) requireActivity()).showSongListDialog(artistList.get(position).getTitle(), list);

        }

            @Override
            public void onMenuClick(View view, int position, String artistName , ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).albumMenu(view , list);

            }
        });
    }

}