package com.erichstark.mobieverywhere.mserver;

import android.os.AsyncTask;
import android.util.Log;

import com.erichstark.mobieverywhere.AccountInfoActivity;
import com.erichstark.mobieverywhere.fragments.GenerateNumberFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Random;

/**
 * Created by Erich on 01/12/15.
 */
public class MobileServerGenRandNumber extends AsyncTask<String, Void, Integer> {

    public static final String SERVER_API_KEY = "3C7e56ZRFQcMXXr";
    public static final String SERVER_NUMBER_URL = "https://mobv.mcomputing.fei.stuba.sk/index.php?r=device/number";

    GenerateNumberFragment caller;

    public MobileServerGenRandNumber(GenerateNumberFragment caller) {
        this.caller = caller;
    }

    @Override
    protected Integer doInBackground(String... params) {
        Log.d("qqq", "McomputingServerLogin.doInBackground(" + params.toString() + ")");

        Random rand = new Random();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVER_NUMBER_URL);
        int number = rand.nextInt(10000) + 1000;
        String token = params[0];

        String mobv_data = "{\"api_key\":\""+SERVER_API_KEY+"\",\"token\":\""+token+"\",\"number\":\""+number+"\"}";
        httpPost.addHeader("X-MOBV-Data", mobv_data);
        httpPost.addHeader("Content-Type", "application/json");

        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());

            Log.d("qqq", "MobileServerGenRandNumber.doInBackground(): responseString: " + responseString);

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                Log.e("qqq", "MobileServerGenRandNumber.doInBackground(): Server error");
                return Integer.valueOf(-1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Integer.valueOf(number);
    }

    protected void onPostExecute(Integer number) {
        caller.onBackgroundTaskCompleted(number.intValue());
    }
}
