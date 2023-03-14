package com.abapp.soundplay.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.abapp.soundplay.Fragment.*;
import com.abapp.soundplay.Model.SongsInfo;

import java.io.File;
import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    Context context;
    int totalPage;
    ArrayList<SongsInfo> arrayList;

    public ViewPagerAdapter(FragmentActivity fm  , Context context , int totalPage  , ArrayList<SongsInfo> arrayList) {
        super(fm);
        this.context = context;
        this.totalPage = totalPage;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment[] fragments  = {
                new FragmentHome(context) ,
                new FragmentAllSong(context , arrayList),
                new FragmentAlbum(context , arrayList),
                new FragmentArtists(context , arrayList),
                new FragmentFolders(context)
        };

        return fragments[position];

    }



    @Override
    public int getItemCount() {
        return totalPage ;
    }

}
