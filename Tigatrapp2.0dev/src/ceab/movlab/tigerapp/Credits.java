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

package ceab.movlab.tigerapp;

import ceab.movelab.tigerapp.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Displays the Credits screen.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Credits extends Activity {

	private ImageView fecytImage;
	private ImageView dipsalutImage;
	private ImageView scmImage;
	private ImageView ceabImage;
	private ImageView movelabImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.credits);

		Util.overrideFonts(this, findViewById(android.R.id.content));
		TextView t = (TextView) findViewById(R.id.shortAbout);
		t.setText(Html.fromHtml(getString(R.string.copyright)));
		t.setTextColor(getResources().getColor(R.color.light_yellow));
		t.setTextSize(15);

		fecytImage = (ImageView) findViewById(R.id.fecytImage);
		fecytImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String url = "http://www.fecyt.es";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		dipsalutImage = (ImageView) findViewById(R.id.dipsalutImage);
		dipsalutImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String url = "http://www.dipsalut.cat";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		ceabImage = (ImageView) findViewById(R.id.ceabImage);
		ceabImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String url = "http://www.ceab.csic.es";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		scmImage = (ImageView) findViewById(R.id.scmImage);
		scmImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String url = "http://serveicontrolmosquits.blogspot.com.es";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		movelabImage = (ImageView) findViewById(R.id.movelabBanner);
		movelabImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String url = "http://movelab.net/";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

	}

	static final private int LICENSE = Menu.FIRST;
	static final private int FUNDING = Menu.FIRST + 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, LICENSE, Menu.NONE, "License Information");
		menu.add(0, FUNDING, Menu.NONE, "Funding Information");

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (LICENSE): {
			Intent i = new Intent(this, About.class);
			startActivity(i);
			return true;
		}

		case (FUNDING): {
			Intent i = new Intent(this, CreditsDetail.class);
			startActivity(i);
			return true;
		}

		}
		return false;
	}

}