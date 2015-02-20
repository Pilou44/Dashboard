package com.freak.dashboard;

import com.freak.dashboard.DashboardService.LocalBinder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.util.Log;

public class DashboardSettings extends PreferenceActivity /*implements OnSharedPreferenceChangeListener */{

	protected static final boolean DEBUG = false;
	protected static final String TAG = DashboardSettings.class.getSimpleName();

	private DashboardService dashboardService;

	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.dashboard_settings);
    }

	@Override
	protected void onPause() {
		// Bind to the dashboard service
		Intent intent = new Intent(this, DashboardService.class);
		boolean successfulBind = bindService(intent, connection, 0);
		/*if (successfulBind) {
			if (DEBUG)
				Log.d(TAG, "unbindService");
			dashboardService.reload();
			successfulBind = false;
			unbindService(connection);
		}*/

		super.onPause();
	}
	
	/**
	 * Bits of service code. You usually won't need to change this.
	 */
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName arg0, IBinder service) {
    		if (DEBUG)
    			Log.d(TAG, "onServiceConnected");
			// We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            dashboardService = binder.getService();
			if (DEBUG)
				Log.d(TAG, "unbindService");
			dashboardService.reload();
			unbindService(connection);

		};
		public void onServiceDisconnected(ComponentName name) {
    		if (DEBUG)
    			Log.d(TAG, "onServiceDisconnected");
			dashboardService = null;
		};
	};

}
