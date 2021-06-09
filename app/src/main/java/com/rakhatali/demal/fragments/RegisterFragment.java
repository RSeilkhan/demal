package com.rakhatali.demal.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rakhatali.demal.NavActivity;
import com.rakhatali.demal.R;

import java.util.HashMap;


public class RegisterFragment extends Fragment {

    private FirebaseAuth auth;
    //boolean check = true;
    private DatabaseReference reference;
    TextInputEditText email,password,userName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button btnRegister = view.findViewById(R.id.register_btn);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        userName = view.findViewById(R.id.userName);

        RelativeLayout mContainerView = view.findViewById(R.id.container);
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.island_back);
        Bitmap blurredBitmap = blur(getContext(), originalBitmap);
        mContainerView.setBackground(new BitmapDrawable(getResources(), blurredBitmap));

        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("ru");
        //FirebaseDatabase db = FirebaseDatabase.getInstance();

        view.findViewById(R.id.back_for_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new WelcomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.findViewById(R.id.to_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        
        return view;
    }


    private void register(){
        final String email_t = email.getText().toString().trim();
        final String password_t = password.getText().toString().trim();
        final String userName_t = userName.getText().toString().trim();

        if (TextUtils.isEmpty(email_t)) {
            email.setError(getString(R.string.fill_email));
            //check = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_t).matches()) {
            email.setError(getString(R.string.incorrect_email_format));
            //check = false;
        } else if (password_t.length() < 5) {
            password.setError(getString(R.string.password_not_more_5));
            //check = false;
        } else if (TextUtils.isEmpty(password_t)) {
            password.setError(getString(R.string.fill_password));
            //check = false;
        } else if (TextUtils.isEmpty(userName_t)) {
            userName.setError(getString(R.string.fill_user_name));
            //check = false;
        }

        auth.createUserWithEmailAndPassword(email_t, password_t)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
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

                        String userId = null;
                        if (firebaseUser != null) {
                            userId = firebaseUser.getUid();
                        }

                        assert userId != null;

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userId);
                        hashMap.put("email", email_t);
                        hashMap.put("userName", userName_t);
                        hashMap.put("password",  password_t);
                        hashMap.put("listenedCount",  0);
                        hashMap.put("listenedMinutes",  0);
                        hashMap.put("imageUrl",  "");

                        reference.setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getContext(), NavActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), R.string.register_error + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }//register()

    public Bitmap blur(Context context, Bitmap image) {
        float bitmap_scale = 0.2f;
        float blur_radius = 15f;

        int width = Math.round(image.getWidth() * bitmap_scale);
        int height = Math.round(image.getHeight() * bitmap_scale);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(blur_radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }
}