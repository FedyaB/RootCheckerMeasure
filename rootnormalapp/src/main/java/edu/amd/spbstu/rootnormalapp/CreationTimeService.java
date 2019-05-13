package edu.amd.spbstu.rootnormalapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

/**
 * A service that returns a timestamp of a moment when the app is initialized. This App is NOT in Magisk Hide List.
 */

public class CreationTimeService extends IntentService {
    private static final int NORM_APP_ID = 2;

    long startTime;

    public CreationTimeService() {
        super("CreationTimeService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            final String CH_ID = "CRT_SERV_CH_ID";
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CH_ID, "Time Stamp Service", NotificationManager.IMPORTANCE_LOW);
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(CreationTimeService.this, CH_ID).setContentTitle("").setContentText("").build();
            startForeground(2, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startTime = System.nanoTime();
        Intent passIntent = new Intent();
        passIntent.setComponent(new ComponentName("edu.amd.spbstu.rootcheckermeasure", "edu.amd.spbstu.rootcheckermeasure.GetTimeStampService"));
        passIntent.putExtra("ROOT_CHECKER_TIME_STAMP", startTime);
        passIntent.putExtra("ROOT_CHECKER_APP", NORM_APP_ID);
        ContextCompat.startForegroundService(CreationTimeService.this, passIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }
}
