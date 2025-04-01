package com.abapp.soundplay.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Helper.MusicArt;
import com.abapp.soundplay.Model.AlbumInfo;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RAHHorizontal extends RecyclerView.Adapter<RAHHorizontal.ViewHolder> {

    Context context;
    ArrayList<SongsInfo> mData;
    ItemClickListener mClickListener;

    MusicArt musicArt;


    RecyclerView recyclerView;

    //main thread
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // data is passed into the constructor
    public RAHHorizontal(Context context, RecyclerView recyclerView, ArrayList<SongsInfo> data) {
        this.mData = data;
        this.context = context;
        this.recyclerView = recyclerView;

        musicArt = MusicArt.getInstance();
    }


    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_recycler_grid, parent, false));
    }


    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        SongsInfo songsInfo = mData.get(position);

        String nameOfSOng = songsInfo.getTitle1();
        nameOfSOng = nameOfSOng.lastIndexOf(".") > 0 ? nameOfSOng.substring(0, nameOfSOng.lastIndexOf(".")) : nameOfSOng;

        holder.myTextView.setText(nameOfSOng);
        holder.playerArtists.setText(songsInfo.getArtist());


//            set song bitmap

        //set song bitmap
        Glide.with(context).clear(holder.imageViewID);
        holder.imageViewID.setImageResource(R.drawable.baseline_music_note_24);

        //wait 300ms
        new Handler().postDelayed(() -> {
            //check item visible
            if (isPositionVisible(position)) {
                //load in bg
                ExecutorService executorService = Executors.newFixedThreadPool(4);
                executorService.execute(() -> {

                    mainHandler.post(() -> holder.imageViewID.setImageResource(R.drawable.baseline_music_note_24));

                    //load image
                    Glide.with(context)
                            .load(musicArt.getAlbumArt(songsInfo))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                                    //show in main thread
                                    mainHandler.post(() -> {
                                        //again check is visible
                                        if (isPositionVisible(holder.getAdapterPosition())) {
                                            holder.imageViewID.setImageDrawable(resource);
                                            holder.imageViewID.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                                        }
                                    });

                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                });

            }

        }, 200);


        holder.itemView.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemClick(v, songsInfo, position, mData);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemLongClick(v, songsInfo, position, mData);
            return true;
        });

        holder.menuImage.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onMenuClick(v, songsInfo, position, mData);
        });

    }


    @Override
    public void onViewRecycled(@NonNull RAHHorizontal.ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.imageViewID);
    }


    private boolean isPositionVisible(int position) {
        if (recyclerView==null) return false;

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
            return position >= firstVisiblePosition && position <= lastVisiblePosition;
        }
        return false;
    }



    //get title first letter
    public String getTitle(int position) {
        return getItem(position).getTitle1().substring(0, 1);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView, playerArtists;
        ImageView imageViewID, menuImage;

        ViewHolder(View itemView) {
            super(itemView);
            playerArtists = itemView.findViewById(R.id.playerArtists);
            myTextView = itemView.findViewById(R.id.playerTitle);
            imageViewID = itemView.findViewById(R.id.playerImage);
            menuImage = itemView.findViewById(R.id.playerMenu);
        }

    }

    // convenience method for getting data at click position
    SongsInfo getItem(int id) {
        return mData.get(id);
    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list);
        void onItemLongClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list);
        void onMenuClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list);
    }
}