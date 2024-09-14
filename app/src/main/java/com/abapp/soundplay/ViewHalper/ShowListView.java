package com.abapp.soundplay.ViewHalper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class ShowListView {


    Context context;
    Activity activity;
    MainActivity mainActivity;


    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;



    public ShowListView(Context context , MainActivity mainActivity, String title, ArrayList<SongsInfo> arrayList) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.activity = (Activity) context;

        showDialog(title , arrayList);
    }




    //searchFunction
    @SuppressLint({"ResourceAsColor", "SetTextI18n", "DefaultLocale"})
    void showDialog(String title, ArrayList<SongsInfo> arrayList) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context , R.style.BottomSheetDialogStyle );
        bottomSheetDialog.setContentView(R.layout.dialog_list_view);


        TextView textView = bottomSheetDialog.findViewById(R.id.titleUpNext);
        recyclerView = bottomSheetDialog.findViewById(R.id.upNextRecyclerView);


        assert textView != null;
        textView.setText(title);

        //ready list show in recycler view view
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RecyclerViewAdapter(context);
        adapter.updateList(arrayList);
        adapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                mainActivity.onItemClick(view, songsInfo, position, list);
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                mainActivity.onItemLongClick(view, songsInfo, position, list);
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                mainActivity.onMenuClick(view, songsInfo , position, list);
            }
        });
        recyclerView.setAdapter(adapter);

        bottomSheetDialog.show();
    }

}
