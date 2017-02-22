package com.bandaids.meatme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AddPeopleActivity extends AppCompatActivity {

    ListView accountList;
    ArrayList<String> accountNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        accountList = (ListView) findViewById(R.id.accountList);

        accountNames = new ArrayList<String>();
        setAccountNames();

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, accountNames);
        accountList.setAdapter(adapter);

        accountList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        accountList.setItemsCanFocus(false);
    }

    public void setAccountNames() {
        accountNames.add("God King Kendra");
        accountNames.add("Sean");
        accountNames.add("Wen Li");
        accountNames.add("Shaheed");

    }

    public void onFindTimes(View view) {
        Intent intent = new Intent(this, MeetingTimesActivity.class);
        startActivity(intent);
    }
}
