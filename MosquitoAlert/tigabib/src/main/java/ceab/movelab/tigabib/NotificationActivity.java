package ceab.movelab.tigabib;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		final Bundle b = getIntent().getExtras();
		if (b.containsKey(NOTIFICATION_ID)) {
			notificationId = b.getInt(NOTIFICATION_ID);
		}

		setContentView(R.layout.notification_layout);

		mRealm = RealmHelper.getInstance().getRealm();

		final Notification notif = RealmHelper.getInstance().getNotificationById(notificationId);// Update person in a transaction
Util.logInfo(this, TAG, String.valueOf(notif.isRead()));
		mRealm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				notif.setRead(true);
			}
		});
Util.logInfo(this, TAG, String.valueOf(notif.isRead()));

		myWebView = (WebView) findViewById(R.id.notificationWebview);
		myWebView.getSettings().setAllowFileAccess(true);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		myWebView.loadUrl(notif.getExpertHtml());
	}

	@Override
	protected void onResume() {
		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		super.onResume();
	}

}
