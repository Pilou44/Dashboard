package com.freak.dashboard;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freak.dashboard.DashboardService.LocalBinder;
import com.freak.dashboard.animation.SonicAnimation;
import com.freak.dashboard.animation.SpriteAnimation;
import com.freak.dashboard.mano.Manometer;
import com.freak.dashboard.shiftlights.ShiftLightsManager;

public class Dashboard extends Activity {

	private static final String TAG = Dashboard.class.getSimpleName();

	private static final long PERIOD = 500;

	private static final boolean DEBUG = false;
	

	private DashboardService dashboardService;

	int rpm = 0;
	int temp = 0;
	int load = 0;
	float voltage = 0;

	private int textColor;
	private int warningColor;
	private int dangerColor;
	
	private int minCoolTemp;
	private int maxCoolTemp;
	
	private Timer updateTimer;
	private Handler handler;
	
	private LinearLayout background;
	private TextView textRPM;
	private TextView textUnitRPM;
	private TextView textLoad;
	private TextView textTemp;
	private TextView textVoltage;
	
	private boolean successfulBind = false;

	private ImageView animation;

	private Manometer mano;
    private ShiftLightsManager shiftManager;
    private SpriteAnimation mAnimation;
    private SharedPreferences mPreferences;

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (DEBUG)
			Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		
		if (DEBUG)
			Log.d(TAG, "startService");
		Intent intent = new Intent(this, DashboardService.class);
		this.startService(intent);
		
		setContentView(R.layout.main);

		background = (LinearLayout) findViewById(R.id.background);
		
		textRPM = (TextView) findViewById(R.id.rpm);
		textLoad = (TextView) findViewById(R.id.load);
		textTemp = (TextView) findViewById(R.id.temp);
		textVoltage = (TextView) findViewById(R.id.voltage);
		textUnitRPM = (TextView) findViewById(R.id.unit_rpm);

		animation = (ImageView) findViewById(R.id.anim);

        ImageView shift1 = (ImageView) findViewById(R.id.shift1);
        ImageView shift2 = (ImageView) findViewById(R.id.shift2);
        ImageView shift3 = (ImageView) findViewById(R.id.shift3);
        ImageView shift4 = (ImageView) findViewById(R.id.shift4);
        ImageView shift5 = (ImageView) findViewById(R.id.shift5);
        shiftManager = new ShiftLightsManager( new ImageView[]{shift1, shift2, shift3, shift4, shift5});

        mano = (Manometer) findViewById(R.id.mano);

		handler = new Handler();

        // Load preferences
        if (DEBUG)
            Log.d(TAG, "Load Preferences");
        mPreferences = getSharedPreferences(this.getString(R.string.key_preferences), 0);
    }

	@Override
	protected void onResume() {
		if (DEBUG)
			Log.d(TAG, "onResume");

		super.onResume();

		// Bind to the dashboard service
		if (DEBUG)
			Log.d(TAG, "Connect to service");
		Intent intent = new Intent(this, DashboardService.class);
		successfulBind = bindService(intent, connection, 0);

        int shiftLight1Value = readIntegerFromPreferences(this.getString(R.string.key_shift_light_1), getResources().getInteger(R.integer.shift_light_1));
        int shiftLight2Value = readIntegerFromPreferences(this.getString(R.string.key_shift_light_2), getResources().getInteger(R.integer.shift_light_2));
        int shiftLight3Value = readIntegerFromPreferences(this.getString(R.string.key_shift_light_3), getResources().getInteger(R.integer.shift_light_3));
        int shiftLight4Value = readIntegerFromPreferences(this.getString(R.string.key_shift_light_4), getResources().getInteger(R.integer.shift_light_4));
        int shiftLight5Value = readIntegerFromPreferences(this.getString(R.string.key_shift_light_5), getResources().getInteger(R.integer.shift_light_5));
        shiftManager.setValues(new int[]{shiftLight1Value, shiftLight2Value, shiftLight3Value, shiftLight4Value, shiftLight5Value}, 3, 1, 1);

        textColor = mPreferences.getInt(this.getString(R.string.key_text_color), getResources().getColor(R.color.text));
        warningColor = mPreferences.getInt(this.getString(R.string.key_warning_color), getResources().getColor(R.color.warning));
        dangerColor = mPreferences.getInt(this.getString(R.string.key_danger_color), getResources().getColor(R.color.danger));
        minCoolTemp = readIntegerFromPreferences(this.getString(R.string.key_min_cool_temp), getResources().getInteger(R.integer.min_cool_temp));
        maxCoolTemp = readIntegerFromPreferences(this.getString(R.string.key_max_cool_temp), getResources().getInteger(R.integer.max_cool_temp));
        mano.setValues(PERIOD, 0, shiftLight5Value, 4, -10, 100, shiftLight5Value, 45);
        mano.setHandColor(dangerColor);
        mano.setManoColor(textColor, dangerColor);

        int backgroundColor = mPreferences.getInt(this.getString(R.string.key_background_color), getResources().getColor(R.color.background));

        textRPM.setTextColor(textColor);
		textUnitRPM.setTextColor(textColor);
		textLoad.setTextColor(textColor);
		textTemp.setTextColor(textColor);
		textVoltage.setTextColor(textColor);
		
		background.setBackgroundColor(backgroundColor);

        int highLoadValue = readIntegerFromPreferences(this.getString(R.string.key_high_load), getResources().getInteger(R.integer.high_load));
        int mediumLoadValue = readIntegerFromPreferences(this.getString(R.string.key_medium_load), getResources().getInteger(R.integer.medium_load));
        int lowLoadValue = readIntegerFromPreferences(this.getString(R.string.key_low_load), getResources().getInteger(R.integer.low_load));
        mAnimation = new SonicAnimation(animation, lowLoadValue, mediumLoadValue, highLoadValue);
        mAnimation.start();

		if (successfulBind) {
    		// Initialize update
   			updateTimer = new Timer();
   			updateTimer.schedule(new TimerTask() { public void run() {
   				if (dashboardService != null)
   					update();
   			}}, 1000, PERIOD);
		}
		
	}

    private int readIntegerFromPreferences(String name, int defaultValue) {
        try {
            return Integer.parseInt(mPreferences.getString(name, "" + defaultValue));
        }
        catch (NumberFormatException e){
            return defaultValue;
        }
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
		}
		public void onServiceDisconnected(ComponentName name) {
    		if (DEBUG)
    			Log.d(TAG, "onServiceDisconnected");
			dashboardService = null;
		}
	};

	@Override
	protected void onPause() {
		if (DEBUG)
			Log.d(TAG, "onPause");
		if (successfulBind) {
			if (DEBUG)
				Log.d(TAG, "unbindService");
			updateTimer.cancel();
			successfulBind = false;
			unbindService(connection);
		}
        mAnimation.stop();
		super.onPause();
	}


	public void update() {
		if (DEBUG)
			Log.d(TAG, "Update datas");
		
		// Update datas
		handler.post(new Runnable() { public void run() {
			
			if (DEBUG)
				Log.d(TAG, "Read datas");
			rpm = dashboardService.getRpm();
			temp = dashboardService.getTemp();
			load = dashboardService.getLoad();
			voltage = dashboardService.getVoltage();
			
			if (DEBUG)
				Log.d(TAG, "Update fields");
			textRPM.setText("" + rpm);
			textLoad.setText("" + load + getResources().getString(R.string.unit_load));
			textTemp.setText("" + temp + getResources().getString(R.string.unit_temp));
			textVoltage.setText(voltage + " " + getResources().getString(R.string.unit_voltage));

			if (temp < minCoolTemp) {
				textTemp.setTextColor(warningColor);
			}
			else if (temp > maxCoolTemp) {
				textTemp.setTextColor(dangerColor);
			}
			else {
				textTemp.setTextColor(textColor);
			}

            mano.setValue(rpm);

			// Update displayed animation
			mAnimation.setValue(load);

			// Update shift lights
            shiftManager.update(rpm);
		}});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.dashboard_settings:
			Intent intent = new Intent(this, DashboardSettings.class);
			startActivity(intent);
			break;
		case R.id.exit:
			dashboardService.stopSelf();
			this.finish();
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
}