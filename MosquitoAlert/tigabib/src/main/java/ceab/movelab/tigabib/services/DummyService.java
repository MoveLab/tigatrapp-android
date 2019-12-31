package ceab.movelab.tigabib.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.Util;

// https://www.spiria.com/en/blog/mobile-development/hiding-foreground-services-notifications-in-android/
public class DummyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notif = new NotificationCompat.Builder(this, "")
                .setSmallIcon(R.drawable.ic_stat_mission)
                .build();
        this.startForeground(Util.NOTIFICATION_ID_SAMPLE, notif);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}