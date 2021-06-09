package com.rakhatali.demal;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rakhatali.demal.utils.BreathePreferences;
import com.rakhatali.demal.utils.SettingsUtils;


public class BreathActivity extends AppCompatActivity implements SettingsDialog.SettingsChangeListener{

    private static final String TAG = BreathActivity.class.getSimpleName();

    private ConstraintLayout contentLayout;
    private TextView statusText;
    private View outerCircleView, innerCircleView;
    private FloatingActionButton fab, backBtn, stop;
    private Animation animationInhaleText, animationExhaleText,
            animationInhaleInnerCircle, animationExhaleInnerCircle;
    private Handler handler = new Handler();
    private boolean animate = true;
    private int holdDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breath);
        BreathePreferences.init(getApplicationContext());

        contentLayout = findViewById(R.id.lt_content);
        contentLayout.setOnTouchListener(contentTouchListener);

        statusText = findViewById(R.id.txt_status);
        statusText.setText(R.string.inhale);

        outerCircleView = findViewById(R.id.v_circle_outer);
        innerCircleView = findViewById(R.id.v_circle_inner);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(backClickListener);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(fabClickListener);

        stop = findViewById(R.id.stop);


        setupBackgroundColor();

        prepareAnimations();

        statusText.startAnimation(animationInhaleText);
        innerCircleView.startAnimation(animationInhaleInnerCircle);



        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(animate){
                    statusText.clearAnimation();
                    innerCircleView.clearAnimation();
                    stop.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    animate=false;
                }else{
                    statusText.startAnimation(animationInhaleText);
                    innerCircleView.startAnimation(animationInhaleInnerCircle);
                    stop.setImageResource(R.drawable.ic_baseline_pause_24);
                    animate=true;
                }
            }
        });

    }

    private void setupBackgroundColor() {
        int backgroundResId = SettingsUtils.getBackgroundByPresetPosition(SettingsUtils
                .getSelectedPreset());
        setOuterCircleBackground(backgroundResId);
    }

    private void setOuterCircleBackground(int backgroundResId) {
        outerCircleView.setBackgroundResource(backgroundResId);
    }

    private void setInhaleDuration(int duration) {
        animationInhaleText.setDuration(duration);
        animationInhaleInnerCircle.setDuration(duration);
    }

    private void setExhaleDuration(int duration) {
        animationExhaleText.setDuration(duration);
        animationExhaleInnerCircle.setDuration(duration);
    }

    private void prepareAnimations() {
        int inhaleDuration = SettingsUtils.getSelectedInhaleDuration();
        int exhaleDuration = SettingsUtils.getSelectedExhaleDuration();
        holdDuration = SettingsUtils.getSelectedHoldDuration();

        // Inhale - make large
        animationInhaleText = AnimationUtils.loadAnimation(this, R.anim.anim_text_inhale);
        animationInhaleText.setFillAfter(true);
        animationInhaleText.setAnimationListener(inhaleAnimationListener);

        animationInhaleInnerCircle = AnimationUtils.loadAnimation(this, R.anim.anim_inner_circle_inhale);
        animationInhaleInnerCircle.setFillAfter(true);
        animationInhaleInnerCircle.setAnimationListener(inhaleAnimationListener);

        setInhaleDuration(inhaleDuration);

        // Exhale - make small
        animationExhaleText = AnimationUtils.loadAnimation(this, R.anim.anim_text_exhale);
        animationExhaleText.setFillAfter(true);
        animationExhaleText.setAnimationListener(exhaleAnimationListener);

        animationExhaleInnerCircle = AnimationUtils.loadAnimation(this, R.anim.anim_inner_circle_exhale);
        animationExhaleInnerCircle.setFillAfter(true);
        animationExhaleInnerCircle.setAnimationListener(exhaleAnimationListener);

        setExhaleDuration(exhaleDuration);

    }

    private Animation.AnimationListener inhaleAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "inhale animation end");
            if(animate){
                statusText.setText(R.string.hold);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText(R.string.exhale);
                        statusText.startAnimation(animationExhaleText);
                        innerCircleView.startAnimation(animationExhaleInnerCircle);
                    }
                }, holdDuration);
            }else{
                statusText.setText(R.string.stopped);
                statusText.clearAnimation();
                innerCircleView.clearAnimation();
                animation.cancel();
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private Animation.AnimationListener exhaleAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "exhale animation end");
            if(animate){
                statusText.setText(R.string.hold);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText(R.string.inhale);
                        statusText.startAnimation(animationInhaleText);
                        innerCircleView.startAnimation(animationInhaleInnerCircle);
                    }
                }, holdDuration);
            }else{
                statusText.setText(R.string.stopped);
                statusText.clearAnimation();
                innerCircleView.clearAnimation();
                animation.cancel();
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

    private View.OnTouchListener contentTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            fab.show();
            backBtn.show();
            stop.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.hide();
                    backBtn.hide();
                    stop.hide();
                }
            }, 2000);
            return false;
        }
    };

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showSettingsDialog();
        }
    };

    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void showSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog(this, this);
        settingsDialog.show();
    }

    @Override
    public void onPresetChanged(int backgroundResId) {
        setOuterCircleBackground(backgroundResId);
    }

    @Override
    public void onInhaleValueChanged(int duration) {
        setInhaleDuration(duration);
    }

    @Override
    public void onExhaleValueChanged(int duration) {
        setExhaleDuration(duration);
    }

    @Override
    public void onHoldValueChanged(int duration) {
        holdDuration = duration;
    }
}
