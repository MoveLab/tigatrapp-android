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

package ceab.movlab.tigerapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;

/**
 * Displays the About screen.
 * 
 * @author John R.B. Palmer
 * 
 */
public class GPLCat extends Activity {

	Resources res;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = this;

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);
		if (PropertyHolder.getLanguage() == null) {
			Intent i2sb = new Intent(GPLCat.this, Switchboard.class);
			startActivity(i2sb);
			finish();
		} else {
			res = getResources();
			Util.setDisplayLanguage(res);
		}

		setContentView(R.layout.gpl_cat);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		TextView t = (TextView) findViewById(R.id.gplText);
		t.setText(readTxt());
		t.setTextColor(getResources().getColor(R.color.light_yellow));
		t.setTextSize(15);

		final TextView mWeb = (TextView) findViewById(R.id.webLink);
		Linkify.addLinks(mWeb, Linkify.ALL);
		mWeb.setLinkTextColor(getResources().getColor(R.color.light_yellow));
		mWeb.setTextSize(getResources().getDimension(R.dimen.textsize_url));

		final TextView mEmail = (TextView) findViewById(R.id.emailLink);
		Linkify.addLinks(mEmail, Linkify.ALL);
		mEmail.setLinkTextColor(getResources().getColor(R.color.light_yellow));
		mEmail.setTextSize(getResources().getDimension(R.dimen.textsize_url));

	}

	private String readTxt() {
		InputStream inputStream = null;
		inputStream = getResources().openRawResource(R.raw.gpl);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
		} catch (IOException e) {

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {

				}
			}
		}
		return byteArrayOutputStream.toString();
	}

	static final private int GPL = Menu.FIRST;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, GPL, Menu.NONE, R.string.view_gpl_english);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (GPL): {
			Intent i = new Intent(this, GPLView.class);
			startActivity(i);
			return true;
		}
		}
		return false;
	}

}