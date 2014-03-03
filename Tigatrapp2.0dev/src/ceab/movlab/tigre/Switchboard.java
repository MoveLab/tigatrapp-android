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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ceab.movlab.tigre.ContentProviderContractReports.Reports;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Main menu screen for app.
 * 
 * @author John R.B. Palmer
 * 
 */

public class Switchboard extends Activity {

	private Button reportButtonAdult;
	private Button reportButtonSite;
	private Button galleryButton;
	private Button mapButton;
	final Context context = this;
	private TimerTask delayTask;
	private Timer myTimer;
	AnimationDrawable ad;
	String lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PropertyHolder.init(context);

		if (PropertyHolder.getUserId() == null) {
			String userId = UUID.randomUUID().toString();
			PropertyHolder.setUserId(userId);
		}

		if (PropertyHolder.isServiceOn()) {

			// Make sure fixes are being scheduled
			// Stop service if it is currently running
			Intent stopFixGet = new Intent(Switchboard.this, FixGet.class);
			stopService(stopFixGet);
			// now schedule
			Intent scheduleService = new Intent(
					"ceab.movlab.tigre.SCHEDULE_SERVICE");
			context.sendBroadcast(scheduleService);
			startService(scheduleService);

		}

		lang = PropertyHolder.getLanguage();
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

		setContentView(R.layout.switchboard);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		ImageView logo = (ImageView) findViewById(R.id.splashLogo);

		reportButtonAdult = (Button) findViewById(R.id.reportButtonAdult);
		reportButtonAdult.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				dialog.setTitle("Report");
				dialog.setMessage("Create new report or edit an existing one?");
				dialog.setCancelable(true);
				dialog.setPositiveButton("Create",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								Intent i = new Intent(Switchboard.this,
										ReportTool.class);
								Bundle b = new Bundle();
								b.putInt("type", Report.TYPE_ADULT);
								i.putExtras(b);
								startActivity(i);
							};
						});
				dialog.setNeutralButton("Edit",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								Intent i = new Intent(Switchboard.this,
										ViewDataActivity.class);
								Bundle b = new Bundle();
								b.putInt("type", Report.TYPE_ADULT);
								i.putExtras(b);

								startActivity(i);
							};
						});

				dialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								d.cancel();
							};
						});

				dialog.show();

			}
		});

		reportButtonSite = (Button) findViewById(R.id.reportButtonSite);
		reportButtonSite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				dialog.setTitle("Report");
				dialog.setMessage("Create new report or edit an existing one?");
				dialog.setCancelable(true);
				dialog.setPositiveButton("Create",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								Intent i = new Intent(Switchboard.this,
										ReportTool.class);
								Bundle b = new Bundle();
								b.putInt("type", Report.TYPE_BREEDING_SITE);
								i.putExtras(b);
								startActivity(i);
							};
						});
				dialog.setNeutralButton("Edit",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								Intent i = new Intent(Switchboard.this,
										ViewDataActivity.class);
								Bundle b = new Bundle();
								b.putInt("type", Report.TYPE_BREEDING_SITE);
								// i.putExtras(b);
								startActivity(i);
							};
						});

				dialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int arg1) {
								d.cancel();
							};
						});

				dialog.show();

			}
		});

		mapButton = (Button) findViewById(R.id.dataMapButton);
		mapButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Switchboard.this, ViewDataActivity.class);
				startActivity(i);

			}
		});

		galleryButton = (Button) findViewById(R.id.reportMainPic);
		galleryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Switchboard.this, PhotoGallery.class);
				startActivity(i);

			}
		});

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(500);
		Animation animationSlow = new AlphaAnimation(0.0f, 1.0f);
		animationSlow.setDuration(2000);

		logo.startAnimation(animationSlow);
		reportButtonAdult.startAnimation(animation);
		reportButtonSite.startAnimation(animation);
		mapButton.startAnimation(animation);
		galleryButton.startAnimation(animation);

	}

	@Override
	protected void onResume() {

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

	static final private int TOGGLE_LANGUAGE = Menu.FIRST;
	static final private int MAIN_WEBSITE = Menu.FIRST + 1;
	static final private int RSS_FEED = Menu.FIRST + 2;
	static final private int SHARE_APP = Menu.FIRST + 3;
	static final private int HELP = Menu.FIRST + 4;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, TOGGLE_LANGUAGE, Menu.NONE, R.string.menu_toggle_language);
		menu.add(0, MAIN_WEBSITE, Menu.NONE, R.string.visit_website);
		menu.add(0, RSS_FEED, Menu.NONE, "RSS Feed");
		menu.add(0, SHARE_APP, Menu.NONE, "share app");
		menu.add(0, HELP, Menu.NONE, "help");

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (TOGGLE_LANGUAGE): {

			lang = PropertyHolder.getLanguage() == "ca" ? "es" : "ca";
			PropertyHolder.setLanguage(lang);
			setLocale(lang);

			return true;
		}

		case (MAIN_WEBSITE): {

			String url = "http://atrapaeltigre.com";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		}

		}
		return false;
	}

	public void setLocale(String lang) {

		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		finish();
		startActivity(getIntent());
	}

}
