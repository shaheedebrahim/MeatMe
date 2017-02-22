package com.bandaids.meatme;

import android.os.AsyncTask;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendTask extends AsyncTask<BasicNameValuePair, Void, HttpResponse> {
    //HttpURLConnection connection;
    String urlName;

    public SendTask(String url) {
        urlName = url;
    }

        /*final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        private static void trustAllHosts() {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }

                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }
            } };

            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection
                        .setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    public HttpResponse doInBackground(BasicNameValuePair... mess) {
            /*for (int i = 0; i < mess.length; i++) {
                try {
                    //Create connection
                    URL url = new URL(urlName);
                    if (url.getProtocol().toLowerCase().equals("https")) {
                        trustAllHosts();
                        Log.d("TEST: ", "HTTPS PROTOCOL USED");
                        HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                        https.setHostnameVerifier(DO_NOT_VERIFY);
                        connection = https;
                    } else {
                        connection = (HttpURLConnection) url.openConnection();
                    }

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty(mess[i].left, mess[i].right);

                    connection.setUseCaches(false);
                    connection.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(urlName);
                    wr.close();

                    //Get Response
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                    String line;
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
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

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}