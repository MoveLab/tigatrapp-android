package ceab.movelab.tigabib;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Main activity that user interacts with while performing the search..
 * 
 * @author John R.B. Palmer
 */
public class HelpActivity extends FragmentActivity {

	private static final String TIGER_HELP_URL_EN = UtilLocal.URL_TIGASERVER + "help/android/en";
	private static final String TIGER_HELP_URL_CA = UtilLocal.URL_TIGASERVER + "help/android/ca";
	private static final String TIGER_HELP_URL_ES = UtilLocal.URL_TIGASERVER + "help/android/es";

	private static final String TIGER_HELP_URL_OFFLINE_EN = "file:///android_asset/html/help_en.html";
	private static final String TIGER_HELP_URL_OFFLINE_CA = "file:///android_asset/html/help_ca.html";
	private static final String TIGER_HELP_URL_OFFLINE_ES = "file:///android_asset/html/help_es.html";

	private WebView myWebView;

	String lang;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {

		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		// if (Util.isOnline(context)) {
		setContentView(R.layout.webview);

		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				if (lang.equals("ca"))
					myWebView.loadUrl(TIGER_HELP_URL_OFFLINE_CA);

				else if (lang.equals("es"))
					myWebView.loadUrl(TIGER_HELP_URL_OFFLINE_ES);

				else
					myWebView.loadUrl(TIGER_HELP_URL_OFFLINE_EN);
			}
		});

		myWebView.getSettings().setAllowFileAccess(true);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		if (Build.VERSION.SDK_INT >= 7) {
			WebViewApi7.api7settings(myWebView, this);
		}

		if (!Util.isOnline(this)) { // loading offline only if not online
			myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}

		if (lang.equals("ca"))
			myWebView.loadUrl(TIGER_HELP_URL_CA);
		else if (lang.equals("es"))
			myWebView.loadUrl(TIGER_HELP_URL_ES);
		else
			myWebView.loadUrl(TIGER_HELP_URL_EN);

		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Util.isOnline(this)) {
			// Check if the key event was the Back button and if there's history
			if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
				myWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.consent_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.language) {
			Intent i = new Intent(HelpActivity.this, LanguageSelector.class);
			startActivity(i);
			return true;
		}
		return false;
	}

}
