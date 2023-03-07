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
import android.widget.Toast;

import com.abapp.soundplay.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS , WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.setFlags(0 , WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if(checkPermission()) postDelay(1500);
        else ActivityCompat.requestPermissions(SplashActivity.this , new String[]{READ_EXTERNAL_STORAGE } , PERMISSION_REQUEST_CODE);

    }


    public void postDelay(int time){
        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();

        },time);
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
                postDelay(1000);
            } else {
                Toast.makeText(SplashActivity.this, "Give Permission to use App!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
