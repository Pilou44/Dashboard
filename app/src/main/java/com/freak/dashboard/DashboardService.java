package com.freak.dashboard;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.prowl.torque.remote.ITorqueService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class DashboardService extends Service {

	private static final String TAG = DashboardService.class.getSimpleName();

	private static final int NOTIFICATION_ID = 0;
	private static final long PERIOD = 300;
	private static final int STATUS_WARNING = 1;
	private static final int STATUS_OK = 2;
	private static final int STATUS_DANGER = 3;

	private static final boolean DEBUG = true;

	// Binder given to clients
    private final IBinder mBinder = new LocalBinder();

	private boolean successfulBind = false;
	private ITorqueService torqueService;
	private Timer updateTimer;
	private short rpm;
	private short load;
	private short temp;
	private float voltage;

	private Notification notification;
	private NotificationManager notificationManager;
	private int status;

	private int minCoolTemp;

	private int maxCoolTemp;
    private boolean mFirstConnection;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        DashboardService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DashboardService.this;
        }
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (DEBUG)
			Log.d(TAG, "onUnbind");

		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		if (DEBUG)
			Log.d(TAG, "Create");
	
		// Load preferences
		reload();

		// Bind to the torque service
		if (DEBUG)
			Log.d(TAG, "Bind to the torque service");
		Intent intent = new Intent();
		intent.setClassName("org.prowl.torque", "org.prowl.torque.remote.TorqueService");
		successfulBind = bindService(intent, connection, Context.BIND_AUTO_CREATE);
		if (successfulBind) {
			if (DEBUG)
				Log.d(TAG, "Successful bind");

            mFirstConnection = true;

			updateTimer = new Timer();
			updateTimer.schedule(new TimerTask() { public void run() {
				if (torqueService != null)
					update();
			}}, 1000, PERIOD);
		}
		
		// Create notification
		if (DEBUG)
			Log.d(TAG, "Create notification");
		status = STATUS_OK;
		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE); 
		final String notificationTitle = getResources().getString(R.string.app_name); 
		final String notificationDesc = getResources().getString(R.string.notification_desc);
		Intent notificationIntent = new Intent(this, Dashboard.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDesc)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(NOTIFICATION_ID, notification);
		
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		if (DEBUG)
			Log.d(TAG, "onDestroy");
		if (successfulBind) {
			updateTimer.cancel();
			unbindService(connection);
		}
		notificationManager.cancel(NOTIFICATION_ID);
		super.onDestroy();
	}

	/**
	 * Bits of service code. You usually won't need to change this.
	 */
	private final ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			torqueService = ITorqueService.Stub.asInterface(service);
		}
		public void onServiceDisconnected(ComponentName name) {
			torqueService = null;
		}
	};

    private void update() {
		if (DEBUG)
			Log.d(TAG, "Update");
		try {
            if (mFirstConnection) {
                Float readSupportedPids = torqueService.getValueForPid(0x00, true);
                byte[] readSupportedPidsBytes = ByteBuffer.allocate(4).putFloat(readSupportedPids).array();
                if (DEBUG)
                    Log.d(TAG, "Supported PIDs 1:" + Arrays.toString(readSupportedPidsBytes));

                readSupportedPids = torqueService.getValueForPid(0x20, true);
                readSupportedPidsBytes = ByteBuffer.allocate(4).putFloat(readSupportedPids).array();
                if (DEBUG)
                    Log.d(TAG, "Supported PIDs 2:" + Arrays.toString(readSupportedPidsBytes));

                readSupportedPids = torqueService.getValueForPid(0x40, true);
                readSupportedPidsBytes = ByteBuffer.allocate(4).putFloat(readSupportedPids).array();
                if (DEBUG)
                    Log.d(TAG, "Supported PIDs 3:" + Arrays.toString(readSupportedPidsBytes));

                readSupportedPids = torqueService.getValueForPid(0x60, true);
                readSupportedPidsBytes = ByteBuffer.allocate(4).putFloat(readSupportedPids).array();
                if (DEBUG)
                    Log.d(TAG, "Supported PIDs 4:" + Arrays.toString(readSupportedPidsBytes));

                mFirstConnection = false;
            }

			Float readRpm = torqueService.getValueForPid(0x0c, true); // Engine RPM
			Float readLoad = torqueService.getValueForPid(0x04, true); // Calculated engine load value
			Float readTemp = torqueService.getValueForPid(0x05, true); // Engine coolant temperature
			Float readVoltage = torqueService.getValueForPid(0xff1238, true); // Voltage

			rpm = readRpm.shortValue();
			load = readLoad.shortValue();
			temp = readTemp.shortValue();
			voltage = readVoltage;

			if (temp < minCoolTemp) {
				if (status != STATUS_WARNING)
				{
					status = STATUS_WARNING;
					notification.icon = R.drawable.notif_icon_warning;
					notificationManager.notify(NOTIFICATION_ID, notification); 
				}
			}
			else if (temp > maxCoolTemp) {
				if (status != STATUS_DANGER)
				{
					status = STATUS_DANGER;
					notification.icon = R.drawable.notif_icon_danger;
					notificationManager.notify(NOTIFICATION_ID, notification); 
				}
			}
			else {
				if (status != STATUS_OK)
				{
					status = STATUS_OK;
					notification.icon = R.drawable.notif_icon;
					notificationManager.notify(NOTIFICATION_ID, notification); 
				}
			}
		} catch(RemoteException e) {
			Log.e(TAG,e.getMessage(),e);
		}
	}

	public synchronized int getRpm() {
		return rpm;
	}

	public synchronized int getLoad() {
		return load;
	}

	public synchronized int getTemp() {
		return temp;
	}

	public synchronized float getVoltage() {
		return voltage;
	}

	public void reload() {
		if (DEBUG)
			Log.d(TAG, "Load preferences");
		SharedPreferences settings = getSharedPreferences(this.getString(R.string.key_preferences), 0);
		minCoolTemp = Integer.parseInt(settings.getString(this.getString(R.string.key_min_cool_temp), "" + getResources().getInteger(R.integer.min_cool_temp)));
		maxCoolTemp = Integer.parseInt(settings.getString(this.getString(R.string.key_max_cool_temp), "" + getResources().getInteger(R.integer.max_cool_temp)));
	}
	
	
}
