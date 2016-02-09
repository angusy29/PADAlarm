package com.example.angusyuen.padalarm;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by angusyuen on 27/01/16.
 */
public class Dungeon {
    String title;
    String country;
    String group;
    DateTime startTimeAsISO;
    boolean willNotify;

    public Dungeon(String title, String country, String group, DateTime startTime) {
        this.title = title;
        this.country = country;
        this.group = group;
        this.startTimeAsISO = startTime;
        willNotify = false;     // do not notify the user of this dungeon initially
    }

    public String getName() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public String getGroup() {
        return group;
    }

    public DateTime getStartTime() {
        return startTimeAsISO;
    }

    public boolean getWillNotify() {
        return willNotify;
    }

    public void setWillNotify(boolean value) {
        willNotify = value;
    }

    // get the time in HH:mm to be displayed on the GUI
    public String getStringTime() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(startTimeAsISO.toDate());
    }

    // get the date of the dungeon, which will be displayed on the GUI
    public String getStringDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        return df.format(startTimeAsISO.toDate());
    }

    @Override
    public String toString() {
        return title + "\n" + startTimeAsISO.toString();
    }
}
