package ceab.movlab.tigerapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ceab.movelab.tigerapp.R;

/**
 * Main activity that user interacts with while performing the search..
 * 
 * @author John R.B. Palmer
 */
public class Help extends FragmentActivity {

	Context context = this;

	private static final String TIGER_HELP_URL_EN = "http://tigaserver.atrapaeltigre.com/help/android/en";
	private static final String TIGER_HELP_URL_CA = "http://tigaserver.atrapaeltigre.com/help/android/ca";
	private static final String TIGER_HELP_URL_ES = "http://tigaserver.atrapaeltigre.com/help/android/es";

	private WebView myWebView;

	String lang;
	Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		lang = Util.setDisplayLanguage(res);

	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onResume() {

		res = getResources();
		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		// if (Util.isOnline(context)) {
		setContentView(R.layout.webview);

		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new WebViewClient());

		if (lang.equals("ca")) {
			myWebView.loadUrl(TIGER_HELP_URL_CA);
		} else if (lang.equals("es")) {
			myWebView.loadUrl(TIGER_HELP_URL_ES);
		} else {
			myWebView.loadUrl(TIGER_HELP_URL_EN);
		}

		/*
		 * } else { setContentView(R.layout.webview_offline); ((TextView)
		 * findViewById(R.id.offlineText)).setText(Html.fromHtml(
		 * getString(R.string.help_offline_html), null, new TigaTagHandler()));
		 * }
		 */
		super.onResume();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Util.isOnline(context)) {
			// Check if the key event was the Back button and if there's history
			if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
				myWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
