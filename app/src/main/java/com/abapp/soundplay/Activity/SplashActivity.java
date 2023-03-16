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
import android.os.AsyncTask;
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


import java.util.ArrayList;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    TextView pleaseWait;
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
        new getListAndStart().execute();
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




    @SuppressLint("StaticFieldLeak")
    public class getListAndStart extends AsyncTask<Void, Void, ArrayList<SongsInfo>> {

        FetchFileData fetchFileData;

        @Override
        protected void onPreExecute() {
            fetchFileData = new FetchFileData(SplashActivity.this);
            Log.d("TAG", "onPreExecute() called");
            super.onPreExecute();
        }


        @Override
        protected ArrayList<SongsInfo> doInBackground(Void... voids) {
            // Perform your background task here

            return new ArrayList<>(fetchFileData.fetchFile(Environment.getExternalStorageDirectory() , false , false , true));
        }


        @Override
        protected void onPostExecute(ArrayList<SongsInfo> arrayList) {
            Log.d("TAG", "onPostExecute() called with: arrayList = [" + arrayList.size() + "]");
            super.onPostExecute(arrayList);

            run = false;
            Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
            homeIntent.putParcelableArrayListExtra("arrayList", arrayList);
            startActivity(homeIntent);
            finish();
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
