package com.abapp.soundplay.Adapter;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;



import androidx.recyclerview.widget.DiffUtil;


import com.abapp.soundplay.Model.SongsInfo;

import java.util.List;

public class SongInfoDiffCallback extends DiffUtil.Callback {

    private final ArrayList<SongsInfo> oldList;
    private final ArrayList<SongsInfo> newList;

    public SongInfoDiffCallback(ArrayList<SongsInfo> oldList, ArrayList<SongsInfo> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getUniqueID() == newList.get(newItemPosition).getUniqueID();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
