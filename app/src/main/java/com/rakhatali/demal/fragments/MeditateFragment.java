package com.rakhatali.demal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakhatali.demal.adapters.AudioFileAdapter;
import com.rakhatali.demal.adapters.CategoryAdapter;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.models.Category;
import com.rakhatali.demal.R;

import java.util.ArrayList;
import java.util.List;

public class MeditateFragment extends Fragment implements CategoryAdapter.OnTextClickListener {

    private List<AudioFile> meditates ;
    private List<Category> categories ;

    private AudioFileAdapter audioFileAdapter;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meditate, container, false);

        RecyclerView recyclerView_meditate = view.findViewById(R.id.recycler_view_meditate);
        recyclerView_meditate.setHasFixedSize(true);
        recyclerView_meditate.setLayoutManager(new GridLayoutManager(getContext(), 2));
        meditates = new ArrayList<>();
        audioFileAdapter = new AudioFileAdapter(getContext(), meditates, "Meditations");
        recyclerView_meditate.setAdapter(audioFileAdapter);

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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories").child("Meditations");
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

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Meditations");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meditates.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    AudioFile audioFile = snap.getValue(AudioFile.class);
                        meditates.add(audioFile);
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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Meditations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meditates.clear();

                for(DataSnapshot snap : snapshot.getChildren()){
                    AudioFile audioFile = snap.getValue(AudioFile.class);
                    assert audioFile != null;
                    if(audioFile.getCategoryId().contains(id)){
                        meditates.add(audioFile);
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