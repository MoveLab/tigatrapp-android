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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONException;

import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;
import ceab.movelab.tigerapp.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Main menu screen for app.
 * 
 * @author John R.B. Palmer
 * 
 */

public class Switchboard extends Activity {

	private ImageView reportButtonAdult;
	private ImageView reportButtonSite;
	private ImageView galleryButton;
	private ImageView mapButton;
	final Context context = this;
	AnimationDrawable ad;
	String lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		if (!PropertyHolder.hasConsented()) {
			Intent i2c = new Intent(Switchboard.this, Consent.class);
			startActivity(i2c);
			finish();
		}

		if (PropertyHolder.getUserId() == null) {
			String userId = UUID.randomUUID().toString();
			PropertyHolder.setUserId(userId);
		}

		if (PropertyHolder.isServiceOn()) {

			long lastScheduleTime = PropertyHolder.lastSampleSchedleMade();
			if (System.currentTimeMillis() - lastScheduleTime > 1000 * 60 * 60 * 24) {
				Intent scheduleService = new Intent(
						TigerBroadcastReceiver.START_SAMPLING_MESSAGE);
				sendBroadcast(scheduleService);
			}

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

		reportButtonAdult = (ImageView) findViewById(R.id.reportButtonAdult);
		reportButtonAdult.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Switchboard.this, ReportTool.class);
				Bundle b = new Bundle();
				b.putInt("type", Report.TYPE_ADULT);
				i.putExtras(b);
				startActivity(i);

			}
		});

		reportButtonSite = (ImageView) findViewById(R.id.reportButtonSite);
		reportButtonSite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Switchboard.this, ReportTool.class);
				Bundle b = new Bundle();
				b.putInt("type", Report.TYPE_BREEDING_SITE);
				i.putExtras(b);
				startActivity(i);
			}
		});

		mapButton = (ImageView) findViewById(R.id.dataMapButton);
		mapButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Switchboard.this, ViewDataActivity.class);
				startActivity(i);

			}
		});

		galleryButton = (ImageView) findViewById(R.id.reportMainPic);
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

		// Stop service if running and restart it
		Intent i = new Intent(Switchboard.this, FixGet.class);
		stopService(i);
		i = new Intent(TigerBroadcastReceiver.START_FIXGET_MESSAGE);
		context.sendBroadcast(i);
		startService(i);
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
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	static final private int TOGGLE_LANGUAGE = Menu.FIRST;
	static final private int MAIN_WEBSITE = Menu.FIRST + 1;
	static final private int RSS_FEED_ATRAPAELTIGRE = Menu.FIRST + 2;
	static final private int SHARE_APP = Menu.FIRST + 3;
	static final private int HELP = Menu.FIRST + 4;
	static final private int LIST_TASKS = Menu.FIRST + 5;
	static final private int TEST_TASK_NOTIFICATION_A = Menu.FIRST + 6;
	static final private int TEST_TASK_NOTIFICATION_B = Menu.FIRST + 7;
	static final private int TEST_TASK_NOTIFICATION_C = Menu.FIRST + 10;
	static final private int RSS_FEED_MOVELAB = Menu.FIRST + 11;
	static final private int ABOUT = Menu.FIRST + 12;
	static final private int WEBMAP = Menu.FIRST + 13;
	static final private int TEST_FIX_SAMPLER = Menu.FIRST + 14;
	static final private int VIEW_CURRENT_FIX_TIMES = Menu.FIRST + 15;
	static final private int FIX_NOW = Menu.FIRST + 16;
	static final private int TEST_TASK_NOTIFICATION_B_TRIGGERS = Menu.FIRST + 17;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, TOGGLE_LANGUAGE, Menu.NONE, R.string.menu_toggle_language);
		menu.add(0, WEBMAP, Menu.NONE, "Webmap");
		menu.add(0, MAIN_WEBSITE, Menu.NONE, R.string.visit_website);
		menu.add(0, RSS_FEED_ATRAPAELTIGRE, Menu.NONE, "Tigatrapp News");
		menu.add(0, RSS_FEED_MOVELAB, Menu.NONE, "MoveLab News");
		menu.add(0, SHARE_APP, Menu.NONE, "share app");
		menu.add(0, LIST_TASKS, Menu.NONE, "List Pending Tasks");
		menu.add(0, HELP, Menu.NONE, "help");
		menu.add(0, ABOUT, Menu.NONE, "about");
		menu.add(0, TEST_TASK_NOTIFICATION_A, Menu.NONE, "Test task type A");
		menu.add(0, TEST_TASK_NOTIFICATION_B, Menu.NONE, "Test task type B");
		menu.add(0, TEST_TASK_NOTIFICATION_B_TRIGGERS, Menu.NONE,
				"Test task type B with triggers");
		menu.add(0, TEST_TASK_NOTIFICATION_C, Menu.NONE, "Test task type C");
		menu.add(0, TEST_FIX_SAMPLER, Menu.NONE, "Testing: Schedule fixes");
		menu.add(0, VIEW_CURRENT_FIX_TIMES, Menu.NONE,
				"Testing: View Fix Schedule");
		menu.add(0, FIX_NOW, Menu.NONE, "Testing: Take Fix Now");

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

		case (WEBMAP): {

			String url = "http://tce.ceab.csic.es/tigatrapp/TigatrappMap.html";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		}

		case (MAIN_WEBSITE): {

			String url = "http://atrapaeltigre.com";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		}

		case (RSS_FEED_ATRAPAELTIGRE): {

			Intent i = new Intent(Switchboard.this, RSSActivity.class);
			i.putExtra(RSSActivity.RSSEXTRA_TITLE, "Latest from Tigatrapp!");
			i.putExtra(RSSActivity.RSSEXTRA_URL,
					"http://atrapaeltigre.com/web/feed/");
			i.putExtra(RSSActivity.RSSEXTRA_DEFAULT_THUMB,
					R.drawable.ic_launcher);
			startActivity(i);
			return true;
		}

		case (RSS_FEED_MOVELAB): {
			Intent i = new Intent(Switchboard.this, RSSActivity.class);
			i.putExtra(RSSActivity.RSSEXTRA_TITLE, "Latest from Movelab!");
			i.putExtra(RSSActivity.RSSEXTRA_URL, "http://movelab.net/web/feed/");
			i.putExtra(RSSActivity.RSSEXTRA_DEFAULT_THUMB,
					R.drawable.movelab_icon);
			startActivity(i);
			return true;
		}

		case (LIST_TASKS): {

			Intent i = new Intent(Switchboard.this, ListPendingTasks.class);
			startActivity(i);
			return true;
		}

		case (HELP): {

			Intent i = new Intent(Switchboard.this, Help.class);
			startActivity(i);
			return true;
		}

		case (ABOUT): {

			Intent i = new Intent(Switchboard.this, Credits.class);
			startActivity(i);
			return true;
		}

		case (SHARE_APP): {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);

			// set the type
			shareIntent.setType("text/plain");

			// add a subject
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Tigatrapp");

			// build the body of the message to be shared
			String shareMessage = "Check out Tigtrapp at <http://atrapaeltigre.com>!";

			// add the message
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					shareMessage);

			// start the chooser for sharing
			startActivity(Intent.createChooser(shareIntent, getResources()
					.getText(R.string.share_with)));

			return true;
		}

		case (TEST_TASK_NOTIFICATION_A): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
			try {
				TaskModel.storeTask(context, TaskModel.makeDemoTaskA()
						.toString());
				intent.putExtra(Tasks.KEY_TASK_HEADING, TaskModel
						.makeDemoTaskA().getString(Tasks.KEY_TASK_HEADING));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.sendBroadcast(intent);
			return true;
		}
		case (TEST_TASK_NOTIFICATION_B): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
			try {
				TaskModel.storeTask(context, TaskModel.makeDemoTaskB()
						.toString());
				intent.putExtra(Tasks.KEY_TASK_HEADING, TaskModel
						.makeDemoTaskB().getString(Tasks.KEY_TASK_HEADING));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.sendBroadcast(intent);
			return true;
		}
		case (TEST_TASK_NOTIFICATION_C): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
			try {
				TaskModel.storeTask(context, TaskModel.makeDemoTaskC()
						.toString());
				intent.putExtra(Tasks.KEY_TASK_HEADING, TaskModel
						.makeDemoTaskC().getString(Tasks.KEY_TASK_HEADING));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.sendBroadcast(intent);
			return true;
		}

		case (TEST_TASK_NOTIFICATION_B_TRIGGERS): {
			TaskModel.storeTask(context, TaskModel.makeDemoTaskBWithTriggers()
					.toString());
			return true;
		}

		case (TEST_FIX_SAMPLER): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.START_SAMPLING_MESSAGE);
			context.sendBroadcast(intent);
			return true;
		}

		case (VIEW_CURRENT_FIX_TIMES): {
			Util.showHelp(context, PropertyHolder.getCurrentFixTimes());

			return true;
		}
		case (FIX_NOW): {
			Intent intent2FixGet = new Intent(context, FixGet.class);
			startService(intent2FixGet);

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
