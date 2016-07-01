package com.freak.dashboard.bus;

/**
 * Created by gbeguin on 27/06/2016.
 */
public class TempStatusEvent {
    private final int mTempStatus;

    public TempStatusEvent(int tempStatus) {
        mTempStatus = tempStatus;
    }

    public int getTempStatus() {
        return mTempStatus;
    }
}
