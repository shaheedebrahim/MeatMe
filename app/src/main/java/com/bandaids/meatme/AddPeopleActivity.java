package com.bandaids.meatme;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AddPeopleActivity extends AppCompatActivity {

    ListView accountList;
    ArrayList<String> accountNames;

    int[] fromDate;
    int[] toDate;

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

        Intent intent = getIntent();
        fromDate = intent.getIntArrayExtra("fromDate");
        toDate = intent.getIntArrayExtra("toDate");

    }

    public void setAccountNames() {
        accountNames.add("God King Kendra");
        accountNames.add("Sean");
        accountNames.add("Wen Li");
        accountNames.add("Shaheed");

    }

    public ArrayList<String> getSelectedPeople() {
        ArrayList<String> people = new ArrayList<>();

        SparseBooleanArray checked = accountList.getCheckedItemPositions();

        for (int i = 0; i < accountList.getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                people.add((String) accountList.getItemAtPosition(i));
            }
        }

        if (people.size() == 0) {
            return null;
        } else {
            return people;
        }
    }

    public void onFindTimes(View view) {
        ArrayList<String> people = getSelectedPeople();

        if (people == null) {
            Snackbar.make(findViewById(android.R.id.content), "Please select at least one person", Snackbar.LENGTH_LONG).show();
        } else {

            Intent intent = new Intent(this, MeetingTimesActivity.class);
            startActivity(intent);
        }
    }
}
