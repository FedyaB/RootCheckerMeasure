package edu.amd.spbstu.rootcheckermeasure.experiments.features;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

import edu.amd.spbstu.rootcheckermeasure.R;

/**
 * PackageManager response time feature
 *  The time needed for PackageManager to return the list of installed packages
 */
public class PackageManagerFeature implements Feature {
    private List<Long> stamps;
    private long timeStamp;
    private Activity activity;

    public PackageManagerFeature(Activity activity) {
        this.activity = activity;
        stamps = new ArrayList<>();
    }

    @Override
    public void runRoutine() {
        String pkgName = callPackageManager();
        // Consider execution time as zero if a request to PackageManager has failed
        stamps.add(pkgName == null ?  0 : timeStamp);
    }

    /**
     * Call the PackageManager method and measure the time needed to execute it
     * * The execution time is returned in timeStamp field
     * @return The name of the first package in a package list if obtained, null otherwise
     */
    private String callPackageManager() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        timeStamp = System.nanoTime();
        List<ResolveInfo> pkgAppsList = activity.getPackageManager().queryIntentActivities( mainIntent, 0);
        timeStamp = System.nanoTime() - timeStamp;
        return pkgAppsList == null || pkgAppsList.isEmpty() ? null : pkgAppsList.get(0).activityInfo.packageName;
    }

    @Override
    public String getFeatureName() {
        return activity.getString(R.string.feature_pm);
    }

    @Override
    public String getString(int i) {
        return Long.toString(stamps.get(i));
    }

    @Override
    public int size() {
        return stamps.size();
    }

    @Override
    public void clear() {
        stamps.clear();
    }

    @Override
    public void destroy() {
        //Do nothing
    }
}
