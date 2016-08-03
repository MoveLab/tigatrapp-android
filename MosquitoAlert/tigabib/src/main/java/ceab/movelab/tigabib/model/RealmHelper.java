package ceab.movelab.tigabib.model;

import android.content.Context;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmHelper {

    private static RealmHelper instance = null;
    private Realm mRealm;

    // Exists only to defeat instantiation
    protected RealmHelper() { }

    public static void initialize(Context ctx) {
        instance = null;
        getInstance();
    }

    public static synchronized RealmHelper getInstance() {
        if ( instance == null ) {
            instance = new RealmHelper();
        }
        return instance;
    }

    public Realm getRealm(Context ctx) {
       if ( mRealm == null ) {
            // https://github.com/realm/realm-java/tree/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample
            // if migration is needed
            RealmConfiguration config = new RealmConfiguration.Builder(ctx)
                    .name("myRealmDB.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
           mRealm = Realm.getInstance(config);
        }
        return mRealm;
    }

    public Realm getRealm() {
        return mRealm;
    }

    public void addOrUpdateNotificationList(final List<Notification> notifList) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(notifList);
            }
        });
    }

    public Notification getNotificationById(int id) {
        Notification results = mRealm.where(Notification.class).equalTo("id", id).findFirst();
        return results;
    }

    public RealmResults<Notification> getAllNotifications() {
        RealmResults<Notification> results = mRealm.where(Notification.class).findAll();
        return results;
    }

    public int getNewNotificationsCount() {
        RealmResults<Notification> results = mRealm.where(Notification.class).findAll(); // !!! filter by new
        return results.size();
    }

}
