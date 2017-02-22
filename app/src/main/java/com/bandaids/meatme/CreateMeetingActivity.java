package com.bandaids.meatme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CreateMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
    }

    public void onSelectAddPeople(View v) {
        AddPeopleMethodFragment addPeopleMethodFragment = new AddPeopleMethodFragment();
        addPeopleMethodFragment.show(getSupportFragmentManager(), "tag");
    }

    public void onSelectFromDate(View v) {
        SelectDatesFragment datesFragment = new SelectDatesFragment();
        datesFragment.show(getSupportFragmentManager(), "tag");
    }

    public void onSelectToDate(View v) {
        SelectDatesFragment datesFragment = new SelectDatesFragment();
        datesFragment.show(getSupportFragmentManager(), "tag");
    }
}
