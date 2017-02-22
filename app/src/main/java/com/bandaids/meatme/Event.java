package com.bandaids.meatme;

public class Event {
    org.joda.time.DateTime start;
    org.joda.time.DateTime finish;

    public Event(org.joda.time.DateTime s, org.joda.time.DateTime f) {
        start = s;
        finish = f;
    }
}