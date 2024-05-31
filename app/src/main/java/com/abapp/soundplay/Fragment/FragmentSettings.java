package com.abapp.soundplay.Fragment;

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
        reloadSongSetting = v.findViewById(R.id.reloadSongSetting);
        SwitchCompat switchListRefresh = v.findViewById(R.id.switchListRefresh);
        switchListRefresh.setChecked(prefs.getPrefs(Prefs.splashSongLoad, false));
        switchListRefresh.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setPrefs(Prefs.splashSongLoad, isChecked));

        SwitchCompat switchDirectPlay = v.findViewById(R.id.switchDirectPlay);
        switchDirectPlay.setChecked(prefs.getPrefs(Prefs.directSong, false));
        switchDirectPlay.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setPrefs(Prefs.directSong, isChecked));


        reloadSongSetting.setOnClickListener(view -> {
            ((MainActivity) requireContext()).refreshList();
            Toast.makeText(requireContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
        });



        LinearLayout nestedScrollView = v.findViewById(R.id.subScrollView);

        for (int i = 0; i < nestedScrollView.getChildCount(); i++) {
            View childView = nestedScrollView.getChildAt(i);
            if (childView instanceof CardView) {
                CardView cardView = (CardView) childView;
                final int position = i;
                cardView.setOnClickListener(v1 -> {
                    if(prefs.setPrefs(Prefs.customTheme, "" + position)){
                        ((MainActivity) requireContext()).restartApp();
                    }
                });
            }
        }




        return v;
    }





}