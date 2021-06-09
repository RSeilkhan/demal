package com.rakhatali.demal.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakhatali.demal.BreathActivity;
import com.rakhatali.demal.MainActivity;
import com.rakhatali.demal.R;
import com.rakhatali.demal.models.User;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;


public class MoreFragment extends Fragment {

    private TextView userName, listenedNumber, listenedMinute, daysRegist;
    private ImageButton settings_btn;
    private Button breathBtn, emailVerify , openWelcome, diary;
    private ImageView profile_image;
    private GoogleSignInAccount signInAccount;
    private FirebaseUser firebaseUser;
    private LinearLayout emailLinear, notLoggedLinear, gymsLinear, countNumbers;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        initViews(view);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);

        if(firebaseUser !=null){
            userData();
        }else{
            userDataLogless();
        }

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new SettingsFragment())
//                        .addToBackStack(null)
//                        .commit();
                newMusic();
            }
        });
//        if(signInAccount == null){
//            Intent intent = new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }else if(firebaseUser == null){
//            Intent intent = new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }

        breathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BreathActivity.class));
            }
        });

        diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DiaryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void userDataLogless() {
        gymsLinear.setVisibility(View.GONE);
        settings_btn.setVisibility(View.GONE);
        profile_image.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
        countNumbers.setVisibility(View.GONE);
        notLoggedLinear.setVisibility(View.VISIBLE);

        openWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });

    }

    private void initViews(View view) {
        settings_btn = view.findViewById(R.id.settingsBtn);
        profile_image = view.findViewById(R.id.profileImage);
        userName = view.findViewById(R.id.userName);
        listenedNumber = view.findViewById(R.id.listenedTimeNumber);
        listenedMinute = view.findViewById(R.id.listenedTimeMinute);
        daysRegist = view.findViewById(R.id.daysRegist);
        breathBtn = view.findViewById(R.id.breathing_btn);
        emailVerify = view.findViewById(R.id.email_verify);
        emailLinear = view.findViewById(R.id.email_linear);
        notLoggedLinear = view.findViewById(R.id.not_logged_linear);
        gymsLinear = view.findViewById(R.id.list_gyms);
        countNumbers = view.findViewById(R.id.countNumbers);
        openWelcome = view.findViewById(R.id.openWelcome);
        diary = view.findViewById(R.id.diary_btn);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(firebaseUser!=null){
            if(firebaseUser.isEmailVerified()){
                emailLinear.setVisibility(View.VISIBLE);
                emailVerify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), R.string.link_to_verify_email_sent , Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), getString(R.string.email_verification_error) + e.getMessage() , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }else{
                emailLinear.setVisibility(View.GONE);
            }
        }else{
            emailLinear.setVisibility(View.GONE);

        }

    }

    private void userData(){
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext()==null){
                    return;
                }
                User user = snapshot.getValue(User.class);

                userName.setText(user.getUserName());
                listenedNumber.setText(String.valueOf(user.getListenedCount()));
                listenedMinute.setText(String.valueOf(user.getListenedMinutes()/60));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    daysRegist.setText(daysCalc(firebaseUser));
                }else{
                    daysRegist.setText("-");
                }

                String profileImageUrl = user.getImageUrl();
                if (!profileImageUrl.isEmpty()) {
                    Glide.with(getContext()).load(profileImageUrl).into(profile_image);
                }else{
                    Glide.with(getContext()).load(R.mipmap.island_back).into(profile_image);
                    System.out.println("IAMGE IS EMPRT");
                }

//                if (!profileImageUrl.isEmpty()) {
//                    if(profileImageUrl.contains("gs://demal")){
//                        profileImageUrl =  profileImageUrl.substring(profileImageUrl.indexOf("ProfileImages"));
//                        storageRef.child(profileImageUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Glide.with(getContext()).load(uri).into(profile_image);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Glide.with(getContext()).load(R.mipmap.island_back).into(profile_image);
//                            }
//                        });
//                    }else{
//                        Glide.with(getContext()).load(profileImageUrl).into(profile_image);
//                    }
//                }else{
//                    Glide.with(getContext()).load(R.mipmap.island_back).into(profile_image);
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String daysCalc(FirebaseUser user){
        Date date= new Timestamp(user.getMetadata().getCreationTimestamp());
        long days = ChronoUnit.DAYS.between(LocalDate.parse(date.toString().substring(0, 10)), LocalDate.now());
        return String.valueOf(days);
    }

//    public void changeFirebase(){
//        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Meditations");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    AudioFile audioFile = snapshot.getValue(AudioFile.class);
//
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("listenCount",  0);
//                    reference.child(audioFile.getId()).updateChildren(hashMap);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public void newMusic(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sleeps");
        String musicID = reference.push().getKey();
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Sleeps").child(musicID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", musicID);
        hashMap.put("name", "todo");
        hashMap.put("description", "todo");
        hashMap.put("sleepUrl", "todo");
        hashMap.put("imageUrl", "todo");
        hashMap.put("listenCount", 0);
        hashMap.put("type", "Sleeps");
        hashMap.put("categoryId", Arrays.asList("-MJfmRndWT0cAYcqHMNo"));
        reference2.setValue(hashMap);
    }
//
//    public void newMusic1(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
//        String musicID = reference.push().getKey();
//        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Categories").child("Sleeps").child(musicID);
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("id", musicID);
//        hashMap.put("name", "todo");
//        reference2.setValue(hashMap);
//    }
}