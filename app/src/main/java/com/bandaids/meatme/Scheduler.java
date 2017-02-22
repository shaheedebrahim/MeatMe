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
            addPossibilities(new Event(current, e.start), durationInMinutes, possibilities);
            if (e.finish.isAfter(current)) {
                current = e.finish;
            }
        }
        addPossibilities(new Event(current, timeSpan.finish), durationInMinutes, possibilities);
        return possibilities;
    }

    private static void addPossibilities(Event openTime, int duration, ArrayList<Event> possibilities) {
        Event next;
        while(openTime.finish.isAfter(openTime.start.plus(Period.minutes(duration)))) {
            next = new Event(openTime.start, openTime.start.plus(Period.minutes(duration)));
            if (timeRangeCheck(next, duration)) {
                possibilities.add(next);
            }
            if ((0 < openTime.start.getMinuteOfHour()) && (openTime.start.getMinuteOfHour() < 30)) {
                openTime.start = openTime.start.minuteOfHour().setCopy(30);
            } else if ((30 < openTime.start.getMinuteOfHour()) && (openTime.start.getMinuteOfHour() <= 59)) {
                openTime.start = openTime.start.minuteOfHour().setCopy(0);
                openTime.start = openTime.start.plusHours(1);
            } else {
                openTime.start = openTime.start.plus(Period.minutes(30));
            }
        }
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