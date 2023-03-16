package com.abapp.soundplay.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RVAAlbumArtists;
import com.abapp.soundplay.R;
import com.abapp.soundplay.Model.AlbumInfo;
import com.abapp.soundplay.Model.SongsInfo;

import java.util.ArrayList;

public class FragmentArtists extends Fragment {

    Context context;

    RecyclerView recyclerView;
    RVAAlbumArtists adapter;
    ArrayList<SongsInfo> arrayList;

    public FragmentArtists() {
    }

    public FragmentArtists(ArrayList<SongsInfo> arrayList){
        this.arrayList = arrayList;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("ARRAY_LIST", arrayList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_artists, container, false);

        context = requireActivity();
        recyclerView = v.findViewById(R.id.recyclerViewArtists);

        if (savedInstanceState != null) {
            arrayList = savedInstanceState.getParcelableArrayList("ARRAY_LIST");
        }

        extractArtisList(arrayList);
        return v;
    }

    public void extractArtisList(ArrayList<SongsInfo> arrayList){
        Log.d("TAG", "extractArtisList() called with: arrayList = [" + arrayList.size() + "]");
        ArrayList<AlbumInfo> artistList = new ArrayList<>();

        for(SongsInfo songsInfo : arrayList){

            boolean b = true;
            for(AlbumInfo albumInfo : artistList){
                if(albumInfo.getTitle().equals(songsInfo.getArtist())){
                    b = false;
                    break;
                }
            }
            if(b) {
                ArrayList<SongsInfo> test1 = new ArrayList<>();
                artistList.add(new AlbumInfo(songsInfo.getArtist() , test1 ));
            }

            for (int i = 0; i < artistList.size() ; i++){
                if(songsInfo.getArtist().equals(artistList.get(i).getTitle())){

                    artistList.get(i).addItem(songsInfo);

                }
            }
        }

        setList(artistList );

    }

    public void setList(ArrayList<AlbumInfo> artistList ){
        artistList.sort(AlbumInfo.AppNameComparator);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RVAAlbumArtists(context , artistList );
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3 ));
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new RVAAlbumArtists.ItemClickListener() {
            @Override
            public void onItemClick(String artistName , int pos , ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).showSongListDialog("" +  artistList.get(pos).getTitle() , list);

        }

            @Override
            public void onMenuClick(View view, int position, String artistName , ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).albumMenu(view , list);

            }
        });
    }

}