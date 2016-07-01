package com.freak.dashboard.bus;

/**
 * Created by gbeguin on 27/06/2016.
 */
public class AnimationEvent {
    private final int mAnimationStatus;

    public AnimationEvent(int animationStatus) {
        mAnimationStatus = animationStatus;
    }

    public int getAnimationStatus() {
        return mAnimationStatus;
    }
}
