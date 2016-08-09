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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import ceab.movelab.tigabib.chrometabs.CustomTabActivityHelper;
import ceab.movelab.tigabib.chrometabs.WebviewFallback;

/**
 * Displays the Pybossa photo validation system screen.
 * 
 * @author Màrius Garcia
 * 
 */
public class PhotoValidationActivity extends Activity {

	//private static String TAG = "PhotoValidation";

	private WebView myWebView;

	String lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( !PropertyHolder.isInit() )
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
		super.onResume();

		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		setContentView(R.layout.webview);

		CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setToolbarColor(Color.CYAN).build();
		CustomTabActivityHelper.openCustomTab(
					this,// activity
					customTabsIntent,
					Uri.parse(UtilLocal.PYBOSSA_URL),
					new WebviewFallback()
			);
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
			Intent i = new Intent(PhotoValidationActivity.this, LanguageSelector.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.license) {
			Intent i = new Intent(PhotoValidationActivity.this, License.class);
			startActivity(i);
			return true;
		}

		return false;
	}

}
