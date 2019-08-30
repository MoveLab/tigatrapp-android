package ceab.movelab.tigabib.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// https://www.spiria.com/en/blog/mobile-development/hiding-foreground-services-notifications-in-android/
public class DummyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Notification.Builder builder = new Notification.Builder(this);
        this.startForeground(Sample.NOTIFICATION_ID_SAMPLE, builder.getNotification());
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