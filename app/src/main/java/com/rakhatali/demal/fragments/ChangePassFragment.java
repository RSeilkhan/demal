package com.rakhatali.demal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakhatali.demal.R;

import java.util.HashMap;


public class ChangePassFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_pass, container, false);

        Button confirmBtn = view.findViewById(R.id.confirm_btn);
        ImageButton backBtn = view.findViewById(R.id.back_btn);
        final TextInputEditText currentPassword = view.findViewById(R.id.current_pass);
        final TextInputEditText newPassword = view.findViewById(R.id.new_pass);
        final TextInputEditText confirmPassword = view.findViewById(R.id.confirm_pass);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curPass = currentPassword.getText().toString().trim();
                final String newPass = newPassword.getText().toString().trim();
                String confPass = confirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(curPass)) {
                    currentPassword.setError(getString(R.string.fill_current_pass));
                    //check = false;
                } else if (newPass.length() < 5) {
                    newPassword.setError(getString(R.string.password_not_more_5));
                    //check = false;
                } else if (TextUtils.isEmpty(newPass)) {
                    newPassword.setError(getString(R.string.fill_new_password));
                    //check = false;
                } else if (TextUtils.isEmpty(confPass)) {
                    confirmPassword.setError(getString(R.string.confirm_new_password));
                    //check = false;
                }

                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(firebaseUser.getEmail(), curPass);
                if(newPass.equals(confPass)){
                    firebaseUser.reauthenticate(credential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firebaseUser.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            updateProfile(newPass, firebaseUser.getUid());
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.fragment_container, new SettingsFragment())
                                                    .addToBackStack(null)
                                                    .commit();
                                            Toast.makeText(getContext(), R.string.password_changed_success, Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), R.string.password_change_fail, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            currentPassword.setError(getString(R.string.incorrect_current_password));
                        }
                    });
                }else{
                    confirmPassword.setError(getString(R.string.passwords_mismatch));
                }
            }
        });
        
        return view;
    }

    private void updateProfile( String password,  String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("password", password);

        reference.updateChildren(hashMap);

    }

}