package com.abapp.soundplay.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RVAAlbumArtistsH;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Model.AlbumInfo;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.ViewModel.LiveDataViewModel;

import java.util.ArrayList;

public class FragmentSearch extends Fragment {

    View v;

    Context context;
    EditText editTextSearch;
    ArrayList<SongsInfo> arrayListAll = new ArrayList<>();
    ArrayList<AlbumInfo> arrayListArtist = new ArrayList<>();


    //list
    TextView emptyListView;
    RecyclerView recyclerViewSearch;
    RecyclerView recyclerViewArtistS;

    RecyclerViewAdapter adapter;
    RVAAlbumArtistsH adapterArtist;

    ArrayList<SongsInfo> searchResultListSong;
    ArrayList<AlbumInfo> searchResultListArtist;

    LiveDataViewModel liveDataViewModel;

    public FragmentSearch() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);

        context = requireContext();


        //get list
        liveDataViewModel = new ViewModelProvider(requireActivity()).get(LiveDataViewModel.class);
        liveDataViewModel.allSongLivaData.observe(getViewLifecycleOwner(), list -> arrayListAll = list);
        liveDataViewModel.artistLivaData.observe(getViewLifecycleOwner(), list -> arrayListArtist = list);


        editTextSearch = v.findViewById(R.id.editTextSearch);
        ImageView backToHome = v.findViewById(R.id.backSearch);

        //recycler view
        emptyListView = v.findViewById(R.id.emptyListView);
        recyclerViewSearch = v.findViewById(R.id.searchRecyclerView);
        recyclerViewArtistS = v.findViewById(R.id.recyclerViewArtistSearch);


        backToHome.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).setDefaultNav();
            editTextSearch.setText("");
        });




        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editTextSearch.getText().toString().isEmpty()) {
                    searchProcess(editTextSearch.getText().toString());
                } else {
                    serRecyclerViewSong(arrayListAll);
                    setRecyclerViewArtist(arrayListArtist);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        //show keyboard
        editTextSearch.requestFocus();
        editTextSearch.postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(editTextSearch, 0);

            //show all list
            serRecyclerViewSong(arrayListAll);
            setRecyclerViewArtist(arrayListArtist);
        },200);


        return v;
    }




    public void searchProcess(String searchStringText) {

        searchResultListSong = new ArrayList<>();
        searchResultListArtist = new ArrayList<>();

        for (SongsInfo songsInfo : arrayListAll) {
            if (searchText(songsInfo.getTitle1(), searchStringText)) {
                searchResultListSong.add(songsInfo);
            }
        }

        for (AlbumInfo albumInfo : arrayListArtist) {
            if (searchText(albumInfo.getTitle(), searchStringText)) {
                searchResultListArtist.add(albumInfo);
            }
        }

        serRecyclerViewSong(searchResultListSong);
        setRecyclerViewArtist(searchResultListArtist);

        if(searchResultListSong.isEmpty() && searchResultListArtist.isEmpty()) emptyListView.setVisibility(View.VISIBLE);
        else emptyListView.setVisibility(View.GONE);


    }





    public void setRecyclerViewArtist(ArrayList<AlbumInfo> arrayList){
        arrayList.sort(AlbumInfo.AppNameComparator);

        //set recycler view;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewArtistS.setLayoutManager(linearLayoutManager);
        adapterArtist = new RVAAlbumArtistsH(requireContext() , arrayList);
        recyclerViewArtistS.setAdapter(adapterArtist);


        adapterArtist.setClickListener(new RVAAlbumArtistsH.ItemClickListener() {
            @Override
            public void onItemClick(String artistName , int position, ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).showSongListDialog(arrayList.get(position).getTitle(), list);

            }

            @Override
            public void onMenuClick(View view, int position, String artistName , ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).albumMenu(view , list);

            }
        });
    }



    public void serRecyclerViewSong(ArrayList<SongsInfo> list) {

        //ready list show in recycler view view
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RecyclerViewAdapter(context, list);
        adapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                ArrayList<SongsInfo> newList = new ArrayList<>();
                newList.add(songsInfo);
                ((MainActivity) requireActivity()).onItemClick(view, songsInfo, 0, newList);

                ((MainActivity) requireActivity()).setDefaultNav();
                editTextSearch.setText("");
            }

            @Override
            public void onItemLongClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                ArrayList<SongsInfo> newList = new ArrayList<>();
                newList.add(songsInfo);
                ((MainActivity) requireActivity()).onItemLongClick(view, songsInfo, 0, newList);

                ((MainActivity) requireActivity()).setDefaultNav();
                editTextSearch.setText("");
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).onMenuClick(view, songsInfo , position, list);
            }

        });
        recyclerViewSearch.setAdapter(adapter);
    }



    // search keyword from a line is give to function
    public static Boolean searchText(String inputString, String findString) {
        inputString = inputString.toLowerCase();
        findString = findString.toLowerCase();

        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.charAt(i) == findString.charAt(0)) {

                if (inputString.length() > i + findString.length()) {
                    if (inputString.substring(i, (i + findString.length())).equals(findString)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}