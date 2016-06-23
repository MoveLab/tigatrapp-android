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
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.UUID;

import ceab.movelab.tigabib.ContProvContractReports.Reports;

/**
 * Main menu screen for app.
 *
 * @author John R.B. Palmer
 *
 */

public class Switchboard extends Activity {

	private RelativeLayout reportButtonAdult;
	private RelativeLayout reportButtonSite;
	private RelativeLayout galleryButton;
	private RelativeLayout mapButton;
	private ImageView pybossaButton;
	private ImageView websiteButton;
	private ImageView menuButton;


	final Context context = this;
	AnimationDrawable ad;
	Resources res;
	String lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		lang = Util.setDisplayLanguage(res);

		if (!Util.privateMode(context) && !PropertyHolder.hasConsented()) {
			Intent i2c = new Intent(Switchboard.this, Consent.class);
			startActivity(i2c);
			finish();

		} else {

			if (PropertyHolder.getUserId() == null) {
				String userId = UUID.randomUUID().toString();
				PropertyHolder.setUserId(userId);
				PropertyHolder.setNeedsMosquitoAlertPop(false);
			} else{

				if (PropertyHolder.needsMosquitoAlertPop() == true) {

					final Dialog dialog = new Dialog(context);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.custom_alert);
					dialog.setCancelable(true);
					TextView alertText = (TextView) dialog
							.findViewById(R.id.alertText);
					alertText.setText(Html.fromHtml(getResources().getString(
							R.string.mosquito_alert_pop)));
					Linkify.addLinks(alertText, Linkify.ALL);
					Button positive = (Button) dialog
							.findViewById(R.id.alertOK);
					Button negative = (Button) dialog
							.findViewById(R.id.alertCancel);
					negative.setVisibility(View.GONE);
					positive.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							PropertyHolder.setNeedsMosquitoAlertPop(false);
							dialog.cancel();
						}
					});
					dialog.show();
				}
			}


			if (Util.privateMode(context)) {

				final long now = System.currentTimeMillis();

				if ((now - PropertyHolder.getLastDemoPopUpTime()) > Util.DAYS) {

					final Dialog dialog = new Dialog(context);

					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.custom_alert);
					dialog.setCancelable(true);

					TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
					alertText.setText(getResources().getString(
							R.string.switchboard_demo_popup));

					Button positive = (Button) dialog.findViewById(R.id.alertOK);
					Button negative = (Button) dialog.findViewById(R.id.alertCancel);
					negative.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							PropertyHolder.setLastDemoPopUpTime(now);
							dialog.cancel();
						}

					});

					positive.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("market://details?id=ceab.movelab.tigatrapp"));
							startActivity(intent);
						}
					});

					dialog.show();

				}
			}

			// open and close databases in order to trigger any updates
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(Util.getReportsUri(context), new String[] { Reports.KEY_ROW_ID }, null, null, null);
			c.close();
			c = cr.query(Util.getMissionsUri(context), new String[] { Reports.KEY_ROW_ID }, null, null, null);
			c.close();

			if (PropertyHolder.isServiceOn()) {
				long lastScheduleTime = PropertyHolder.lastSampleScheduleMade();
				if (System.currentTimeMillis() - lastScheduleTime > (1000 * 60 * 60 * 24)) {
					Util.internalBroadcast(context, Messages.START_DAILY_SAMPLING);
				}
			}

			setContentView(R.layout.switchboard);

			reportButtonAdult = (RelativeLayout) findViewById(R.id.reportButtonAdult);
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

			reportButtonSite = (RelativeLayout) findViewById(R.id.reportButtonSite);
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

			mapButton = (RelativeLayout) findViewById(R.id.reportButtonMap);
			mapButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(Switchboard.this, MapData.class);
					startActivity(i);
				}
			});

			galleryButton = (RelativeLayout) findViewById(R.id.reportMainPic);
			galleryButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(Switchboard.this, PhotoGallery.class);
					startActivity(i);
				}
			});

			websiteButton = (ImageView) findViewById(R.id.mainSiteButton);
			websiteButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = UtilLocal.URL_PROJECT;
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);

				}
			});

			pybossaButton = (ImageView) findViewById(R.id.pybossaButton);
			pybossaButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(Switchboard.this, PhotoValidationActivity.class);
					startActivity(i);
				}
			});

			menuButton = (ImageView) findViewById(R.id.menuButton);
			menuButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					openOptionsMenu();

				}
			});

			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(500);

			reportButtonAdult.startAnimation(animation);
			reportButtonSite.startAnimation(animation);
			mapButton.startAnimation(animation);
			galleryButton.startAnimation(animation);
			websiteButton.startAnimation(animation);
			menuButton.startAnimation(animation);

		}

	}

	@Override
	protected void onResume() {
		res = getResources();
		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
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

		if (item.getItemId() == R.id.tigatrappNews) {
			Intent i = new Intent(Switchboard.this, RSSActivity.class);
			i.putExtra(RSSActivity.RSSEXTRA_TITLE, getResources().getString(R.string.rss_title_tigatrapp));
			if (lang.equals("ca"))
				i.putExtra(RSSActivity.RSSEXTRA_URL, Util.URL_RSS_CA);
			// Note we are only doing the blog in Catalan or Spanish, so if user
			// is in English or Spanish, both will go to Spanish RSS feed
			else
				i.putExtra(RSSActivity.RSSEXTRA_URL, Util.URL_RSS_ES);
			i.putExtra(RSSActivity.RSSEXTRA_DEFAULT_THUMB, R.drawable.ic_launcher);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.taskList) {
			Intent i = new Intent(Switchboard.this, MissionListActivity.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.settings) {
			Intent i = new Intent(Switchboard.this, Settings.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.help) {
			Intent i = new Intent(Switchboard.this, Help.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.about) {
			Intent i = new Intent(Switchboard.this, About.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.shareApp) {

			Intent shareIntent = new Intent(Intent.ACTION_SEND);

			// set the type
			shareIntent.setType("text/plain");

			// add a subject
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Tigatrapp");

			// build the body of the message to be shared
			String shareMessage = getResources().getString(
					R.string.project_website);

			// add the message
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					shareMessage);

			// start the chooser for sharing
			startActivity(Intent.createChooser(shareIntent, getResources()
					.getText(R.string.share_with)));

			return true;
		}
		return false;
	}

}
