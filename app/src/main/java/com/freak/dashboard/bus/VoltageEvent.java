package com.freak.dashboard.bus;

public class VoltageEvent {
    private final float mVoltage;

    public VoltageEvent(float voltage) {
        mVoltage = voltage;
    }

    public float getVoltage() {
        return mVoltage;
    }
}
