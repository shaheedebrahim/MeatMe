package com.bandaids.meatme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CreateMeetingActivity extends AppCompatActivity {

    static int[] fromDate;
    static int[] toDate;

    /**
     * int[0] = day of month
     * int[1] = month
     * int[2] = year
     **/
    public static void setFromDate(int[] date) {
        fromDate = date;
    }

    /**
     * int[0] = day of month
     * int[1] = month
     * int[2] = year
     **/
    public static void setToDate(int[] date) {
        toDate = date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
    }

    public void onSelectAddPeople(View v) {
        Bundle bundle = new Bundle();

        bundle.putIntArray("fromDate", fromDate);
        bundle.putIntArray("toDate", toDate);

        AddPeopleMethodFragment addPeopleMethodFragment = new AddPeopleMethodFragment();
        addPeopleMethodFragment.setArguments(bundle);
        addPeopleMethodFragment.show(getSupportFragmentManager(), "tag");
    }

    public void onSelectFromDate(View v) {
        SelectDatesFragment datesFragment = new SelectDatesFragment();
        datesFragment.show(getSupportFragmentManager(), "fromDateTag");
    }

    public void onSelectToDate(View v) {
        SelectDatesFragment datesFragment = new SelectDatesFragment();
        datesFragment.show(getSupportFragmentManager(), "toDateTag");
    }
}
