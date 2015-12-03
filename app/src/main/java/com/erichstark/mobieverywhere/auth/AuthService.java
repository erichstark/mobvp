package com.erichstark.mobieverywhere.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Erich on 01/12/15.
 */

public class AuthService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        Authenticator authenticator = new Authenticator(this);
        return authenticator.getIBinder();
    }
}