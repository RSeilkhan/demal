package com.rakhatali.demal.utils;

import com.rakhatali.demal.R;

public enum Preset {

    FIRST_BREATH(0, R.drawable.ic_breath_circle),
    SECOND_BREATH(1, R.drawable.ic_breath_circle_2),
    THIRD_BREATH(2, R.drawable.ic_breath_circle_3),
    FOURTH_BREATH(3, R.drawable.ic_breath_circle_4),
    FIFTH_BREATH(4, R.drawable.bg_circle_preset_morning_salad);

    private final int settingsPosition;
    private final int resId;

    Preset(int settingsPosition, int resId) {
        this.settingsPosition = settingsPosition;
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

}
