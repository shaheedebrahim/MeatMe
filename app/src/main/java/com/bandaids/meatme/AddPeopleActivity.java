package com.bandaids.meatme;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AddPeopleActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ArrayAdapter adapter;
    GoogleApiClient mGoogleApiClient;
    ListView accountList;
    static ArrayList<String> accountNames;
    static HashMap<String, String> idMap;

    static int[] fromDate;
    static int[] toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        idMap = new HashMap<>();

        accountList = (ListView) findViewById(R.id.accountList);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        accountNames = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, accountNames);
        accountList.setAdapter(adapter);

        accountList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        accountList.setItemsCanFocus(false);

        Intent intent = getIntent();
        fromDate = intent.getIntArrayExtra("fromDate");
        toDate = intent.getIntArrayExtra("toDate");

        Log.d("FROM DATE: ", Arrays.toString(toDate));
    }

    private void display() {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
            SendTask task = new SendTask(getString(R.string.server_url_query), "query");
            BasicNameValuePair[] pairs = new BasicNameValuePair[accountNames.size() + 4];
            pairs[0] = new BasicNameValuePair("session_id", MainActivity.session_id);
            pairs[1] = new BasicNameValuePair("user_ids", String.valueOf(MainActivity.user_id));
            for (int i = 0; i < accountNames.size(); i++) pairs[i+2] = new BasicNameValuePair("user_ids", idMap.get(accountNames.get(i)));

            try {
                DateTime from = new DateTime(fromDate[2], fromDate[1], fromDate[0], 0, 0);
                DateTime to = new DateTime(toDate[2], toDate[1], toDate[0], 0, 0);
                pairs[pairs.length - 2] = new BasicNameValuePair("min_time", String.valueOf(from.getMillis() / 1000));
                pairs[pairs.length - 1] = new BasicNameValuePair("max_time", String.valueOf(to.getMillis() / 1000));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("WHOLE ARRAY", Arrays.toString(pairs));

            for (int i = 0; i < pairs.length; i++) Log.d(":SLKDJFJKGJKG", pairs[i].toString());

            task.execute(pairs);

            try {
                task.get(5000, TimeUnit.MILLISECONDS);

                //while (MainActivity.session_id.equals(""));

                //finish();
            }
            catch (TimeoutException e) {
                e.printStackTrace();
                System.exit(1);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, MeetingTimesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location LastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (LastLocation != null) {
                Log.d("Location update: ", "Updated current location");

                BasicNameValuePair[] pairs = new BasicNameValuePair[3];
                pairs[0] = new BasicNameValuePair("session_id", MainActivity.session_id);
                pairs[1] = new BasicNameValuePair("lat", String.valueOf(LastLocation.getLatitude()));
                pairs[2] = new BasicNameValuePair("lon", String.valueOf(LastLocation.getLongitude()));

                SendTask task = new SendTask(getString(R.string.server_url_find), "find");
                task.adapter = adapter;
                task.execute(pairs);
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
