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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakhatali.demal.R;
import com.rakhatali.demal.adapters.AudioFileAdapter;
import com.rakhatali.demal.adapters.CategoryAdapter;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.models.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MusicFragment extends Fragment implements CategoryAdapter.OnTextClickListener {

    private List<AudioFile> musics;
    private List<Category> categories ;
    private AudioFileAdapter audioFileAdapter;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);

        RecyclerView recyclerView_music = view.findViewById(R.id.recycler_view_music);
        recyclerView_music.setHasFixedSize(true);
        recyclerView_music.setLayoutManager(new GridLayoutManager(getContext(), 2));
        musics = new ArrayList<>();
        audioFileAdapter = new AudioFileAdapter(getContext(), musics, "Musics");
        recyclerView_music.setAdapter(audioFileAdapter);

        RecyclerView recycler_view_category = view.findViewById(R.id.recycler_view_category);
        recycler_view_category.setHasFixedSize(true);
        recycler_view_category.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categories, this);
        recycler_view_category.setAdapter(categoryAdapter);


        readAudios();
        return view;
    }

    private void readAudios(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories").child("Musics");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Category category = snap.getValue(Category.class);
                    categories.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Musics");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musics.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    AudioFile audioFile = snap.getValue(AudioFile.class);
                    musics.add(audioFile);
                }
                audioFileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onTextClick(final String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Musics");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musics.clear();

                for(DataSnapshot snap : snapshot.getChildren()){
                    AudioFile audioFile = snap.getValue(AudioFile.class);
                    assert audioFile != null;
                    if(audioFile.getCategoryId().contains(id)){
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

    public void newMusic(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Musics");
        String musicID = reference.push().getKey();
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Musics").child(musicID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", musicID);
        hashMap.put("name", "todo");
        hashMap.put("description", "todo");
        hashMap.put("musicUrl", "todo");
        hashMap.put("imageUrl", "todo");
        hashMap.put("categoryId", Arrays.asList("-MJfmRndWT0cAYcqHMNo"));
        reference2.setValue(hashMap);
    }

}