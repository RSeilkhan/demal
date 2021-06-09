package com.rakhatali.demal.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rakhatali.demal.fragments.AudioFragment;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.rakhatali.demal.NavActivity.audioFiles;
import static com.rakhatali.demal.NavActivity.bottomSheetBehavior;


public class AudioFileAdapter extends RecyclerView.Adapter<AudioFileAdapter.ViewHolder> {


    public static int globalPosition = -1;

    private final Context mContext;
    private final List<AudioFile> mAudio;
    private String audioName="";
    public AudioFileAdapter(Context mContext, List<AudioFile> mAudio, String audioName) {
        this.mContext = mContext;
        this.mAudio = mAudio;
        this.audioName = audioName;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_file_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final AudioFile audioFile = mAudio.get(position);
        String audioImageUrl = audioFile.getImageUrl();
        RoundedCorners mRoundCornetTransform = new RoundedCorners(
                45);
        try{

            Glide.with(mContext).load(audioImageUrl).transform(new MultiTransformation(new CenterCrop(), mRoundCornetTransform))
                    .into(holder.audioImage);

        }
        catch(Exception e){
            Glide.with(mContext).load(R.mipmap.island_back).transform(new MultiTransformation(new CenterCrop(), mRoundCornetTransform))
                    .into(holder.audioImage);
        }

        holder.audioName.setText(audioFile.getName());
        holder.audioDescription.setText(audioFile.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(position, audioFile);
            }
        });
        holder.audioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(position, audioFile);
            }
        });
    }

    private void start(int position, AudioFile audioFile){
        final Handler handler = new Handler();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        if(globalPosition != position){
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.controls_container,  new AudioFragment()).commit();
            globalPosition = position;
            audioFiles = mAudio;
            if(firebaseUser!=null){
                incrementCounter(reference.child("Users").child(firebaseUser.getUid()).child("listenedCount"),
                        reference.child(audioName).child(audioFile.getId()).child("listenCount")
                );
            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }, 1000);
    }

    public void incrementCounter(DatabaseReference firebase, DatabaseReference firebases) {
        firebase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);

                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Toast.makeText(mContext, R.string.error +" " + error, Toast.LENGTH_SHORT).show();
                }
            }

        });

        firebases.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);

                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Toast.makeText(mContext, R.string.error +" " + error, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    @Override
    public int getItemCount() {
        return mAudio.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView audioName;
        public final TextView audioDescription;
        public final ImageView audioImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioImage = itemView.findViewById(R.id.image);
            audioName = itemView.findViewById(R.id.name);
            audioDescription = itemView.findViewById(R.id.description);
        }
    }

}
