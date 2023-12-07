package glotech.smartgasapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import glotech.smartgasapp.R;

import java.io.IOException;

public class NotificationForegroundService extends Service {
    private static final String CHANNEL_ID = "NotificationForegroundServiceChannel";
    private Handler handler;
    private Runnable notificationRunnable;
    private double gasVolume;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        scheduleNotificationCheck();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This service is not designed to be bound to an activity
        return null;
    }

    public void setGasVolume(double gasVolume) {
        this.gasVolume = gasVolume;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Update gasVolume here if needed
        Notification notification = buildNotification();
        startForeground(1, notification);
        // Schedule the task to run periodically using AlarmManager
        scheduleTask();
        return START_STICKY;
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Gas Guard Notification")
                .setContentText("您的瓦斯容量小於" + 3 + "kg")
                .setSmallIcon(R.drawable.baseline_shopping_cart_24)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(false) // Make the notification dismissible
                .build();

        return notification;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    private void scheduleTask() {
        Intent taskIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                taskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Schedule the alarm to repeat at 14:00 and 18:00
        scheduleAlarm(alarmManager, pendingIntent, 16, 00);
        scheduleAlarm(alarmManager, pendingIntent, 18, 0);
    }

    private void scheduleAlarm(AlarmManager alarmManager, PendingIntent pendingIntent, int hour, int minute) {
        long intervalMillis = 24 * 60 * 60 * 1000; // 24 hours
        long triggerAtMillis = getTriggerAtMillis(hour, minute);

        // Set the alarm to repeat at the specified time every day
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
    }

    private long getTriggerAtMillis(int hour, int minute) {
        // Get the current time
        long currentTimeMillis = System.currentTimeMillis();

        // Calculate the desired time in millis
        long desiredTimeMillis = currentTimeMillis + ((hour * 60 + minute) * 60 * 1000);

        // If the desired time has already passed, schedule it for the next day
        if (desiredTimeMillis <= currentTimeMillis) {
            desiredTimeMillis += 24 * 60 * 60 * 1000;
        }

        return desiredTimeMillis;
    }

    // Other methods remain unchanged

    private void scheduleNotificationCheck() {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Schedule the first notification check at the next minute
        long intervalMillis = 60 * 1000; // 1 minute
        long triggerAtMillis = System.currentTimeMillis() + intervalMillis;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}


