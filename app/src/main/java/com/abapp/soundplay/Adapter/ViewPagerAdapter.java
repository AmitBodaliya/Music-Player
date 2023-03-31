package com.abapp.soundplay.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.abapp.soundplay.Fragment.*;

public class ViewPagerAdapter extends FragmentStateAdapter {

    Context context;
    int totalPage;

    public ViewPagerAdapter(FragmentActivity fm  , Context context , int totalPage ) {
        super(fm);
        this.context = context;
        this.totalPage = totalPage;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment[] fragments  = {
                new FragmentAllSong(),
                new FragmentAlbum(),
                new FragmentArtists(),
                new FragmentFolders()
        };

        return fragments[position];

    }



    @Override
    public int getItemCount() {
        return totalPage ;
    }

}
