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
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ceab.movelab.tigabib.ContProvContractMissions.Tasks;
import ceab.movelab.tigabib.chrometabs.CustomTabActivityHelper;
import ceab.movelab.tigabib.chrometabs.WebviewFallback;
import ceab.movelab.tigabib.model.Notification;
import ceab.movelab.tigabib.model.RealmHelper;
import io.realm.Realm;

/**
 * Main menu screen for app.
 *
 * @author John R.B. Palmer
 *
 */
public class SwitchboardActivity extends Activity {

	private static String TAG = "SwitchboardActivity";

	private RelativeLayout reportButtonAdult;
	private RelativeLayout reportButtonSite;
	private RelativeLayout mapButton;
	private RelativeLayout pybossaButton;
	private RelativeLayout notificationsButton;
	private RelativeLayout missionsButton;
	private ImageView websiteButton;
	private ImageView menuButton;

	private String lang;

	private Realm mRealm;

	final private static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 555;
	private ArrayList<String> mPermissionsDenied;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

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
		if ( !Util.privateMode(this) && !PropertyHolder.hasReconsented() ) { // MG - 9/8/16
			Intent i2c = new Intent(SwitchboardActivity.this, ConsentActivity.class);
			startActivity(i2c);
			finish();
		} else {
			if (PropertyHolder.getUserId() == null) {
				String userId = UUID.randomUUID().toString();
				PropertyHolder.setUserId(userId);
				PropertyHolder.setNeedsMosquitoAlertPop(false);
			} else {
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
			}

			if (Util.privateMode(this)) {

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

			// open and close databases in order to trigger any updates
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(Util.getReportsUri(this), new String[]{ContProvContractReports.Reports.KEY_ROW_ID}, null, null, null);
			if ( c != null ) c.close();
			c = cr.query(Util.getMissionsUri(this), new String[]{ContProvContractReports.Reports.KEY_ROW_ID}, null, null, null);
			if ( c != null ) c.close();

			c = cr.query(Util.getMissionsUri(this), new String[]{Tasks.KEY_ID},
					Tasks.KEY_ACTIVE + " = 1 AND " + Tasks.KEY_DONE + " = 0", null, null);
			if ( c != null ) {
				((TextView) findViewById(R.id.reportMissionsNumberText)).setText(String.valueOf(c.getCount()));
			}

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
					Intent i = new Intent(SwitchboardActivity.this, ReportTool.class);
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
					Intent i = new Intent(SwitchboardActivity.this, ReportTool.class);
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
//					Intent i = new Intent(SwitchboardActivity.this, PhotoValidationActivity.class);
//					startActivity(i);
					CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
							.setToolbarColor(getResources().getColor(R.color.green_pybossa)).build();
					CustomTabActivityHelper.openCustomTab(
							SwitchboardActivity.this,// activity
							customTabsIntent,
							Uri.parse(UtilLocal.PYBOSSA_URL),
							new WebviewFallback()
					);
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

			missionsButton = (RelativeLayout) findViewById(R.id.reportMissionsLayout);
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

	@Override
	protected void onResume() {
		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}
		super.onResume();

		mRealm = RealmHelper.getInstance().getRealm(SwitchboardActivity.this);
		loadRemoteNotifications();
	}

	@Override
	protected void onDestroy() {
		if ( mRealm != null ) mRealm.close(); // Remember to close Realm when done.
		super.onDestroy();
	}

	private void loadRemoteNotifications() {
		String notificationUrl = Util.API_NOTIFICATION + "?user_id=" + PropertyHolder.getUserId();

		Ion.with(this)
			.load(Util.URL_TIGASERVER_API_ROOT + notificationUrl)
			.setHeader("Accept", "application/json")
			.setHeader("Content-type", "application/json")
			.setHeader("Authorization", UtilLocal.TIGASERVER_AUTHORIZATION)
			.as(new TypeToken<List<Notification>>(){})
			.setCallback(new FutureCallback<List<Notification>>() {
				@Override
				public void onCompleted(Exception e, List<Notification> result) {
					// do stuff with the result or error
					if ( result != null ) {
						Util.logInfo(SwitchboardActivity.this, TAG, result.toString());
						RealmHelper.getInstance().addOrUpdateNotificationList(result);
					}

					updateNotificationCount();
				}
			});
	}

	private void updateNotificationCount() {
		int count = RealmHelper.getInstance().getNewNotificationsCount();
		((TextView) findViewById(R.id.reportNotificationsNumberText)).setText(String.valueOf(count));
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
			Intent i = new Intent(SwitchboardActivity.this, Help.class);
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
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MosquitoAlert");
			// build the body of the message to be shared
			String shareMessage = getResources().getString(R.string.project_website);
			// add the message
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
			// start the chooser for sharing
			startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_with)));

			return true;
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
		ArrayList<String> permissions = new ArrayList<String>();
		ArrayList<String> permissionsDenied = new ArrayList<String>();
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
