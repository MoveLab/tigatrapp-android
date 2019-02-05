/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: info@atrapaeltigre.com
 * 
 * This file is part of Tigatrapp.
 * 
 * Tigatrapp is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Tigatrapp is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Tigatrapp.  If not, see <http://www.gnu.org/licenses/>.
 **/

package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Displays the License screen.
 * 
 * @author John R.B. Palmer
 * 
 */
public class LicenseActivity extends Activity {

	private static final String LICENSE_URL_EN = UtilLocal.URL_TIGASERVER + "license/android/en";
	private static final String LICENSE_URL_CA = UtilLocal.URL_TIGASERVER + "license/android/ca";
	private static final String LICENSE_URL_ES = UtilLocal.URL_TIGASERVER + "license/android/es";
	private static final String LICENSE_URL_ZH = UtilLocal.URL_TIGASERVER + "license/android/zh-cn";

	private static final String LICENSE_URL_OFFLINE_EN = "file:///android_asset/html/license_en.html";
	private static final String LICENSE_URL_OFFLINE_CA = "file:///android_asset/html/license_ca.html";
	private static final String LICENSE_URL_OFFLINE_ES = "file:///android_asset/html/license_es.html";
	private static final String LICENSE_URL_OFFLINE_ZH = "file:///android_asset/html/license_zh.html";

	private WebView myWebView;

	private String lang;

	//private FirebaseAnalytics mFirebaseAnalytics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		// if (Util.isOnline(context)) {
		setContentView(R.layout.webview);

		// Obtain the FirebaseAnalytics instance.
		//mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		// [START set_current_screen]
		//mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_license", "License");
		// [END set_current_screen]

		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				if (lang.equals("ca"))
					myWebView.loadUrl(LICENSE_URL_OFFLINE_CA);
				else if (lang.equals("es"))
					myWebView.loadUrl(LICENSE_URL_OFFLINE_ES);
				else if (lang.equals("zh"))
					myWebView.loadUrl(LICENSE_URL_OFFLINE_ZH);
				else
					myWebView.loadUrl(LICENSE_URL_OFFLINE_EN);

			}

		});

		myWebView.getSettings().setAllowFileAccess(true);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		WebViewApi7.api7settings(myWebView, this); // replace this

		if (!Util.isOnline(this)) { // loading offline only if not online
			myWebView.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}

		if (lang.equals("ca"))
			myWebView.loadUrl(LICENSE_URL_CA);
		else if (lang.equals("es"))
			myWebView.loadUrl(LICENSE_URL_ES);
		else if (lang.equals("zh"))
			myWebView.loadUrl(LICENSE_URL_ZH);
		else
			myWebView.loadUrl(LICENSE_URL_EN);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ( Util.isOnline(this) ) {
			// Check if the key event was the Back button and if there's history
			if ( (keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack() ) {
				myWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.license_menu, menu);
		Util.setMenuTextColor(menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.language) {

			Intent i = new Intent(LicenseActivity.this, LanguageSelectorActivity.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.gpl) {

			Intent i = new Intent(LicenseActivity.this, GPLViewActivity.class);
			startActivity(i);
			return true;
		}
		return false;
	}

}
