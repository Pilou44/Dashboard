package com.freak.dashboard.bus;

/**
 * Created by gbeguin on 27/06/2016.
 */
public class RPMEvent {
    private final int mRpm;

    public RPMEvent(int rpm) {
        mRpm = rpm;
    }

    public int getRpm() {
        return mRpm;
    }
}
