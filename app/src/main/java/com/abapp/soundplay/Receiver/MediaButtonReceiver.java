package com.abapp.soundplay.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;


public class MediaButtonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null && event.getAction() == KeyEvent.ACTION_UP) {

                Log.i("TAG", "onReceive: MediaButtonReceiver " + event.getKeyCode());

                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        // Play or pause the music here
//                        context.sendBroadcast(new Intent("TRACKS_TRACKS").putExtra("action_name", "" + Action.ACTION_PLAY_PAUSE));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        // Skip to next track here
//                        context.sendBroadcast(new Intent("TRACKS_TRACKS").putExtra("action_name", "" + Action.ACTION_NEXT));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        // Skip to previous track here
//                        context.sendBroadcast(new Intent("TRACKS_TRACKS").putExtra("action_name", "" + Action.ACTION_PREVIOUS));
                        break;
                }
            }
        } else if ("ACTION_PREVIOUS".equals(intent.getAction())) {
            // Skip to previous track here
//            context.sendBroadcast(new Intent("TRACKS_TRACKS").putExtra("action_name", "" + Action.ACTION_PREVIOUS));
        } else if ("ACTION_NEXT".equals(intent.getAction())) {
            // Skip to next track here
//            context.sendBroadcast(new Intent("TRACKS_TRACKS").putExtra("action_name", "" + Action.ACTION_NEXT));
        }

    }
}