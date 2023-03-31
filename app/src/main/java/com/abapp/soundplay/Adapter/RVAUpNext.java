package com.abapp.soundplay.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Helper.MusicArt;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;

import java.util.ArrayList;
import java.util.Collections;

public class RVAUpNext extends RecyclerView.Adapter<RVAUpNext.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    Context context;
    ArrayList<SongsInfo> mData;

    ItemClickListener mClickListener;
    int playingSong = -1;

    MusicArt musicArt;

    // data is passed into the constructor
    public RVAUpNext(Context context, ArrayList<SongsInfo> data) {
        this.mData = data;
        this.context = context;

        musicArt = MusicArt.getInstance();
    }


    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_recycler_with_drag, parent, false));
    }


    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        SongsInfo songsInfo = mData.get(position);

        String nameOfSOng = songsInfo.getTitle();

        if (songsInfo.getPath().isDirectory()) {
            holder.myTextView.setText(nameOfSOng);

            holder.imageViewID.setImageResource(R.drawable.baseline_folder_24);

            holder.menuImage.setVisibility(View.GONE);
            holder.playerArtist.setVisibility(View.GONE);
            holder.playerSongLength.setVisibility(View.GONE);

        } else {
            nameOfSOng = nameOfSOng.lastIndexOf(".") > 0 ? nameOfSOng.substring(0, nameOfSOng.lastIndexOf(".")) : nameOfSOng;

            holder.myTextView.setText(nameOfSOng);

//            set song bitmap
            Bitmap testBitmap = musicArt.getAlbumArt(songsInfo, holder.imageViewID);
            if (testBitmap != null) {
                holder.imageViewID.setImageBitmap(testBitmap);
            }


            //set artist
            if (songsInfo.getArtist().equals("")) holder.playerArtist.setText("<unknown>");
            else holder.playerArtist.setText(songsInfo.getArtist());


            //set duration
            if (!songsInfo.getSongLength().equals("")) holder.playerSongLength.setText(songsInfo.getSongLength());

        }


        holder.itemView.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemClick(v, songsInfo, position, mData);
        });

        holder.menuImage.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onMenuClick(v, songsInfo, position, mData);
        });

        if (playingSong == position){
            holder.imageViewID.setImageResource(R.drawable.baseline_audio_wave);
            holder.itemView.setBackgroundResource(R.color.fadeColorSecondary);
        }else holder.itemView.setBackgroundResource(0);

    }


    public void setCurrentSOng(int pos){
        int temp = playingSong;
        playingSong = pos;

        if (temp != -1) notifyItemChanged(temp);

        notifyItemChanged(playingSong);
    }


    //get title first letter
    public String getTitle(int position) {
        return getItem(position).getTitle().substring(0, 1);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }



    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(RVAUpNext.ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundResource(R.color.fadeColorSecondary);
    }

    @Override
    public void onRowClear(RVAUpNext.ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundResource(0);

        if (mClickListener != null) mClickListener.onItemMoved();
        notifyItemChanged(playingSong);
    }




    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        TextView myTextView, playerSongLength, playerArtist;
        ImageView imageViewID, menuImage , moveImageView;


        @SuppressLint("ClickableViewAccessibility")
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.playerTitle);
            imageViewID = itemView.findViewById(R.id.playerImage);
            moveImageView = itemView.findViewById(R.id.moveImageView);
            menuImage = itemView.findViewById(R.id.playerMenu);
            playerArtist = itemView.findViewById(R.id.playerTitleArtist);
            playerSongLength = itemView.findViewById(R.id.lengthOfSong);
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

        void onMenuClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list);
        void onItemMoved();
    }


}