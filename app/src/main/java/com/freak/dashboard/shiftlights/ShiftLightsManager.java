package com.freak.dashboard.shiftlights;

import android.os.Handler;
import android.widget.ImageView;

import com.freak.dashboard.R;
import com.freak.dashboard.bus.RPMEvent;

import org.greenrobot.eventbus.Subscribe;

public class ShiftLightsManager {

    private final ImageView[] mImages;
    private final int[] mOn;
    private final int[] mOff;
    private int[] mValues;
    private final boolean[] mState;
    private boolean mShiftLightOn;
    private Handler handler;

    public ShiftLightsManager(ImageView[] imageViews) {
        mImages= imageViews;
        mValues = new int[mImages.length];
        mState = new boolean[mImages.length];
        mOn = new int[mImages.length];
        mOff = new int[mImages.length];
        mShiftLightOn = false;
        handler = new Handler();
    }

    public void setValues(int[] values, int green, int yellow, int red) {
        if (values.length == mImages.length) {
            mValues = values;
            for (int i = 0 ; i < green ; i++) {
                mOn[i] = R.drawable.green_light;
            }
            for (int i = green ; i < green + yellow ; i++) {
                mOn[i] = R.drawable.yellow_light;
            }
            for (int i = green + yellow ; i < green + yellow + red ; i++) {
                mOn[i] = R.drawable.red_light;
            }
            for (int i = 0 ; i < mImages.length ; i++) {
                mOff[i] = R.drawable.off_light;
            }
        }
    }

    @Subscribe
    public void onRPMEvent(final RPMEvent event) {
        handler.post(new Runnable() {
            public void run() {
                update(event.getRpm());
            }
        });
    }

    private void update(int rpm) {
        if (rpm >= mValues[0]) {
            mShiftLightOn = true;
            for (int i = 0; i < mImages.length; i++) {
                if (rpm >= mValues[i]) {
                    switchOn(i);
                } else {
                    switchOff(i);
                }
            }
        }
        else if (mShiftLightOn) {
            for (int i = 0; i < mImages.length; i++) {
                switchOff(i);
            }
            mShiftLightOn = false;
        }
    }

    private void switchOff(int i) {
        if (mState[i]) {
            mImages[i].setImageResource(mOff[i]);
            mState[i] = false;
        }
    }

    private void switchOn(int i) {
        if (!mState[i]) {
            mImages[i].setImageResource(mOn[i]);
            mState[i] = true;
        }
    }
}
