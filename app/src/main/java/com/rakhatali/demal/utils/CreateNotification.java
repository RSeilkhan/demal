package com.rakhatali.demal.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rakhatali.demal.R;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.services.NotificationActionService;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_BACK;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_PLAY;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_SKIP;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_STOP;
import static com.rakhatali.demal.utils.ApplicationClass.CHANNEL_ID_1;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel1";

    public static Notification notification;

    public static void createNotification(final Context context, final AudioFile audioFile, final int playPauseBtn, int pos, int size){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            final MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            final int drw_back, drw_skip, drw_stop;
            Intent intentBack = new Intent(context, NotificationActionService.class).setAction(ACTION_BACK);
            final PendingIntent pendingIntentBack = PendingIntent.getBroadcast(context, 0, intentBack, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_back = R.drawable.ic_back_ten;

            Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            final PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentSkip = new Intent(context, NotificationActionService.class).setAction(ACTION_SKIP);
            final PendingIntent pendingIntentSkip = PendingIntent.getBroadcast(context, 0, intentSkip, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_skip = R.drawable.ic_skip_ten;

            Intent intentStop = new Intent(context, NotificationActionService.class).setAction(ACTION_STOP);
            final PendingIntent pendingIntentStop = PendingIntent.getBroadcast(context, 0, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_stop = R.drawable.ic_baseline_stop_24;

            Glide.with(context).asBitmap().load(audioFile.getImageUrl()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    notification = new NotificationCompat.Builder(context, CHANNEL_ID_1)
                            .setSmallIcon(R.drawable.ic_play)
                            .setLargeIcon(bitmap)
                            .setContentTitle(audioFile.getName())
                            .setContentText(audioFile.getDescription())
                            .addAction(drw_back, "Back", pendingIntentBack)
                            .addAction(playPauseBtn, "Play", pendingIntentPlay)
                            .addAction(drw_skip, "Skip", pendingIntentSkip)
                            .addAction(drw_stop, "Stop", pendingIntentStop)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                    .setShowActionsInCompactView(0, 1, 2)
                                    .setMediaSession(mediaSessionCompat.getSessionToken()))
                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .setOnlyAlertOnce(true)

                            //.setContentIntent(contentIntent)
                            .setOngoing(true)
                            .setShowWhen(false)
                            //.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .build();
                    //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManagerCompat.notify(0,notification);
                }
                @Override
                public void onLoadCleared(Drawable placeholder) {
                }
            });

        }
    }

}
