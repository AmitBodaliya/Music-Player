package com.abapp.soundplay.ViewHalper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;

import java.util.ArrayList;

public class SearchView {

    Context context;
    Activity activity;
    ArrayList<SongsInfo> arrayList;
    MainActivity mainActivity;


    RecyclerView recyclerViewSearch;
    RecyclerViewAdapter adapter;
    ArrayList<SongsInfo> searchResultList;

    //alert dialog.
    AlertDialog alertDialog;

    public SearchView(Context context,MainActivity mainActivity, ArrayList<SongsInfo> arrayList) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.activity = (Activity) context;
        this.arrayList = arrayList;

    }


    //searchFunction
    @SuppressLint({"ResourceAsColor", "SetTextI18n", "DefaultLocale"})
    public void showDialog() {
        Rect displayRectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);


        final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.FullScreenAlertDialogStyle);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialog_search_box, null);

        mView.setMinimumWidth((int) (displayRectangle.width() * 1f));
        mView.setMinimumHeight((int) (displayRectangle.height() * 1f));

        EditText editTextSearch = mView.findViewById(R.id.editTextSearch);
        ImageView backToHome = mView.findViewById(R.id.backSearch);
        recyclerViewSearch = mView.findViewById(R.id.searchRecyclerView);

        showSearchResult(arrayList);

        alert.setView(mView);
        alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

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


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        }

        backToHome.setOnClickListener(v -> alertDialog.dismiss());


        alertDialog.show();

        editTextSearch.requestFocus();
        editTextSearch.postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(editTextSearch, 0);
        },200);


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
                mainActivity.onItemClick(view, songsInfo, 0, newList);

                alertDialog.dismiss();
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                mainActivity.onMenuClick(view, songsInfo , position, list);
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
