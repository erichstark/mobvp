package com.erichstark.mobieverywhere.mserver;

import android.os.AsyncTask;
import android.util.Log;

import com.erichstark.mobieverywhere.AuthenticatorActivity;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Erich on 01/12/15.
 */
public class MobileServer extends AsyncTask<String, Void, MobileUser> {

    public static final String SERVER_API_KEY = "3C7e56ZRFQcMXXr";
    public static final String SERVER_LOGIN_URL = "https://mobv.mcomputing.fei.stuba.sk/index.php?r=device/auth";
    AuthenticatorActivity caller;

    public MobileServer(AuthenticatorActivity caller) {
        this.caller = caller;
    }

    @Override
    protected MobileUser doInBackground(String... params) {

        MobileUser user = null;
        String username = params[0];
        String password = md5(params[1]);

        Log.d("qqq", "McomputingServerLogin.doInBackground('" + params[0] + "', '" + params[1] + "') pass hash: '" + password + "'");


        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVER_LOGIN_URL);

        String mobv_data = "{\"api_key\":\""+SERVER_API_KEY+"\",\"username\":\""+username+"\",\"password\":\""+password+"\"}";
        httpPost.addHeader("X-MOBV-Data", mobv_data);
        httpPost.addHeader("Content-Type", "application/json");

        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            Log.d("qqq", "McomputingServerLogin.doInBackground(): responseString: " + responseString);

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                Log.e("qqq", "McomputingServerLogin.doInBackground(): Server error: " + response.getStatusLine().getStatusCode());
                return null;
            }

            user = new Gson().fromJson(responseString, MobileUser.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    protected void onPostExecute(MobileUser user) {
        caller.onBackgroundTaskCompleted(user);
    }

    private String md5(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // pridane osetretie kvoli exception
        if (md != null) {
            md.update(str.getBytes());
        }

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}