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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rakhatali.demal.NavActivity;
import com.rakhatali.demal.R;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    boolean check = true;
    TextInputEditText email;
    TextInputEditText password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button login_btn = view.findViewById(R.id.login_btn_frag);
        TextView resetPass = view.findViewById(R.id.reset_password);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        password.setCursorVisible(false);


        auth = FirebaseAuth.getInstance();
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference users = db.getReference("Users");

        ImageButton backBtn = view.findViewById(R.id.back_for_main);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new WelcomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.findViewById(R.id.to_regist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new ResetPasswordFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        RelativeLayout mContainerView = view.findViewById(R.id.container);
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.island_back);
        Bitmap blurredBitmap = blur( getContext(), originalBitmap );
        mContainerView.setBackground(new BitmapDrawable(getResources(), blurredBitmap));

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        
        return view;
    }



    private void login(){
        String email_t = email.getText().toString().trim();
        String password_t = password.getText().toString().trim();

        if(TextUtils.isEmpty(email_t)){
            email.setError(getString(R.string.fill_email));
            check=false;
        }else if(TextUtils.isEmpty(password_t)){
            password.setError(getString(R.string.fill_password));
            check=false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_t).matches()) {
            email.setError(getString(R.string.incorrect_email_format));
            //check = false;
        }

        if(check){
            auth.signInWithEmailAndPassword(email_t, password_t)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(getContext(), NavActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), R.string.authorize_error + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

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