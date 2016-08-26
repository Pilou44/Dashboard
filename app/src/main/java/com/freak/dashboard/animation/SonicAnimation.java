package com.freak.dashboard.animation;

import android.widget.ImageView;

import com.freak.dashboard.R;

public class SonicAnimation extends SpriteAnimation {

    public SonicAnimation(ImageView animation) {
        super(animation);
    }

    @Override
    protected int getAnimationStop() {
        return R.drawable.sonic_stand_animation;
    }

    @Override
    protected int getAnimationLow() {
        return R.drawable.sonic_walk_animation;
    }

    @Override
    protected int getAnimationMedium() {
        return R.drawable.sonic_run_animation;
    }

    @Override
    protected int getAnimationHigh() {
        return R.drawable.sonic_gold_animation;
    }
}
