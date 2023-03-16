package com.abapp.soundplay.Notification;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.abapp.soundplay.Model.SongsInfo;
import com.abapp.soundplay.R;
import com.abapp.soundplay.Receiver.Action;
import com.abapp.soundplay.Receiver.NotificationActionService;


public class CreateNotification {


    public static Notification notification;

    public static void createNotification(Context context, SongsInfo songsInfo, int playButton, int pos, int size) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            Bitmap icon = songsInfo.getBitmapImage();

            PendingIntent pendingIntentPrevious;
            int drw_previous;

            if (pos == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(Action.ACTION_PREVIOUS);
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_IMMUTABLE);
                drw_previous = R.drawable.baseline_skip_previous_24;
            }

            Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(Action.ACTION_PLAY_PAUSE);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_IMMUTABLE);

            PendingIntent pendingIntentNext;
            int drw_next;

            if (pos == size) {
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionService.class).setAction(Action.ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_IMMUTABLE);
                drw_next = R.drawable.baseline_skip_next_24;
            }

            // Get the duration of the media item
//            MediaMetadataCompat metadata = mediaSessionCompat.getController().getMetadata();
//            long duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);

//            MediaControllerCompat mediaController = mediaSessionCompat.getController();
//            long progress = mediaController.getPlaybackState().getPosition();


            //create notification
            notification = new NotificationCompat.Builder(context, Action.CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_music_note_24)
                    .setContentTitle(songsInfo.getTitle().substring(0, songsInfo.getTitle().lastIndexOf(".")))
                    .setContentText(songsInfo.getArtist())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(playButton, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();


            notificationManagerCompat.notify(1, notification);



        }
    }
}