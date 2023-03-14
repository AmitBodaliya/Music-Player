package com.abapp.soundplay.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;

import java.util.ArrayList;

public class FragmentAllSong extends Fragment {

    Context context;
    ArrayList<SongsInfo> arrayList;

    public FragmentAllSong(Context context, ArrayList<SongsInfo> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_allsong, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewAllSOng);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context, arrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recyclerViewAdapter);


        recyclerViewAdapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onItemClick(view, songsInfo ,position, arrayList );
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position , ArrayList<SongsInfo> arrayList) {
                ((MainActivity) requireActivity()).onMenuClick(view, songsInfo ,position, arrayList);
            }
        });


        return v;
    }

}