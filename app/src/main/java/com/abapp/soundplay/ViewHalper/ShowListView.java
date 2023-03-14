package com.abapp.soundplay.ViewHalper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;

import java.util.ArrayList;

public class ShowListView {


    Context context;
    Activity activity;
    MainActivity mainActivity;

    public ShowListView(Context context , MainActivity mainActivity, String title, ArrayList<SongsInfo> arrayList) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.activity = (Activity) context;

        showDialog(title , arrayList);
    }



    //searchFunction
    @SuppressLint({"ResourceAsColor", "SetTextI18n", "DefaultLocale"})
    void showDialog(String title, ArrayList<SongsInfo> arrayList) {
        Rect displayRectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);


        final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.FullScreenAlertDialogStyle);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialog_list_view, null);

        mView.setMinimumWidth((int) (displayRectangle.width() * 1f));
        mView.setMinimumHeight((int) (displayRectangle.height() * 1f));

        ImageView backUpNext = mView.findViewById(R.id.backUpNext);
        TextView textView = mView.findViewById(R.id.titleUpNext);
        RecyclerView recyclerView = mView.findViewById(R.id.upNextRecyclerView);


        textView.setText(title);


        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);


        //ready list show in recycler view view
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, arrayList);
        adapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                mainActivity.onItemClick(view, songsInfo, position, list);
                alertDialog.dismiss();
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                mainActivity.onMenuClick(view, songsInfo , position, list);
            }
        });
        recyclerView.setAdapter(adapter);

        backUpNext.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        }

        alertDialog.show();


    }


}
