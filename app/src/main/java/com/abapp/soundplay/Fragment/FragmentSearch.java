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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;

import java.util.ArrayList;

public class FragmentSearch extends Fragment {

    View v;

    Context context;
    EditText editTextSearch;
    ArrayList<SongsInfo> arrayList;


    RecyclerView recyclerViewSearch;
    RecyclerViewAdapter adapter;
    ArrayList<SongsInfo> searchResultList;


    public FragmentSearch() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);

        context = requireContext();
        arrayList = ((MainActivity) requireContext()).getArrayList();


        editTextSearch = v.findViewById(R.id.editTextSearch);
        ImageView backToHome = v.findViewById(R.id.backSearch);

        recyclerViewSearch = v.findViewById(R.id.searchRecyclerView);
        backToHome.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).setDefaultNav();
            editTextSearch.setText("");
        });


        showSearchResult(arrayList);


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editTextSearch.getText().toString().equals("")) {
                    searchProcess(editTextSearch.getText().toString());
                } else {
                    showSearchResult(arrayList);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextSearch.requestFocus();
        editTextSearch.postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(editTextSearch, 0);
        },200);


        return v;
    }




    public void searchProcess(String searchStringText) {

        searchResultList = new ArrayList<>();

        for (SongsInfo songsInfo : arrayList) {
            if (searchText(songsInfo.getTitle(), searchStringText)) {
                searchResultList.add(songsInfo);
            }
        }

        showSearchResult(searchResultList);

    }

    public void showSearchResult(ArrayList<SongsInfo> list) {

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