package qorda_projects.tracktive.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by sorengoard on 11/01/2017.
 */

public class TracktiveSyncService extends Service{
    private static final Object sSyncAdapterLock = new Object();
    private static TracktiveSyncAdapter sTracktiveSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sTracktiveSyncAdapter == null) {
                sTracktiveSyncAdapter = new TracktiveSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sTracktiveSyncAdapter.getSyncAdapterBinder();
    }
}
