package com.abapp.soundplay.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
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
import com.abapp.soundplay.R;
import com.abapp.soundplay.Model.AlbumInfo;
import com.abapp.soundplay.Model.SongsInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RVAAlbumArtists extends RecyclerView.Adapter<RVAAlbumArtists.ViewHolder> {

    MediaMetadataRetriever metaRetriever;
    private final ArrayList<AlbumInfo> albumInfoArrayList;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    MusicArt musicArt;
    Context context;

    RecyclerView recyclerView;

    //main thread
    private final Handler mainHandler = new Handler(Looper.getMainLooper());


    // data is passed into the constructor
    public RVAAlbumArtists(Context context, RecyclerView recyclerView, ArrayList<AlbumInfo> data ) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.mInflater = LayoutInflater.from(context);
        this.albumInfoArrayList = data;

        musicArt = MusicArt.getInstance();
        this.metaRetriever = new MediaMetadataRetriever();
    }


    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_view_artist, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        AlbumInfo albumInfo = albumInfoArrayList.get(position);

        //set title
        String artistName = albumInfo.getTitle();
        holder.titleName.setText((artistName.isEmpty() ? "<unknown>" : artistName)); //title



        //set song bitmap
        Glide.with(context).clear(holder.titleImage);
        holder.titleImage.setImageResource(R.drawable.baseline_music_note_24);


        //wait 300ms
        new Handler().postDelayed(() -> {
            //check item visible
            if (isPositionVisible(position)) {
                //load in bg
                ExecutorService executorService = Executors.newFixedThreadPool(4);
                executorService.execute(() -> {

                    mainHandler.post(() -> holder.titleImage.setImageResource(R.drawable.baseline_music_note_24));


                    //load image
                    Glide.with(context)
                            .load(musicArt.getAlbumArt(albumInfo.getArrayList().get(0)))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                                    //show in main thread
                                    mainHandler.post(() -> {
                                        //again check is visible
                                        if (isPositionVisible(holder.getAdapterPosition())) {
                                            holder.titleImage.setImageDrawable(resource);
                                            holder.titleImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
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



    @Override
    public void onViewRecycled(@NonNull RVAAlbumArtists.ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.titleImage);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return albumInfoArrayList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleName;
        ImageView titleImage;
        ImageView menuImage;

        ViewHolder(View itemView) {
            super(itemView);
            titleImage = itemView.findViewById(R.id.titleImage);
            titleName = itemView.findViewById(R.id.artistName);
            menuImage = itemView.findViewById(R.id.artistMenu);

            itemView.setOnClickListener(view -> {
                if (mClickListener != null)
                    mClickListener.onItemClick(albumInfoArrayList.get(getAdapterPosition() ).getTitle() , getAdapterPosition() , albumInfoArrayList.get(getAdapterPosition()).getArrayList() );
            });

            menuImage.setOnClickListener(v -> {
                if (mClickListener != null)
                    mClickListener.onMenuClick(v, getAdapterPosition(), albumInfoArrayList.get(getAdapterPosition()).getTitle() , albumInfoArrayList.get(getAdapterPosition()).getArrayList());
            });
        }

    }



    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(String artistName , int position , ArrayList<SongsInfo> list);
        void onMenuClick(View view, int position, String artistName , ArrayList<SongsInfo> list);
    }
}