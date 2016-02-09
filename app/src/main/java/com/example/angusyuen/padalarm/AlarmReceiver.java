package com.example.angusyuen.padalarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Received!!" + " " + intent.getStringExtra("Title"));
        Intent service1 = new Intent(context, AlarmService.class);
        service1.putExtra("Title", intent.getStringExtra("Title"));
        context.startService(service1);
    }
}