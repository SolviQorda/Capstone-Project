package qorda_projects.tracktive.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by sorengoard on 11/01/2017.
 */

public class TracktiveAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private TracktiveAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new TracktiveAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}


