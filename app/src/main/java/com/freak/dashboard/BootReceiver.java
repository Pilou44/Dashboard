package com.freak.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.key_preferences), 0);
			if (settings.getBoolean(context.getString(R.string.key_start_on_boot), false))
			{
	            Intent dashboardIntent = new Intent(context, Dashboard.class);
	            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            context.startActivity(dashboardIntent);
			}
        }
	}

}
