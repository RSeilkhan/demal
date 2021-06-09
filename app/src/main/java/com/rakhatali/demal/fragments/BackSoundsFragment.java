package com.rakhatali.demal.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rakhatali.demal.adapters.BackSoundsAdapter;

import com.rakhatali.demal.models.BackSound;
import com.rakhatali.demal.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackSoundsFragment extends Fragment implements BackSoundsAdapter.OnMusicClickListener{

    private MediaPlayer mediaPlayer;
    private List<BackSound> backSoundList;
    private BackSoundsAdapter backSoundsAdapter;
    private int prevPos =-1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_back_sounds, container, false);

        //getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        backSoundList = new ArrayList<>();
        backSoundsAdapter = new BackSoundsAdapter(getContext(), backSoundList,this);
        recyclerView.setAdapter(backSoundsAdapter);
        SeekBar playerSeekBar = view.findViewById(R.id.volume_seekbar);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setVolume(0.5f, 0.5f);

        ImageButton backBtn = view.findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        playerSeekBar.setProgress(50);
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar playerSeekBar, int progress, boolean isFromUser) {
                float volume = progress/ 100f;
                if(mediaPlayer!=null)
                    mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar playerSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar playerSeekBar) {

            }
        });
        readBackSounds();
        return view;
    }

    private void readBackSounds(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BackSounds");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                backSoundList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BackSound backSound = snapshot.getValue(BackSound.class);

                    backSoundList.add(backSound);
                }
                backSoundsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMusicClick(int position, String backUrl, File temp) {

        if(position!=prevPos){
            if(mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            prevPos = position;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if(!temp.exists()) {
                try {
                    mediaPlayer.setDataSource(getContext(), Uri.parse(backUrl));
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    mediaPlayer.setDataSource(temp.toString());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }else{
                mediaPlayer.start();
            }
        }
    }
}


