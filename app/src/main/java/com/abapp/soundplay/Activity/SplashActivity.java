package com.abapp.soundplay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import com.abapp.soundplay.MyApplication;
import com.abapp.soundplay.R;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(new MyApplication().applyCustomTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS , WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(0 , WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //check permission and grant permission
        if(checkPermission()) postDelay();
        else requestPermission();

//        generateAndCopyColorXML(this);
    }


    public static void generateAndCopyColorXML(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<resources>\n\n");

        int[][] colorAttrs = {
                {com.google.android.material.R.attr.colorPrimary, Integer.parseInt("colorPrimary")},
                {com.google.android.material.R.attr.colorOnPrimary, Integer.parseInt("colorOnPrimary")},
                {com.google.android.material.R.attr.colorPrimaryContainer, Integer.parseInt("colorPrimaryContainer")},
                {com.google.android.material.R.attr.colorOnPrimaryContainer, Integer.parseInt("colorOnPrimaryContainer")},
                {com.google.android.material.R.attr.colorSecondary, Integer.parseInt("colorSecondary")},
                {com.google.android.material.R.attr.colorOnSecondary, Integer.parseInt("colorOnSecondary")},
                {com.google.android.material.R.attr.colorSurface, Integer.parseInt("colorSurface")},
                {com.google.android.material.R.attr.colorOnSurface, Integer.parseInt("colorOnSurface")},
//                {com.google.android.material.R.attr.colorBackground, Integer.parseInt("colorBackground")},
//                {com.google.android.material.R.attr.statusBarColor, Integer.parseInt("colorStatusBar")},
//                {com.google.android.material.R.attr.navigationBarColor, Integer.parseInt("colorNavigationBar")},
//                {com.google.android.material.R.attr.textColorPrimary, Integer.parseInt("textColorPrimary")},
//                {com.google.android.material.R.attr.textColorSecondary, Integer.parseInt("textColorSecondary")}
        };

        for (int[] attr : colorAttrs) {
            TypedValue typedValue = new TypedValue();
            if (context.getTheme().resolveAttribute(attr[0], typedValue, true)) {
                int color = typedValue.data;
                String hexColor = String.format("#%08X", (color & 0xFFFFFFFF));
                stringBuilder.append("    <color name=\"").append(attr[1]).append("\">")
                        .append(hexColor).append("</color>\n");
            }
        }

        stringBuilder.append("\n</resources>");

        String colorXML = stringBuilder.toString();

        // Copy to Clipboard
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("colors.xml", colorXML);
        clipboard.setPrimaryClip(clip);

        // Show a Toast Message
        Toast.makeText(context, "Colors copied to clipboard!", Toast.LENGTH_SHORT).show();
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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





    public void postDelay(){

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 3100); // 3.1
        // -second delay
    }

}