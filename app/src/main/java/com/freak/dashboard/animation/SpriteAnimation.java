package com.freak.dashboard.animation;

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

    private static final long ANIMATION_PERIOD = 100;

    private final int[] mAnimationStop;
    private final int[] mAnimationLow;
    private final int[] mAnimationMedium;
    private final int[] mAnimationHigh;

    private int[] mRunningAnim;
    private int mIndexAnim = -1;
    private Timer mAnimationTimer;
    private final Handler mAnimationHandler;

    private final ImageView mAnimation;

    public SpriteAnimation(ImageView animation){
        mAnimation = animation;
        mAnimationHandler = new Handler();
        mAnimationStop = getAnimationStop();
        mAnimationLow = getAnimationLow();
        mAnimationMedium = getAnimationMedium();
        mAnimationHigh = getAnimationHigh();
        mRunningAnim = mAnimationStop;
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
        EventBus.getDefault().unregister(this);

        mAnimationTimer.cancel();
    }

    private void updateAnimation() {
        mAnimationHandler.post(new Runnable() { public void run() {
            mIndexAnim++;
            mIndexAnim = mIndexAnim%mRunningAnim.length;
            mAnimation.setImageResource(mRunningAnim[mIndexAnim]);
        }});
    }

    @Subscribe
    public void onAnimationEvent(final AnimationEvent event) {
        if (event.getAnimationStatus() == GetInfoThread.ANIM_STATUS_SPEED_0){
            mRunningAnim = mAnimationStop;
        } else if (event.getAnimationStatus() == GetInfoThread.ANIM_STATUS_SPEED_1){
            mRunningAnim = mAnimationLow;
        } else if (event.getAnimationStatus() == GetInfoThread.ANIM_STATUS_SPEED_2){
            mRunningAnim = mAnimationMedium;
        } else {
            mRunningAnim = mAnimationHigh;
        }
        mIndexAnim = -1;
    }


    /*public void setValue(int value){
        // Update displayed animation
        if (mRunningAnim == mAnimationStop){
            if (value >= mLowPlus){
                mRunningAnim = mAnimationLow;
                mIndexAnim = -1;
            }
        }
        else if (mRunningAnim == mAnimationLow){
            if (value >= mMediumPlus) {
                mRunningAnim = mAnimationMedium;
                mIndexAnim = -1;
            }
            else if (value <= mLowMinus){
                mRunningAnim = mAnimationStop;
                mIndexAnim = -1;
            }
        }
        else if (mRunningAnim == mAnimationMedium){
            if (value >= mHighPlus) {
                mRunningAnim = mAnimationHigh;
                mIndexAnim = -1;
            }
            else if (value <= mMediumMinus){
                mRunningAnim = mAnimationLow;
                mIndexAnim = -1;
            }
        }
        else if (mRunningAnim == mAnimationHigh){
            if (value <= mHighMinus){
                mRunningAnim = mAnimationMedium;
                mIndexAnim = -1;
            }
        }
    }*/

    protected abstract int[] getAnimationStop();
    protected abstract int[] getAnimationLow();
    protected abstract int[] getAnimationMedium();
    protected abstract int[] getAnimationHigh();

}
