package com.example.angusyuen.padalarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

// Creating a service class (instead of just doing it on the onReceive()) enables it to work in the background.
public class AlarmService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Dungeon time!");
        builder.setContentText("Time to run " + intent.getStringExtra("Title") + "!");
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentIntent(pendingIntent);

        System.out.println("Building notification...");

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}