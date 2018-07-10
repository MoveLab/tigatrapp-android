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
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import ceab.movelab.tigabib.ContProvContractMissions.Tasks;
import ceab.movelab.tigabib.ContProvContractReports.Reports;

import static ceab.movelab.tigabib.Util.buildCustomAlert;

/**
 * Activity for identifying and reporting mosquitoes and breeding sites.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ReportToolActivity extends Activity {

	private static String TAG = "ReportToolActivity";

	final Context context = this;

	private MyCountDownTimer countDownTimer;

	private boolean gpsAvailable;
	private boolean networkLocationAvailable;

	private boolean isEditing;

	private Report thisReport;

	LocationManager locationManager;
	LocationListener gpsListener;
	LocationListener networkListener;
	Location currentLocation;

	int type = -1;
	int locationChoice = -1;
/*	int LOCATION_CHOICE_SELECTED = 1;
	int LOCATION_CHOICE_CURRENT = 0;
	int LOCATION_CHOICE_MISSING = -1;*/

	ScrollView reportScroll;

	TextView reportTitle;
	RelativeLayout reportTitleRow;
	RelativeLayout reportConfirmationRow;
	RelativeLayout reportLocationRow;
	RelativeLayout reportCurrentLocationRow;
	RelativeLayout reportSelectedLocationRow;
	RelativeLayout reportPhotoRow;
	RelativeLayout reportNoteRow;

	CheckBox reportConfirmationCheck;
	CheckBox reportLocationCheck;
	CheckBox reportPhotoCheck;
	CheckBox reportNoteCheck;

//	ImageView reportPhotoAttachImage;
//	ImageView reportConfirmationImage;
	ImageView reportCurrentLocationImage;
//	ImageView reportMapImage;
//	ImageView reportNoteImage;

	private TextView photoCount;
	private Button buttonReportSubmit;
	private RadioGroup locationRadioGroup;

	private String myMessage;

	public static final int REQUEST_CODE_TAKE_PHOTO = 1;
	public static final int REQUEST_CODE_MAPSELECTOR = 2;
	public static final int REQUEST_CODE_ATTACHED_PHOTOS = 3;
	public static final int REQUEST_CODE_REPORT_RESPONSES = 4;
	public static final int REQUEST_CODE_GET_PHOTO_FROM_GALLERY = 5;

//	public static final String EXTRA_PHOTO_URI_ARRAY = "photoUriArray";
//	public static final String EXTRA_PHOTO_TIME_ARRAY = "photoTimeArray";
//	public static final String EXTRA_REPORT_ID = "reportId";
//	public static final String EXTRA_REPORT_VERSION = "reportVersions";

	public static final String PREVIOUS_LAT = "previous_lat";
	public static final String PREVIOUS_LON = "previous_lon";

	private boolean has_edited_location = false;

	private String lang;

	private FirebaseAnalytics mFirebaseAnalytics;


	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Util.logInfo(TAG, "on create ReportTool");

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		lang = Util.setDisplayLanguage(getResources());

		// Obtain the FirebaseAnalytics instance.
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		Bundle b = getIntent().getExtras();
		type = b.getInt("type");
		isEditing = b.containsKey("reportId");

		if ( isEditing ) {

			this.setTitle(context.getResources().getString(R.string.activity_label_report_editing));

			ContentResolver cr = getContentResolver();
			String sc = Reports.KEY_REPORT_ID + " = '" + b.getString("reportId") + "' AND "
					+ Reports.KEY_LATEST_VERSION + " = 1 AND "
					+ Reports.KEY_DELETE_REPORT + " = 0";

			Cursor c = cr.query(Util.getReportsUri(context), Reports.KEYS_ALL, sc, null, null);

			if ( c != null && c.moveToLast() ) {
				int userIdCol = c.getColumnIndexOrThrow(Reports.KEY_USER_ID);
				int reportIdCol = c.getColumnIndexOrThrow(Reports.KEY_REPORT_ID);
				int reportTimeCol = c.getColumnIndexOrThrow(Reports.KEY_REPORT_TIME);
				int creationTimeCol = c.getColumnIndexOrThrow(Reports.KEY_CREATION_TIME);
				int reportVersionCol = c.getColumnIndexOrThrow(Reports.KEY_REPORT_VERSION);
				int versionTimeStringCol = c.getColumnIndexOrThrow(Reports.KEY_VERSION_TIME_STRING);
				int typeCol = c.getColumnIndexOrThrow(Reports.KEY_TYPE);
				int confirmationCol = c.getColumnIndexOrThrow(Reports.KEY_CONFIRMATION);
				int confirmationCodeCol = c.getColumnIndexOrThrow(Reports.KEY_CONFIRMATION_CODE);
				int locationChoiceCol = c.getColumnIndexOrThrow(Reports.KEY_LOCATION_CHOICE);
				int currentLocationLonCol = c.getColumnIndexOrThrow(Reports.KEY_CURRENT_LOCATION_LON);
				int currentLocationLatCol = c.getColumnIndexOrThrow(Reports.KEY_CURRENT_LOCATION_LAT);
				int selectedLocationLonCol = c.getColumnIndexOrThrow(Reports.KEY_SELECTED_LOCATION_LON);
				int selectedLocationLatCol = c.getColumnIndexOrThrow(Reports.KEY_SELECTED_LOCATION_LAT);
				int noteCol = c.getColumnIndexOrThrow(Reports.KEY_NOTE);
				int photoAttachedCol = c.getColumnIndexOrThrow(Reports.KEY_PHOTO_ATTACHED);
				int photoUrisCol = c.getColumnIndexOrThrow(Reports.KEY_PHOTO_URIS);
				int serverTimestampCol = c.getColumnIndexOrThrow(Reports.KEY_SERVER_TIMESTAMP);
				int deleteReportCol = c.getColumnIndexOrThrow(Reports.KEY_DELETE_REPORT);
				int latestVersionCol = c.getColumnIndexOrThrow(Reports.KEY_LATEST_VERSION);
				int packageNameCol = c.getColumnIndexOrThrow(Reports.KEY_PACKAGE_NAME);
				int packageVersionCol = c.getColumnIndexOrThrow(Reports.KEY_PACKAGE_VERSION);
				int phoneManufacturerCol = c.getColumnIndexOrThrow(Reports.KEY_PHONE_MANUFACTURER);
				int phoneModelCol = c.getColumnIndexOrThrow(Reports.KEY_PHONE_MODEL);
				int osCol = c.getColumnIndexOrThrow(Reports.KEY_OS);
				int osVersionCol = c.getColumnIndexOrThrow(Reports.KEY_OS_VERSION);
				int osLanguageCol = c.getColumnIndexOrThrow(Reports.KEY_OS_LANGUAGE);
				int appLanguageCol = c.getColumnIndexOrThrow(Reports.KEY_APP_LANGUAGE);
				int missionIDCol = c.getColumnIndexOrThrow(Reports.KEY_MISSION_ID);

				// note that we increment the version number here
				thisReport = new Report(context, UUID.randomUUID().toString(),
						c.getString(userIdCol), c.getString(reportIdCol),
						(c.getInt(reportVersionCol) + 1),
						c.getLong(reportTimeCol),
						c.getString(creationTimeCol),
						c.getString(versionTimeStringCol),
						c.getInt(typeCol),
						c.getString(confirmationCol),
						c.getInt(confirmationCodeCol),
						c.getInt(locationChoiceCol),
						c.getFloat(currentLocationLatCol), c.getFloat(currentLocationLonCol),
						c.getFloat(selectedLocationLatCol), c.getFloat(selectedLocationLonCol),
						c.getInt(photoAttachedCol),
						c.getString(photoUrisCol),
						c.getString(noteCol),
						Report.UPLOADED_NONE,
						c.getLong(serverTimestampCol),
						c.getInt(deleteReportCol),
						c.getInt(latestVersionCol),
						c.getString(packageNameCol),
						c.getInt(packageVersionCol),
						c.getString(phoneManufacturerCol),
						c.getString(phoneModelCol),
						c.getString(osCol),
						c.getString(osVersionCol),
						c.getString(osLanguageCol),
						c.getString(appLanguageCol),
						c.getInt(missionIDCol));
			}
			if ( c!= null ) c.close();
		} else {
			this.setTitle(context.getResources().getString(R.string.activity_label_report_new));
			thisReport = new Report(type, PropertyHolder.getUserId());
		}

		setContentView(R.layout.report);

		reportScroll = (ScrollView) findViewById(R.id.reportView);
		reportTitle = (TextView) findViewById(R.id.reportTitle);
		reportTitleRow = (RelativeLayout) findViewById(R.id.reportTitleRow);

		reportConfirmationRow = (RelativeLayout) findViewById(R.id.reportConfirmationRow);
		reportLocationRow = (RelativeLayout) findViewById(R.id.reportLocationRow);
		reportCurrentLocationRow = (RelativeLayout) findViewById(R.id.reportCurrentLocationRow);
		reportSelectedLocationRow = (RelativeLayout) findViewById(R.id.reportSelectedLocationRow);
		reportPhotoRow = (RelativeLayout) findViewById(R.id.reportPhotoRow);
		reportNoteRow = (RelativeLayout) findViewById(R.id.reportNoteRow);
		reportConfirmationCheck = (CheckBox) findViewById(R.id.reportConfirmationCheck);
		reportLocationCheck = (CheckBox) findViewById(R.id.reportLocationCheck);
		reportPhotoCheck = (CheckBox) findViewById(R.id.reportPhotoCheck);
		photoCount = (TextView) findViewById(R.id.photoCount);
		reportNoteCheck = (CheckBox) findViewById(R.id.reportNoteCheck);
		locationRadioGroup = (RadioGroup) findViewById(R.id.whereFoundRadioGroup);
		reportCurrentLocationImage = (ImageView) findViewById(R.id.reportCurrentLocationImage);
		buttonReportSubmit = (Button) findViewById(R.id.buttonReportSubmit);

		if (isEditing) {
			reportTitle.setText((type == Report.TYPE_BREEDING_SITE ? getResources().getString(R.string.edit_title_site) : getResources().getString(R.string.edit_title_adult)) + "\n"
							+ getResources().getString(R.string.created_on) + " "
							+ Util.userDate(new Date((thisReport.reportTime))));

			reportConfirmationCheck.setText(getResources().getString(R.string.paso1));
			reportLocationCheck.setText(getResources().getString(R.string.paso2));
			reportPhotoCheck.setText(getResources().getString(R.string.paso3));
			reportNoteCheck.setText(getResources().getString(R.string.paso4));

			buttonReportSubmit.setText(getResources().getString(R.string.update));
		} else {
			reportTitle.setText(getResources().getText(type == Report.TYPE_BREEDING_SITE ? R.string.report_title_site : R.string.report_title_adult));
		}

		if ( icicle != null ) {
			has_edited_location = icicle.getBoolean("has_edited_location");
			thisReport.reportId = icicle.getString("reportId");
			thisReport.confirmation = icicle.getString("confirmation");
			thisReport.confirmationCode = icicle.getInt("confirmation_code");
			thisReport.locationChoice = icicle.getInt("locationChoice");
			if (icicle.containsKey("currentLocationLat"))
				thisReport.currentLocationLat = icicle.getFloat("currentLocationLat");
			if (icicle.containsKey("currentLocationLon"))
				thisReport.currentLocationLon = icicle.getFloat("currentLocationLon");
			if (icicle.containsKey("selectedLocationLat"))
				thisReport.selectedLocationLat = icicle.getFloat("selectedLocationLat");
			if (icicle.containsKey("selectedLocationLon"))
				thisReport.selectedLocationLon = icicle.getFloat("selectedLocationLon");
			thisReport.photoAttached = icicle.getInt("photoAttached");
			thisReport.note = icicle.getString("note");
			thisReport.setPhotoUris(context, icicle.getString(Reports.KEY_PHOTO_URIS));
		}

		if ( thisReport.reportId == null ) {
			thisReport.reportId = Util.makeReportId();
		}

		OnClickListener ocl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.reportTitleRow) {
					Util.showHelp(context,
							type == Report.TYPE_ADULT ? context.getResources()
									.getString(R.string.adult_report_help_html)
									: context.getResources().getString(
											R.string.site_report_help_html));
					return;
				} else if (v.getId() == R.id.reportConfirmationRow) {
					try {
						String thisTaskType = type == Report.TYPE_ADULT ? MissionModel
								.makeAdultConfirmation(context).getString(Tasks.KEY_TASK_JSON) : MissionModel
								.makeSiteConfirmation(context).getString(Tasks.KEY_TASK_JSON);
						Intent i = new Intent(ReportToolActivity.this, MissionActivity.class);
						i.putExtra(Tasks.KEY_TASK_JSON, thisTaskType);
						if (thisReport.confirmation != null) {
							i.putExtra(Tasks.KEY_RESPONSES_JSON, thisReport.confirmation);
						}
						startActivityForResult(i, REQUEST_CODE_REPORT_RESPONSES);

					} catch (JSONException e1) {
						Util.logError(TAG, "error: " + e1);
					}
					return;
				} else if (v.getId() == R.id.reportLocationRow) {
					if (!reportLocationCheck.isChecked())
						buildLocationMenu();
					return;
				} else if (v.getId() == R.id.reportCurrentLocationRow) {
					if (currentLocation == null) {
						if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
								|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
							buildLocationAlert(getResources().getString(R.string.nolocation_alert_report));
						} else if (!gpsAvailable && !networkLocationAvailable) {
							buildAlertMessageNoGpsNoNet(getResources().getString(R.string.noGPSnoNetAlert));
						} else {
							buildLocationAlert(getResources().getString(R.string.nolocnogps_alert));
						}
					} else {
						locationRadioGroup.check(R.id.whereRadioButtonHere);
						has_edited_location = true;
						reportLocationCheck.setChecked(true);
						final float clat = (float) currentLocation.getLatitude();
						final float clon = (float) currentLocation.getLongitude();

						thisReport.currentLocationLat = clat;
						thisReport.currentLocationLon = clon;
						thisReport.locationChoice = Report.LOCATION_CHOICE_CURRENT;

						/*
						 * Util.toast( context,
						 * "FOR TESTING... Aitana: what values are displayed here?\n\nLat: "
						 * + String.format("%.5g%n",
						 * thisReport.currentLocationLat) + "\nLon: " +
						 * String.format("%.5g%n",
						 * thisReport.currentLocationLon));
						 */
						Util.toast(context, getResources().getString(R.string.added_current_loc)
								+ "\n\nLat: " + String.format("%.5g%n", thisReport.currentLocationLat)
								+ "\nLon: " + String.format("%.5g%n", thisReport.currentLocationLon));
					}

					return;
				} else if (v.getId() == R.id.reportSelectedLocationRow) {
					goToMapSelector();
					return;
				} else if (v.getId() == R.id.reportPhotoRow) {
					Intent intentPhotos = new Intent(ReportToolActivity.this, AttachedPhotosActivity.class);
					intentPhotos.putExtra(Reports.KEY_PHOTO_URIS, thisReport.photoUrisJson.toString());
					startActivityForResult(intentPhotos, REQUEST_CODE_ATTACHED_PHOTOS);
					return;
				} else if (v.getId() == R.id.reportNoteRow) {
					buildReportNoteDialog();
					return;
				}
			}
		};

		reportTitleRow.setOnClickListener(ocl);
		reportConfirmationRow.setOnClickListener(ocl);
		reportLocationRow.setOnClickListener(ocl);
		reportCurrentLocationRow.setOnClickListener(ocl);
		reportSelectedLocationRow.setOnClickListener(ocl);
		reportPhotoRow.setOnClickListener(ocl);
		reportNoteRow.setOnClickListener(ocl);

		reportConfirmationCheck.setChecked(thisReport.confirmationCode > 0);

		if ( has_edited_location ) {
			if ( thisReport.locationChoice == Report.LOCATION_CHOICE_CURRENT )
				locationRadioGroup.check(R.id.whereRadioButtonHere);
			else if ( thisReport.locationChoice == Report.LOCATION_CHOICE_SELECTED )
				locationRadioGroup.check(R.id.whereRadioButtonOtherPlace);
		}
		reportLocationCheck.setChecked(thisReport.locationChoice != Report.MISSING);

		if ( type == Report.TYPE_ADULT ) {
			reportConfirmationCheck.setText(getResources().getString(R.string.paso1_2));
			reportPhotoCheck.setText(getResources().getString(R.string.paso3));
		}
		else
			reportConfirmationCheck.setText(getResources().getString(R.string.paso1));
		reportPhotoCheck.setText(getResources().getString(R.string.paso3));	// star !!!

		if ( thisReport.photoUrisJson != null
				&& thisReport.photoUrisJson.length() > 0 ) {
			photoCount.setVisibility(View.VISIBLE);
			photoCount.setText(String.valueOf(thisReport.photoUrisJson.length()));
			reportPhotoCheck.setChecked(true);
			thisReport.photoAttached = Report.YES;
		} else {
			photoCount.setVisibility(View.GONE);
			reportPhotoCheck.setChecked(false);
			thisReport.photoAttached = Report.NO;
		}
		reportNoteCheck.setChecked(thisReport.note != null);
		buttonReportSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if ( thisReport.confirmation == null
						|| thisReport.confirmation.equals("")
						|| !reportConfirmationCheck.isChecked()
						|| !reportLocationCheck.isChecked()
						|| (type == Report.TYPE_BREEDING_SITE && !reportPhotoCheck.isChecked()) ) {
					Util.toast(context, getResources().getString(
						(type == Report.TYPE_ADULT ? R.string.toast_report_before_submitting_adult
								: R.string.toast_report_before_submitting_site))
								+ "\n\n"
								+ (reportConfirmationCheck.isChecked() ? "" : (getResources().getString(R.string.toast_complete_checklist) + "\n"))
								+ (reportLocationCheck.isChecked() ? "" : (getResources().getString(R.string.toast_specify_location) + "\n"))
								+ ((type == Report.TYPE_BREEDING_SITE &&
									!reportPhotoCheck.isChecked()) ? getResources().getString(R.string.toast_attach_photo) : ""));
				} else {
					myMessage = getResources().getString(R.string.report_sent);
					buildMailMessage(myMessage);
				}
			}
		});

		countDownTimer = new MyCountDownTimer(5 * 60 * 1000, 5 * 60 * 1000);
	}

	private class mLocationListener implements LocationListener {
		/**
		 * Defines LocationListener behavior upon reception of a location fix
		 * update from the LocationManager.
		 */
		public void onLocationChanged(Location location) {

			// Quick return if given location is null or has an invalid time
			if (location == null || location.getTime() < 0) {
				return;
			} else {
				if ( currentLocation == null
						|| (location.getAccuracy() < currentLocation.getAccuracy())) {

					currentLocation = location;

					reportCurrentLocationImage.clearAnimation();
					reportCurrentLocationImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_location_found));
				}

				if (location.getAccuracy() < 100) {
					removeLocationUpdates();
					if (countDownTimer != null) {
						try {
							countDownTimer.cancel();
						} catch (Exception e) {
							Util.logError(TAG, "exception cancelling countdown timer " + e.getMessage());
						}
					} else {
						countDownTimer = new MyCountDownTimer(5 * 60 * 1000, 5 * 60 * 1000);
					}
					countDownTimer.start();
				}

			}
		}

		/**
		 * Defines behavior when the given provider is disabled.
		 * 
		 * @param provider
		 *            The provider to be disabled
		 */
		public void onProviderDisabled(String provider) {
			removeLocationUpdate(provider);
		}

		/**
		 * Defines behavior when the given provider is re-enabled. Currently no
		 * behavior is defined.
		 * 
		 * @param provider
		 *            The provider to be re-enabled
		 */
		public void onProviderEnabled(String provider) { }

		public void onStatusChanged(String provider, int status, Bundle extras) {
			/*
			 * If provider service is no longer available, stop trying to get
			 * updates from both ceab.movelab.tigabib.providers but start timer.
			 */
			if ( status != LocationProvider.AVAILABLE ) {
				removeLocationUpdate(provider);
				if (countDownTimer != null) {
					try {
						countDownTimer.cancel();
					} catch (Exception e) {
						Util.logError(TAG, "exception cancelling countdown timer");
					}
				} else {
					countDownTimer = new MyCountDownTimer(5 * 60 * 1000, 5 * 60 * 1000);
				}
				countDownTimer.start();
			}
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		icicle.putBoolean("has_edited_location", has_edited_location);
		icicle.putString("reportId", thisReport.reportId);
		icicle.putString("confirmation", thisReport.confirmation);
		icicle.putInt("confirmation_code", thisReport.confirmationCode);
		icicle.putInt("locationChoice", thisReport.locationChoice);

		if (thisReport.selectedLocationLat != null)
			icicle.putFloat("selectedLocationLat", thisReport.selectedLocationLat);

		if (thisReport.selectedLocationLon != null)
			icicle.putFloat("selectedLocationLon", thisReport.selectedLocationLon);

		if (thisReport.currentLocationLat != null)
			icicle.putFloat("currentLocationLat", thisReport.currentLocationLat);

		if (thisReport.currentLocationLon != null)
			icicle.putFloat("currentLocationLon", thisReport.currentLocationLon);

		icicle.putInt("photoAttached", thisReport.photoAttached);
		icicle.putString("note", thisReport.note);
		icicle.putString(Reports.KEY_PHOTO_URIS, thisReport.photoUrisJson.toString());

		if (type != -1)
			icicle.putInt("type", type);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if ( !Util.setDisplayLanguage(getResources()).equals(lang) ) {
			finish();
			startActivity(getIntent());
		}

		if (isEditing) {
			// [START set_current_screen]
			mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_report_tool",
					( type == Report.TYPE_BREEDING_SITE ? "Report Breeding Site Edit" : "Report Mosquito Edit") );
			// [END set_current_screen]
		} else {
			// [START set_current_screen]
			mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_report_tool",
					( type == Report.TYPE_BREEDING_SITE ? "Report Breeding Site" : "Report Mosquito") );
			// [END set_current_screen]
		}

		gpsAvailable = false;
		networkLocationAvailable = false;

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		try {
			if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
				gpsListener = new mLocationListener();
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
				gpsAvailable = true;
			}
			if ( locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {
				networkListener = new mLocationListener();
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
				networkLocationAvailable = true;
			}
		} catch (SecurityException se) {
			Crashlytics.log("ReportToolActivity: onResume");
			Crashlytics.logException(new SecurityException());
		}

		if ( currentLocation == null ) {

			if ( !gpsAvailable && !networkLocationAvailable ) {
				reportCurrentLocationImage.clearAnimation();
				reportCurrentLocationImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_location_off));
			} else {
				reportCurrentLocationImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_location_searching));
				Animation blink = new AlphaAnimation(0.0f, 1.0f);
				blink.setDuration(300);
				blink.setStartOffset(20);
				blink.setRepeatMode(Animation.REVERSE);
				blink.setRepeatCount(Animation.INFINITE);
				reportCurrentLocationImage.startAnimation(blink);
			}
		} else {
			reportCurrentLocationImage.clearAnimation();
			reportCurrentLocationImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_location_found));
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		Util.logInfo(TAG, "on pause");

		removeLocationUpdates();
		try {
			countDownTimer.cancel();
		} catch (Exception e) {
			Util.logError(TAG, "exception cancelling countdown timer");
		}
		countDownTimer = null;
	}

	@Override
	public void onStop() {
		super.onStop();
		Util.logInfo(TAG, "on stop");

		removeLocationUpdates();
		try {
			countDownTimer.cancel();
		} catch (Exception e) {
			Util.logError(TAG, "exception cancelling countdown timer");
		}
		countDownTimer = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Util.logInfo(TAG, "on destroy");

		removeLocationUpdates();
		clearFields();
		try {
			countDownTimer.cancel();
		} catch (Exception e) {
			Util.logError(TAG, "exception cancelling countdown timer");
		}
		countDownTimer = null;
	}

	// utilities
	private void removeLocationUpdates() {
		Util.logInfo(TAG, "remove location updates");

		try {
			if (locationManager != null) {
				Util.logInfo(TAG, "remove location updates 1");
				if (gpsListener != null)
					locationManager.removeUpdates(gpsListener);
				if (networkListener != null)
					locationManager.removeUpdates(networkListener);
			} else {
				Util.logInfo(TAG, "remove location updates 2");
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				if (gpsListener != null)
					locationManager.removeUpdates(gpsListener);
				if (networkListener != null)
					locationManager.removeUpdates(networkListener);
			}
		} catch (SecurityException se) {
			Crashlytics.log("ReportToolActivity: removeLocationUpdates");
			Crashlytics.logException(new SecurityException());
		}
		gpsListener = null;
		networkListener = null;
		locationManager = null;
	}

	// utilities
	private void removeLocationUpdate(String provider) {
		try {
			if (locationManager != null) {
				if ( provider.contentEquals(LocationManager.NETWORK_PROVIDER) ) {
					if (networkListener != null) {
						locationManager.removeUpdates(networkListener);
						networkListener = null;
					}
				} else {
					if (gpsListener != null) {
						locationManager.removeUpdates(gpsListener);
						gpsListener = null;
					}
				}
			} else {
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				if ( provider.contentEquals(LocationManager.NETWORK_PROVIDER) ) {
					if (networkListener != null) {
						locationManager.removeUpdates(networkListener);
						networkListener = null;
					}
				} else {
					if (gpsListener != null) {
						locationManager.removeUpdates(gpsListener);
						gpsListener = null;
					}
				}
			}
		} catch (SecurityException se) {
			Crashlytics.log("ReportToolActivity: removeLocationUpdate");
			Crashlytics.logException(new SecurityException());
		}
	}

	private void buildMailMessage(String message) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.mail_message_alert);

		TextView reportIdTitle = (TextView) dialog.findViewById(R.id.yourIdIsText);
		TextView reportIdText = (TextView) dialog.findViewById(R.id.reportIdText);
		reportIdTitle.setVisibility(View.GONE);
		reportIdText.setVisibility(View.GONE);

		TextView mText = (TextView) dialog.findViewById(R.id.alertText);
		mText.setText(Html.fromHtml(message));
		mText.setPadding(10, 10, 10, 10);

		Button positive = (Button) dialog.findViewById(R.id.alertOK);
		Button cancel = (Button) dialog.findViewById(R.id.alertCancel);

		positive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				removeLocationUpdates();

				// Intent goHome = new Intent(ReportTool.this,
				// SwitchboardActivity.class);
				// startActivity(goHome);
				/*
				 * Report thisReport = new Report( reportID,
				 * System.currentTimeMillis(), size, legs, stripe, herethere,
				 * currentLocation != null ? String
				 * .valueOf(currentLocation.getLongitude()) : null,
				 * currentLocation != null ? String
				 * .valueOf(currentLocation.getLatitude()) : null, selectedLon
				 * != -1 ? String.valueOf(selectedLon) : null, selectedLat != -1
				 * ? String.valueOf(selectedLat) : null, note, mailingSpecimen ?
				 * "si" : "no", photoAttached ? "si" : "no", photoUri);
				 */
				// uploadReport(thisReport, Util.SERVER);

				if ( thisReport.reportTime == Report.MISSING )
					thisReport.reportTime = System.currentTimeMillis();

				try {
					PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
					thisReport.packageName = pInfo.packageName;
					thisReport.packageVersion = pInfo.versionCode;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				String language = PropertyHolder.getLanguage();
				new ReportUploadTask(context, getContentResolver(), thisReport, isEditing, language).execute(context);

				dialog.cancel();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}
		switch (requestCode) {
		case REQUEST_CODE_ATTACHED_PHOTOS: {
			if ( resultCode == RESULT_OK ) {
				if ( data.hasExtra(Reports.KEY_PHOTO_URIS) ) {
					String incomingPhotoUris = data.getStringExtra(Reports.KEY_PHOTO_URIS);
					if ( incomingPhotoUris.length() > 0 ) {
						thisReport.setPhotoUris(context, incomingPhotoUris);
						photoCount.setVisibility(View.VISIBLE);
						photoCount.setText(String.valueOf(thisReport.photoUrisJson.length()));
						reportPhotoCheck.setChecked(true);
						thisReport.photoAttached = Report.YES;

						// MG - Following method removed file from gallery
/*						for (int i = 0; i < thisReport.photoUrisJson.length(); i++) {
							try {
								JSONObject row = thisReport.photoUrisJson.getJSONObject(i);
								*//*MediaScannerConnection.scanFile(context, new String[]{row.getString("photo_uri")},
										new String[]{"image/*"}, new MediaScannerConnection.OnScanCompletedListener() {
											public void onScanCompleted(String path, Uri uri) {
												Util.logInfo(this.getClass().toString(), "Finished scanning " + path);
											}
										});
										https://stackoverflow.com/questions/8379690/androids-media-scanner-how-do-i-remove-files
										MediaScannerConnection.scanFile(context,
										new String[]{fileToDelete, fileToAdd},
										null, null); *//*
							}
							catch (Exception e) {
								e.printStackTrace();
							};
						}*/
					}
					else {
						photoCount.setVisibility(View.GONE);
						reportPhotoCheck.setChecked(false);
						thisReport.photoAttached = Report.NO;
						thisReport.setPhotoUris(context, null);
					}
				} else {
					photoCount.setVisibility(View.GONE);
					reportPhotoCheck.setChecked(false);
					thisReport.photoAttached = Report.NO;
					thisReport.setPhotoUris(context, null);
				}
			}
			break;
		}
		case REQUEST_CODE_MAPSELECTOR: {
			thisReport.selectedLocationLat = null;
			thisReport.selectedLocationLon = null;

			if ( resultCode == RESULT_OK && data != null ) {
				if ( data.hasExtra(MapSelectorV2Activity.LAT )) {
					thisReport.selectedLocationLat = (float) data.getDoubleExtra(MapSelectorV2Activity.LAT, -1);
				}
				if ( data.hasExtra(MapSelectorV2Activity.LON )) {
					thisReport.selectedLocationLon = (float) data.getDoubleExtra(MapSelectorV2Activity.LON, -1);
				}
			}
			if ( thisReport.selectedLocationLat != null
					&& thisReport.selectedLocationLon != null ) {
				locationRadioGroup.check(R.id.whereRadioButtonOtherPlace);
				reportLocationCheck.setChecked(true);
				thisReport.locationChoice = Report.LOCATION_CHOICE_SELECTED;
				has_edited_location = true;
			}
			break;
		}
		case REQUEST_CODE_REPORT_RESPONSES: {
			reportConfirmationCheck.setChecked(false);
			if ( resultCode == RESULT_OK ) {
				if (data.hasExtra(Tasks.KEY_RESPONSES_JSON)) {
					String responses = data.getStringExtra(Tasks.KEY_RESPONSES_JSON);
					thisReport.confirmation = responses;
				}
				if (data.hasExtra(Reports.KEY_CONFIRMATION_CODE)) {
					thisReport.confirmationCode = data.getIntExtra(
							Reports.KEY_CONFIRMATION_CODE,
							Report.CONFIRMATION_CODE_POSITIVE);
					if (thisReport.confirmationCode > 0) {
						reportConfirmationCheck.setChecked(true);
					}
				}
			//} else {
				// TODO
			}
		}
		}
	}

	private class ReportUploadTask extends AsyncTask<Context, Integer, Boolean> {

		private Context mContext;
		private ContentResolver mCR;
		private Report mReport;
		private boolean isEditing;
		private String mLanguage;

		private ProgressDialog mProgDialog;
		private int myProgress;
		private int resultFlag;

		private int OFFLINE = 0;
		private int UPLOAD_ERROR = 1;
		private int DATABASE_ERROR = 2;
		private int SUCCESS = 3;
		private int PRIVATE_MODE = 4;

		ReportUploadTask(Context ctx, ContentResolver cr, Report report, boolean editing, String language)  {
			this.mContext = ctx;
			this.mCR = cr;
			this.mReport = report;
			this.isEditing = editing;
			this.mLanguage = language;
		}

		@Override
		protected void onPreExecute() {

			resultFlag = SUCCESS;

			mProgDialog = new ProgressDialog(mContext);
			mProgDialog.setTitle(mContext.getResources().getString(R.string.progtitle_report));
			mProgDialog.setIndeterminate(false);
			mProgDialog.setMax(100);
			mProgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgDialog.show();

			myProgress = 0;

			// Send Firebase Event
//			Bundle bundle = new Bundle();
//			bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type == Report.TYPE_BREEDING_SITE ? "Breeding Site" : "Mosquito");
//			mFirebaseAnalytics.logEvent("ma_evt_btn_send_report", bundle);
		}

		protected Boolean doInBackground(Context... context) {

			myProgress = 2;
			publishProgress(myProgress);

			mReport.versionTimeString = Util.ecma262(System.currentTimeMillis());
			if ( !isEditing )
				mReport.creation_time = Util.ecma262(System.currentTimeMillis());

			myProgress = 4;
			publishProgress(myProgress);

			thisReport.phoneManufacturer = Build.MANUFACTURER;
			thisReport.phoneModel = Build.MODEL;
			thisReport.os = "Android";
			thisReport.osversion = Integer.toString(Build.VERSION.SDK_INT);
			thisReport.osLanguage = Locale.getDefault().getLanguage();
			thisReport.appLanguage = mLanguage;

			myProgress = 10;
			publishProgress(myProgress);

			// First save report to internal DB
			Uri repUri = Util.getReportsUri(context[0]);
			Uri thisReportUri = mCR.insert(repUri, ContProvValuesReports.createReport(thisReport));

			// now mark all prior reports as not latest version
			String where = Reports.KEY_REPORT_ID + " = '" + thisReport.reportId + "' AND "
					+ Reports.KEY_REPORT_VERSION + " < " + thisReport.reportVersion;
			ContentValues cv = new ContentValues();
			cv.put(Reports.KEY_LATEST_VERSION, 0);
			mCR.update(repUri, cv, where, null);

			myProgress = 20;
			publishProgress(myProgress);

			if ( !Util.privateMode() ) {
				// now test if there is a data connection
				if ( !Util.isOnline(context[0]) ) {
					resultFlag = OFFLINE;
					return false;
				}
				if ( !PropertyHolder.isRegistered() ) {
					Util.registerOnServer(context[0]);
					try {
						// Get FCM token and register on server
						String token = FirebaseInstanceId.getInstance().getToken();
						Util.registerFCMToken(context[0], token, PropertyHolder.getUserId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				myProgress = 80;
				publishProgress(myProgress);

				int uploadResult = thisReport.upload(context[0]);

				if ( uploadResult == Report.UPLOADED_ALL ) {
					myProgress = 100;
					publishProgress(myProgress);
					// mark as uploaded
					cv = new ContentValues();
					cv.put(Reports.KEY_UPLOADED, uploadResult);
					int nUpdated = mCR.update(thisReportUri, cv, null, null);
Util.logInfo(TAG, "report uri " + thisReportUri);
Util.logInfo(TAG, "n updated " + nUpdated);
					resultFlag = SUCCESS;

					// try sync and make sure daily syncs are scheduled
					Util.internalBroadcast(context[0], Messages.START_DAILY_SYNC);
				} else
					resultFlag = UPLOAD_ERROR;

			} else {
				resultFlag = PRIVATE_MODE;
			}

			return true;
		}

		protected void onProgressUpdate(Integer... progress) {
			mProgDialog.setProgress(progress[0]);
		}

		protected void onPostExecute(Boolean result) {

			try {
				mProgDialog.dismiss();
				mProgDialog = null;
			} catch (Exception e) {
				// I realize this is ugly, but it is a solution to the problem discussed here:
				// https://stackoverflow.com/questions/2745061/java-lang-illegalargumentexception-view-not-attached-to-window-manager/5102572#5102572
			}

			if ( result && resultFlag == SUCCESS ) {
				Util.toastTimed(mContext, getResources().getString(R.string.report_sent_confirmation), Toast.LENGTH_LONG);
				mReport.clear();
				clearFields();

				// Send Firebase Event
//				Bundle bundle = new Bundle();
//				bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type == Report.TYPE_BREEDING_SITE ? "Breeding Site" : "Mosquito");
//				mFirebaseAnalytics.logEvent("ma_evt_send_report_success", bundle);

				finish();
			} else {
				if ( resultFlag == OFFLINE ) {
					buildCustomAlert(context, getResources().getString(R.string.offline_report));
				}

				if (resultFlag == UPLOAD_ERROR || resultFlag == DATABASE_ERROR) {
					Intent uploaderIntent = new Intent(ReportToolActivity.this, SyncData.class);
					startService(uploaderIntent);

					buildCustomAlert(context, getResources().getString(R.string.upload_error_report));
					thisReport.clear();
					clearFields();
				}

				if (resultFlag == PRIVATE_MODE) {
					buildCustomAlert(context, getResources().getString(R.string.report_sent_confirmation));
					thisReport.clear();
					clearFields();
				}
			}
		}
	}

	public void clearFields() {
		currentLocation = null;
		locationChoice = -1;
		myMessage = null;
	}

	// https://stackoverflow.com/questions/2306503/how-to-make-an-alert-dialog-fill-90-of-screen-size
	public static int getScreenWidth(Activity activity) {
		Point size = new Point();
		activity.getWindowManager().getDefaultDisplay().getSize(size);
		return size.x;
	}

	public void buildReportNoteDialog() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_note);
		dialog.getWindow().setLayout((int) (getScreenWidth(this) * .85), ViewGroup.LayoutParams.WRAP_CONTENT);

		final EditText noteText = (EditText) dialog.findViewById(R.id.noteEditText);

		if (thisReport.note != null && thisReport.note.length() > 0)
			noteText.setText(thisReport.note);

		Button okB = (Button) dialog.findViewById(R.id.addNoteOKButton);
		// if button is clicked, close the custom dialog
		okB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				thisReport.note = noteText.getText().toString();

				if (thisReport.note.length() > 0) {
					reportNoteCheck.setChecked(true);
				} else {
					reportNoteCheck.setChecked(false);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}


	public void buildLocationAlert(String message) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_alert);
		dialog.setCancelable(false);

		TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
		alertText.setText(message);

		Button positive = (Button) dialog.findViewById(R.id.alertOK);
		Button negative = (Button) dialog.findViewById(R.id.alertCancel);
		negative.setVisibility(View.GONE);

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();

	}

	public void buildLocationMenu() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.location_menu);
		dialog.setCancelable(true);

		Button currB = (Button) dialog.findViewById(R.id.currentLocButton);
		Button selectB = (Button) dialog.findViewById(R.id.selectLocButton);

		currB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (currentLocation == null) {
					if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
							|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

						buildLocationAlert(getResources().getString(R.string.nolocation_alert_report));
					} else if (!gpsAvailable && !networkLocationAvailable) {
						buildAlertMessageNoGpsNoNet(getResources().getString(R.string.noGPSnoNetAlert));
					} else {
						buildLocationAlert(getResources().getString(R.string.nolocnogps_alert));
					}
				} else {

					locationRadioGroup.check(R.id.whereRadioButtonHere);
					reportLocationCheck.setChecked(true);
					has_edited_location = true;
					final float cLat = (float) currentLocation.getLatitude();
					final float cLon = (float) currentLocation.getLongitude();

					thisReport.currentLocationLat = cLat;
					thisReport.currentLocationLon = cLon;
					thisReport.locationChoice = Report.LOCATION_CHOICE_CURRENT;

					/*
					 * Util.toast( context,
					 * "FOR TESTING... Aitana: what values are displayed here?\n\nLat: "
					 * + String.format("%.5g%n", thisReport.currentLocationLat)
					 * + "\nLon: " + String.format("%.5g%n",
					 * thisReport.currentLocationLon));
					 */
					Util.toast(context, getResources().getString(R.string.added_current_loc)
							+ "\n\nLat: " + String.format("%.5g%n", thisReport.currentLocationLat)
							+ "\nLon: " + String.format("%.5g%n", thisReport.currentLocationLon));
				}

				dialog.cancel();
			}
		});

		selectB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToMapSelector();
				dialog.cancel();
			}
		});

		dialog.show();

	}

	public void buildAlertMessageNoGpsNoNet(String message) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_alert);
		dialog.setCancelable(false);

		TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
		alertText.setText(message);

		Button positive = (Button) dialog.findViewById(R.id.alertOK);
		Button negative = (Button) dialog.findViewById(R.id.alertCancel);

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				dialog.dismiss();
			}
		});

		negative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();

	}

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			currentLocation = null;

			if ( locationManager == null ) {
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			}

			try {
				if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					gpsListener = new mLocationListener();
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
					gpsAvailable = true;
				}
				if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					networkListener = new mLocationListener();
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
					networkLocationAvailable = true;
				}
			} catch (SecurityException se) {
				Crashlytics.log("ReportToolActivity: onFinish");
				Crashlytics.logException(new SecurityException());
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// do nothing
		}
	}

	/*public void buildLeaveReportWarning() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(getResources().getString(R.string.exit_question));
		dialog.setMessage(getResources().getString(
				R.string.report_not_saved_warning));
		dialog.setCancelable(true);
		dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface d, int arg1) {
						Intent i = new Intent(ReportToolActivity.this, SwitchboardActivity.class);
						startActivity(i);
						finish();
					}

				});

		dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface d, int arg1) {
						d.cancel();
					}
				});

		dialog.show();
	}*/

	private void goToMapSelector() {
		Intent i = new Intent(ReportToolActivity.this, MapSelectorV2Activity.class);
		Bundle b = new Bundle();
		if (isEditing) {
			Util.logInfo(TAG, "about to put location extras");
			if (thisReport.locationChoice == Report.LOCATION_CHOICE_CURRENT) {
				Util.logInfo(TAG, "current location");
				if ( thisReport.currentLocationLat != null ) {
					Util.logInfo(TAG, "current lat: " + thisReport.currentLocationLat);
					b.putFloat(Messages.makeIntentExtraKey(context, PREVIOUS_LAT), thisReport.currentLocationLat);
				}
				if ( thisReport.currentLocationLon != null ) {
					Util.logInfo(TAG, "current lon: " + thisReport.currentLocationLon);
					b.putFloat(Messages.makeIntentExtraKey(context, PREVIOUS_LON), thisReport.currentLocationLon);
				}
			} else if ( thisReport.locationChoice == Report.LOCATION_CHOICE_SELECTED ) {
				if ( thisReport.selectedLocationLat != null ) {
					Util.logInfo(TAG, "selected lat: " + thisReport.selectedLocationLat);
					b.putFloat(Messages.makeIntentExtraKey(context, PREVIOUS_LAT), thisReport.selectedLocationLat);
				}
				if ( thisReport.selectedLocationLon != null ) {
					Util.logInfo(TAG, "selected lon: " + thisReport.selectedLocationLon);
					b.putFloat(Messages.makeIntentExtraKey(context, PREVIOUS_LON), thisReport.selectedLocationLon);
				}
			}
		}
		i.putExtras(b);
		startActivityForResult(i, REQUEST_CODE_MAPSELECTOR);
	}

}
