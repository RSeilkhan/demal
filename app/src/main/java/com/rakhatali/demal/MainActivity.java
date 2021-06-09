package com.rakhatali.demal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rakhatali.demal.fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new WelcomeFragment()).commit();

    }
}