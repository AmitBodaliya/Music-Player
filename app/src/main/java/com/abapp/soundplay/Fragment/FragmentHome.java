package com.abapp.soundplay.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.R;


public class FragmentHome extends Fragment {

    View v;

    LinearLayout allSongLayout ,folderLayout, favLayout;
    CardView nowPlaying;


    public FragmentHome() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        allSongLayout = v.findViewById(R.id.allSongLayout);
        folderLayout = v.findViewById(R.id.folderLayout);
        favLayout = v.findViewById(R.id.favLayout);
        nowPlaying = v.findViewById(R.id.nowPlayingLayout);


//        allSongLayout.setOnClickListener(v1 -> ((MainActivity) requireActivity()).setViewPagerItem(1));
//
//        folderLayout.setOnClickListener(v1 -> ((MainActivity) requireActivity()).setViewPagerItem(4));

        favLayout.setOnClickListener(v1 -> ((MainActivity) requireActivity()).showAllFavSOng());

        nowPlaying.setOnClickListener(v1 -> ((MainActivity) requireActivity()).openPlayingSong());


        return v;
    }

}