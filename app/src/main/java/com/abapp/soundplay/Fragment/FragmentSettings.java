package com.abapp.soundplay.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.abapp.soundplay.Activity.AboutActivity;
import com.abapp.soundplay.Activity.SettingActivity;
import com.abapp.soundplay.Helper.FetchFileData;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class FragmentSettings extends Fragment {

    LinearLayout reloadSongSetting, settingSetting, aboutSetting;


    LiveDataViewModel liveDataViewModel;

    public FragmentSettings() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);


        //refresh list
        reloadSongSetting = v.findViewById(R.id.reloadSongSetting);
        settingSetting = v.findViewById(R.id.settingSetting);
        aboutSetting = v.findViewById(R.id.aboutSetting);


        reloadSongSetting.setOnClickListener(view -> {
            refreshList();
            Toast.makeText(requireContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
        });

        settingSetting.setOnClickListener(view -> startActivity(new Intent(requireActivity(), SettingActivity.class)));

        aboutSetting.setOnClickListener(view -> startActivity(new Intent(requireActivity(), AboutActivity.class)));



        return v;
    }

    public void refreshList(){
        AtomicReference<ArrayList<SongsInfo>> arrayList = new AtomicReference<>();
        Handler handler = new Handler(msg -> {
            // background task is complete, update UI here
            if (arrayList.get() != null) liveDataViewModel.setData(arrayList.get());
            return true;
        });


        FetchFileData fetchFileData = new FetchFileData(requireActivity());
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            arrayList.set(new ArrayList<>(fetchFileData.fetchFile(Environment.getExternalStorageDirectory(), false, false, true)));

            handler.sendEmptyMessage(0);
        });

    }

}