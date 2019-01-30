package ceab.movelab.tigabib;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import ceab.movelab.tigabib.model.Notification;
import ceab.movelab.tigabib.model.RealmHelper;
import io.realm.Realm;

public class NotificationActivity extends Activity {

	public static String TAG = "NotificationActivity";

	public static String NOTIFICATION_ID = "notification_id";

	private String lang;

	private Realm mRealm;
	private int notificationId;
	private WebView myWebView;

	//private FirebaseAnalytics mFirebaseAnalytics;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.notification_layout);

		mRealm = RealmHelper.getInstance().getRealm(this);

        final Bundle b = getIntent().getExtras();
        if ( b != null && b.containsKey(NOTIFICATION_ID) ) {
            notificationId = b.getInt(NOTIFICATION_ID);
        }
        else
        	this.finish();

		final Notification notif = RealmHelper.getInstance().getNotificationById(mRealm, notificationId);
Util.logInfo(TAG, String.valueOf(notif.isAcknowledged()));
		mRealm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				notif.setAcknowledged(true);
			}
		});
Util.logInfo(TAG, String.valueOf(notif.isAcknowledged()));

		myWebView = (WebView) findViewById(R.id.notificationWebview);
		myWebView.getSettings().setAllowFileAccess(true);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		myWebView.loadData(notif.getExpertHtml(), "text/html", "UTF-8");
		acknowledgeNotification(notif.getId());

		//mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
	}

	private void acknowledgeNotification(int notifId) {
		String notificationUrl = Util.API_NOTIFICATION + "?id=" + notifId + "&acknowledged=true";
Util.logInfo("===========", "BuildConfig.DEBUG >> " + BuildConfig.DEBUG);
Util.logInfo("===========", Util.URL_TIGASERVER_API_ROOT + notificationUrl);
		Ion.with(this)
			.load(Util.URL_TIGASERVER_API_ROOT + notificationUrl)
			.setHeader("Accept", "application/json")
			.setHeader("Content-type", "application/json")
			.setHeader("Authorization", UtilLocal.TIGASERVER_AUTHORIZATION)
			.setBodyParameter("id", String.valueOf(notifId))
			.setBodyParameter("acknowledged", "true")
			.as(new TypeToken<Notification>(){})
			.setCallback(new FutureCallback<Notification>() {
				@Override
				public void onCompleted(Exception e, Notification result) {
					// do stuff with the result or error
					if ( result != null )
						Util.logInfo(TAG, result.toString());
				}
			});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if ( !Util.setDisplayLanguage(getResources()).equals(lang) ) {
			finish();
			startActivity(getIntent());
		}

		// [START set_current_screen]
		//mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_notification", "Notification Single");
		// [END set_current_screen]
	}

	@Override
	protected void onDestroy() {
		if ( mRealm != null ) mRealm.close(); // Remember to close Realm when done.
		super.onDestroy();
	}
}
