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
import com.freak.dashboard.mano.Manometer;

public class Dashboard extends Activity {

	private static final String TAG = Dashboard.class.getSimpleName();

	private static final long PERIOD = 500;
	private static final long ANIMATION_PERIOD = 100;

	private static final boolean DEBUG = false;
	

	private DashboardService dashboardService;

	int rpm = 0;
	int temp = 0;
	int load = 0;
	float voltage = 0;

	private int highLoadValue;
	private int mediumLoadValue;
	private int lowLoadValue;
	
	private int shiftLight1Value;
	private int shiftLight2Value;
	private int shiftLight3Value;
	private int shiftLight4Value;
	private int shiftLight5Value;
	
	private int textColor;
	private int warningColor;
	private int dangerColor;
	
	private int minCoolTemp;
	private int maxCoolTemp;
	
	private Timer updateTimer;
	private Handler handler;
	private Handler animationHandler;
	
	private LinearLayout background;
	private TextView textRPM;
	private TextView textUnitRPM;
	private TextView textLoad;
	private TextView textTemp;
	private TextView textVoltage;
	
	private boolean successfulBind = false;
	private Timer animationTimer;

	private ImageView animation;
	private int[] animEnCours = SonicAnimation.sonic_wait;
	private int indexAnim = 0;

	private boolean shiftLightOn;
	private ImageView shift1;
	private ImageView shift2;
	private ImageView shift3;
	private ImageView shift4;
	private ImageView shift5;
	private Manometer mano;

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

		shift1 = (ImageView) findViewById(R.id.shift1);
		shift2 = (ImageView) findViewById(R.id.shift2);
		shift3 = (ImageView) findViewById(R.id.shift3);
		shift4 = (ImageView) findViewById(R.id.shift4);
		shift5 = (ImageView) findViewById(R.id.shift5);

        mano = (Manometer) findViewById(R.id.mano);

		handler = new Handler();
		animationHandler = new Handler();

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

		shiftLightOn = false;
		
		// Load preferences
		if (DEBUG)
			Log.d(TAG, "Load Preferences");
		SharedPreferences settings = getSharedPreferences("com.freak.dashboard_preferences", 0);
		highLoadValue = Integer.parseInt(settings.getString(DashboardSettings.KEY_HIGH_LOAD, "" + getResources().getInteger(R.integer.high_load)));
		mediumLoadValue = Integer.parseInt(settings.getString(DashboardSettings.KEY_MEDIUM_LOAD, "" + getResources().getInteger(R.integer.medium_load)));
		lowLoadValue = Integer.parseInt(settings.getString(DashboardSettings.KEY_LOW_LOAD, "" + getResources().getInteger(R.integer.low_load)));
		shiftLight1Value = Integer.parseInt(settings.getString(DashboardSettings.KEY_SHIFT_LIGHT_1, "" + getResources().getInteger(R.integer.shift_light_1)));
		shiftLight2Value = Integer.parseInt(settings.getString(DashboardSettings.KEY_SHIFT_LIGHT_2, "" + getResources().getInteger(R.integer.shift_light_2)));
		shiftLight3Value = Integer.parseInt(settings.getString(DashboardSettings.KEY_SHIFT_LIGHT_3, "" + getResources().getInteger(R.integer.shift_light_3)));
		shiftLight4Value = Integer.parseInt(settings.getString(DashboardSettings.KEY_SHIFT_LIGHT_4, "" + getResources().getInteger(R.integer.shift_light_4)));
		shiftLight5Value = Integer.parseInt(settings.getString(DashboardSettings.KEY_SHIFT_LIGHT_5, "" + getResources().getInteger(R.integer.shift_light_5)));
		textColor = settings.getInt(DashboardSettings.KEY_TEXT_COLOR, getResources().getColor(R.color.text));
		warningColor = settings.getInt(DashboardSettings.KEY_WARNING_COLOR, getResources().getColor(R.color.warning));
		dangerColor = settings.getInt(DashboardSettings.KEY_DANGER_COLOR, getResources().getColor(R.color.danger));
		minCoolTemp = Integer.parseInt(settings.getString(DashboardSettings.KEY_MIN_COOL_TEMP, "" + getResources().getInteger(R.integer.min_cool_temp)));
		maxCoolTemp = Integer.parseInt(settings.getString(DashboardSettings.KEY_MAX_COOL_TEMP, "" + getResources().getInteger(R.integer.max_cool_temp)));
        mano.setValues(PERIOD, 0, getResources().getInteger(R.integer.shift_light_5), 4, -10, 100);
        mano.setHandColor(dangerColor);
        mano.setManoColor(textColor);

        int backgroundColor = settings.getInt(DashboardSettings.KEY_BACKGROUND_COLOR, getResources().getColor(R.color.background));

        textRPM.setTextColor(textColor);
		textUnitRPM.setTextColor(textColor);
		textLoad.setTextColor(textColor);
		textTemp.setTextColor(textColor);
		textVoltage.setTextColor(textColor);
		
		background.setBackgroundColor(backgroundColor);

		if (successfulBind) {
    		// Initialize update
   			updateTimer = new Timer();
   			updateTimer.schedule(new TimerTask() { public void run() {
   				if (dashboardService != null)
   					update();
   			}}, 1000, PERIOD);
		}
		
		// Initialize animation
		if (DEBUG)
			Log.d(TAG, "Initialize animation");
		animationTimer = new Timer();
		animationTimer.schedule(new TimerTask() { public void run() {
			updateAnimation();
		}}, ANIMATION_PERIOD, ANIMATION_PERIOD);
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
		animationTimer.cancel();
		super.onPause();
	}

	public void updateAnimation() {
		animationHandler.post(new Runnable() { public void run() {
			indexAnim++;
			indexAnim = indexAnim%animEnCours.length;
			animation.setImageResource(animEnCours[indexAnim]);
		}});

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
			if (load >= highLoadValue)
				animEnCours = SonicAnimation.sonic_gold;
			else if (load >= mediumLoadValue)
				animEnCours = SonicAnimation.sonic_run;
			else if (load >= lowLoadValue)
				animEnCours = SonicAnimation.sonic_walk;
			else
				animEnCours = SonicAnimation.sonic_wait;

			// Update shift lights
			if (rpm >= shiftLight1Value) {
				shiftLightOn = true;
				if (rpm >= shiftLight5Value)
				{
					shift1.setImageResource(R.drawable.green_light);
					shift2.setImageResource(R.drawable.green_light);
					shift3.setImageResource(R.drawable.green_light);
					shift4.setImageResource(R.drawable.yellow_light);
					shift5.setImageResource(R.drawable.red_light);
				}
				else if (rpm >= shiftLight4Value)
				{
					shift1.setImageResource(R.drawable.green_light);
					shift2.setImageResource(R.drawable.green_light);
					shift3.setImageResource(R.drawable.green_light);
					shift4.setImageResource(R.drawable.yellow_light);
					shift5.setImageResource(R.drawable.off_light);
				}
				else if (rpm >= shiftLight3Value)
				{
					shift1.setImageResource(R.drawable.green_light);
					shift2.setImageResource(R.drawable.green_light);
					shift3.setImageResource(R.drawable.green_light);
					shift4.setImageResource(R.drawable.off_light);
					shift5.setImageResource(R.drawable.off_light);
				}
				else if (rpm >= shiftLight2Value)
				{
					shift1.setImageResource(R.drawable.green_light);
					shift2.setImageResource(R.drawable.green_light);
					shift3.setImageResource(R.drawable.off_light);
					shift4.setImageResource(R.drawable.off_light);
					shift5.setImageResource(R.drawable.off_light);
				}
				else
				{
					shift1.setImageResource(R.drawable.green_light);
					shift2.setImageResource(R.drawable.off_light);
					shift3.setImageResource(R.drawable.off_light);
					shift4.setImageResource(R.drawable.off_light);
					shift5.setImageResource(R.drawable.off_light);
				}
			}
			else if (shiftLightOn) {
				shift1.setImageResource(R.drawable.off_light);
				shift2.setImageResource(R.drawable.off_light);
				shift3.setImageResource(R.drawable.off_light);
				shift4.setImageResource(R.drawable.off_light);
				shift5.setImageResource(R.drawable.off_light);
				shiftLightOn = false;
			}


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