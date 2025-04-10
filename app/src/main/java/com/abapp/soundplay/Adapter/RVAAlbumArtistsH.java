package com.abapp.soundplay.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Helper.MusicArt;
import com.abapp.soundplay.Model.AlbumInfo;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;

import java.util.ArrayList;

public class RVAAlbumArtistsH extends RecyclerView.Adapter<RVAAlbumArtistsH.ViewHolder> {

    MediaMetadataRetriever metaRetriever;
    private final ArrayList<AlbumInfo> albumInfoArrayList;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    MusicArt musicArt;


    // data is passed into the constructor
    public RVAAlbumArtistsH(Context context, ArrayList<AlbumInfo> data ) {
        this.mInflater = LayoutInflater.from(context);
        this.albumInfoArrayList = data;

        musicArt = MusicArt.getInstance();
        this.metaRetriever = new MediaMetadataRetriever();
    }


    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_view_artist_h, parent, false);
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
        Bitmap testBitmap = musicArt.getAlbumArt(albumInfo.getArrayList().get(0));
        if (testBitmap != null) holder.titleImage.setImageBitmap(testBitmap);
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