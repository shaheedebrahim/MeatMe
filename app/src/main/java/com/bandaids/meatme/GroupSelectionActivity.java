package com.bandaids.meatme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class GroupSelectionActivity extends AppCompatActivity {

    ListView groupList;
    ArrayList<String> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selection);

        groupList = (ListView) findViewById(R.id.groupsList);
        groups = new ArrayList<>();
        setupGroups();

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, groups);
        groupList.setAdapter(adapter);

        groupList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        groupList.setItemsCanFocus(false);


    }

    public ArrayList<String> getSelectedGroups() {
        ArrayList<String> selectedGroups = new ArrayList<>();

        SparseBooleanArray selectedLocations = groupList.getCheckedItemPositions();

        for (int i = 0; i < selectedLocations.size(); i++) {
            if (selectedLocations.get(i)) {
                selectedGroups.add((String) groupList.getItemAtPosition(i));
            }
        }

        return selectedGroups;
    }

    private void setupGroups() {
        groups = (ArrayList<String>) Database.database.getAllGroups().clone();
    }
}
