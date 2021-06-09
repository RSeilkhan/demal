package com.rakhatali.demal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakhatali.demal.R;
import com.rakhatali.demal.adapters.AudioFileAdapter;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.models.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class HomeFragment extends Fragment {

    Button backgrounds, backgroundSounds;
    private List<AudioFile> favorites;
    private List<AudioFile> meditations;
    private List<AudioFile> sleeps;
    private List<AudioFile> musics;

    private AudioFileAdapter audioFileAdapter;
    FirebaseUser firebaseUser;
    private List<String> listFavs;
    //https://guides.codepath.com/android/using-the-recyclerview#animators top site

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView recycler_view_favs = view.findViewById(R.id.recycler_view_favs);
        recycler_view_favs.setHasFixedSize(true);
        recycler_view_favs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView recycler_top_meditations = view.findViewById(R.id.recycler_view_top_meditations);
        recycler_top_meditations.setHasFixedSize(true);
        recycler_top_meditations.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView recycler_top_musics = view.findViewById(R.id.recycler_view_top_musics);
        recycler_top_musics.setHasFixedSize(true);
        recycler_top_musics.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView recycler_top_sleeps = view.findViewById(R.id.recycler_view_top_sleeps);
        recycler_top_sleeps.setHasFixedSize(true);
        recycler_top_sleeps.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        favorites = new ArrayList<>();
        audioFileAdapter = new AudioFileAdapter(getContext(),favorites, "Meditations");
        recycler_view_favs.setAdapter(audioFileAdapter);

        meditations = new ArrayList<>();
        audioFileAdapter = new AudioFileAdapter(getContext(),meditations, "Meditations");
        recycler_top_meditations.setAdapter(audioFileAdapter);

        musics = new ArrayList<>();
        audioFileAdapter = new AudioFileAdapter(getContext(),musics, "Musics");
        recycler_top_musics.setAdapter(audioFileAdapter);

        sleeps = new ArrayList<>();
        audioFileAdapter = new AudioFileAdapter(getContext(),sleeps, "Sleeps");
        recycler_top_sleeps.setAdapter(audioFileAdapter);


        if(firebaseUser!=null){
            myFavorites();
            if(listFavs.size()<0){
                view.findViewById(R.id.favs_text).setVisibility(View.GONE);
            }else{
                view.findViewById(R.id.favs_text).setVisibility(View.VISIBLE);
            }
        }
        else{
            view.findViewById(R.id.favs_text).setVisibility(View.GONE);
        }
        readMeditations();
        readMusics();
        readSleeps();

        backgrounds = view.findViewById(R.id.backgrounds);

        backgrounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new BackgroundFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        backgroundSounds = view.findViewById(R.id.back_sounds);

        backgroundSounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new BackSoundsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioFiles");
//        String audId = reference.push().getKey();
//
//        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("AudioFiles").child(audId);
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("id", audId);
//        hashMap.put("name", "");
//        hashMap.put("description", "Какое-то описание про эту медитацию");
//        hashMap.put("imageUrl",  "");
//        hashMap.put("audioUrl",  ""); 
//        hashMap.put("categoryId", Arrays.asList("-MJfmRndWT0cAYcqHMNo"));
//
//        reference2.setValue(hashMap);


        return view;
    }

    private void myFavorites(){
        listFavs = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Favorites")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    listFavs.add(snapshot.getKey());
                }
                readFavorites();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readFavorites(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Meditations");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favorites.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AudioFile post = snapshot.getValue(AudioFile.class);

                    for(String id : listFavs){
                        System.out.println("POST IDS" + post.getId());
                        if(post.getId().equals(id)){
                            favorites.add(post);
                        }
                    }
                }
                audioFileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMeditations(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Meditations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meditations.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    AudioFile audioFile = snap.getValue(AudioFile.class);
                    if(audioFile.getListenCount()>0){
                        meditations.add(audioFile);
                    }
                }
                audioFileAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMusics(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Musics");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musics.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AudioFile audioFile = snapshot.getValue(AudioFile.class);
                    if(audioFile.getListenCount()>0){
                        musics.add(audioFile);
                    }
                }
                audioFileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSleeps(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sleeps");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sleeps.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AudioFile audioFile = snapshot.getValue(AudioFile.class);
                    if(audioFile.getListenCount()>0){
                        sleeps.add(audioFile);
                    }
                }
                audioFileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

