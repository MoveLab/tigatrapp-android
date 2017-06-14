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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ceab.movelab.tigabib.model.Notification;
import ceab.movelab.tigabib.model.RealmHelper;
import ceab.movelab.tigabib.model.Score;
import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Main menu screen for app.
 *
 * @author John R.B. Palmer
 * @author Màrius Garcia
 *
 */
public class SwitchboardActivity extends Activity {

	private RelativeLayout reportButtonAdult;
	private RelativeLayout reportButtonSite;
	private RelativeLayout mapButton;
	private RelativeLayout pybossaButton;
	private RelativeLayout notificationsButton;
	private TextView mScorePointsText;
//	private RelativeLayout missionsButton;
//	private ImageView websiteButton;
//	private ImageView menuButton;

	private String lang;
	private Realm mRealm;

	private BroadcastReceiver mMissionsBroadcastReceiver;

	private final static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 555;
	private ArrayList<String> mPermissionsDenied;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		mMissionsBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.hasExtra(Messages.INTERNAL_MESSAGE_EXTRA)) {
					String extra = intent.getStringExtra(Messages.INTERNAL_MESSAGE_EXTRA);
					Util.logInfo(this.getClass().getName(), "extra: " + extra);
				}
//Toast.makeText(SwitchboardActivity.this, "location", Toast.LENGTH_SHORT).show();
				//updateMissionCount();
			}
		};
		//LocalBroadcastManager.getInstance(this).registerReceiver(mMissionsBroadcastReceiver, new IntentFilter("aaa"));

		/// Android 6.0 check for permissions first of all
		mPermissionsDenied = getDeniedPermissions();
		if ( mPermissionsDenied.size() > 0 ) {
			askForPermissions();
		}
		else {
			onCreateWithPermissions();
		}
	}

	private void onCreateWithPermissions() {
		if ( !Util.privateMode() && !PropertyHolder.hasReconsented() ) { // MG - 9/8/16
			Intent i2c = new Intent(SwitchboardActivity.this, ConsentActivity.class);
			startActivity(i2c);
			finish();
		} else {
			if (PropertyHolder.getUserId() == null) {
				String userId = UUID.randomUUID().toString();
				PropertyHolder.setUserId(userId);
				PropertyHolder.setNeedsMosquitoAlertPop(false);
			} /*else {
				if (PropertyHolder.needsMosquitoAlertPop()) {
					final Dialog dialog = new Dialog(this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.custom_alert);
					dialog.setCancelable(true);

					TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
					alertText.setText(Html.fromHtml(getResources().getString(R.string.mosquito_alert_pop)));
					Linkify.addLinks(alertText, Linkify.ALL);

					Button positive = (Button) dialog.findViewById(R.id.alertOK);
					Button negative = (Button) dialog.findViewById(R.id.alertCancel);
					negative.setVisibility(View.GONE);
					positive.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							PropertyHolder.setNeedsMosquitoAlertPop(false);
							dialog.cancel();
						}
					});
					dialog.show();
				}
			}*/


			if ( Util.privateMode() ) {
				final long now = System.currentTimeMillis();

				if ((now - PropertyHolder.getLastDemoPopUpTime()) > Util.DAYS) {

					final Dialog dialog = new Dialog(this);

					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.custom_alert);
					dialog.setCancelable(true);

					TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
					alertText.setText(getResources().getString(R.string.switchboard_demo_popup));

					Button positive = (Button) dialog.findViewById(R.id.alertOK);
					Button negative = (Button) dialog.findViewById(R.id.alertCancel);
					negative.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							PropertyHolder.setLastDemoPopUpTime(now);
							dialog.cancel();
						}
					});

					positive.setOnClickListener(new View.OnClickListener() {
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

			setContentView(R.layout.switchboard);

			if (PropertyHolder.isServiceOn()) {
				long lastScheduleTime = PropertyHolder.lastSampleScheduleMade();
				if (System.currentTimeMillis() - lastScheduleTime > (1000 * 60 * 60 * 24)) {
					Util.internalBroadcast(this, Messages.START_DAILY_SAMPLING);
				}
			}

			reportButtonAdult = (RelativeLayout) findViewById(R.id.reportButtonAdult);
			reportButtonAdult.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(SwitchboardActivity.this, ReportToolActivity.class);
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
					Intent i = new Intent(SwitchboardActivity.this, ReportToolActivity.class);
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
					Intent i = new Intent(SwitchboardActivity.this, MapDataV2Activity.class);
					startActivity(i);
				}
			});

			pybossaButton = (RelativeLayout) findViewById(R.id.reportMainPic);
			pybossaButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(SwitchboardActivity.this, PhotoValidationActivity.class);
					startActivity(i);
					/*CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
							.setToolbarColor(getResources().getColor(R.color.pybossa_bar)).build();
					CustomTabActivityHelper.openCustomTab(
							SwitchboardActivity.this, // activity
							customTabsIntent,
							Uri.parse(getPybossaUrl()),
							new WebviewFallback()
					);*/
				}
			});

			notificationsButton = (RelativeLayout) findViewById(R.id.reportNotificationsLayout);
			notificationsButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(SwitchboardActivity.this, NotificationListActivity.class);
					startActivity(i);
				}
			});

			mScorePointsText = (TextView) findViewById(R.id.scorePointsText);
			mScorePointsText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Dialog dialog = new Dialog(SwitchboardActivity.this);

					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.custom_alert);
					dialog.setCancelable(true);

					TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
					alertText.setText(R.string.info_scoring);

					Button positive = (Button) dialog.findViewById(R.id.alertOK);
					Button negative = (Button) dialog.findViewById(R.id.alertCancel);
					negative.setVisibility(View.GONE);

					positive.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.cancel();
						}
					});

					dialog.show();
				}
			});
			/*missionsButton = (RelativeLayout) findViewById(R.id.reportMissionsLayout);
			missionsButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(SwitchboardActivity.this, MissionListActivity.class);
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

			menuButton = (ImageView) findViewById(R.id.menuButton);
			menuButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openOptionsMenu();
				}
			});
*/
			/*Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(500);

			reportButtonAdult.startAnimation(animation);
			reportButtonSite.startAnimation(animation);
			mapButton.startAnimation(animation);
			pybossaButton.startAnimation(animation);
			websiteButton.startAnimation(animation);
			menuButton.startAnimation(animation);*/
		}
	}

/*	private String getPybossaUrl() {
		String url = UtilLocal.PYBOSSA_URL;
		if ( lang.equals("ca") )
			url += "?lang=ca";
		else if ( lang.equals("es") )
			url += "?lang=es";
		else
			url += "?lang=en";
		url += "&timestamp=" + System.currentTimeMillis();
		return url;
	}*/

	@Override
	protected void onResume() {
		if ( !Util.setDisplayLanguage(getResources()).equals(lang) ) {
			finish();
			startActivity(getIntent());
		}
		super.onResume();
		if ( mRealm == null ) {
			try {
Util.logInfo(this.getClass().getName(), "onResume Realm ==================");
				mRealm = RealmHelper.getInstance().getRealm(this);
Util.logInfo(this.getClass().getName(), "onResume Realm 2");
			}
			// https://github.com/realm/realm-java/issues/3264
			catch (IllegalArgumentException e) {
				e.printStackTrace();
Util.logInfo(this.getClass().getName(), "onResume IllegalArgumentException");
				// throw non-fatal
				Crashlytics.log("Realm deleting");
				//Crashlytics.setString("Method", "updateNotificationCount");
				Crashlytics.logException(e);
				RealmConfiguration config = new RealmConfiguration.Builder(this)
						.name("myRealmDB.realm")
						.schemaVersion(1)
						.build();
				Realm.deleteRealm(config);
				//Realm.setDefaultConfiguration(config);
				Realm.getInstance(config);
			}
		}

		LocalBroadcastManager.getInstance(this)
				.registerReceiver(mMissionsBroadcastReceiver, new IntentFilter(Messages.SHOW_TASK_NOTIFICATION));

		if ( mPermissionsDenied.size() == 0 ) {
			loadRemoteNotifications();
			loadScore(); // !!! optimization, call this load score only if the notifications array is empty.
			updateNotificationCount();
			//updateMissionCount();
		}
	}

	@Override
	protected void onDestroy() {
		if ( mRealm != null ) mRealm.close(); // Remember to close Realm when done.
		super.onDestroy();
	}

	private void loadRemoteNotifications() {
		//  (recordeu que a la crida de notificacions se li pot passar un paràmetre locale=[es|ca|en] per controlar l'idioma de les notificacions).
		String notificationUrl = Util.URL_TIGASERVER_API_ROOT + Util.API_NOTIFICATION + "?user_id=" + PropertyHolder.getUserId();
//Util.logInfo("==============", "TEST");
Log.d("===========", "Authorization >> " + UtilLocal.TIGASERVER_AUTHORIZATION);
Log.d("===========", notificationUrl);
		Ion.with(this)
			.load(notificationUrl)
			.setHeader("Accept", "application/json")
			.setHeader("Content-type", "application/json")
			.setHeader("Authorization", UtilLocal.TIGASERVER_AUTHORIZATION)
			.as(new TypeToken<List<Notification>>(){})
			.setCallback(new FutureCallback<List<Notification>>() {
				@Override
				public void onCompleted(Exception e, List<Notification> result) {
					// do stuff with the result or error
Log.d("===========", "result " + result);
					if ( result != null && result.size() > 0 ) {
Util.logInfo(this.getClass().getName(), "loadRemoteNotifications >> " + result.toString());
						RealmHelper.getInstance().addOrUpdateNotificationList(mRealm, result);
					}
					updateNotificationCount();
				}
			});
	}

	private void loadScore() {
		//http://humboldt.ceab.csic.es/api/user_score/?user_id=00012362-528A-496B-BFE5-06E61D2642F9 >> Agustí
		// http://humboldt.ceab.csic.es/api/user_score/?user_id=be0fb42b-6cb2-4cfc-bc92-8762b86faf89 >> Nexus 5
		// 2d039878-0aab-454a-862e-626011b780ff
		String notificationUrl = Util.URL_TIGASERVER_API_ROOT + Util.API_SCORE + "?user_id=" + PropertyHolder.getUserId();
Log.d("===========", "Authorization >> " + UtilLocal.TIGASERVER_AUTHORIZATION);
Log.d("===========", notificationUrl);
		Ion.with(this)
			.load(notificationUrl)
			.setHeader("Accept", "application/json")
			.setHeader("Content-type", "application/json")
			.setHeader("Authorization", UtilLocal.TIGASERVER_AUTHORIZATION)
			.as(new TypeToken<Score>(){})
			.setCallback(new FutureCallback<Score>() {
				@Override
				public void onCompleted(Exception e, Score result) {
					// do stuff with the result or error
Log.d("===========", "result score" + result.toString());
					if ( result != null ) {
Util.logInfo(this.getClass().getName(), "loadScore >> " + result.toString());
						RealmHelper.getInstance().updateScore(mRealm, result);
					}
					updateScoreScreenFromRealm();
				}
			});
	}

	private void updateScoreScreenFromRealm() {
		if ( mRealm != null && !mRealm.isClosed() ) {
			Score score = RealmHelper.getInstance().getScore(mRealm);
			if ( score != null  && score.getScore() != null ) {
				mScorePointsText.setText(score.getScore() > 100 ? "100" : String.valueOf(score.getScore()));
				// get label value from resources
				int resourceId = this.getResources().getIdentifier(score.getScoreLabel(), "string", this.getPackageName());
				((TextView) findViewById(R.id.scoreLevelText)).setText(resourceId);
			}
		}
		else {
			// throw exception
			Crashlytics.log("Realm is null or closed");
			Crashlytics.setString("Method", "updateScore");
			Crashlytics.logException(new IllegalStateException());
		}
	}

	private void updateNotificationCount() {
		if ( mRealm != null && !mRealm.isClosed() ) {
			int count = RealmHelper.getInstance().getNewNotificationsCount(mRealm);
			((TextView) findViewById(R.id.reportNotificationsNumberText)).setText(count > 99 ? "99+" : String.valueOf(count));
		}
		else {
			// throw exception
			Crashlytics.log("Realm is null");
			Crashlytics.setString("Method", "updateNotificationCount");
			Crashlytics.logException(new Exception());
		}
	}

	/*private void updateMissionCount() {
		// open and close databases in order to trigger any updates
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(Util.getReportsUri(this), new String[]{ContProvContractReports.Reports.KEY_ROW_ID}, null, null, null);
		if ( c != null ) c.close();
		c = cr.query(Util.getMissionsUri(this), new String[]{ContProvContractReports.Reports.KEY_ROW_ID}, null, null, null);
		if ( c != null ) c.close();

		c = cr.query(Util.getMissionsUri(this), new String[]{Tasks.KEY_ID},
				Tasks.KEY_ACTIVE + " = 1 AND " + Tasks.KEY_DONE + " = 0", null, null);
		if ( c != null ) {
			try {
				((TextView) findViewById(R.id.reportMissionsNumberText)).setText(String.valueOf(c.getCount()));
			}
			catch (Exception e) {
				Toast.makeText(SwitchboardActivity.this, "updateMissionCount exception", Toast.LENGTH_SHORT).show();
			}
		}
	}*/

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMissionsBroadcastReceiver);
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
			Intent i = new Intent(SwitchboardActivity.this, RSSActivity.class);
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
		} else if (item.getItemId() == R.id.gallery) {
			Intent i = new Intent(SwitchboardActivity.this, PhotoGallery.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.settings) {
			Intent i = new Intent(SwitchboardActivity.this, SettingsActivity.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.help) {
			Intent i = new Intent(SwitchboardActivity.this, HelpActivity.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.about) {
			Intent i = new Intent(SwitchboardActivity.this, AboutActivity.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.shareApp) {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			// set the type
			shareIntent.setType("text/plain");
			// add a subject
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
			// build the body of the message to be shared
			String shareMessage = getResources().getString(R.string.project_website);
			// add the message
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
			// start the chooser for sharing
			startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_with)));

			Answers.getInstance().logShare(new ShareEvent()
					.putMethod("Mail")
					.putContentName("Global")
					.putContentType("App") // Map, Photo
					.putContentId(PropertyHolder.getUserId()));
			return true;
        } else if (item.getItemId() == R.id.web) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(UtilLocal.URL_PROJECT));
            startActivity(i);
        }
		return false;
	}

	public void askForPermissions() {
		String[] permissionsArray = mPermissionsDenied.toArray(new String[mPermissionsDenied.size()]);
		if (permissionsArray.length > 0) {
			ActivityCompat.requestPermissions(SwitchboardActivity.this, permissionsArray, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
			{
				boolean allGranted = true;
				// Cbeck for all granted permissions
				for (int i = 0; i < grantResults.length; i++)
					allGranted = allGranted && (grantResults[i] == PackageManager.PERMISSION_GRANTED);

				if ( allGranted ) {
					onCreateWithPermissions();
				}
				else {
					// Permission Denied
					mPermissionsDenied = getDeniedPermissions();
//Toast.makeText(SwitchboardActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							askForPermissions();
						}
					}, 2000);
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}

	}

	// https://github.com/tajchert/Nammu/blob/master/nammu/src/main/java/pl/tajchert/nammu/Nammu.java
	public ArrayList<String> getDeniedPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		ArrayList<String> permissionsDenied = new ArrayList<>();
		// Group location
		permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
		permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		// Group ??
		//permissions.add(Manifest.permission.BATTERY_STATS);
		// Group Storage
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		for (String permission : permissions) {
			if (!hasPermission(this, permission)) {
				permissionsDenied.add(permission);
			}
			//else
			//	Toast.makeText(this, "Permission granted\n" + permission, Toast.LENGTH_SHORT).show();
		}
		return permissionsDenied;
	}


	public boolean hasPermission(Activity activity, String permission) {
		return ContextCompat.checkSelfPermission(activity, permission) == 0;
	}
}
