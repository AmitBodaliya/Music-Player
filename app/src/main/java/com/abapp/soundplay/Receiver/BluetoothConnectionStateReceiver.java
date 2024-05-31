package com.abapp.soundplay.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

public class BluetoothConnectionStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG", "onReceive: BluetoothConnectionStateReceiver"  );
        if (Objects.equals(intent.getAction(), Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
                Log.d("TAG", "onReceive() called with: context = [" + context + "], intent = [" + state + "]");
            if (state == 0) {
                // Headset is unplugged, pause playback
                // Pause your music playback here
            } else if (state == 1) {
                // Headset is unplugged, pause playback
                // Pause your music playback here
            }
        }
    }
}
