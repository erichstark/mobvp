package com.erichstark.mobieverywhere;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.erichstark.mobieverywhere.auth.AuthData;
import com.erichstark.mobieverywhere.mserver.MobileServer;
import com.erichstark.mobieverywhere.mserver.MobileUser;

/**
 * Created by Erich on 01/12/15.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public static final String KEY_USERNAME = "authenticatoractivity.username";
    public static final String KEY_PASSWORD = "authenticatoractivity.password";
    public static final String KEY_NEW_LOGIN = "authenticatoractivity.newlogin";

    private String password = "";
    private String username = "";
    private boolean new_login = false;
    private Account mAccount = null;
    AccountManager accountManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_authenticator);

        Intent intent = getIntent();
        mAccount = loadAccount();

        if(intent.hasExtra(AuthenticatorActivity.KEY_NEW_LOGIN))
            new_login = true;

        if(mAccount != null && new_login == false) {
            logIntoApplication();
        }
    }

    private Account loadAccount() {
        accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(AuthData.ACCOUNT_TYPE);

        for (Account account : accounts) {
            if(AuthData.ACCOUNT_NAME.equals(account.name)) {
                return account;
            }
        }
        return null;
    }

    public void submitOnClick(View view) {
        username = ((TextView) findViewById(R.id.login_username)).getText().toString();
        password = ((TextView) findViewById(R.id.login_password)).getText().toString();

        new MobileServer(this).execute(username, password);
    }

    private void logIntoApplication() {
        username = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_USERNAME);
        password = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_PASSWORD);

        new MobileServer(this).execute(username, password);
    }

    public void onBackgroundTaskCompleted(MobileUser user) {
        if(user == null) {
            Context context = getApplicationContext();
            CharSequence text = "Loggin error!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, AuthData.ACCOUNT_NAME);
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, AuthData.ACCOUNT_TYPE);
        data.putString(AccountManager.KEY_AUTHTOKEN, user.getSession_token());

        String profilName = user.getFirst_name() + " " + user.getLast_name();
        data.putString(AuthData.KEY_ACCOUNT_NAME, profilName);
        data.putString(AuthData.KEY_ACCOUNT_USERNAME, username);
        data.putString(AuthData.KEY_ACCOUNT_PASSWORD, password);
        data.putString(AuthData.KEY_ACCOUNT_USER_ID, String.valueOf(user.getUser_id()));

        if(mAccount == null) {
            Log.d("qqq", "Account - creating");
            final Account account = new Account(AuthData.ACCOUNT_NAME, AuthData.ACCOUNT_TYPE);
            accountManager.addAccountExplicitly(account, password, data);
        } else {
            Log.d("qqq", "Account - exist, storing data");
            accountManager.setUserData(mAccount, AccountManager.KEY_AUTHTOKEN, user.getSession_token());
            accountManager.setUserData(mAccount, AuthData.KEY_ACCOUNT_NAME, profilName);
            accountManager.setUserData(mAccount, AuthData.KEY_ACCOUNT_USERNAME, username);
            accountManager.setUserData(mAccount, AuthData.KEY_ACCOUNT_PASSWORD, password);
            accountManager.setUserData(mAccount, AuthData.KEY_ACCOUNT_USER_ID, String.valueOf(user.getUser_id()));
        }

        Intent accountInfoIntent = new Intent(getApplicationContext(), MainActivity.class);
        accountInfoIntent.putExtras(data);
        startActivity(accountInfoIntent);
    }
}