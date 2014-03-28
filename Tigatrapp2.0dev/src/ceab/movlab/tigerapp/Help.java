package ceab.movlab.tigerapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;

/**
 * Main activity that user interacts with while performing the search..
 * 
 * @author John R.B. Palmer
 */
public class Help extends FragmentActivity {

	Context context = this;

	private static final String TIGER_HELP_URL = "http://tce.ceab.csic.es/tigaDev2/TigatrappHelp.html";
	private static final String TIGER_HELP_URL_CA = "http://tce.ceab.csic.es/tigaDev2/TigatrappHelp-ca.html";
	private static final String TIGER_HELP_URL_ES = "http://tce.ceab.csic.es/tigaDev2/TigatrappHelp-es.html";

	private WebView myWebView;

	String lang = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);
		if (PropertyHolder.getLanguage() == null) {
			Intent i2sb = new Intent(Help.this, Switchboard.class);
			startActivity(i2sb);
			finish();
		} else {
			Resources res = getResources();
			Util.setDisplayLanguage(res);
		}

		lang = PropertyHolder.getLanguage();

	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onResume() {

		if (Util.isOnline(context)) {
			setContentView(R.layout.webview);

			myWebView = (WebView) findViewById(R.id.webview);

			myWebView.getSettings().setSupportMultipleWindows(true);

			if (lang.equals("ca")) {
				myWebView.loadUrl(TIGER_HELP_URL_CA);
			}
			else if (lang.equals("es")) {
				myWebView.loadUrl(TIGER_HELP_URL_ES);
			} else{
				myWebView.loadUrl(TIGER_HELP_URL);
			}

		} else {
			setContentView(R.layout.webview_offline);
			((TextView) findViewById(R.id.offlineText)).setText(Html
					.fromHtml(getString(R.string.help_offline_html)));
		}

		super.onResume();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
			myWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
