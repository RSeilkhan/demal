package com.rakhatali.demal;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.rakhatali.demal.utils.Preset;
import com.rakhatali.demal.utils.SettingsUtils;

public class SettingsDialog extends Dialog {

    private SettingsChangeListener listener;

    private RadioGroup radioGroup;

    public SettingsDialog(@NonNull Context context, @NonNull SettingsChangeListener listener) {
        super(context, R.style.Theme_SettingsDialog);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_overlay);
        getWindow().getAttributes().windowAnimations = R.style.Theme_SettingsDialog;

        radioGroup = findViewById(R.id.rg_gradients);
        SeekBar inhaleSeekBar = findViewById(R.id.seekBar_inhale);
        SeekBar exhaleSeekBar = findViewById(R.id.seekBar_exhale);
        SeekBar holdSeekBar = findViewById(R.id.seekBar_hold);
        Button closeButton = findViewById(R.id.btn_close);

        radioGroup.setOnCheckedChangeListener(checkedChangeListener);
        inhaleSeekBar.setOnSeekBarChangeListener(inhaleSeekBarChangeListener);
        exhaleSeekBar.setOnSeekBarChangeListener(exhaleSeekBarChangeListener);
        holdSeekBar.setOnSeekBarChangeListener(holdSeekBarChangeListener);
        closeButton.setOnClickListener(closeBtnClickListener);

        ((RadioButton) radioGroup.getChildAt(SettingsUtils.getSelectedPreset())).setChecked(true);
        inhaleSeekBar.setProgress(SettingsUtils.getSelectedInhaleDuration() / 1000);
        exhaleSeekBar.setProgress(SettingsUtils.getSelectedExhaleDuration() / 1000);
        holdSeekBar.setProgress(SettingsUtils.getSelectedHoldDuration() / 1000);
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            Preset selectedPreset;
            switch (checkedId) {
                case R.id.rb_1:
                    selectedPreset = Preset.FIRST_BREATH;
                    break;
                case R.id.rb_2:
                    selectedPreset = Preset.SECOND_BREATH;
                    break;
                case R.id.rb_3:
                    selectedPreset = Preset.THIRD_BREATH;
                    break;
                case R.id.rb_4:
                    selectedPreset = Preset.FOURTH_BREATH;
                    break;
                case R.id.rb_5:
                    selectedPreset = Preset.FIFTH_BREATH;
                    break;
                default:
                    selectedPreset = Preset.FIFTH_BREATH;
                    break;
            }
            SettingsUtils.saveSelectedPreset(selectedPreset.ordinal());
            listener.onPresetChanged(selectedPreset.getResId());
        }
    };

    private SeekBar.OnSeekBarChangeListener inhaleSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            listener.onInhaleValueChanged(progress != 0 ? progress * 1000 : 1000);
            SettingsUtils.saveSelectedInhaleDuration(progress != 0 ? progress * 1000 :
                    1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private SeekBar.OnSeekBarChangeListener exhaleSeekBarChangeListener = new SeekBar
            .OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            listener.onExhaleValueChanged(progress != 0 ? progress * 1000 : 1000);
            SettingsUtils.saveSelectedExhaleDuration(progress != 0 ? progress * 1000 :
                    1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private SeekBar.OnSeekBarChangeListener holdSeekBarChangeListener = new SeekBar
            .OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            listener.onHoldValueChanged(progress * 1000);
            SettingsUtils.saveSelectedHoldDuration(progress * 1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private View.OnClickListener closeBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public interface SettingsChangeListener {
        void onPresetChanged(int backgroundResId);
        void onInhaleValueChanged(int duration);
        void onExhaleValueChanged(int duration);
        void onHoldValueChanged(int duration);
    }

}
