package com.bandaids.meatme;

import org.joda.time.Period;

import java.util.ArrayList;

public class Scheduler {

    public static int StartDayTime = 420;
    public static int EndDayTime = 1380;

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
        if (duration < (EndDayTime - StartDayTime)) { //Will the meeting fit in time span set
            if ((e.start.toLocalDate().isEqual(e.finish.toLocalDate())) && //On same day
                    (StartDayTime <= e.start.minuteOfDay().get()) && //Starts after start of day
                    (EndDayTime >= e.finish.minuteOfDay().get())) { //Ends before end of day
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
