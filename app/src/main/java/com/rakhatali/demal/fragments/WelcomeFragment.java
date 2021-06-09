package com.rakhatali.demal.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakhatali.demal.NavActivity;
import com.rakhatali.demal.R;

import java.util.HashMap;


public class WelcomeFragment extends Fragment {

    public static final int GOOGLE_SIGN_IN_CODE = 10005;
    SignInButton signIn;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;
    FirebaseUser firebaseUser;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button registerBtn = view.findViewById(R.id.openRegister);
        Button loginBtn = view.findViewById(R.id.openLogin);
        Button laterBtn = view.findViewById(R.id.loginLater);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        signIn = view.findViewById(R.id.google_button);
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.request_id_token))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(getContext(), signInOptions);
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

        if(firebaseUser != null ){
            startActivity(new Intent(getContext(), NavActivity.class));
            getActivity().finish();
        }else if(signInAccount != null){
            startActivity(new Intent(getContext(),NavActivity.class));
            getActivity().finish();
        }
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = signInClient.getSignInIntent();
                startActivityForResult(sign, GOOGLE_SIGN_IN_CODE);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new RegisterFragment()).commit();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new LoginFragment()).commit();
            }
        });
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NavActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN_IN_CODE){
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount signInAccount = signInTask.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

                        uidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()) {
                                    regInRealTime(firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(),
                                            firebaseUser.getDisplayName(), firebaseUser.getUid());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        startActivity(new Intent(getContext(),NavActivity.class));
                        getActivity().finish();
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void regInRealTime(String email, String imageUrl, String name, String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("email", email);
        hashMap.put("userName", name);
        hashMap.put("password",  "password");
        hashMap.put("listenedCount",  0);
        hashMap.put("listenedMinutes",  0);
        hashMap.put("imageUrl",  imageUrl);
        reference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), R.string.success_register, Toast.LENGTH_SHORT).show();
            }
        });
    }


}