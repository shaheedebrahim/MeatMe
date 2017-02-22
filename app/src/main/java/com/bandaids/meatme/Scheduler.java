package com.bandaids.meatme;

import org.joda.time.Period;

import java.util.ArrayList;

public class Scheduler {

    public static org.joda.time.DateTime StartDayTime = new org.joda.time.DateTime(0, 0, 0, 7, 0);
    public static org.joda.time.DateTime EndDayTime = new org.joda.time.DateTime(0, 0, 0, 23, 0);

    public static ArrayList<Event> schedule(ArrayList<Event> busy, int durationInMinutes, Event timeSpan) {
        ArrayList<Event> possibilities = new ArrayList<Event>();
        org.joda.time.DateTime current = timeSpan.start;
        double opening;
        for( Event e : busy ) {
            if (e.start.isAfter(current.plus(Period.minutes(durationInMinutes)))) {
                possibilities.add(new Event(current, e.start));
            }
            if (e.finish.isAfter(current)) {
                current = e.finish;
            }
        }
        if (timeSpan.finish.isAfter(current.plus(Period.minutes(durationInMinutes)))) {
            possibilities.add(new Event(current, timeSpan.finish));
        }
        return possibilities;
    }

    private static boolean timeRangeCheck(Event e, int duration) {
        if (duration < (EndDayTime.minuteOfDay().get() - StartDayTime.minuteOfDay().get())) { //Will the meeting fit in time span set
            if ((e.start.toLocalDate().isEqual(e.finish.toLocalDate())) && //On same day
                    (StartDayTime.minuteOfDay().get() <= e.start.minuteOfDay().get()) && //Starts after start of day
                    (EndDayTime.minuteOfDay().get() >= e.finish.minuteOfDay().get())) { //Ends before end of day
                return true;
            } else {
                return false;
            }
        } else {
            // If it can't fit in the time span given, ignore
            return true;
        }
    }
}
