package com.abapp.soundplay.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abapp.soundplay.Model.SongsInfo;

import java.util.ArrayList;

public class LiveDataViewModel extends ViewModel {

    public MutableLiveData<ArrayList<SongsInfo>> arrayListMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<SongsInfo>> getLiveList() {
        return arrayListMutableLiveData;
    }


    public void setData(ArrayList<SongsInfo> arrayList){
        Log.i("TAG", "setData: " + arrayList.size());
        arrayListMutableLiveData.setValue(arrayList);
    }

}
