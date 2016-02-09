package com.example.angusyuen.padalarm;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by angusyuen on 6/02/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Dungeon> allDungeons;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView dungeonTitle;
        TextView dungeonTime;
        TextView dungeonDate;
        TextView cardID;
        ImageButton sendNotify;
        Boolean willNotify;

        // one particular "card" information holder
        public ViewHolder(View v) {
            super(v);
            dungeonTitle = (TextView) v.findViewById(R.id.dungeonTitle);
            dungeonTime = (TextView) v.findViewById(R.id.dungeonTime);
            dungeonDate = (TextView) v.findViewById(R.id.dungeonDate);
            cardID = (TextView) v.findViewById(R.id.cardID);
            sendNotify = (ImageButton) v.findViewById(R.id.sendNotify);
            willNotify = false;

            sendNotify.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (willNotify == true) {
                        willNotify = false;
                        System.out.println("willNotify is now false");
                        sendNotify.setColorFilter(v.getResources().getColor(R.color.colorNegative, null));

                        System.out.println(cardID.getText().toString());
                        allDungeons.get(Integer.valueOf(cardID.getText().toString())).setWillNotify(false);

                    } else {
                        willNotify = true;
                        System.out.println("willNotify is now true");
                        sendNotify.setColorFilter(v.getResources().getColor(R.color.colorPositive, null));

                        System.out.println(cardID.getText().toString());
                        allDungeons.get(Integer.valueOf(cardID.getText().toString())).setWillNotify(true);

                        setAlarm(v, allDungeons.get(Integer.valueOf(cardID.getText().toString())).getName(),
                                allDungeons.get(Integer.valueOf(cardID.getText().toString())).getStartTime());
                    }
                }
            });
        }
    }

    public MyAdapter(ArrayList<Dungeon> allDungeons) {
        this.allDungeons = allDungeons;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.dungeonTitle.setText(allDungeons.get(position).getName());
        System.out.println(allDungeons.get(position).getStringTime());
        holder.dungeonTime.setText(allDungeons.get(position).getStringTime());
        holder.dungeonDate.setText(allDungeons.get(position).getStringDate());
        holder.cardID.setText(String.valueOf(position));
    }

    @Override
    // returns the number of dungeons
    public int getItemCount() {
        return allDungeons.size();
    }

    public ArrayList<Dungeon> getAllDungeons() {
        return allDungeons;
    }

    // to set alarm manager to set specific time for the notification to ring
    public void setAlarm(View v, String title, DateTime time){
        AlarmManager alarmManager = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(v.getContext(), AlarmReceiver.class);
        alarmIntent.putExtra("Title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(v.getContext(), 0, alarmIntent, 0);
        Calendar alarmStartTime = Calendar.getInstance();

        System.out.println("Setting alarm for " + time.getHourOfDay() + " " + time.getMinuteOfHour() + " " + alarmIntent.getStringExtra("Title"));

        alarmStartTime.set(Calendar.HOUR_OF_DAY, 21);
        alarmStartTime.set(Calendar.MINUTE, 30);
        alarmStartTime.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC, alarmStartTime.getTimeInMillis(), getInterval(), pendingIntent);
    }

    private int getInterval(){
        int days = 1;
        int hours = 24;
        int minutes = 60;
        int seconds = 60;
        int milliseconds = 1000;
        int repeatMS = days * hours * minutes * seconds * milliseconds;
        return repeatMS;
    }

}