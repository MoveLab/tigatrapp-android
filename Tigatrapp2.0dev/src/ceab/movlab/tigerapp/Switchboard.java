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
import java.util.UUID;

import org.json.JSONException;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import ceab.movelab.tigerapp.R;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		if (PropertyHolder.getLanguage() == null) {
			Log.d("SB", "about to build lang selector");
			Intent i2l = new Intent(Switchboard.this, LanguageSelector.class);
			startActivity(i2l);
			finish();
		} else {
			Resources res = getResources();
			Util.setDisplayLanguage(res);

			if (!PropertyHolder.hasConsented()) {
				Intent i2c = new Intent(Switchboard.this, Consent.class);
				startActivity(i2c);
				finish();
				
			}else{

				if (PropertyHolder.getUserId() == null) {
					String userId = UUID.randomUUID().toString();
					PropertyHolder.setUserId(userId);
				}

				if (PropertyHolder.isServiceOn()) {

					long lastScheduleTime = PropertyHolder
							.lastSampleSchedleMade();
					if (System.currentTimeMillis() - lastScheduleTime > 1000 * 60 * 60 * 24) {
						Intent scheduleService = new Intent(
								TigerBroadcastReceiver.START_SAMPLING_MESSAGE);
						sendBroadcast(scheduleService);
					}

				}

				setContentView(R.layout.switchboard);

				Util.overrideFonts(this, findViewById(android.R.id.content));

				ImageView logo = (ImageView) findViewById(R.id.splashLogo);

				reportButtonAdult = (ImageView) findViewById(R.id.reportButtonAdult);
				reportButtonAdult
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								Intent i = new Intent(Switchboard.this,
										ReportTool.class);
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

						Intent i = new Intent(Switchboard.this,
								ReportTool.class);
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

						Intent i = new Intent(Switchboard.this,
								ViewDataActivity.class);
						startActivity(i);

					}
				});

				galleryButton = (ImageView) findViewById(R.id.reportMainPic);
				galleryButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent i = new Intent(Switchboard.this,
								PhotoGallery.class);
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
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.switchboard_menu, menu);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (R.id.webmap): {

			String url = "http://tce.ceab.csic.es/tigatrapp/TigatrappMap.html";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		}

		case (R.id.mainSite): {

			String url = "http://atrapaeltigre.com";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		}

		case (R.id.tigatrappNews): {

			Intent i = new Intent(Switchboard.this, RSSActivity.class);
			i.putExtra(RSSActivity.RSSEXTRA_TITLE, "Latest from Tigatrapp!");
			i.putExtra(RSSActivity.RSSEXTRA_URL,
					"http://atrapaeltigre.com/web/feed/");
			i.putExtra(RSSActivity.RSSEXTRA_DEFAULT_THUMB,
					R.drawable.ic_launcher);
			startActivity(i);
			return true;
		}

		case (R.id.movelabNews): {
			Intent i = new Intent(Switchboard.this, RSSActivity.class);
			i.putExtra(RSSActivity.RSSEXTRA_TITLE, "Latest from Movelab!");
			i.putExtra(RSSActivity.RSSEXTRA_URL, "http://movelab.net/web/feed/");
			i.putExtra(RSSActivity.RSSEXTRA_DEFAULT_THUMB,
					R.drawable.movelab_icon);
			startActivity(i);
			return true;
		}

		case (R.id.taskList): {

			Intent i = new Intent(Switchboard.this, TaskListActivity.class);
			startActivity(i);
			return true;
		}

		case (R.id.help): {

			Intent i = new Intent(Switchboard.this, Help.class);
			startActivity(i);
			return true;
		}

		case (R.id.about): {

			Intent i = new Intent(Switchboard.this, Credits.class);
			startActivity(i);
			return true;
		}

		case (R.id.shareApp): {
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

		case (R.id.taskA): {
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
		case (R.id.taskB): {
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
		case (R.id.taskC): {
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

		case (R.id.taskLocTrig): {
			TaskModel.storeTask(context, TaskModel.makeDemoTaskBWithTriggers()
					.toString());
			return true;
		}

		case (R.id.scheduleFixes): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.START_SAMPLING_MESSAGE);
			context.sendBroadcast(intent);
			return true;
		}

		case (R.id.viewFixSchedule): {
			Util.showHelp(context, PropertyHolder.getCurrentFixTimes());

			return true;
		}
		case (R.id.fixNow): {
			Intent intent = new Intent(
					TigerBroadcastReceiver.START_FIXGET_MESSAGE);
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
		finish();

	}

}
