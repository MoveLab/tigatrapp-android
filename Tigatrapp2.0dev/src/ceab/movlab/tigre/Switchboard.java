/*
 * Tigatrapp
 * Copyright (C) 2013  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
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

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Main menu screen for app.
 * 
 * @author John R.B. Palmer
 * 
 */

public class Switchboard extends Activity {

	private TextView mTFB;
	private TextView mTDB;
	private TextView mainPhoto;
	private TextView mWMB;
	private ImageView mWebSiteButton;
	final Context context = this;
	Vibrator mVib;
	Timer myTimer;
	TimerTask myTimerTask;

	int screenSize;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.switchboard);

		PropertyHolder.init(context);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		mVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		mTFB = (TextView) findViewById(R.id.tigerfinder_button);
		mTDB = (TextView) findViewById(R.id.tigerdriver_button);

		screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		mTFB.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mTFB.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.orange_oval_pressed));
					mTFB.setPadding(15, 15, 15, 15);
					mTFB.setTextColor(getResources().getColor(
							R.color.orange_glow));
					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					mTFB.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.orange_oval));
					mTFB.setPadding(15, 15, 15, 15);
					mTFB.setTextColor(getResources().getColor(R.color.black));
					mVib.vibrate(50);

				}
				return false;
			}

		});

		mTFB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// create an intent object and tell it where to go
				Intent i = new Intent(Switchboard.this, ReportTool.class);
				// start the intent
				startActivity(i);

			}
		});

		mTDB.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mTDB.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.orange_oval_pressed));
					mTDB.setPadding(15, 15, 15, 15);
					mTDB.setTextColor(getResources().getColor(
							R.color.orange_glow));
					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					mTDB.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.orange_oval));
					mTDB.setPadding(15, 15, 15, 15);
					mTDB.setTextColor(getResources().getColor(R.color.black));
					mVib.vibrate(50);

				}
				return false;
			}

		});

		mTDB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// create an intent object and tell it where to go
				Intent i = new Intent(Switchboard.this,
						DriverStartActivity.class);
				// start the intent
				startActivity(i);

			}
		});

		mWMB = (TextView) findViewById(R.id.dataMapButton);
		mWMB.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mWMB.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_rectangle_pressed));
					mWMB.setPadding(10, 10, 10, 10);
					mWMB.setTextColor(getResources()
							.getColor(R.color.blue_glow));

					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					mWMB.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_rectangle));
					mWMB.setPadding(10, 10, 10, 10);
					mWMB.setTextColor(getResources().getColor(R.color.white));
					mVib.vibrate(50);
				}
				return false;
			}

		});
		mWMB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Switchboard.this, ViewDataActivity.class);
				startActivity(i);

			}
		});

		mWebSiteButton = (ImageView) findViewById(R.id.webSiteButton);

		mWebSiteButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mWebSiteButton.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.grey_rectangle_pressed));
					mWebSiteButton.setPadding(10, 0, 10, 0);
					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					mWebSiteButton.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.grey_rectangle));
					mWebSiteButton.setPadding(10, 0, 10, 0);
					mVib.vibrate(50);
				}
				return false;
			}

		});

		mWebSiteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// Intent i = new Intent(Switchboard.this, WebMap.class);
				// startActivity(i);

				String url = "http://atrapaeltigre.com";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		mainPhoto = (TextView) findViewById(R.id.reportMainPic);
		mainPhoto.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mainPhoto.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_rectangle_pressed));
					mainPhoto.setPadding(10, 10, 10, 10);
					mainPhoto.setTextColor(getResources().getColor(
							R.color.blue_glow));

					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					mainPhoto.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_rectangle));
					mainPhoto.setPadding(10, 10, 10, 10);
					mainPhoto.setTextColor(getResources().getColor(
							R.color.white));
					mVib.vibrate(50);
				}
				return false;
			}

		});
		mainPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Switchboard.this, PhotoGallery.class);
				startActivity(i);

			}
		});

	}

	@Override
	protected void onResume() {

		if (!PropertyHolder.isActivated()) {
			startActivity(new Intent(Switchboard.this, Activation.class));
			finish();
		}

		
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			// do something on back.
			Intent i = new Intent(this, Credits.class);
			startActivity(i);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
