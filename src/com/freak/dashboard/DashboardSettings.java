package com.freak.dashboard;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DashboardSettings extends PreferenceActivity /*implements OnSharedPreferenceChangeListener */{

    public static final String START_ON_BOOT_PREF = "start_on_boot";
    public static final String KEY_HIGH_LOAD = "high_load";
    public static final String KEY_MEDIUM_LOAD = "medium_load";
    public static final String KEY_LOW_LOAD = "low_load";
    public static final String KEY_SHIFT_LIGHT_1 = "shift_light_1";
    public static final String KEY_SHIFT_LIGHT_2 = "shift_light_2";
    public static final String KEY_SHIFT_LIGHT_3 = "shift_light_3";
    public static final String KEY_SHIFT_LIGHT_4 = "shift_light_4";
    public static final String KEY_SHIFT_LIGHT_5 = "shift_light_5";
    public static final String KEY_TEXT_COLOR = "text_color";
    public static final String KEY_WARNING_COLOR = "warningt_color";
    public static final String KEY_DANGER_COLOR = "danger_color";
    public static final String KEY_BACKGROUND_COLOR = "background_color";
    public static final String KEY_MIN_COOL_TEMP = "min_cool_temp";
    public static final String KEY_MAX_COOL_TEMP = "max_cool_temp";
    
	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.dashboard_settings);
    }
	
}
