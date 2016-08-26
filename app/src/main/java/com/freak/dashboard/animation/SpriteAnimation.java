package com.freak.dashboard.animation;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.freak.dashboard.GetInfoThread;
import com.freak.dashboard.bus.AnimationEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

public abstract class SpriteAnimation {

    private static final boolean DEBUG = false;
    private static final String TAG = SpriteAnimation.class.getSimpleName();

    private final int mAnimationStop;
    private final int mAnimationLow;
    private final int mAnimationMedium;
    private final int mAnimationHigh;

    private final ImageView mAnimationView;
    private AnimationDrawable mAnimation;

    public SpriteAnimation(ImageView animation){
        mAnimationView = animation;
        mAnimationStop = getAnimationStop();
        mAnimationLow = getAnimationLow();
        mAnimationMedium = getAnimationMedium();
        mAnimationHigh = getAnimationHigh();


        mAnimationView.setImageResource(mAnimationStop);
        mAnimation = (AnimationDrawable) mAnimationView.getDrawable();
        mAnimation.start();
    }

    @Subscribe
    public void onAnimationEvent(final AnimationEvent event) {
        if (event.getAnimationStatus() == GetInfoThread.ANIM_STATUS_SPEED_0){
            mAnimation.stop();
            mAnimationView.setImageResource(mAnimationStop);
            mAnimation = (AnimationDrawable) mAnimationView.getDrawable();
            mAnimation.start();
        } else if (event.getAnimationStatus() == GetInfoThread.ANIM_STATUS_SPEED_1){
            mAnimation.stop();
            mAnimationView.setImageResource(mAnimationLow);
            mAnimation = (AnimationDrawable) mAnimationView.getDrawable();
            mAnimation.start();
        } else if (event.getAnimationStatus() == GetInfoThread.ANIM_STATUS_SPEED_2){
            mAnimation.stop();
            mAnimationView.setImageResource(mAnimationMedium);
            mAnimation = (AnimationDrawable) mAnimationView.getDrawable();
            mAnimation.start();
        } else {
            mAnimation.stop();
            mAnimationView.setImageResource(mAnimationHigh);
            mAnimation = (AnimationDrawable) mAnimationView.getDrawable();
            mAnimation.start();
        }
    }

    protected abstract int getAnimationStop();
    protected abstract int getAnimationLow();
    protected abstract int getAnimationMedium();
    protected abstract int getAnimationHigh();

}
