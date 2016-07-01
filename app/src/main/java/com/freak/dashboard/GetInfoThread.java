package com.freak.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Log;

import com.freak.dashboard.bus.AnimationEvent;
import com.freak.dashboard.bus.LoadEvent;
import com.freak.dashboard.bus.RPMEvent;
import com.freak.dashboard.bus.TempEvent;
import com.freak.dashboard.bus.TempStatusEvent;
import com.freak.dashboard.bus.VoltageEvent;

import org.greenrobot.eventbus.EventBus;
import org.prowl.torque.remote.ITorqueService;

import java.util.Random;

public class GetInfoThread extends Thread {

    private static final boolean DEBUG = true;
    private static final String TAG    = GetInfoThread.class.getSimpleName();

    public static final int TEMP_STATUS_WARNING = 1;
    public static final int TEMP_STATUS_OK = 2;
    public static final int TEMP_STATUS_DANGER = 3;

    public static final int ANIM_STATUS_SPEED_0 = 0;
    public static final int ANIM_STATUS_SPEED_1 = 1;
    public static final int ANIM_STATUS_SPEED_2 = 2;
    public static final int ANIM_STATUS_SPEED_3 = 3;

    public static final long PERIOD = 300;

    private final ITorqueService mService;
    private int mMinCoolTemp;
    private int mMaxCoolTemp;
    private int mHighPlus;
    private int mHighMinus;
    private int mMediumPlus;
    private int mMediumMinus;
    private int mLowPlus;
    private int mLowMinus;
    private final Context context;
    private boolean mRun;
    private int mTempStatus;
    private int mAnimationStatus;

    public GetInfoThread(Context context, ITorqueService service) {
        mService = service;
        mTempStatus = TEMP_STATUS_OK;
        mAnimationStatus = ANIM_STATUS_SPEED_0;
        this.context = context;

        reload();
    }

    private int readIntegerFromPreferences(SharedPreferences settings, String name, int defaultValue) {
        try {
            return Integer.parseInt(settings.getString(name, "" + defaultValue));
        }
        catch (NumberFormatException e){
            return defaultValue;
        }
    }

    @Override
    public void run() {
        int time = 0;
        mRun = true;
        while (mRun) {
            try {
                time %= 4;
                Float readRpm = mService.getValueForPid(0x0c, true); // Engine RPM
                EventBus.getDefault().post(new RPMEvent(readRpm.shortValue()));

                Float readLoad = mService.getValueForPid(0x04, true); // Calculated engine load value
                EventBus.getDefault().post(new LoadEvent(readLoad.shortValue()));
                if (mAnimationStatus == ANIM_STATUS_SPEED_0){
                    if (readLoad.shortValue() >= mLowPlus){
                        mAnimationStatus = ANIM_STATUS_SPEED_1;
                        EventBus.getDefault().post(new AnimationEvent(mAnimationStatus));
                    }
                } else if (mAnimationStatus == ANIM_STATUS_SPEED_1){
                    if (readLoad.shortValue() >= mMediumPlus) {
                        mAnimationStatus = ANIM_STATUS_SPEED_2;
                        EventBus.getDefault().post(new AnimationEvent(mAnimationStatus));
                    } else if (readLoad.shortValue() <= mLowMinus){
                        mAnimationStatus = ANIM_STATUS_SPEED_0;
                        EventBus.getDefault().post(new AnimationEvent(mAnimationStatus));
                    }
                } else if (mAnimationStatus == ANIM_STATUS_SPEED_2){
                    if (readLoad.shortValue() >= mHighPlus) {
                        mAnimationStatus = ANIM_STATUS_SPEED_3;
                        EventBus.getDefault().post(new AnimationEvent(mAnimationStatus));
                    } else if (readLoad.shortValue() <= mMediumMinus){
                        mAnimationStatus = ANIM_STATUS_SPEED_1;
                        EventBus.getDefault().post(new AnimationEvent(mAnimationStatus));
                    }
                } else if (mAnimationStatus == ANIM_STATUS_SPEED_3){
                    if (readLoad.shortValue() <= mHighMinus){
                        mAnimationStatus = ANIM_STATUS_SPEED_2;
                        EventBus.getDefault().post(new AnimationEvent(mAnimationStatus));
                    }
                }

                if (time == 0) {
                    Float readTemp = mService.getValueForPid(0x05, true); // Engine coolant temperature

                    if (readTemp.shortValue() < mMinCoolTemp) {
                        if (mTempStatus != TEMP_STATUS_WARNING) {
                            mTempStatus = TEMP_STATUS_WARNING;
                            EventBus.getDefault().post(new TempStatusEvent(mTempStatus));
                        }
                    } else if (readTemp.shortValue() > mMaxCoolTemp) {
                        if (mTempStatus != TEMP_STATUS_DANGER) {
                            mTempStatus = TEMP_STATUS_DANGER;
                            EventBus.getDefault().post(new TempStatusEvent(mTempStatus));
                        }
                    } else {
                        if (mTempStatus != TEMP_STATUS_OK) {
                            mTempStatus = TEMP_STATUS_OK;
                            EventBus.getDefault().post(new TempStatusEvent(mTempStatus));
                        }
                    }

                    EventBus.getDefault().post(new TempEvent(readTemp.intValue()));
                } else if (time == 1) {
                    Float readVoltage = mService.getValueForPid(0xff1238, true); // Voltage
                    EventBus.getDefault().post(new VoltageEvent(readVoltage));
                }

                time++;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        mRun = false;
    }

    public void reload() {
        if (DEBUG)
            Log.d(TAG, "Load preferences");
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.key_preferences), 0);
        mMinCoolTemp = Integer.parseInt(settings.getString(context.getString(R.string.key_min_cool_temp), "" + context.getResources().getInteger(R.integer.min_cool_temp)));
        mMaxCoolTemp = Integer.parseInt(settings.getString(context.getString(R.string.key_max_cool_temp), "" + context.getResources().getInteger(R.integer.max_cool_temp)));

        int highLoadValue = readIntegerFromPreferences(settings, context.getString(R.string.key_high_load), context.getResources().getInteger(R.integer.high_load));
        int mediumLoadValue = readIntegerFromPreferences(settings, context.getString(R.string.key_medium_load), context.getResources().getInteger(R.integer.medium_load));
        int lowLoadValue = readIntegerFromPreferences(settings, context.getString(R.string.key_low_load), context.getResources().getInteger(R.integer.low_load));

        int margin = ((highLoadValue * 5)/100)+1;
        mHighPlus = highLoadValue + margin;
        mHighMinus = highLoadValue - margin;
        margin = ((mediumLoadValue * 5)/100)+1;
        mMediumPlus = mediumLoadValue + margin;
        mMediumMinus = mediumLoadValue - margin;
        margin = ((lowLoadValue * 5)/100)+1;
        mLowPlus = lowLoadValue + margin;
        mLowMinus = lowLoadValue - margin;
    }
}
