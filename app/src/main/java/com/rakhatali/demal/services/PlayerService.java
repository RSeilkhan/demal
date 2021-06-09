package com.rakhatali.demal.services;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rakhatali.demal.fragments.AudioFragment;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.NavActivity;
import com.rakhatali.demal.R;
import com.rakhatali.demal.utils.ActionPlaying;
import com.rakhatali.demal.utils.NotificationReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.rakhatali.demal.NavActivity.audioFiles;
import static com.rakhatali.demal.NavActivity.bottomSheetBehavior;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_BACK;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_PLAY;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_SKIP;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_STOP;
import static com.rakhatali.demal.utils.ApplicationClass.CHANNEL_ID_1;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {

    final MyBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    Uri uri;
    int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;
    public File localFile = null;
    Bitmap icon = null;
    //public String audioUrl, audioName, audioImage, audioDesc;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    public class MyBinder extends Binder {
        public PlayerService getService(){
            return PlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int pos = intent.getIntExtra("servicePosition", -3);

        String audioUrl = intent.getStringExtra("audioUrl");
        String actionName = intent.getStringExtra("ActionName");

        playMedia(pos, audioUrl);

        if(actionName != null){
            switch (actionName){
                case ACTION_PLAY:
                    if(actionPlaying != null){
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                case ACTION_SKIP:
                    if(actionPlaying != null){
                        actionPlaying.skipBtnClicked();
                    }
                    break;
                case ACTION_BACK:
                    if(actionPlaying != null){
                        actionPlaying.backBtnClicked();
                    }
                    break;
                case ACTION_STOP:
                    if(actionPlaying != null){
                        actionPlaying.stopBtnClicked();
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int pos, String audioUrl) {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            createMediaPlayer( audioUrl);
            mediaPlayer.start();

        }else{
            createMediaPlayer( audioUrl);
            mediaPlayer.start();
        }
    }

    public File getFilePath(String audioUrl){

        String audioLink = audioUrl;

        audioLink = audioLink.substring(audioLink.lastIndexOf("/"));
        audioLink = audioLink.substring(1, audioLink.indexOf("mp3")+3);
        File dir = new File(this.getApplicationInfo().dataDir, "Demal/AudioFiles");
        if(!dir.exists())
            dir.mkdirs();
        localFile = new File(dir, audioLink);

        return localFile;
    }
    public boolean ifNull(){
        return mediaPlayer != null;
    }
    public void downloadFile(String url){
        new DownloadFileFromURL().execute(url);
    }
    public void start(){
        mediaPlayer.start();
    }
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public void stopAudio(){
        mediaPlayer.stop();
    }
    public void release(){
        mediaPlayer.release();
    }
    public int getDuration(){
        return mediaPlayer.getDuration();
    }
    public void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    void createMediaPlayer(String audioUrl){
        if(getFilePath(audioUrl).exists()){
            uri = Uri.parse(getFilePath( audioUrl).toString());
        }else{
            uri = Uri.parse(audioUrl);
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
    }
    public int getCurrentPosition(){
        if(mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }else{
            return 0;
        }
    }
    public void clearMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();// this will clear memory
            mediaPlayer = null;
        }
    }
    public void pause(){
        mediaPlayer.pause();
    }
    public void onCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (actionPlaying != null){
            //TODO change to navactivity with fragment meditate
            startActivity(new Intent(getApplicationContext(),NavActivity.class));

        }
    }

    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }

    public void cancelNotification(){
        NotificationManagerCompat.from(getApplicationContext()).cancel(0);
    }

    public void showNotification(final int playPauseBtn, String audioImage, final String audioName, final String audioDesc)
    {
        Intent intent = new Intent(this, AudioFragment.class);
        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_BACK);
        final PendingIntent backPendingIntent = PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        final PendingIntent playPendingIntent = PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_SKIP);
        final PendingIntent skipPendingIntent = PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_STOP);
        final PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                Glide.with(getApplicationContext()).asBitmap().load(audioImage).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_1)
                                .setSmallIcon(playPauseBtn)
                                .setLargeIcon(bitmap)
                                .setContentTitle(audioName)
                                .setContentText(audioDesc)
                                .addAction(R.drawable.ic_baseline_fast_rewind_24, "Back", backPendingIntent)
                                .addAction(playPauseBtn, "Play", playPendingIntent)
                                .addAction(R.drawable.ic_baseline_fast_forward_24, "Skip", skipPendingIntent)
                                .addAction(R.drawable.ic_baseline_stop_24, "Stop", stopPendingIntent)
                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setOnlyAlertOnce(true)
                                .setContentIntent(contentIntent)
                                .setOngoing(true)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .build();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0,notification);
                    }
                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                    }
                });

    }
    public class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                OutputStream output = new FileOutputStream(localFile);

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }
    }


}
