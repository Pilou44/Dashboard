package com.freak.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			SharedPreferences settings = context.getSharedPreferences("com.freak.dashboard_preferences", 0);
			if (settings.getBoolean(DashboardSettings.START_ON_BOOT_PREF, false))
			{
	            Intent dashboardIntent = new Intent("com.freak.dashboard");
	            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            context.startActivity(dashboardIntent);
			}
        }
	}

}
