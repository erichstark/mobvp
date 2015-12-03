package com.erichstark.mobieverywhere.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erichstark.mobieverywhere.AuthenticatorActivity;
import com.erichstark.mobieverywhere.R;
import com.erichstark.mobieverywhere.auth.AuthData;
import com.erichstark.mobieverywhere.mserver.MobileServerGenRandNumber;

/**
 * Created by Erich on 01/12/15.
 */
public class GenerateNumberFragment extends Fragment {

    String token;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_generate_number, container, false);

        Account mAccount = null;
        AccountManager accountManager = AccountManager.get(getActivity());
        Account[] accounts = accountManager.getAccountsByType(AuthData.ACCOUNT_TYPE);

        for (Account account : accounts) {
            if(AuthData.ACCOUNT_NAME.equals(account.name)) {
                mAccount = account;
            }
        }

        //username = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_USERNAME);
        //password = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_PASSWORD);
        //profilName = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_NAME);
        token = accountManager.getUserData(mAccount, AccountManager.KEY_AUTHTOKEN);
        //userId = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_USER_ID);


        view.findViewById(R.id.info_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MYTOKEN", "text: " + token);
                new MobileServerGenRandNumber(GenerateNumberFragment.this).execute(token);
            }
        });

        return view;
    }

    public void onBackgroundTaskCompleted(int number) {
        Log.d("MYTOKEN", "number: " + number);
        if (number == -1) {
            Intent intent = new Intent(getActivity(), AuthenticatorActivity.class);
            intent.putExtra(AuthenticatorActivity.KEY_NEW_LOGIN, true);
            startActivity(intent);
        }
        ((TextView) view.findViewById(R.id.info_number)).setText(String.valueOf(number));

    }
}
