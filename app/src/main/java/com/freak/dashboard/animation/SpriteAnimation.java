package com.freak.dashboard.animation;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public abstract class SpriteAnimation {

    private static final boolean DEBUG = false;
    protected static final String TAG = SpriteAnimation.class.getSimpleName();

    private static final long ANIMATION_PERIOD = 100;

    private int mAnimationStop[];
    private int mAnimationLow[];
    private int mAnimationMedium[];
    private int mAnimationHigh[];

    private int[] mAnimEnCours;
    private int mIndexAnim = -1;
    private Timer mAnimationTimer;
    private Handler mAnimationHandler;

    private final ImageView mAnimation;
    private final int mLow;
    private final int mMedium;
    private final int mHigh;

    public SpriteAnimation(ImageView animation, int low, int medium, int high){
        mAnimation = animation;
        mLow = low;
        mMedium = medium;
        mHigh = high;
        mAnimationHandler = new Handler();
        mAnimationStop = getAnimationStop();
        mAnimationLow = getAnimationLow();
        mAnimationMedium = getAnimationMedium();
        mAnimationHigh = getAnimationHigh();
        mAnimEnCours = mAnimationStop;
    }

    public void start(){
        // Initialize animation
        if (DEBUG)
            Log.d(TAG, "Initialize animation");
        mAnimationTimer = new Timer();
        mAnimationTimer.schedule(new TimerTask() { public void run() {
            updateAnimation();
        }}, ANIMATION_PERIOD, ANIMATION_PERIOD);
    }

    public void stop(){
        mAnimationTimer.cancel();
    }

    private void updateAnimation() {
        mAnimationHandler.post(new Runnable() { public void run() {
            mIndexAnim++;
            mIndexAnim = mIndexAnim%mAnimEnCours.length;
            mAnimation.setImageResource(mAnimEnCours[mIndexAnim]);
        }});
    }

    public void setValue(int value){
        // Update displayed animation
        if ((value >= mHigh) && (mAnimEnCours != mAnimationHigh)) {
            mAnimEnCours = mAnimationHigh;
            mIndexAnim = -1;
        }
        else if ((value >= mMedium) && (mAnimEnCours != mAnimationMedium)) {
            mAnimEnCours = mAnimationMedium;
            mIndexAnim = -1;
        }
        else if ((value >= mLow) && (mAnimEnCours != mAnimationLow)) {
            mAnimEnCours = mAnimationLow;
            mIndexAnim = -1;
        }
        else if (mAnimEnCours != mAnimationStop) {
            mAnimEnCours = mAnimationStop;
            mIndexAnim = -1;
        }
    }

    protected abstract int[] getAnimationStop();
    protected abstract int[] getAnimationLow();
    protected abstract int[] getAnimationMedium();
    protected abstract int[] getAnimationHigh();

}