package com.bandaids.meatme;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Event implements Comparable<Event> {
    org.joda.time.DateTime start;
    org.joda.time.DateTime finish;

    public Event(org.joda.time.DateTime s, org.joda.time.DateTime f) {
        start = s;
        finish = f;
    }

    @Override
    public int compareTo(Event e) {
        if (start.isBefore(e.start)) {
            return -1;
        } else if (start.isAfter(e.start)){
            return 1;
        }
        return 0;
    }

    public String toString() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM HH:mm");
        return "From " + dtf.parseDateTime(start.toString()) + "to " + dtf.parseDateTime(finish.toString());
    }
}