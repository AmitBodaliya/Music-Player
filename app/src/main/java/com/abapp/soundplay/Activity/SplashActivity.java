package com.abapp.soundplay.Activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.abapp.soundplay.Helper.FetchFileData;
import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.Room.Songs.MyDatabaseSongs;
import com.abapp.soundplay.Room.Songs.SongsRepository;
import com.abapp.soundplay.params.Prefs;


import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    TextView pleaseWait;
    private static final int PERMISSION_REQUEST_CODE = 101;

    Prefs prefs;
    SongsRepository songsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefs = new Prefs(this);
        songsRepository = new SongsRepository(MyDatabaseSongs.getDatabase(this));

        //please wait
        pleaseWait = findViewById(R.id.pleaseWait);
        showProgress(0);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS , WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.setFlags(0 , WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if(checkPermission()) postDelay();
        else ActivityCompat.requestPermissions(SplashActivity.this , new String[]{READ_EXTERNAL_STORAGE } , PERMISSION_REQUEST_CODE);

    }


    public void postDelay(){
        AtomicBoolean startActivity = new AtomicBoolean(false);

        FetchFileData fetchFileData = new FetchFileData(this);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (songsRepository.itemCount() == 0){

                ArrayList<SongsInfo> arrayList =  new ArrayList<>(fetchFileData.fetchFile(Environment.getExternalStorageDirectory() , false , false , true));
                songsRepository.insertAll(arrayList);

                run = false;
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();


            }else{
                startActivity.set(true);
            }
        });

        new Handler().postDelayed(() -> {
            if (startActivity.get()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },1700);

    }



    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();

        } else {
            int result = ContextCompat.checkSelfPermission(SplashActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(SplashActivity.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("TAG" , grantResults[0] + " " + grantResults.length );
        int[] ints = {0};
        super.onRequestPermissionsResult(requestCode, permissions, ints );

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                postDelay();
            } else {
                Toast.makeText(SplashActivity.this, "Give Permission to use App!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }




    boolean run = true;
    @SuppressLint("SetTextI18n")
    private void showProgress(int i) {
        if (run) {
            String[] array = {"loading.", "loading..", "loading..."};
            pleaseWait.setText("" + array[i % 3]);

            new Handler().postDelayed(() -> showProgress(i + 1), 1000);
        }

    }


}
