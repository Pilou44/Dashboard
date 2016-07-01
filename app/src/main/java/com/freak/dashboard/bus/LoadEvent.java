package com.freak.dashboard.bus;

/**
 * Created by gbeguin on 27/06/2016.
 */
public class LoadEvent {
    private final short mLoad;

    public LoadEvent(short load) {
        mLoad = load;
    }

    public short getLoad() {
        return mLoad;
    }
}
