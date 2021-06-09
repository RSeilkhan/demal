package com.rakhatali.demal.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.R;
import com.rakhatali.demal.services.PlayerService;
import com.rakhatali.demal.utils.ActionPlaying;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.rakhatali.demal.adapters.AudioFileAdapter.globalPosition;
import static com.rakhatali.demal.fragments.AudioFragment.playPauseBtn;
import static com.rakhatali.demal.NavActivity.audioFiles;

import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class MiniPlayerFragment extends Fragment implements ActionPlaying, ServiceConnection {

    ImageView audioImage;
    TextView audioDesc, audioName;
    public static Button playPauseBtnMini;
    PlayerService playerService;
    public List<AudioFile> listAudios = new ArrayList<>();
    public int position;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_player, container, false);

        audioDesc = view.findViewById(R.id.audio_desc_miniPlayer);
        audioName = view.findViewById(R.id.audio_name_miniPlayer);
        audioImage = view.findViewById(R.id.audio_image_miniPlayer);
        playPauseBtnMini = (Button) view.findViewById(R.id.play_pause_miniPlayer);
        listAudios = audioFiles;

        Intent intent = new Intent(getContext(), PlayerService.class);
        getActivity().bindService(intent, this, BIND_AUTO_CREATE);
        audioDesc.setSelected(true);

        playPauseBtnMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
//        SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
//        int p = preferences.getInt("positionGlobal", -1);
//        System.out.println("positionGlobal2"+ p);
//        listAudios = audioFiles;
//        //position = globalPosition;
//        position = p;
        position = globalPosition;
        audioDesc.setText(listAudios.get(position).getDescription());
        audioName.setText(listAudios.get(position).getName());

        String audioImageUrl = listAudios.get(position).getImageUrl();

        Glide.with(getContext()).load(audioImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(audioImage);

        playThreadBtn();
//        skipThreadBtn();
//        backThreadBtn();
        stopThreadBtn();
        super.onResume();

        playPauseBtnMini.setBackgroundResource(R.drawable.ic_pause);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //playerService.clearMediaPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(this);

    }



    private void playThreadBtn() {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtnMini.setOnClickListener(new View.OnClickListener() {
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
        if(playerService.ifNull()){
            if(playerService.isPlaying()){
                playPauseBtnMini.setBackgroundResource(R.drawable.ic_play);
                playPauseBtn.setImageResource(R.drawable.ic_play);
                playerService.pause();
            }else{
                playPauseBtnMini.setBackgroundResource(R.drawable.ic_pause);
                playPauseBtn.setImageResource(R.drawable.ic_pause);

                playerService.start();
            }
        }

    }

    @Override
    public void skipBtnClicked() {

    }

    @Override
    public void backBtnClicked() {

    }

    private void stopThreadBtn() {
        Thread backThread = new Thread() {
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

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        PlayerService.MyBinder myBinder = (PlayerService.MyBinder) service;
        playerService = myBinder.getService();
        playerService.setCallBack(MiniPlayerFragment.this);
        if(playerService.ifNull())
            playerService.onCompleted();

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        playerService = null;
    }

}