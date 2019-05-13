package edu.amd.spbstu.rootcheckermeasure.experiments.features.app_start_time;

import android.content.ComponentName;
import android.content.Intent;

import edu.amd.spbstu.rootcheckermeasure.MainActivity;
import edu.amd.spbstu.rootcheckermeasure.R;

/**
 * Launch time of an app from Magisk Hide List
 */
public class HideTimeFeature extends AppStartTimeFeature {
    private static final String HIDELIST_APP_NAME = "edu.amd.spbstu.roothidelist";

    public HideTimeFeature(MainActivity activity) {
        super(activity);
    }

    @Override
    protected int getAppId() {
        return 1; // Predefined ID determines whether it is 'Hide List' app
    }

    @Override
    protected Intent createServiceIntent() {
        Intent serviceIntentHide = new Intent();
        serviceIntentHide.setComponent(new ComponentName(HIDELIST_APP_NAME, HIDELIST_APP_NAME + SERVICE_NAME));
        return serviceIntentHide;
    }

    @Override
    public String getFeatureName() {
        return activity.getString(R.string.feature_hide);
    }
}
