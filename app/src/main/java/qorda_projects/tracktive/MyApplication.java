package qorda_projects.tracktive;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by sorengoard on 08/02/2017.
 */

public class MyApplication extends Application{

    public Tracker mTracker;

    public void startTracking( ) {
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.tracking_info);
            ga.enableAutoActivityReports(this);
        }

    }

    public Tracker getTracker () {

        startTracking();
        return mTracker;
    }

}
