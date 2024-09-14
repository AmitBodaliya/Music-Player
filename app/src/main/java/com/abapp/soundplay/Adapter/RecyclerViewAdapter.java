package com.abapp.soundplay.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Helper.MusicArt;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.databinding.ItemViewRecyclerBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  implements FastScrollRecyclerView.SectionedAdapter{

    Context context;
    ArrayList<SongsInfo> mData;
    ItemClickListener mClickListener;

    MusicArt musicArt;

    RecyclerView recyclerView;

    int lastPos = -1;


    //main thread
    private final Handler mainHandler = new Handler(Looper.getMainLooper());


    // data is passed into the constructor
    public RecyclerViewAdapter(Context context) {
        this.mData = new ArrayList<>();
        this.context = context;

        musicArt = MusicArt.getInstance();
    }

    // data is passed into the constructor
    public RecyclerViewAdapter(RecyclerView r, Context context) {
        this.recyclerView= r;
        this.mData = new ArrayList<>();
        this.context = context;

        musicArt = MusicArt.getInstance();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<SongsInfo> data){
        this.mData = data;
        notifyDataSetChanged();
    }



    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemViewRecyclerBinding binding = ItemViewRecyclerBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }


    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        SongsInfo songsInfo = mData.get(position);

        String nameOfSOng = songsInfo.getTitle1();

        if (songsInfo.getPath().isDirectory()) {
            holder.binding.playerTitle.setText(nameOfSOng);
            holder.binding.playerImage.setImageResource(R.drawable.baseline_folder_24);
            holder.binding.layoutPlayerImage.setBackgroundResource(0);
            holder.binding.playerMenu.setVisibility(View.GONE);
            holder.binding.playerTitleArtist.setVisibility(View.GONE);
            holder.binding.lengthOfSong.setVisibility(View.GONE);

        } else {
            holder.binding.playerImage.setBackgroundResource(R.color.colorSecondaryVar);
            nameOfSOng = nameOfSOng.lastIndexOf(".") > 0 ? nameOfSOng.substring(0, nameOfSOng.lastIndexOf(".")) : nameOfSOng;

            holder.binding.playerTitle.setText(nameOfSOng);



//            // Clear any previous Glide request
            Glide.with(context).clear(holder.binding.playerImage);
            holder.binding.playerImage.setImageResource(R.drawable.baseline_music_note_24);


            //wait 300ms
            new Handler().postDelayed(() -> {
                //check item visible
                if (isPositionVisible(position)) {
                    //load in bg
                    ExecutorService executorService = Executors.newFixedThreadPool(4);
                    executorService.execute(() -> {

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
                                                holder.binding.playerImage.setImageDrawable(resource);
                                                holder.binding.playerImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale));
                                            }
                                        });

                                    }
                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
                    });

                }

            }, 300);




            //set artist
            if (songsInfo.getArtist().isEmpty()) holder.binding.playerTitleArtist.setText("<unknown>");
            else holder.binding.playerTitleArtist.setText(songsInfo.getArtist());


            //set duration
            if (!songsInfo.getSongLength().isEmpty()) holder.binding.lengthOfSong.setText(songsInfo.getSongLength());

        }


        holder.binding.getRoot().setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemClick(v, songsInfo, position, mData);
        });

        holder.binding.getRoot().setOnLongClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemLongClick(v, songsInfo, position, mData);
            return true;
        });

        holder.binding.playerMenu.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onMenuClick(v, songsInfo, position, mData);
        });

    }


    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.binding.playerImage);
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

    @NonNull
    @Override
    public String getSectionName(int position) {
        return mData.get(position).getTitle1().substring(0, 1).toUpperCase();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewRecyclerBinding binding;

        ViewHolder(ItemViewRecyclerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
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