package edu.amd.spbstu.rootcheckermeasure.experiments.features.app_start_time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import edu.amd.spbstu.rootcheckermeasure.GetTimeStampService;
import edu.amd.spbstu.rootcheckermeasure.MainActivity;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.Feature;

/**
 * App launch time feature (abstract)
 */
abstract class AppStartTimeFeature implements Feature {
    static final String SERVICE_NAME = ".CreationTimeService"; // A name of the service that returns timestamp at the time of launch

    private List<Long> stamps;
    MainActivity activity;

    private long timeStampBegin;
    private TimeStampReceiver timeStampReceiver;
    private Intent serviceIntent;
    private boolean readyTest;

    /**
     * Receiver of broadcasts from GetTimeStampService that is called by launched apps
     */
    private class TimeStampReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get app ID and launch timestamp from CreationTimeService implemented in launched app
            int app_id = intent.getIntExtra("ROOT_CHECKER_APP", 0);
            if (app_id == getAppId())
                stamps.add(intent.getLongExtra("ROOT_CHECKER_TIME_STAMP", 0) - timeStampBegin);
            readyTest = true;
        }
    }

    /**
     * Get app ID which allows to determine what kind of app was launched
     * @return An app ID
     */
    protected abstract int getAppId();

    /**
     * Create service intent which allows to start a CreationTimeService service of a specific app
     * @return A service intent
     */
    protected abstract Intent createServiceIntent();

    AppStartTimeFeature(MainActivity activity) {
        this.activity = activity;
        this.stamps = new ArrayList<>();

        timeStampReceiver = new TimeStampReceiver();
        IntentFilter intentFilter = new IntentFilter(GetTimeStampService.ROOT_CHECKER_PROCEED_TIME_STAMP);
        this.activity.registerReceiver(timeStampReceiver, intentFilter);

        serviceIntent = createServiceIntent();
    }

    @Override
    public String getString(int i) {
        return Long.toString(stamps.get(i));
    }

    @Override
    public void runRoutine() {
        try {
            timeStampBegin = System.nanoTime();
            ContextCompat.startForegroundService(activity, serviceIntent);
            readyTest = false;
            // Busy waiting until the launch time is obtained
            while (!readyTest)
                Thread.sleep(200);
        }
        catch (Exception e){
            //Do nothing
        }
    }

    @Override
    public void clear() {
        stamps.clear();
    }

    @Override
    public int size() {
        return stamps.size();
    }

    @Override
    public void destroy() {
        activity.unregisterReceiver(timeStampReceiver);
    }
}
