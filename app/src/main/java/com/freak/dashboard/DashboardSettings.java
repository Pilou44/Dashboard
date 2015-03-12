package com.freak.dashboard;

import com.freak.dashboard.DashboardService.LocalBinder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class DashboardSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener /*implements OnSharedPreferenceChangeListener */{

    private static final boolean DEBUG = false;
    private static final String TAG = DashboardSettings.class.getSimpleName();
    private static final int PICK_IMAGE = 1;

	private DashboardService dashboardService;

	@Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.dashboard_settings);
    }

	@Override
    @SuppressWarnings("deprecation")
	protected void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		// Bind to the dashboard service
		Intent intent = new Intent(this, DashboardService.class);
		bindService(intent, connection, 0);
		super.onPause();
	}

    @Override
    @SuppressWarnings("deprecation")
    protected void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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

		}
		public void onServiceDisconnected(ComponentName name) {
    		if (DEBUG)
    			Log.d(TAG, "onServiceDisconnected");
			dashboardService = null;
		}
	};

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_background_picture))) {
            if (DEBUG)
                Log.d(TAG, "Change background");
            if (Integer.decode(sharedPreferences.getString(key, "-1")) == -2) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri _uri = data.getData();

            //User had pick an image.
            Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();

            //Link to the image
            final String imageFilePath = cursor.getString(0);
            SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
            editor.putString(getString(R.string.key_background_path), imageFilePath);
            editor.putString(getString(R.string.key_background_picture), "-3");
            ((ListPreference)findPreference(getString(R.string.key_background_picture))).setValueIndex(((ListPreference)findPreference(getString(R.string.key_background_picture))).findIndexOfValue("-3"));
            editor.apply();
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
