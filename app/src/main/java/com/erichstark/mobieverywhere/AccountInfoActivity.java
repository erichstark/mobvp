package com.erichstark.mobieverywhere;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.erichstark.mobieverywhere.auth.AuthData;
import com.erichstark.mobieverywhere.mserver.MobileServer;
import com.erichstark.mobieverywhere.mserver.MobileServerGenRandNumber;

/**
 * Created by Erich on 01/12/15.
 */
public class AccountInfoActivity extends AppCompatActivity {

    private String username = "";
    private String password = "";
    private String token = "";
    private String profilName = "";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountinfo);

        Account mAccount = null;
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(AuthData.ACCOUNT_TYPE);

        for (Account account : accounts) {
            if(AuthData.ACCOUNT_NAME.equals(account.name)) {
                mAccount = account;
            }
        }

        if(mAccount == null) {
            Intent intentActivity = new Intent(getApplicationContext(), AuthenticatorActivity.class);
            intentActivity.putExtra(AuthenticatorActivity.KEY_NEW_LOGIN, true);
            startActivity(intentActivity);
        }

        username = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_USERNAME);
        password = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_PASSWORD);
        profilName = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_NAME);
        token = accountManager.getUserData(mAccount, AccountManager.KEY_AUTHTOKEN);
        userId = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_USER_ID);

        ((TextView) findViewById(R.id.info_username)).setText(username);
        ((TextView) findViewById(R.id.info_name)).setText(profilName);
        ((TextView) findViewById(R.id.info_token)).setText(token);
        ((TextView) findViewById(R.id.info_userid)).setText(userId);
    }

    public void onBackgroundTaskCompleted(int number) {
        if(number == -1) {
            Intent intent = new Intent(getApplicationContext(), AuthenticatorActivity.class);
            intent.putExtra(AuthenticatorActivity.KEY_NEW_LOGIN, true);
            startActivity(intent);
        }
        ((TextView) findViewById(R.id.info_number)).setText(String.valueOf(number));
    }

    public void submitOnClick(View view) {
        Log.d("MYTOKEN", "text: " + token);
        //new MobileServerGenRandNumber(this).execute(token);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activityinfo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(false);
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menu_login:
                item.setChecked(true);
                intent = new Intent(getApplicationContext(), AuthenticatorActivity.class);
                intent.putExtra(AuthenticatorActivity.KEY_NEW_LOGIN, true);
                startActivity(intent);
                return true;
            case R.id.menu_info:
                item.setChecked(true);
                return true;
//            case R.id.menu_overpass:
//                item.setChecked(true);
//                intent = new Intent(getApplicationContext(), OverpassActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.menu_overpass_settings:
//                item.setChecked(true);
//                intent = new Intent(getApplicationContext(), OverpassSettingActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.menu_volley:
//                item.setChecked(true);
//                intent = new Intent(getApplicationContext(), VolleyActivity.class);
//                startActivity(intent);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
