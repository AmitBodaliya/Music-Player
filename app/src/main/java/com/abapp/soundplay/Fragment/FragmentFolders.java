package com.abapp.soundplay.Fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abapp.soundplay.Activity.MainActivity;
import com.abapp.soundplay.Adapter.FolderPathAdapter;
import com.abapp.soundplay.Adapter.RecyclerViewAdapter;
import com.abapp.soundplay.Helper.FetchFileData;
import com.abapp.soundplay.R;
import com.abapp.soundplay.Model.SongsInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentFolders extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter ;

    //ints
    File uri;
    RecyclerView recyclerViewPath;
    FolderPathAdapter folderPathAdapter;
    CardView back;

    FetchFileData fetchFileData;
    String defaultFile = Environment.getExternalStorageDirectory() + "";


    public FragmentFolders(){
        this.uri = Environment.getExternalStorageDirectory();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folders, container, false);

        fetchFileData = new FetchFileData(requireContext());

        //ints
        recyclerView = v.findViewById(R.id.recyclerViewFolders);
        back = v.findViewById(R.id.backFolder);
        recyclerViewPath = v.findViewById(R.id.recyclerViewPath);


        if(defaultFile.equals(uri.toString()))  back.setVisibility(View.GONE);
        else back.setVisibility(View.VISIBLE);

        back.setOnClickListener(v1 -> {
            if(!uri.toString().equals(defaultFile) ){
                uri = uri.getParentFile();
                assert uri != null;
                setRecyclerview(uri);
                back.setVisibility(View.VISIBLE);
            }
            else back.setVisibility(View.GONE);
        });



        setRecyclerview(uri);
        return v;
    }


    public void setRecyclerview(File file){

        // set folder path
        List<String> mTexts = new ArrayList<>();
        File tempFile = file;

        while (true){
            assert tempFile != null;
            if (tempFile.equals(Environment.getExternalStorageDirectory())){
                mTexts.add("Storage/");
                break;
            }else {
                mTexts.add(tempFile.getName() + "/");
                tempFile = tempFile.getParentFile();
            }
        }

        Collections.reverse(mTexts);
        recyclerViewPath.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.scrollToPosition(mTexts.size() - 1);
        recyclerViewPath.setLayoutManager(linearLayoutManager);
        folderPathAdapter = new FolderPathAdapter(mTexts);
        recyclerViewPath.setAdapter(folderPathAdapter);


        //set back button
        if(!defaultFile.equals(file.toString())) back.setVisibility(View.VISIBLE);
        else back.setVisibility(View.GONE);


        ArrayList<SongsInfo> mySongDirList = fetchFileData.fetchFile(file, false, true, false);
        ArrayList<SongsInfo> mySongList = fetchFileData.fetchFile(file, true , false , false);

        mySongDirList.addAll(mySongList);

        recyclerViewAdapter = new RecyclerViewAdapter(requireContext() , mySongDirList);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                File tempUri = songsInfo.getPath();
                if(tempUri.isDirectory()){
                    uri = tempUri;
                    setRecyclerview(tempUri);
                }else{
                    ((MainActivity) requireActivity()).onItemClick(view,songsInfo,position , mySongList);
                }
            }

            @Override
            public void onMenuClick(View view, SongsInfo songsInfo, int position, ArrayList<SongsInfo> list) {
                ((MainActivity) requireActivity()).onMenuClick(view, songsInfo ,position, list);
            }
        });
    }

}