package com.rakhatali.demal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.rakhatali.demal.fragments.AudioFragment;
import com.rakhatali.demal.fragments.HomeFragment;
import com.rakhatali.demal.fragments.MeditateFragment;
import com.rakhatali.demal.fragments.MoreFragment;
import com.rakhatali.demal.fragments.MusicFragment;
import com.rakhatali.demal.fragments.SleepFragment;
import com.rakhatali.demal.models.AudioFile;

import java.io.File;
import java.util.List;
import java.util.Locale;


public class NavActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private final int REQUEST_CODE = 1;
    public static List<AudioFile> audioFiles;
    Fragment selectedFragment = null;
    public static BottomSheetBehavior<View> bottomSheetBehavior;
    public static ImageView home_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener (navListener);
        home_container = findViewById(R.id.nav_back);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        //permission();
        //audioFiles = getAllAudio();

        setLocalization();

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                if (getSupportFragmentManager().findFragmentById(R.id.controls_container) != null) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        AudioFragment fragment = (AudioFragment) getSupportFragmentManager().findFragmentById(R.id.controls_container);
                        fragment.stopBtnClicked();
                        (fragment).getFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                View playingCon = AudioFragment.mini_player_frag;
//                playingCon.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                if (getSupportFragmentManager().findFragmentById(R.id.controls_container) != null) {
                    View playingCon = findViewById(R.id.mini_player_frag);
                    bottomNav.setAlpha(1 - slideOffset);
                    playingCon.setAlpha(1 - slideOffset);
                }
            }
        });
    }

    private void setLocalization() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString("localization", "rus");
        setLocal(this, savedText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        File dir = new File(getApplicationInfo().dataDir, "Demal/BackgroundImage");
        final File temp = new File(dir, "BackgroundImage.jpg");
        if (temp.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(temp.getAbsolutePath());
            Bitmap blurredBitmap = blur( this, myBitmap );
            home_container.setImageBitmap(blurredBitmap);
        }else{
            Glide.with(NavActivity.this).load(R.mipmap.island_back).into(home_container);
        }
    }

    public static Bitmap blur(Context context, Bitmap image) {
        float bitmap_scale = 0.4f;
        float blur_radius = 9f;

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
        private void permission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NavActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }else{
            //audioFiles = getAllAudio();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
              //audioFiles = getAllAudio();
            }else{
                ActivityCompat.requestPermissions(NavActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId() == R.id.nav_home)
                    {
                        selectedFragment = new HomeFragment();
                    }
                    else if(item.getItemId() == R.id.nav_sleep)
                    {
                        selectedFragment = new SleepFragment();
                    }
                    else if(item.getItemId() == R.id.nav_meditate)
                    {
                        selectedFragment = new MeditateFragment();
                    }
                    else if(item.getItemId() == R.id.nav_music)
                    {
                        selectedFragment = new MusicFragment();
                    }
                    else if(item.getItemId() == R.id.nav_more)
                    {
                        selectedFragment = new MoreFragment();
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return false;
                }
            };

}
