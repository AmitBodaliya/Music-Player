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

public class FragmentAlbum extends Fragment {

    Context context;

    RecyclerView recyclerView;
    RVAAlbumArtists adapter;
    ArrayList<SongsInfo> arrayList;

    public FragmentAlbum() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_album, container, false);

        context = requireActivity();
        recyclerView = v.findViewById(R.id.recyclerViewAlbum);

        arrayList = ((MainActivity) requireActivity()).getArrayList();

        extractAlbumList(arrayList);
        return v;
    }

    public void extractAlbumList(ArrayList<SongsInfo> arrayList){
        Log.d("TAG", "extractAlbumList() called with: arrayList = [" + arrayList.size() + "]");
        ArrayList<AlbumInfo> albumList = new ArrayList<>();

        for(SongsInfo songsInfo : arrayList){

            boolean b = true;

            for(AlbumInfo albumInfo : albumList){
                if (albumInfo.getTitle().equals(songsInfo.getAlbum())) {
                    b = false;
                    break;
                }
            }
            if(b) {
                ArrayList<SongsInfo> test1 = new ArrayList<>();
                albumList.add(new AlbumInfo(songsInfo.getAlbum() , test1));
            }

            for (int i = 0; i < albumList.size() ; i++){
                if(songsInfo.getAlbum().equals(albumList.get(i).getTitle())){
                    albumList.get(i).addItem(songsInfo);
                }
            }
        }

        setList(albumList );

    }

    public void setList(ArrayList<AlbumInfo> albumList ){
        albumList.sort(AlbumInfo.AppNameComparator);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RVAAlbumArtists(context , albumList );
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3 ));
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new RVAAlbumArtists.ItemClickListener() {
            @Override
            public void onItemClick(String artistName , int position, ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).showSongListDialog("" + albumList.get(position).getTitle() , list);
            }

            @Override
            public void onMenuClick(View view, int position, String artistName ,  ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).albumMenu(view , list);
            }
        });

    }

}