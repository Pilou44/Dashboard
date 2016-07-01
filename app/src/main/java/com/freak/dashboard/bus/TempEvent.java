package com.freak.dashboard.bus;

/**
 * Created by gbeguin on 27/06/2016.
 */
public class TempEvent {
    private final int mTemp;

    public TempEvent(int temp) {
        mTemp = temp;
    }

    public int getTemp() {
        return mTemp;
    }
}
