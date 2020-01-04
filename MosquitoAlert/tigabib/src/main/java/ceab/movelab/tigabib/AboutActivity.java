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
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Displays the About screen.
 * 
 * @author John R.B. Palmer
 * 
 */
public class AboutActivity extends Activity {

	//private static String TAG = "About";

	private static final String ABOUT_URL_EN = UtilLocal.URL_TIGASERVER + "about/android/en";
	private static final String ABOUT_URL_CA = UtilLocal.URL_TIGASERVER + "about/android/ca";
	private static final String ABOUT_URL_ES = UtilLocal.URL_TIGASERVER + "about/android/es";
	private static final String ABOUT_URL_ZH = UtilLocal.URL_TIGASERVER + "about/android/zh-cn/";

	private static final String ABOUT_URL_OFFLINE_EN = "file:///android_asset/html/about_en.html";
	private static final String ABOUT_URL_OFFLINE_CA = "file:///android_asset/html/about_ca.html";
	private static final String ABOUT_URL_OFFLINE_ES = "file:///android_asset/html/about_es.html";
	private static final String ABOUT_URL_OFFLINE_ZH = "file:///android_asset/html/about_zh.html";

	private WebView myWebView;
	private boolean isLoaded = false;

	private static String lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.webview);
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

		// https://code.google.com/p/android/issues/detail?id=32755
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				if ( !isLoaded ) loadOffline();
			}
			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				if ( !isLoaded ) loadOffline();
			}

			@Override
			public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
				if ( !isLoaded ) loadOffline();
			}

			/*@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if ( url.contains("your404page.html") ) {
					loadOffline();
					return true;
				}
				return false;
			}*/
		});
		/*myWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);

				CharSequence notfound = "not found";
				if ( title.contains(notfound) ) {
					view.stopLoading();
					loadOffline();
				}
			}
			*//*public void onProgressChanged(WebView view, int progress) {
				int a = (progress * 1000);
			}*//*
		});*/

		myWebView.getSettings().setAllowFileAccess(true);
		//myWebView.getSettings().setJavaScriptEnabled(true);
		//myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		/*if (Build.VERSION.SDK_INT >= 7) {
			WebViewApi7.api7settings(myWebView, this);
		}

		if (!Util.isOnline(this)) { // loading offline only if not online
			myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}*/

		if (lang.equals("ca"))
			myWebView.loadUrl(ABOUT_URL_CA);
		else if (lang.equals("es"))
			myWebView.loadUrl(ABOUT_URL_ES);
		else if (lang.equals("zh"))
			myWebView.loadUrl(ABOUT_URL_ZH);
		else
			myWebView.loadUrl(ABOUT_URL_EN);

	}

	private void loadOffline() {
		if (lang.equals("ca"))
			myWebView.loadUrl(ABOUT_URL_OFFLINE_CA);
		else if (lang.equals("es"))
			myWebView.loadUrl(ABOUT_URL_OFFLINE_ES);
		else if (lang.equals("zh"))
			myWebView.loadUrl(ABOUT_URL_OFFLINE_ZH);
		else
			myWebView.loadUrl(ABOUT_URL_OFFLINE_EN);
		isLoaded = true;
	}

/*	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ( Util.isOnline(this) ) {
			// Check if the key event was the Back button and if there's history
			if ( (keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack() ) {
				myWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.about_menu, menu);
		Util.setMenuTextColor(menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.language) {
			Intent i = new Intent(this, LanguageSelectorActivity.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.license) {
			Intent i = new Intent(this, LicenseActivity.class);
			startActivity(i);
			return true;
		}
		return false;
	}

}
