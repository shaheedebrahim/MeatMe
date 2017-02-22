package com.bandaids.meatme;
public class DateTime implements Comparable<DateTime>{
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private double totalDateTime;

    public DateTime(int nYear, int nMonth, int nDay, int nHour, int nMinute) {
        year = nYear;
        month = nMonth;
        day = nDay;
        hour = nHour;
        minute = nMinute;
        totalDateTime = (minute * 0.0001) + (hour * 0.01) + (day) + (month * 100) + (year * 10000);
    }

    @Override
    public int compareTo(DateTime dt) {
        return (int)(diffMin(dt));
    }

    public double diffMin(DateTime dt) {
        double answer = 0;
        answer += (year - dt.year)*525600;
        answer += (month - dt.month)*43800;
        answer += (day - dt.day)*1440;
        answer += (hour - dt.hour)*60;
        answer += minute - dt.minute;
        return answer;
    }
    public int compareDayTo(DateTime dt) {
        return (int)(Math.floor(totalDateTime) - Math.floor(dt.totalDateTime));
    }

    public String printMe() {
        return year + "/" + month + "/" + day + " " + hour + ":" + minute;
    }
}