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

import org.json.JSONException;

import ceab.movlab.tigre.ContentProviderContractTasks.Tasks;
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
	static final private int LIST_TASKS = Menu.FIRST + 5;
	static final private int TEST_TASK_NOTIFICATION0 = Menu.FIRST + 6;
	static final private int TEST_TASK_NOTIFICATION1 = Menu.FIRST + 7;
	static final private int TEST_TASK_NOTIFICATION2 = Menu.FIRST + 8;
	static final private int TEST_TASK_NOTIFICATION3 = Menu.FIRST + 9;
	static final private int TEST_TASK_NOTIFICATION4 = Menu.FIRST + 10;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, TOGGLE_LANGUAGE, Menu.NONE, R.string.menu_toggle_language);
		menu.add(0, MAIN_WEBSITE, Menu.NONE, R.string.visit_website);
		menu.add(0, RSS_FEED, Menu.NONE, "RSS Feed");
		menu.add(0, SHARE_APP, Menu.NONE, "share app");
		menu.add(0, HELP, Menu.NONE, "help");
		menu.add(0, LIST_TASKS, Menu.NONE, "List Pending Tasks");
		menu.add(0, TEST_TASK_NOTIFICATION0, Menu.NONE, "Test task type 0");
		menu.add(0, TEST_TASK_NOTIFICATION1, Menu.NONE, "Test task type 1");
		menu.add(0, TEST_TASK_NOTIFICATION2, Menu.NONE, "Test task type 2");
		menu.add(0, TEST_TASK_NOTIFICATION3, Menu.NONE, "Test task type 3");
		menu.add(0, TEST_TASK_NOTIFICATION4, Menu.NONE, "Test task type 4");
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

		case (LIST_TASKS): {

			Intent i = new Intent(Switchboard.this, ListPendingTasks.class);
			startActivity(i);
			return true;
		}

		case (TEST_TASK_NOTIFICATION0): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
			try {
				TaskModel.storeTask(context, TaskModel.makeDemoTask0()
						.toString());
				intent.putExtra(Tasks.KEY_TASK_HEADING, TaskModel
						.makeDemoTask0().getString(Tasks.KEY_TASK_HEADING));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.sendBroadcast(intent);
			return true;
		}
		case (TEST_TASK_NOTIFICATION1): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
			try {
				TaskModel.storeTask(context, TaskModel.makeDemoTask1()
						.toString());
				intent.putExtra(Tasks.KEY_TASK_HEADING, TaskModel
						.makeDemoTask1().getString(Tasks.KEY_TASK_HEADING));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.sendBroadcast(intent);
			return true;
		}
		case (TEST_TASK_NOTIFICATION2): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
			try {
				TaskModel.storeTask(context, TaskModel.makeDemoTask2()
						.toString());
				intent.putExtra(Tasks.KEY_TASK_HEADING, TaskModel
						.makeDemoTask2().getString(Tasks.KEY_TASK_HEADING));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.sendBroadcast(intent);
			return true;
		}
		case (TEST_TASK_NOTIFICATION3): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
			try {
				TaskModel.storeTask(context, TaskModel.makeDemoTask3()
						.toString());
				intent.putExtra(Tasks.KEY_TASK_HEADING, TaskModel
						.makeDemoTask3().getString(Tasks.KEY_TASK_HEADING));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.sendBroadcast(intent);
			return true;
		}
		case (TEST_TASK_NOTIFICATION4): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
			try {
				TaskModel.storeTask(context, TaskModel.makeDemoTask4()
						.toString());
				intent.putExtra(Tasks.KEY_TASK_HEADING, TaskModel
						.makeDemoTask4().getString(Tasks.KEY_TASK_HEADING));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.sendBroadcast(intent);
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
