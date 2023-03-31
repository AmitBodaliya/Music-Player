package com.abapp.soundplay.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.abapp.soundplay.Adapter.ViewPagerAdapter;
import com.abapp.soundplay.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FragmentLibrary extends Fragment {

    View view;
    Context context;

    String[] tabTitle = {"Songs", "Album", "Artists", "Folder"};

    //ints
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;


    public FragmentLibrary() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library, container, false);
        context = requireContext();

        Log.i("TAG", "onCreateView: Library");

        tabLayout =  view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager);


        int id = viewPager2.getCurrentItem();
        viewPagerAdapter = new ViewPagerAdapter((FragmentActivity) context, context, tabTitle.length);


        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.setCurrentItem(id, false);
        viewPager2.setOffscreenPageLimit(tabTitle.length - 1);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(tabTitle[position])).attach();


        return view;
    }


}