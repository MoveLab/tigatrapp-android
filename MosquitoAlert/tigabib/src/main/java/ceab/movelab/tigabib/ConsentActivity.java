/*
 * Tigatrapp
 * Copyright (C) 2012, 2013 John R.B. Palmer
 * Contact: jrpalmer@princeton.edu
 * 
 * This file is part of Space Mapper.
 * 
 * Space Mapper is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Space Mapper is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 */

package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.UUID;

/**
 * Displays the IRB consent form and allows users to consent or decline.
 * 
 * @author John R.B. Palmer
 * 
 */
public class ConsentActivity extends Activity {

	private Context context;
	private String lang;

	private static final String CONSENT_URL_OFFLINE_EN = "file:///android_asset/html/consent_en.html";
	private static final String CONSENT_URL_OFFLINE_CA = "file:///android_asset/html/consent_ca.html";
	private static final String CONSENT_URL_OFFLINE_ES = "file:///android_asset/html/consent_es.html";
	private static final String CONSENT_URL_OFFLINE_ZH = "file:///android_asset/html/consent_zh.html";

	private WebView myWebView;

	//private FirebaseAnalytics mFirebaseAnalytics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getApplicationContext();

		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(context);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.consent);

		// Obtain the FirebaseAnalytics instance.
		//mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if ( !Util.setDisplayLanguage(getResources()).equals(lang) ) {
			finish();
			startActivity(getIntent());
		}
		// [START set_current_screen]
		//mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_consent", "Consent");
		// [END set_current_screen]

		final Button consentButton = (Button) findViewById(R.id.consent_button);
		consentButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Date now = new Date(System.currentTimeMillis());
				String consentTime = Util.userDate(now);
				PropertyHolder.setConsentTime(consentTime);
				PropertyHolder.setConsent(true);
				PropertyHolder.setReconsent(true); // MG - 9/8/16

				if ( PropertyHolder.getUserId() == null ) {
					// set user_UUID
					String userId = UUID.randomUUID().toString();
					PropertyHolder.setUserId(userId);
					PropertyHolder.setNeedsMosquitoAlertPop(false);
					Util.registerOnServer(MyApp.getAppContext());

                    // Get token and register on server
                    String token = FirebaseInstanceId.getInstance().getToken();
                    Util.registerFCMToken(ConsentActivity.this, token, PropertyHolder.getUserId());

					// Obtain the FirebaseAnalytics instance.
					//FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
					//mFirebaseAnalytics.setUserId(userId);
				}

				// start daily sampling
				Util.internalBroadcast(context, Messages.START_DAILY_SAMPLING);

				// start daily sync
				Util.internalBroadcast(context, Messages.START_DAILY_SYNC);

				// launch switchboard
				Intent i = new Intent(ConsentActivity.this, SwitchboardActivity.class);
				startActivity(i);
				finish();
			}
		});

		final Button consentDeclineButton = (Button) findViewById(R.id.consent_decline_button);
		consentDeclineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		myWebView = (WebView) findViewById(R.id.consent_webview);
		myWebView.setWebViewClient(new MyWebViewClient());
		// myWebView.getSettings().setJavaScriptEnabled(true);

		if (lang.equals("ca"))
			myWebView.loadUrl(CONSENT_URL_OFFLINE_CA);
		else if (lang.equals("es"))
			myWebView.loadUrl(CONSENT_URL_OFFLINE_ES);
		else if (lang.equals("zh"))
			myWebView.loadUrl(CONSENT_URL_OFFLINE_ZH);
		else
			myWebView.loadUrl(CONSENT_URL_OFFLINE_EN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.consent_menu, menu);
		Util.setMenuTextColor(menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if ( item.getItemId() == R.id.language ) {
			Intent i = new Intent(ConsentActivity.this, LanguageSelectorActivity.class);
			startActivity(i);
			return true;
		}
		return false;
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// open all links in normal browser
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(i);
			return true;
		}
	}
}
