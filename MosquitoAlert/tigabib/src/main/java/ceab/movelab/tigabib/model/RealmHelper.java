package ceab.movelab.tigabib.model;

import android.content.Context;

import java.util.List;

import ceab.movelab.tigabib.Util;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmHelper {

    private static RealmHelper instance = null;

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
       //if ( mRealm == null ) {
            // https://github.com/realm/realm-java/tree/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample
            // if migration is needed
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("myRealmDB.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
           //mRealm = Realm.getInstance(config);
        //}
        return Realm.getInstance(config);
    }

/*
    public Realm getRealm() {
        return mRealm;
    }
*/

    public void addOrUpdateNotificationList(final Realm realm, final List<Notification> notifList) {
        if ( notifList != null ) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(notifList);
                }
            });
        }
    }

    public Notification getNotificationById(final Realm realm, int id) {
        return realm.where(Notification.class).equalTo("id", id).findFirst();
    }

/*    public RealmResults<Notification> getAllNotifications() {
        //RealmResults<Notification> results = mRealm.where(Notification.class).findAll();
        return mRealm.where(Notification.class).findAll();
    }*/

    public RealmResults<Notification> getNotificationsRead(final Realm realm, boolean ack) {
        RealmResults<Notification> results = realm.where(Notification.class).equalTo("acknowledged", ack).findAll();
        Util.logInfo(this.getClass().getName(), "getNotificationsRead (" + ack + ") >> " + results.size());
        return  realm.where(Notification.class).equalTo("acknowledged", ack).findAll();
    }

    public int getNewNotificationsCount(final Realm realm) {
        //RealmResults<Notification> results = mRealm.where(Notification.class).findAll(); // !!! filter by new
        //RealmResults<Notification> results = mRealm.where(Notification.class).equalTo("read", false).findAll();
        RealmResults<Notification> results =  getNotificationsRead(realm, false);
        return results.size();
    }

}
