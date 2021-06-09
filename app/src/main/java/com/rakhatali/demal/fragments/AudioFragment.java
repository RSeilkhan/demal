package com.rakhatali.demal.fragments;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.R;
import com.rakhatali.demal.services.OnClearFromRecentService;
import com.rakhatali.demal.services.PlayerService;
import com.rakhatali.demal.utils.ActionPlaying;
import com.rakhatali.demal.utils.CreateNotification;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.rakhatali.demal.NavActivity.bottomSheetBehavior;
import static com.rakhatali.demal.adapters.AudioFileAdapter.globalPosition;
import static com.rakhatali.demal.fragments.MiniPlayerFragment.playPauseBtnMini;
import static com.rakhatali.demal.NavActivity.audioFiles;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_BACK;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_PLAY;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_SKIP;
import static com.rakhatali.demal.utils.ApplicationClass.ACTION_STOP;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AudioFragment extends Fragment implements ActionPlaying, ServiceConnection{

    private ImageView image;
    private TextView name, description, currentTime, totalDuration;
    private ImageButton favorite,download,skip_back, skip_forward;
    public static ImageButton playPauseBtn; //TODO memory leak
    private SeekBar playerSeekBar;
    int position = -1;
    private List<AudioFile> listAudios = new ArrayList<>();
    private Handler handler = new Handler();
    private Thread backThread;
    private PlayerService playerService;
    private FirebaseUser firebaseUser;
    private long start, end;
    private float seconds;
    NotificationManager notifcationManager;
    private Runnable runnable ;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        initViews(view);
        getIntentMethod();
        metaData();
        final String id = audioFiles.get(position).getId();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            isFavorite(id, firebaseUser);
        }else{
            favorite.setVisibility(View.GONE);
        }

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar playerSeekBar, int progress, boolean fromUser) {
                if(playerService != null && fromUser){
                    playerService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar playerSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar playerSeekBar) {

            }
        });

        ImageButton expandBtn= view.findViewById(R.id.expand_btn);

        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED );
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerService.downloadFile(audioFiles.get(position).getAudioUrl());
                download.setVisibility(View.GONE);
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favorite.getTag().equals("favorite")){
                    FirebaseDatabase.getInstance().getReference().child("Favorites").child(firebaseUser.getUid())
                            .child(audioFiles.get(position).getId()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Favorites").child(firebaseUser.getUid())
                            .child(audioFiles.get(position).getId()).removeValue();
                }
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            getContext().registerReceiver(broadcastReceiver, new IntentFilter("AUDIOS_AUDIOS"));
            getContext().startService(new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class));
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                if(playerService != null){
                    int mCurrentPosition = playerService.getCurrentPosition() / 1000;
                    playerSeekBar.setProgress(mCurrentPosition);
                    currentTime.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        };
        Objects.requireNonNull(getActivity()).runOnUiThread(runnable);

        //CreateNotification.createNotification(getContext(), listAudios.get(1), R.drawable.ic_baseline_pause_24, 1, listAudios.size() -1);
        start = System.currentTimeMillis();

        CreateNotification.createNotification(getContext(), listAudios.get(position),
                R.drawable.ic_play, position, listAudios.size()-1);

        return view;
    }

    private void createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "Demal", NotificationManager.IMPORTANCE_LOW);

            notifcationManager = getContext().getSystemService(NotificationManager.class);
            if(notifcationManager != null){
                notifcationManager.createNotificationChannel(channel);
            }

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment childFragment = new MiniPlayerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.mini_player_frag, childFragment).commit();
    }

    @Override
    public void onResume() {
        Intent intent = new Intent(getContext(), PlayerService.class);
        Objects.requireNonNull(getContext()).bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        skipThreadBtn();
        backThreadBtn();
        stopThreadBtn();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        end = System.currentTimeMillis();
        seconds = (end - start) / 1000F;
        incrementCounter(Math.round(seconds));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notifcationManager.cancelAll();
        }
        end = System.currentTimeMillis();
        seconds = (end - start) / 1000F;
        incrementCounter(Math.round(seconds));
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);

        }
        playerService.clearMediaPlayer();
        getContext().unregisterReceiver(broadcastReceiver);
        getContext().unbindService(this);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionName");

            switch (action){
                case ACTION_BACK:
                    backBtnClicked();
                    break;
                case ACTION_PLAY:
                    playPauseBtnClicked();
                    break;
                case ACTION_SKIP:
                    skipBtnClicked();
                    break;
                case ACTION_STOP:
                    stopBtnClicked();
                    break;

            }
        }
    };

    private void playThreadBtn() {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() {
        if(playerService.isPlaying()){
            CreateNotification.createNotification(getContext(), listAudios.get(position),
                    R.drawable.ic_play, position, listAudios.size()-1);


            playPauseBtn.setImageResource(R.drawable.ic_play);
            playPauseBtnMini.setBackgroundResource(R.drawable.ic_play);
            //playerService.showNotification(R.drawable.ic_baseline_play_arrow_24);
            playerService.pause();
            playerSeekBar.setMax(playerService.getDuration()/1000);
            end = System.currentTimeMillis();
            seconds = (end - start) / 1000F;
            incrementCounter(Math.round(seconds));
        }else{
            CreateNotification.createNotification(getContext(), listAudios.get(position),
                    R.drawable.ic_pause, position, listAudios.size()-1);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            playPauseBtnMini.setBackgroundResource(R.drawable.ic_pause);
            //playerService.showNotification(R.drawable.ic_baseline_pause_24);
            playerService.start();
            playerSeekBar.setMax(playerService.getDuration()/1000);

            start = System.currentTimeMillis();

        }
    }

    private void skipThreadBtn() {
        Thread skipThread = new Thread() {
            @Override
            public void run() {
                super.run();
                skip_forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        skipBtnClicked();
                    }
                });
            }
        };
        skipThread.start();
    }

    @Override
    public void skipBtnClicked() {
        if (playerService.ifNull()) {
            int currentPosition = playerService.getCurrentPosition();
            if (currentPosition + 10000 <= playerService.getDuration()) {
                playerService.seekTo(currentPosition + 10000);
                currentTime.setText(formattedTime(playerService.getCurrentPosition() / 1000));

            } else {
                playerService.seekTo(playerService.getDuration());
            }
        }
    }

    private void backThreadBtn() {
        backThread = new Thread(){
            @Override
            public void run() {
                super.run();
                skip_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        backBtnClicked();
                    }
                });
            }
        };
        backThread.start();
    }

    @Override
    public void backBtnClicked() {
        if (playerService.ifNull()) {
            int currentPosition = playerService.getCurrentPosition();
            if (currentPosition - 10000 >= 0) {
                playerService.seekTo(currentPosition - 10000);
                currentTime.setText(formattedTime(playerService.getCurrentPosition() / 1000));
            } else {
                playerService.seekTo(0);
            }
        }
    }

    private void stopThreadBtn() {
        backThread = new Thread(){
            @Override
            public void run() {
                super.run();
                stopBtnClicked();
            }
        };
        backThread.start();
    }

    @Override
    public void stopBtnClicked() {
        if (playerService!=null && !playerService.ifNull()) {
            playerService.pause();
            playerService.stopAudio();
            playerService.release();
            //playerService.cancelNotification();
            notifcationManager.cancelAll();
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                stopBtnClicked();
            }
//            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
//            playPauseBtnMini.setBackgroundResource(R.drawable.ic_play);
        }
    }

    public void visibleDownload(String audioUrl){
        if(playerService.getFilePath(audioUrl).exists()){
            download.setVisibility(View.GONE);
        }else{
            download.setVisibility(View.VISIBLE);
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        String totalOut = minutes + ":" + seconds;
        String totalNew = minutes + ":" + "0" + seconds;
        if(seconds.length() == 1){
            return totalNew;
        }else{
            return totalOut;
        }
    }

    public void incrementCounter(final int sec) {
        if(firebaseUser!=null){
            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                    .child("listenedMinutes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long value = (long) snapshot.getValue();
                    value = value + sec;
                    snapshot.getRef().setValue(value);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getIntentMethod() {

        listAudios = audioFiles;
        position = globalPosition;

        if(listAudios!=null){
            playPauseBtn.setImageResource(R.drawable.ic_pause);
        }

        Intent intent = new Intent(getContext(), PlayerService.class);
        intent.putExtra("servicePosition", position);
        intent.putExtra("audioUrl", audioFiles.get(position).getAudioUrl());
        intent.putExtra("audioName", audioFiles.get(position).getName());
        intent.putExtra("audioImage", audioFiles.get(position).getImageUrl());
        intent.putExtra("audioDesc", audioFiles.get(position).getDescription());
        getActivity().startService(intent);
    }

    private void initViews(View view) {
        image = view.findViewById(R.id.image);
        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);
        playerSeekBar = view.findViewById(R.id.playerSeekBar);
        playPauseBtn = view.findViewById(R.id.playPause);
        currentTime = view.findViewById(R.id.currentTime);
        totalDuration = view.findViewById(R.id.totalDuration);
        favorite = view.findViewById(R.id.favorite);
        skip_back = view.findViewById(R.id.skip_back);
        skip_forward = view.findViewById(R.id.skip_forward);
        download = view.findViewById(R.id.download);
    }

    private void metaData(){
        String audioImageUrl;
        if(listAudios.size()==0){
            audioImageUrl = "https://od.lk/s/NjBfNTY4MTI0MDZf/pexels-ian-beckley-2440079.jpg";
            name.setText(R.string.name);
            description.setText(R.string.desc);
        }else{
            audioImageUrl = listAudios.get(position).getImageUrl();
            description.setText(listAudios.get(position).getDescription());
            name.setText(listAudios.get(position).getName());
        }

        try{
            Glide.with(getContext()).load(audioImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);

        }
        catch(Exception e){
            Glide.with(getContext()).load(R.mipmap.island_back).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);

        }
    }

    private void isFavorite(final String postId, FirebaseUser fireBaseUser){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Favorites")
                .child(fireBaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()){
                    favorite.setImageResource(R.drawable.ic_favorite_pressed);
                    favorite.setTag("favour");
                }else{
                    favorite.setImageResource(R.drawable.ic_favorite);
                    favorite.setTag("favorite");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        PlayerService.MyBinder myBinder = (PlayerService.MyBinder) service;
        playerService = myBinder.getService();
        playerService.setCallBack(AudioFragment.this);
        int durationTotal = playerService.getDuration()/1000;
        visibleDownload(listAudios.get(position).getAudioUrl());
        playerSeekBar.setMax(durationTotal);
        totalDuration.setText(formattedTime(durationTotal));
        playerService.onCompleted();

        //playerService.showNotification(R.drawable.ic_baseline_pause_24);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        playerService = null;
    }


}
