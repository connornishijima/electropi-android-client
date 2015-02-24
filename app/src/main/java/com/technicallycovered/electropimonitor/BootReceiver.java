package com.technicallycovered.electropimonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_TAG, "Boot Received");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(Constants.LOG_TAG, "BootReceiver BOOT_COMPLETED");

            Intent alarmIntent = new Intent(context, BootReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int interval = 30000;
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

            Log.i(Constants.LOG_TAG, "EP checkIn Alarm Set");
        }

        Log.i(Constants.LOG_TAG, "Starting service");
        Intent myIntent = new Intent(context, PingService.class);

        context.stopService(myIntent);
        context.startService(myIntent);
    }
}