package com.rakhatali.demal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakhatali.demal.R;
import com.rakhatali.demal.adapters.DiaryAdapter;
import com.rakhatali.demal.models.Diary;

import java.util.ArrayList;
import java.util.List;


public class DiaryFragment extends Fragment implements DiaryAdapter.OnDiaryClickListener{

    private List<Diary> diaryList;
    private DiaryAdapter diaryAdapter;
    private FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        RecyclerView recyclerView_diary = view.findViewById(R.id.recycler_view_diary);
        recyclerView_diary.setHasFixedSize(true);
        recyclerView_diary.setLayoutManager(new LinearLayoutManager(getContext()));
        diaryList = new ArrayList<>();
        diaryAdapter = new DiaryAdapter(getContext(), diaryList, this);
        recyclerView_diary.setAdapter(diaryAdapter);
        mUser=FirebaseAuth.getInstance().getCurrentUser();

        FloatingActionButton fab = view.findViewById(R.id.add_diary_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddDiaryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageButton backBtn = view.findViewById(R.id.diary_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MoreFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        readDiaries();
        return view;
    }

    @Override
    public void onDiaryClick(Diary diary, int position) {
        Bundle bundle = new Bundle();
        System.out.println("HERE IS CONTENT" + diary.getContent());
        bundle.putString("diaryTitle", diary.getTitle());
        bundle.putString("diaryContent", diary.getContent());
        bundle.putLong("diaryDate", diary.getAddedDate());
        bundle.putInt("diaryImage", diary.getImageName());
        bundle.putString("diaryId", diary.getId());

        DiaryDetailFragment diaryDetailFragment = new DiaryDetailFragment();
        diaryDetailFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,  diaryDetailFragment).commit();
    }

    private void readDiaries() {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Diaries").child(mUser.getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diaryList.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Diary diary = snap.getValue(Diary.class);
                    System.out.println("DIARY IMAGE " + diary.getImageName());
                    diaryList.add(diary);
                }
                diaryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}