package com.erichstark.mobieverywhere;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.erichstark.mobieverywhere.auth.AuthData;
import com.erichstark.mobieverywhere.fragments.GenerateNumberFragment;
import com.erichstark.mobieverywhere.fragments.OverpassFragment;
import com.erichstark.mobieverywhere.fragments.VolleyMessengerFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Account mAccount = null;
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(AuthData.ACCOUNT_TYPE);

        for (Account account : accounts) {
            if (AuthData.ACCOUNT_NAME.equals(account.name)) {
                mAccount = account;
            }
        }

        if (mAccount == null) {
            Intent intentActivity = new Intent(getApplicationContext(), AuthenticatorActivity.class);
            intentActivity.putExtra(AuthenticatorActivity.KEY_NEW_LOGIN, true);
            startActivity(intentActivity);
        }

        final String fullName;
        final String userName;


        userName = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_USERNAME);
        //password = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_PASSWORD);
        fullName = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_NAME);
        //token = accountManager.getUserData(mAccount, AccountManager.KEY_AUTHTOKEN);
        //userId = accountManager.getUserData(mAccount, AuthData.KEY_ACCOUNT_USER_ID);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

//        ActionBarDrawerToggle toggle;
//        toggle = new ActionBarDrawerToggle(
//                this,                  /* host Activity */
//                drawer,         /* DrawerLayout object */
//                toolbar,  /* nav drawer icon to replace 'Up' caret */
//                R.string.navigation_drawer_open,  /* "open drawer" description */
//                R.string.navigation_drawer_close  /* "close drawer" description */
//        ) {
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                Log.d("MOBI", "username: " + tvUserName.getText());
//                Log.d("MOBI", "fullname: " + fullName);
//            }
//        };


        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView tvFullName = (TextView) header.findViewById(R.id.fullName);
        TextView tvUserName = (TextView) header.findViewById(R.id.userName);
        tvFullName.setText(fullName);
        tvUserName.setText(userName);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_camera) {
            // Handle the camera action
            fragment = new GenerateNumberFragment();

        } else if (id == R.id.nav_send) {
            fragment = new VolleyMessengerFragment();

        } else if (id == R.id.nav_poi) {
            fragment = new OverpassFragment();

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_gallery) {

        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
