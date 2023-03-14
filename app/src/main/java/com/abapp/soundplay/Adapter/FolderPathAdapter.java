package com.abapp.soundplay.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.R;

import java.util.List;


public class FolderPathAdapter extends RecyclerView.Adapter<FolderPathAdapter.ViewHolder> {

    private final List<String> mTexts;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewPath);
        }
    }

    public FolderPathAdapter(List<String> texts) {
        mTexts = texts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_folder_path, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mTexts.get(position));
    }

    @Override
    public int getItemCount() {
        return mTexts.size();
    }
}

