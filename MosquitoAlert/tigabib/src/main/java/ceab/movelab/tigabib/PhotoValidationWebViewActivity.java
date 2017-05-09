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
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Displays the Pybossa photo validation system screen.
 * 
 * @author Màrius Garcia
 * 
 */
public class PhotoValidationWebViewActivity extends Activity {

	//private static String TAG = "PhotoValidation";

	public static final String PYBOSSA_URL_PARAM = "pybossa_url";

	private WebView myWebView;

	private String myUrl;

	String lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		Bundle b = getIntent().getExtras();
		if (b.containsKey(PYBOSSA_URL_PARAM))
			myUrl = b.getString(PYBOSSA_URL_PARAM);
		else
			finish();

		setContentView(R.layout.webview);

		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//Toast.makeText(PhotoValidationActivity.this, url, Toast.LENGTH_SHORT).show();
				String internalUrl = UtilLocal.PYBOSSA_URL;
				// all links with in our site will be open inside the webview, links that start as of our domain
				if ( url != null && url.startsWith(internalUrl) ) {
					return false;
				}
				// all links that points outside the site will be open in a normal android browser
				else  {
					view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					return true;
				}
			}
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(PhotoValidationWebViewActivity.this, "error", Toast.LENGTH_SHORT).show();
			}
		});
		myWebView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Util.logInfo("MyApplication", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId() );
				return true;
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		//myWebView.getSettings().setAllowFileAccess(true);
		myWebView.getSettings().setJavaScriptEnabled(true);
		//myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		myWebView.loadUrl(myUrl);
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
		inflater.inflate(R.menu.about_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.language) {
			Intent i = new Intent(PhotoValidationWebViewActivity.this, LanguageSelector.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.license) {
			Intent i = new Intent(PhotoValidationWebViewActivity.this, LicenseActivity.class);
			startActivity(i);
			return true;
		}

		return false;
	}

	
}
