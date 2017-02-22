package com.bandaids.meatme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MeetingTimesActivity extends AppCompatActivity {

    ListView meetingTimesList;
    ArrayList<String> meetingTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_times);

        meetingTimes = new ArrayList<>();
        setupMeetingTimes();

        meetingTimesList = (ListView) findViewById(R.id.meetingTimesList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, meetingTimes);
        meetingTimesList.setAdapter(adapter);

        meetingTimesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        meetingTimesList.setItemsCanFocus(false);

    }

    public void setupMeetingTimes() {
        meetingTimes.add("GOD KING KENDRA");
        meetingTimes.add("Peon Sean");
    }
}
