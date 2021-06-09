package com.rakhatali.demal.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakhatali.demal.adapters.BackgroundAdapter;
import com.rakhatali.demal.models.Background;
import com.rakhatali.demal.R;
import java.util.ArrayList;
import java.util.List;

public class BackgroundFragment extends Fragment {

    private List<Background> mBack;
    private BackgroundAdapter backgroundAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_background, container, false);

        //getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mBack = new ArrayList<>();
        backgroundAdapter = new BackgroundAdapter(getContext(), mBack);
        mRecyclerView.setAdapter(backgroundAdapter);

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

        readBacks();
        return view;
    }
    private void readBacks(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Backgrounds");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBack.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Background background = snapshot.getValue(Background.class);

                    mBack.add(background);
                }
                backgroundAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

