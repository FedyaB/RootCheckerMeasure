package edu.amd.spbstu.rootcheckermeasure.experiments.features.app_start_time;

import android.content.ComponentName;
import android.content.Intent;

import edu.amd.spbstu.rootcheckermeasure.MainActivity;
import edu.amd.spbstu.rootcheckermeasure.R;

/**
 * Launch time of an app NOT from Magisk Hide List
 */
public class NormTimeFeature extends AppStartTimeFeature {
    private static final String NORMAL_APP_NAME = "edu.amd.spbstu.rootnormalapp";

    public NormTimeFeature(MainActivity activity) {
        super(activity);
    }

    @Override
    protected int getAppId() {
        return 2; // Predefined ID determines whether it is NOT 'Hide List' app
    }

    @Override
    protected Intent createServiceIntent() {
        Intent serviceIntentNorm = new Intent();
        serviceIntentNorm.setComponent(new ComponentName(NORMAL_APP_NAME, NORMAL_APP_NAME + SERVICE_NAME));
        return serviceIntentNorm;
    }

    @Override
    public String getFeatureName() {
        return activity.getString(R.string.feature_norm);
    }
}
