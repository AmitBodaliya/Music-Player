package com.abapp.soundplay.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;
import com.abapp.soundplay.params.Prefs;

public class FragmentSettings extends Fragment {

    LinearLayout openSite;
    LinearLayout reloadSongSetting;


    LiveDataViewModel liveDataViewModel;

    public FragmentSettings() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        Prefs prefs = new Prefs(requireContext());
        liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);


        //refresh list
        openSite = v.findViewById(R.id.openSite);
        reloadSongSetting = v.findViewById(R.id.reloadSongSetting);

        //openSite
        openSite.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amitbodaliya.com"))));

        SwitchCompat switchDirectPlay = v.findViewById(R.id.switchDirectPlay);
        switchDirectPlay.setChecked(prefs.getPrefs(Prefs.directSong, false));
        switchDirectPlay.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setPrefs(Prefs.directSong, isChecked));

        SwitchCompat switchReloadOnOpen = v.findViewById(R.id.switchReloadOnOpen);
        switchReloadOnOpen.setChecked(prefs.getPrefs(Prefs.reloadOnOpen, true));
        switchReloadOnOpen.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setPrefs(Prefs.reloadOnOpen, isChecked));


        reloadSongSetting.setOnClickListener(view -> {
            ((MainActivity) requireContext()).refreshList();
            Toast.makeText(requireContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
        });


        return v;
    }





}