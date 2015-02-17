package com.freak.dashboard;

import java.util.Timer;
import java.util.TimerTask;

import org.prowl.torque.remote.ITorqueService;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Dashboard extends Activity {

	private static final String TAG = Dashboard.class.getSimpleName();

	private static final long PERIOD = 500;
	private static final long ANIMATION_PERIOD = 100;

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
	private int backgroundColor;
	
	private int minCoolTemp;
	private int maxCoolTemp;
	
	private ITorqueService torqueService;
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

	private int rpm = 0;
	private int temp = 0;
	private Float load = (float) 0;

	private boolean shiftLightOn;
	private ImageView shift1;
	private ImageView shift2;
	private ImageView shift3;
	private ImageView shift4;
	private ImageView shift5;

	private int NOTIFICATION_ID = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

		handler = new Handler();
		animationHandler = new Handler();

		//Récupération du notification Manager 
		final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE); 

		//Récupération du titre et description de la notification 
		final String notificationTitle = getResources().getString(R.string.app_name); 
		final String notificationDesc = getResources().getString(R.string.notification_desc);        

		//Création de la notification avec spécification de l'icône de la notification et le texte qui apparait à la création de la notification 
		final Notification notification = new Notification(R.drawable.notif_icon, notificationTitle, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		// Intent qui lancera vers l'activité MainActivity
		Intent notificationIntent = new Intent(this, Dashboard.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		//Définition de la redirection au moment du clic sur la notification. Dans notre cas la notification redirige vers notre application 
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		//Notification & Vibration 
		notification.setLatestEventInfo(this, notificationTitle, notificationDesc, pendingIntent); 

		notificationManager.notify(NOTIFICATION_ID, notification); 
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Bind to the torque service
		Intent intent = new Intent();
		intent.setClassName("org.prowl.torque", "org.prowl.torque.remote.TorqueService");
		successfulBind = bindService(intent, connection, 0);

		shiftLightOn = false;
		
		// Load preferences
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
		backgroundColor = settings.getInt(DashboardSettings.KEY_BACKGROUND_COLOR, getResources().getColor(R.color.background));
		minCoolTemp = Integer.parseInt(settings.getString(DashboardSettings.KEY_MIN_COOL_TEMP, "" + getResources().getInteger(R.integer.min_cool_temp)));
		maxCoolTemp = Integer.parseInt(settings.getString(DashboardSettings.KEY_MAX_COOL_TEMP, "" + getResources().getInteger(R.integer.max_cool_temp)));
		
		textRPM.setTextColor(textColor);
		textUnitRPM.setTextColor(textColor);
		textLoad.setTextColor(textColor);
		textTemp.setTextColor(textColor);
		textVoltage.setTextColor(textColor);
		
		background.setBackgroundColor(backgroundColor);

		if (successfulBind) {
			updateTimer = new Timer();
			updateTimer.schedule(new TimerTask() { public void run() {
				if (torqueService != null)
					update();
			}}, 1000, PERIOD);
		}

		animationTimer = new Timer();
		animationTimer.schedule(new TimerTask() { public void run() {
			updateAnimation();
		}}, ANIMATION_PERIOD, ANIMATION_PERIOD);
	}



	@Override
	protected void onPause() {
		super.onPause();
		if (successfulBind) {
			updateTimer.cancel();
			unbindService(connection);
		}
		animationTimer.cancel();
	}

	@Override
	protected void onDestroy() {
		//final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE); 
		//notificationManager.cancel(NOTIFICATION_ID); 
		super.onDestroy();
	}

	public void updateAnimation() {
		animationHandler.post(new Runnable() { public void run() {
			indexAnim++;
			indexAnim = indexAnim%animEnCours.length;
			animation.setBackgroundResource(animEnCours[indexAnim]);
		}});

	}
	
	public void update() {
		String text = ""; 
		try {

			float readRpm = torqueService.getValueForPid(0x0c, true); // Engine RPM
			float readLoad = torqueService.getValueForPid(0x04, true); // Calculated engine load value
			float readTemp = torqueService.getValueForPid(0x05, true); // Engine coolant temperature
			float readVoltage = torqueService.getValueForPid(0xff1238, true); // Voltage

			text = text + ((int)readRpm) + "\n" + readLoad + "\n" + ((int)readTemp) + "\n" + readVoltage + "\n";

		} catch(RemoteException e) {
			Log.e(TAG,e.getMessage(),e);
		}

		// Update datas
		final String myText = text;
		handler.post(new Runnable() { public void run() {
			
			// Update TextViews
			int index1, index2;

			index1 = 0;
			index2 = myText.indexOf("\n");
			try {
				rpm = Integer.valueOf(myText.substring(index1, index2));
			} catch (NumberFormatException e) {
				Log.e(TAG,e.getMessage(),e);
			}
			textRPM.setText("" + rpm);

			index1 = index2 + 1;
			index2 = myText.indexOf("\n", index1);
			try {
				load = Float.valueOf(myText.substring(index1, index2));
			} catch (NumberFormatException e) {
				Log.e(TAG,e.getMessage(),e);
			}
			textLoad.setText("" + load.shortValue() + getResources().getString(R.string.unit_load));

			index1 = index2 + 1;
			index2 = myText.indexOf("\n", index1);
			try {
				temp = Integer.valueOf(myText.substring(index1, index2));
			} catch (NumberFormatException e) {
				Log.e(TAG,e.getMessage(),e);
			}
			textTemp.setText("" + temp + getResources().getString(R.string.unit_temp));
			if (temp < minCoolTemp)
				textTemp.setTextColor(warningColor);
			else if (temp > maxCoolTemp)
				textTemp.setTextColor(dangerColor);
			else 
				textTemp.setTextColor(textColor);

			index1 = index2 + 1;
			index2 = myText.indexOf("\n", index1);
			textVoltage.setText(myText.substring(index1, index2) + " " + getResources().getString(R.string.unit_voltage));

			
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
					shift1.setBackgroundResource(R.drawable.green_light);
					shift2.setBackgroundResource(R.drawable.green_light);
					shift3.setBackgroundResource(R.drawable.green_light);
					shift4.setBackgroundResource(R.drawable.yellow_light);
					shift5.setBackgroundResource(R.drawable.red_light);
				}
				else if (rpm >= shiftLight4Value)
				{
					shift1.setBackgroundResource(R.drawable.green_light);
					shift2.setBackgroundResource(R.drawable.green_light);
					shift3.setBackgroundResource(R.drawable.green_light);
					shift4.setBackgroundResource(R.drawable.yellow_light);
					shift5.setBackgroundResource(R.drawable.off_light);
				}
				else if (rpm >= shiftLight3Value)
				{
					shift1.setBackgroundResource(R.drawable.green_light);
					shift2.setBackgroundResource(R.drawable.green_light);
					shift3.setBackgroundResource(R.drawable.green_light);
					shift4.setBackgroundResource(R.drawable.off_light);
					shift5.setBackgroundResource(R.drawable.off_light);
				}
				else if (rpm >= shiftLight2Value)
				{
					shift1.setBackgroundResource(R.drawable.green_light);
					shift2.setBackgroundResource(R.drawable.green_light);
					shift3.setBackgroundResource(R.drawable.off_light);
					shift4.setBackgroundResource(R.drawable.off_light);
					shift5.setBackgroundResource(R.drawable.off_light);
				}
				else
				{
					shift1.setBackgroundResource(R.drawable.green_light);
					shift2.setBackgroundResource(R.drawable.off_light);
					shift3.setBackgroundResource(R.drawable.off_light);
					shift4.setBackgroundResource(R.drawable.off_light);
					shift5.setBackgroundResource(R.drawable.off_light);
				}
			}
			else if (shiftLightOn) {
				shift1.setBackgroundResource(R.drawable.off_light);
				shift2.setBackgroundResource(R.drawable.off_light);
				shift3.setBackgroundResource(R.drawable.off_light);
				shift4.setBackgroundResource(R.drawable.off_light);
				shift5.setBackgroundResource(R.drawable.off_light);
				shiftLightOn = false;
			}


		}});
	}

	/**
	 * Bits of service code. You usually won't need to change this.
	 */
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			torqueService = ITorqueService.Stub.asInterface(service);
		};
		public void onServiceDisconnected(ComponentName name) {
			torqueService = null;
		};
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.dashboard_settings:
			Intent intent = new Intent(Dashboard.this, DashboardSettings.class);
			startActivity(intent);
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