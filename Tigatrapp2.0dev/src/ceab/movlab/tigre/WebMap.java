/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: tigatrapp@ceab.csic.es
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

package ceab.movlab.tigre;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Displays the Credits screen.
 * 
 * @author John R.B. Palmer
 * 
 */
public class WebMap extends Activity {

	private WebView webView;
	private static final String MAP_URL = "http://tce.ceab.csic.es/tigatrapp/TigatrappMap.html";
	private static final String SITE_URL = "http://atrapaeltigre.com";
	private boolean atSite = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.webmap);

		setupWebView();
	}

	@Override
	protected void onResume() {

		webView.loadUrl(MAP_URL);

		super.onResume();
	}

	/** Sets up the WebView object and loads the URL of the page **/
	@SuppressLint("SetJavaScriptEnabled")
	private void setupWebView() {

		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(MAP_URL);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if (atSite) {
				finish();
			} else {
				webView.loadUrl(SITE_URL);
				atSite = true;

			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	static final private int REFRESH = Menu.FIRST;
	static final private int WEBSITE = Menu.FIRST + 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, REFRESH, Menu.NONE,
				getResources().getString(R.string.refresh));
		menu.add(0, WEBSITE, Menu.NONE,
				getResources().getString(R.string.visit_website));

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (REFRESH): {
			webView.loadUrl(MAP_URL);
			return true;
		}

		case (WEBSITE): {
			String url = "http://atrapaeltigre.com/";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		}

		}
		return false;
	}

}