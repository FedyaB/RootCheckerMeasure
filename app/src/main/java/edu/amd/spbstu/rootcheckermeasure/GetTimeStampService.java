package edu.amd.spbstu.rootcheckermeasure;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * A service which is used to obtain timestamp from another app and the kind of this app (Hide List or not)
 */

public class GetTimeStampService extends IntentService {
    public static final String ROOT_CHECKER_PROCEED_TIME_STAMP = "ROOT_CHECKER_PROCEED_TIME_STAMP";

    public GetTimeStampService() {
        super("GetTimeStampService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Create notification channel used to obtain data from another app
        if (Build.VERSION.SDK_INT >= 26) {
            final String CH_ID = "GS_SERV_CH_ID";
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CH_ID, "Time Stamp Service", NotificationManager.IMPORTANCE_LOW);
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(GetTimeStampService.this, CH_ID).setContentTitle("").setContentText("").build();
            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent broadcastIntent = new Intent(ROOT_CHECKER_PROCEED_TIME_STAMP);
        broadcastIntent.putExtra("ROOT_CHECKER_TIME_STAMP", intent.getLongExtra("ROOT_CHECKER_TIME_STAMP", 0));
        broadcastIntent.putExtra("ROOT_CHECKER_APP", intent.getIntExtra("ROOT_CHECKER_APP", 0));
        broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(broadcastIntent);
    }
}
