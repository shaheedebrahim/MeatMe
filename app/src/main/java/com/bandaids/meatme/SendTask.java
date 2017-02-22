package com.bandaids.meatme;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SendTask extends AsyncTask<BasicNameValuePair, Void, HttpResponse> {
    String urlName;
    String type;
    ArrayAdapter adapter;

    /* Parameter t can take one of three values:
        - "login" - to support a login request
        - "find" - to support find by location
        - "query" - to query calendar of selected user set
     */
    public SendTask(String url, String t) {
        type = t;
        urlName = url;
    }

    public HttpResponse doInBackground(BasicNameValuePair... mess) {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();
            HttpPost httpPost = new HttpPost(urlName);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            for (int i = 0; i < mess.length; i++) nameValuePairs.add(mess[i]);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            return response;
        } catch (ClientProtocolException e) {
            Log.e("TEST: ", "Error sending auth code to backend.", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("TEST: ", "Error sending auth code to backend.", e);
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(HttpResponse v) {
        try {
            String resp = EntityUtils.toString(v.getEntity());
            JSONObject dict = new JSONObject(resp);
            if (type.equals("login")) {
                Log.d("TEST: ", dict.toString());
                if (!(dict.isNull("session_id") || dict.isNull("user_id"))) {
                    MainActivity.user_id = dict.getInt("user_id");
                    MainActivity.session_id = dict.getString("session_id");
                    SavedAuthInfo.setUName(GoogleAuthActivity.mainContext, MainActivity.session_id);
                }
            }
            else if (type.equals("query")) {
                Log.d("TESTERONI: ", dict.toString());
                ArrayList<Event> busy = new ArrayList<>();
                Iterator<?> keys = dict.keys();

                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    if (dict.get(key) instanceof JSONArray) {
                        JSONArray times = dict.getJSONArray(key);
                        for (int i = 0; i < times.length(); i++) {
                            JSONObject time = times.getJSONObject(i);
                            DateTime start = new DateTime(time.getLong("start_time") * 1000, DateTimeZone.UTC);
                            DateTime end = new DateTime(time.getLong("end_time") * 1000, DateTimeZone.UTC);
                            busy.add(new Event(start, end));
                        }
                    }
                }

                Collections.sort(busy);

                Log.d("BUSY LIST: ", busy.toString());

                DateTime from = new DateTime(AddPeopleActivity.fromDate[2], AddPeopleActivity.fromDate[1], AddPeopleActivity.fromDate[0], 0, 0);
                DateTime to = new DateTime(AddPeopleActivity.toDate[2], AddPeopleActivity.toDate[1], AddPeopleActivity.toDate[0], 0, 0);
                ArrayList<Event> poss = Scheduler.schedule(busy, 60, new Event(from, to));

                Log.d("POSS LIST: ", poss.toString());
            }
            else if (type == "find") {
                Log.d("RESPONSE: ", resp);
                JSONArray people = dict.getJSONArray("users");
                AddPeopleActivity.accountNames.clear();
                AddPeopleActivity.idMap = new HashMap<>();
                for (int i = 0; i < people.length(); i++) {
                    JSONObject person = people.getJSONObject(i);
                    String name = person.getString("user_name");
                    AddPeopleActivity.accountNames.add(name);
                    AddPeopleActivity.idMap.put(name, String.valueOf(person.getInt("user_id")));
                    adapter.notifyDataSetChanged();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}