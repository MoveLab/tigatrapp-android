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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import ceab.movlab.tigre.ContentProviderContractPhotos.TigaPhotos;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;

/**
 * Activity for identifying and reporting tiger mosquitoes.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ReportTool extends Activity {

	private CountDownTimer countDownTimer;

	private boolean gpsAvailable;
	private boolean networkLocationAvailable;

	private boolean editing;

	private Report thisReport;

	LocationManager locationManager;
	LocationListener locationListener1;
	LocationListener locationListener2;
	Location currentLocation;

	int type = -1;
	int locationChoice = -1;
	int LOCATION_CHOICE_SELECTED = 1;
	int LOCATION_CHOICE_CURRENT = 0;
	int LOCATION_CHOICE_MISSING = -1;

	final Context context = this;

	ScrollView reportScroll;

	TextView reportTitle;

	RelativeLayout reportConfirmationRow;
	RelativeLayout reportLocationRow;
	RelativeLayout reportCurrentLocationRow;
	RelativeLayout reportSelectedLocationRow;
	RelativeLayout reportPhotoRow;
	RelativeLayout reportNoteRow;
	RelativeLayout reportMailingRow;

	CheckBox reportConfirmationCheck;
	CheckBox reportLocationCheck;
	CheckBox reportPhotoCheck;
	CheckBox reportNoteCheck;
	CheckBox reportMailingCheck;

	ImageView reportPhotoAttachImage;
	ImageView reportConfirmationImage;
	ImageView reportCurrentLocationImage;
	ImageView reportMapImage;
	ImageView reportNoteImage;
	ImageView reportMailingImge;

	TextView photoCount;

	ImageButton mSendRep;

	TextView reportSubmitButtonLabel;

	RadioGroup locationRadioGroup;

	String lang;

	String message;

	public static final int REQUEST_CODE_TAKE_PHOTO = 1;
	public static final int REQUEST_CODE_MAPSELECTOR = 2;
	public static final int REQUEST_CODE_ATTACHED_PHOTOS = 3;

	public static final String EXTRA_PHOTO_URI_ARRAY = "photoUriArray";
	public static final String EXTRA_PHOTO_TIME_ARRAY = "photoTimeArray";
	public static final String EXTRA_REPORT_ID = "reportId";
	public static final String EXTRA_REPORT_VERSION = "reportVersions";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		lang = PropertyHolder.getLanguage();
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

		Bundle b = getIntent().getExtras();
		type = b.getInt("type");
		editing = b.containsKey("reportId");

		if (editing) {

			ContentResolver cr = getContentResolver();
			String sc = Reports.KEY_REPORT_ID + " = '"
					+ b.getString("reportId") + "'";

			Cursor c = cr.query(Reports.CONTENT_URI, Reports.KEYS_ALL, sc,
					null, Reports.KEY_REPORT_VERSION + " ASC");

			if (c.moveToLast()) {

				int rowIdCol = c.getColumnIndexOrThrow(Reports.KEY_ROW_ID);
				int userIdCol = c.getColumnIndexOrThrow(Reports.KEY_USER_ID);
				int reportIdCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_ID);
				int reportTimeCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_TIME);
				int reportVersionCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_VERSION);
				int typeCol = c.getColumnIndexOrThrow(Reports.KEY_TYPE);
				int confirmationCol = c
						.getColumnIndexOrThrow(Reports.KEY_CONFIRMATION);
				int locationChoiceCol = c
						.getColumnIndexOrThrow(Reports.KEY_LOCATION_CHOICE);
				int currentLocationLonCol = c
						.getColumnIndexOrThrow(Reports.KEY_CURRENT_LOCATION_LON);
				int currentLocationLatCol = c
						.getColumnIndexOrThrow(Reports.KEY_CURRENT_LOCATION_LAT);
				int selectedLocationLonCol = c
						.getColumnIndexOrThrow(Reports.KEY_SELECTED_LOCATION_LON);
				int selectedLocationLatCol = c
						.getColumnIndexOrThrow(Reports.KEY_SELECTED_LOCATION_LAT);
				int noteCol = c.getColumnIndexOrThrow(Reports.KEY_NOTE);
				int mailingCol = c.getColumnIndexOrThrow(Reports.KEY_MAILING);
				int photoAttachedCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHOTO_ATTACHED);
				int uploadedCol = c.getColumnIndexOrThrow(Reports.KEY_UPLOADED);
				int serverTimestampCol = c
						.getColumnIndexOrThrow(Reports.KEY_SERVER_TIMESTAMP);
				int deleteReportCol = c
						.getColumnIndexOrThrow(Reports.KEY_DELETE_REPORT);
				int latestVersionCol = c
						.getColumnIndexOrThrow(Reports.KEY_LATEST_VERSION);

				Util.toast(
						context,
						c.getString(reportIdCol) + " "
								+ c.getInt(reportVersionCol));

				// note that we increment the version number here
				thisReport = new Report(c.getString(userIdCol),
						c.getString(reportIdCol),
						c.getInt(reportVersionCol) + 1,
						c.getLong(reportTimeCol), c.getInt(typeCol),
						c.getString(confirmationCol),
						c.getInt(locationChoiceCol),
						c.getFloat(currentLocationLatCol),
						c.getFloat(currentLocationLonCol),
						c.getFloat(selectedLocationLatCol),
						c.getFloat(selectedLocationLonCol),
						c.getInt(photoAttachedCol), c.getString(noteCol),
						c.getInt(mailingCol), c.getInt(uploadedCol),
						c.getLong(serverTimestampCol),
						c.getInt(deleteReportCol), c.getInt(latestVersionCol),
						new ArrayList<Photo>());

			}
			c.close();

			// note that here I am subtracting 1 from the version since it is
			// already incremented. Perhaps there is a less confusing way to
			// organize
			// this.
			sc = TigaPhotos.KEY_REPORT_ID + " = '" + thisReport.reportId
					+ "' AND " + TigaPhotos.KEY_REPORT_VERSION + " = "
					+ (thisReport.reportVersion - 1) + " AND "
					+ TigaPhotos.KEY_USER_ID + " = '" + thisReport.userId + "'";

			c = cr.query(TigaPhotos.CONTENT_URI, TigaPhotos.KEYS_ALL, sc, null,
					null);

			if (c.moveToLast()) {

				int rowIdCol = c.getColumnIndexOrThrow(TigaPhotos.KEY_ROW_ID);
				int userIdCol = c.getColumnIndexOrThrow(TigaPhotos.KEY_USER_ID);
				int reportIdCol = c
						.getColumnIndexOrThrow(TigaPhotos.KEY_REPORT_ID);
				int reportVersionCol = c
						.getColumnIndexOrThrow(TigaPhotos.KEY_REPORT_VERSION);
				int photoUriCol = c
						.getColumnIndexOrThrow(TigaPhotos.KEY_PHOTO_URI);
				int photoTimeCol = c
						.getColumnIndexOrThrow(TigaPhotos.KEY_PHOTO_TIME);

				while (!c.isAfterLast()) {

					thisReport.photos.add(new Photo(c.getString(reportIdCol), c
							.getInt(reportVersionCol),
							c.getString(photoUriCol), c.getLong(photoTimeCol),
							Report.NO, Report.MISSING, Report.NO));

					c.moveToNext();
				}

			}
			c.close();

		} else {
			thisReport = new Report(type, PropertyHolder.getUserId());
		}

		setContentView(R.layout.report);

		reportScroll = (ScrollView) findViewById(R.id.reportView);
		reportTitle = (TextView) findViewById(R.id.reportTitle);
		reportConfirmationRow = (RelativeLayout) findViewById(R.id.reportConfirmationRow);
		reportLocationRow = (RelativeLayout) findViewById(R.id.reportLocationRow);
		reportCurrentLocationRow = (RelativeLayout) findViewById(R.id.reportCurrentLocationRow);
		reportSelectedLocationRow = (RelativeLayout) findViewById(R.id.reportSelectedLocationRow);
		reportPhotoRow = (RelativeLayout) findViewById(R.id.reportPhotoRow);
		reportNoteRow = (RelativeLayout) findViewById(R.id.reportNoteRow);
		reportMailingRow = (RelativeLayout) findViewById(R.id.reportMailingRow);
		reportConfirmationCheck = (CheckBox) findViewById(R.id.reportConfirmationCheck);
		reportLocationCheck = (CheckBox) findViewById(R.id.reportLocationCheck);
		reportPhotoCheck = (CheckBox) findViewById(R.id.reportPhotoCheck);
		photoCount = (TextView) findViewById(R.id.photoCount);
		reportNoteCheck = (CheckBox) findViewById(R.id.reportNoteCheck);
		reportMailingCheck = (CheckBox) findViewById(R.id.reportMailingCheck);
		locationRadioGroup = (RadioGroup) findViewById(R.id.whereFoundRadioGroup);
		reportCurrentLocationImage = (ImageView) findViewById(R.id.reportCurrentLocationImage);

		reportSubmitButtonLabel = (TextView) findViewById(R.id.reportSubmitButtonLabel);

		if (editing) {
			reportTitle.setText("Edit Report " + thisReport.reportId
					+ " created on " + Util.iso8601(thisReport.reportTime));

			reportSubmitButtonLabel.setText("Update");
		} else {
			reportTitle
					.setText(getResources()
							.getText(
									type == Report.TYPE_BREEDING_SITE ? R.string.report_title_site
											: R.string.report_title_adult));
		}
		reportMailingRow
				.setVisibility(type == Report.TYPE_BREEDING_SITE ? View.GONE
						: View.VISIBLE);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		if (icicle != null) {
			thisReport.reportId = icicle.getString("reportId");
			thisReport.confirmation = icicle.getString("confirmation");
			thisReport.locationChoice = icicle.getInt("locationChoice");
			thisReport.currentLocationLat = icicle
					.getFloat("currentLocationLat");
			thisReport.currentLocationLon = icicle
					.getFloat("currentLocationLon");
			thisReport.selectedLocationLat = icicle
					.getFloat("selectedLocationLat");
			thisReport.selectedLocationLon = icicle
					.getFloat("selectedLocationLon");
			thisReport.photoAttached = icicle.getInt("photoAttached");
			thisReport.note = icicle.getString("note");
			thisReport.mailing = icicle.getInt("mailing");
			thisReport.reassemblePhotos(
					icicle.getStringArray(EXTRA_PHOTO_URI_ARRAY),
					icicle.getLongArray(EXTRA_PHOTO_TIME_ARRAY));
		}

		if (thisReport.reportId == null) {
			Random mRandom = new Random();

			// I am removing potentially confusing characters 0, o, and O
			String[] digits = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
					"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
					"M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
					"Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
					"l", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x",
					"y", "z" };

			/*
			 * I am giving the report IDs 4 digits using the set of 62
			 * alphanumeric characters taking (capitalization into account). If
			 * we would receive 1000 reports, the probability of at least two
			 * ending up with the same random ID is about .03 (based on the
			 * Taylor approximation solution to the birthday paradox: 1-
			 * exp((-(1000^2))/((62^4)*2))). For 100 reports, the probability is
			 * about .0003. Since each report is also linked to a unique userID,
			 * and since the only consequence of a double ID would be to make it
			 * harder for us to link a mailed sample to a report -- assuming the
			 * report with the double ID included a mailed sample -- this seems
			 * like a reasonable risk to take. We could reduce the probability
			 * by adding digits, but then it would be harder for users to record
			 * their report IDs.
			 * 
			 * UPDATE: I now removed 0 and o and O to avoid confusion, so the
			 * probabilities would need to be recaclulated...
			 */

			thisReport.reportId = digits[mRandom.nextInt(58)]
					+ digits[mRandom.nextInt(58)] + digits[mRandom.nextInt(58)]
					+ digits[mRandom.nextInt(58)];
		}

		OnClickListener ocl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case (R.id.reportConfirmationRow): {
					buildConfirmationDialog(type);
					return;
				}
				case (R.id.reportLocationRow): {
					if (!reportLocationCheck.isChecked())
						Util.toast(context,
								"Please select 'Current' or 'Choose on map' below.");
					return;
				}

				case (R.id.reportCurrentLocationRow): {

					if (currentLocation == null) {
						if (locationManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)
								|| locationManager
										.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

							buildLocationAlert(getResources().getString(
									R.string.nolocation_alert_report));
						} else {
							buildLocationAlert(getResources().getString(
									R.string.nolocnogps_alert));
						}
					} else {

						locationRadioGroup.check(R.id.whereRadioButtonHere);
						reportLocationCheck.setChecked(true);
						thisReport.currentLocationLat = (float) currentLocation
								.getLatitude();

						Util.toast(
								context,
								"Added current location.\n\nLat: "
										+ String.format("%.5g%n",
												currentLocation.getLatitude())
										+ "\nLon: "
										+ String.format("%.5g%n",
												currentLocation.getLongitude()));
					}

					return;
				}

				case (R.id.reportSelectedLocationRow): {
					Intent i = new Intent(ReportTool.this, MapSelector.class);
					startActivityForResult(i, REQUEST_CODE_MAPSELECTOR);
					return;
				}
				case (R.id.reportPhotoRow): {
					Intent i = new Intent(ReportTool.this, AttachedPhotos.class);
					i.putExtra(EXTRA_PHOTO_URI_ARRAY,
							thisReport.photoUris2Array());
					i.putExtra(EXTRA_PHOTO_TIME_ARRAY,
							thisReport.photoTimes2Array());
					i.putExtra(EXTRA_REPORT_ID, thisReport.reportId);
					i.putExtra(EXTRA_REPORT_VERSION, thisReport.reportVersion);
					startActivityForResult(i, REQUEST_CODE_ATTACHED_PHOTOS);

					return;
				}
				case (R.id.reportNoteRow): {
					buildReportNoteDialog();
					return;
				}
				case (R.id.reportMailingRow): {
					buildMailingDialog();
					return;
				}

				}
			}
		};

		reportConfirmationRow.setOnClickListener(ocl);
		reportLocationRow.setOnClickListener(ocl);
		reportCurrentLocationRow.setOnClickListener(ocl);
		reportSelectedLocationRow.setOnClickListener(ocl);
		reportPhotoRow.setOnClickListener(ocl);
		reportNoteRow.setOnClickListener(ocl);
		reportMailingRow.setOnClickListener(ocl);

		reportConfirmationCheck.setChecked(thisReport.confirmation != null);

		if (thisReport.locationChoice == Report.LOCATION_CHOICE_CURRENT)
			locationRadioGroup.check(R.id.whereRadioButtonHere);
		else if (thisReport.locationChoice == Report.LOCATION_CHOICE_SELECTED)
			locationRadioGroup.check(R.id.whereRadioButtonOtherPlace);

		reportLocationCheck
				.setChecked(thisReport.locationChoice != Report.MISSING);

		if (thisReport.photos.size() > 0) {
			photoCount.setVisibility(View.VISIBLE);
			photoCount.setText(String.valueOf(thisReport.photos.size()));
			reportPhotoCheck.setChecked(true);
			thisReport.photoAttached = Report.YES;
		} else {
			photoCount.setVisibility(View.GONE);
			reportPhotoCheck.setChecked(false);
			thisReport.photoAttached = Report.NO;
		}
		reportNoteCheck.setChecked(thisReport.note != null);
		reportMailingCheck.setChecked(thisReport.mailing == Report.YES);

		if (currentLocation == null) {
			reportCurrentLocationImage.setBackgroundDrawable(getResources()
					.getDrawable(R.drawable.ic_action_location_searching));
			Animation blink = new AlphaAnimation(0.0f, 1.0f);
			blink.setDuration(300);
			blink.setStartOffset(20);
			blink.setRepeatMode(Animation.REVERSE);
			blink.setRepeatCount(Animation.INFINITE);
			reportCurrentLocationImage.startAnimation(blink);
		} else {
			reportCurrentLocationImage.setBackgroundDrawable(getResources()
					.getDrawable(R.drawable.ic_action_location_found));
		}

		mSendRep = (ImageButton) findViewById(R.id.buttonReportSubmit);

		mSendRep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (currentLocation == null
						&& (thisReport.selectedLocationLat == null || thisReport.selectedLocationLon == null)) {

					if (locationManager == null) {
						locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
					}

					if (locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)
							|| locationManager
									.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

						buildLocationAlert(getResources().getString(
								R.string.nolocation_alert_report));
					} else {
						buildLocationAlert(getResources().getString(
								R.string.nolocnogps_alert));
					}

				} else {

					if (thisReport.mailing == Report.YES) {

						message = getResources().getString(
								R.string.mail_message)
								+ "<br/><br/>"
								+ getResources().getString(R.string.movelab)
								+ "<br/>"
								+ getResources().getString(
										R.string.movelab_address1)
								+ "<br/>"
								+ getResources().getString(
										R.string.movelab_address2)
								+ "<br/><br/>"
								+ getResources()
										.getString(R.string.report_sent);

					} else {
						message = getResources()
								.getString(R.string.report_sent);
					}
					buildMailMessage(message);

				}
			}

		});

		countDownTimer = new MyCountDownTimer(5 * 60 * 1000, 5 * 30 * 1000);

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
				if (currentLocation == null
						|| (currentLocation != null && location.getAccuracy() < currentLocation
								.getAccuracy())) {

					currentLocation = location;

					reportCurrentLocationImage.clearAnimation();
					reportCurrentLocationImage
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.ic_action_location_found));

				}

				if (location.getAccuracy() < 100) {
					removeLocationUpdate("gps");
					removeLocationUpdate("network");
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
		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			/*
			 * If provider service is no longer available, stop trying to get
			 * updates from both providers but start timer.
			 */
			if (status == LocationProvider.OUT_OF_SERVICE) {
				removeLocationUpdate(provider);
				countDownTimer.start();
			}
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		icicle.putString("reportId", thisReport.reportId);
		icicle.putString("confirmation", thisReport.confirmation);
		icicle.putInt("locationChoice", thisReport.locationChoice);

		if (thisReport.selectedLocationLat != null)
			icicle.putFloat("selectedLocationLat",
					thisReport.selectedLocationLat);

		if (thisReport.selectedLocationLon != null)
			icicle.putFloat("selectedLocationLon",
					thisReport.selectedLocationLon);

		if (thisReport.currentLocationLat != null)
			icicle.putFloat("currentLocationLat", thisReport.currentLocationLat);

		if (thisReport.currentLocationLon != null)
			icicle.putFloat("currentLocationLon", thisReport.currentLocationLon);

		icicle.putInt("photoAttached", thisReport.photoAttached);
		icicle.putString("note", thisReport.note);
		icicle.putInt("mailing", thisReport.mailing);

		icicle.putStringArray(EXTRA_PHOTO_URI_ARRAY,
				thisReport.photoUris2Array());
		icicle.putLongArray(EXTRA_PHOTO_TIME_ARRAY,
				thisReport.photoTimes2Array());

	}

	@Override
	protected void onResume() {

		gpsAvailable = false;
		networkLocationAvailable = false;

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationListener1 = new mLocationListener();
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener1);
			gpsAvailable = true;
		}
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationListener2 = new mLocationListener();
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
			networkLocationAvailable = true;
		}

		if (!gpsAvailable && !networkLocationAvailable) {
			buildAlertMessageNoGpsNoNet(getResources().getString(
					R.string.noGPSnoNetAlert));
		}
		super.onResume();
	}

	@Override
	public void onPause() {

		removeLocationUpdate("gps");

		removeLocationUpdate("network");

		locationListener1 = null;
		locationListener2 = null;
		locationManager = null;

		super.onPause();

	}

	@Override
	public void onDestroy() {
		removeLocationUpdate("gps");

		removeLocationUpdate("network");

		locationListener1 = null;
		locationListener2 = null;
		locationManager = null;

		clearFields();
		super.onDestroy();

	}

	// utilities
	private void removeLocationUpdate(String provider) {
		LocationListener listener = provider.equals("gps") ? locationListener1
				: locationListener2;
		if (locationManager != null && listener != null)
			locationManager.removeUpdates(listener);
	}

	private void buildMailMessage(String message) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.mail_message_alert);

		TextView reportIdText = (TextView) dialog
				.findViewById(R.id.reportIdText);
		reportIdText.setText(thisReport.reportId);

		TextView mText = (TextView) dialog.findViewById(R.id.alertText);
		mText.setText(Html.fromHtml(message));
		mText.setPadding(10, 10, 10, 10);

		Button positive = (Button) dialog.findViewById(R.id.alertOK);
		Button cancel = (Button) dialog.findViewById(R.id.alertCancel);

		positive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				removeLocationUpdate("gps");
				// Log.e(TAG,
				// "gps listener stopped by onDestroy");

				removeLocationUpdate("network");
				// Log.e(TAG,
				// "network listener stopped by onDestroy");

				locationListener1 = null;
				locationListener2 = null;
				locationManager = null;

				// Intent goHome = new Intent(ReportTool.this,
				// Switchboard.class);
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

				thisReport.reportTime = System.currentTimeMillis();
				new ReportUploadTask().execute(context);

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

		switch (requestCode) {

		case (REQUEST_CODE_ATTACHED_PHOTOS): {

			if (resultCode == RESULT_OK) {

				thisReport.reassemblePhotos(
						data.getStringArrayExtra(EXTRA_PHOTO_URI_ARRAY),
						data.getLongArrayExtra(EXTRA_PHOTO_TIME_ARRAY));

				if (thisReport.photos.size() > 0) {
					photoCount.setVisibility(View.VISIBLE);
					photoCount
							.setText(String.valueOf(thisReport.photos.size()));
					reportPhotoCheck.setChecked(true);
					thisReport.photoAttached = Report.YES;
				} else {
					photoCount.setVisibility(View.GONE);
					reportPhotoCheck.setChecked(false);
					thisReport.photoAttached = Report.NO;
				}
			}

			break;

		}

		case (REQUEST_CODE_MAPSELECTOR): {

			thisReport.selectedLocationLat = null;
			thisReport.selectedLocationLon = null;

			if (data != null) {
				double slat = data.getDoubleExtra(MapSelector.LAT, -1);
				double slon = data.getDoubleExtra(MapSelector.LON, -1);

				thisReport.selectedLocationLat = slat == -1 ? null : Float
						.valueOf(String.valueOf(slat));
				thisReport.selectedLocationLon = slon == -1 ? null : Float
						.valueOf(String.valueOf(slon));

			}
			if (resultCode == RESULT_OK
					&& thisReport.selectedLocationLat != null
					&& thisReport.selectedLocationLon != null) {
				locationRadioGroup.check(R.id.whereRadioButtonOtherPlace);
				reportLocationCheck.setChecked(true);
				thisReport.locationChoice = LOCATION_CHOICE_SELECTED;
			}
			break;
		}
		}

	}

	public class ReportUploadTask extends AsyncTask<Context, Integer, Boolean> {

		ProgressDialog prog;

		int myProgress;

		int resultFlag;

		int OFFLINE = 0;
		int UPLOAD_ERROR = 1;
		int DATABASE_ERROR = 2;
		int SUCCESS = 3;
		int PRIVATE_MODE = 4;

		@Override
		protected void onPreExecute() {

			PropertyHolder.init(context);
			resultFlag = SUCCESS;

			prog = new ProgressDialog(context);
			prog.setTitle(getResources().getString(R.string.progtitle_report));
			prog.setIndeterminate(false);
			prog.setMax(100);
			prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prog.show();

			myProgress = 0;

		}

		protected Boolean doInBackground(Context... context) {

			// First save report to internal DB
			ContentResolver cr = getContentResolver();
			Uri dbUri = Reports.CONTENT_URI;
			cr.insert(dbUri,
					ContentProviderValuesReports.createReport(thisReport));

			dbUri = TigaPhotos.CONTENT_URI;

			for (Photo thisPhoto : thisReport.photos) {
				cr.insert(dbUri,
						ContentProviderValuesPhotos.createPhoto(thisPhoto));
			}

			if (!Util.privateMode) {

				// now test if there is a data connection
				if (!Util.isOnline(context[0])) {

					resultFlag = OFFLINE;
					return false;

				}

				if (thisReport.upload(context[0]))
					resultFlag = SUCCESS;
				else
					resultFlag = UPLOAD_ERROR;

			} else {
				resultFlag = PRIVATE_MODE;
			}
			return true;
		}

		protected void onProgressUpdate(Integer... progress) {

			prog.setProgress(progress[0]);
		}

		protected void onPostExecute(Boolean result) {

			prog.dismiss();

			if (result && resultFlag == SUCCESS) {
				Util.toast(
						context,
						getResources().getString(
								R.string.report_sent_confirmation));

				thisReport.clear();
				clearFields();
				finish();

			} else {

				if (resultFlag == OFFLINE) {

					Intent uploadSchedulerIntent = new Intent(
							"ceab.movlab.tigre.UPLOADS_NEEDED");
					context.sendBroadcast(uploadSchedulerIntent);

					buildCustomAlert(getResources().getString(
							R.string.offline_report));

				}

				if (resultFlag == UPLOAD_ERROR || resultFlag == DATABASE_ERROR) {

					Intent uploaderIntent = new Intent(ReportTool.this,
							FileUploader.class);
					startService(uploaderIntent);

					buildCustomAlert(getResources().getString(
							R.string.upload_error_report));

					thisReport.clear();
					clearFields();

				}

				if (resultFlag == PRIVATE_MODE) {
					buildCustomAlert(getResources().getString(
							R.string.report_sent_confirmation));

					thisReport.clear();
					clearFields();

				}
			}

		}
	}

	public void clearFields() {

		currentLocation = null;
		locationChoice = -1;
		message = null;

	}

	public void buildReportNoteDialog() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.add_note);
		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));

		final EditText noteText = (EditText) dialog
				.findViewById(R.id.noteEditText);

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

	public void buildMailingDialog() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.send_specimen);
		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));

		Button okB = (Button) dialog.findViewById(R.id.sendSpecimenOKButton);
		// if button is clicked, close the custom dialog
		okB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RadioGroup mSpecYN = (RadioGroup) dialog
						.findViewById(R.id.specRadioGroup);

				int specId = mSpecYN.getCheckedRadioButtonId();
				if (specId == R.id.specRadioButtonYes) {
					thisReport.mailing = Report.YES;
					reportMailingCheck.setChecked(true);
				}
				if (specId == R.id.specRadioButtonNo) {
					thisReport.mailing = Report.NO;
					reportMailingCheck.setChecked(false);
				}

				dialog.dismiss();
			}
		});

		dialog.show();

	}

	public void buildConfirmationDialog(int type) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.tiger_checklist);
		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));

		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(getResources()
				.getString(
						type == Report.TYPE_ADULT ? R.string.identifying_mosquitoes_title
								: R.string.identifying_breeding_site_title));

		final ImageButton helpButton1 = (ImageButton) dialog
				.findViewById(R.id.confirmationQ1HelpButton);
		final ImageButton helpButton2 = (ImageButton) dialog
				.findViewById(R.id.confirmationQ2HelpButton);
		final ImageButton helpButton3 = (ImageButton) dialog
				.findViewById(R.id.confirmationQ3HelpButton);
		final ImageButton helpButton4 = (ImageButton) dialog
				.findViewById(R.id.confirmationQ4HelpButton);

		if (type == Report.TYPE_BREEDING_SITE) {
			helpButton1.setVisibility(View.GONE);
			helpButton2.setVisibility(View.GONE);
			helpButton3.setVisibility(View.GONE);

			dialog.findViewById(R.id.confirmationQ1RadioGroup).setVisibility(
					View.GONE);

			final Spinner confirmationQ1Spinner = 
					(Spinner) findViewById(R.id.confirmationQ1Spinner);
			ArrayAdapter<CharSequence> confirmationQ1SpinnerAdapter = ArrayAdapter
					.createFromResource(this,
							R.array.confirmation_q1_site_array,
							android.R.layout.simple_spinner_item);
			confirmationQ1SpinnerAdapter
					.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
			confirmationQ1Spinner.setAdapter(confirmationQ1SpinnerAdapter);

			String confQ1SpinnerSelection = String.valueOf(confirmationQ1Spinner
					.getSelectedItemPosition());
		} else {
			dialog.findViewById(R.id.confirmationQ4).setVisibility(View.GONE);
			dialog.findViewById(R.id.confirmationQ4View).setVisibility(
					View.GONE);
		}

		helpButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				dialog.setContentView(R.layout.check_help);

				Util.overrideFonts(context,
						dialog.findViewById(android.R.id.content));

				TextView mText = (TextView) dialog
						.findViewById(R.id.checkHelpText);
				mText.setText(getResources().getString(
						R.string.q1_sizecolor_text));
				final ImageView mImage = (ImageView) dialog
						.findViewById(R.id.checkHelpImage);
				mImage.setImageResource(R.drawable.m);

				dialog.show();
			}
		});

		helpButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				dialog.setContentView(R.layout.check_help);
				Util.overrideFonts(context,
						dialog.findViewById(android.R.id.content));

				TextView mText = (TextView) dialog
						.findViewById(R.id.checkHelpText);
				mText.setText(getResources().getString(
						R.string.q3_headthorax_text));
				final ImageView mImage = (ImageView) dialog
						.findViewById(R.id.checkHelpImage);
				mImage.setImageResource(R.drawable.n);

				dialog.show();
			}
		});

		helpButton3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				dialog.setContentView(R.layout.check_help);
				Util.overrideFonts(context,
						dialog.findViewById(android.R.id.content));

				TextView mText = (TextView) dialog
						.findViewById(R.id.checkHelpText);
				mText.setText(getResources().getString(
						R.string.q2_abdomenlegs_text));
				final ImageView mImage = (ImageView) dialog
						.findViewById(R.id.checkHelpImage);
				mImage.setImageResource(R.drawable.o);

				dialog.show();
			}
		});

		Button okB = (Button) dialog.findViewById(R.id.confirmationButtonOK);
		// if button is clicked, close the custom dialog
		okB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				int id;
				int q1response = 0;
				int q2response = 0;
				int q3response = 0;

				RadioGroup mLegsYN = (RadioGroup) dialog
						.findViewById(R.id.confirmationQ3RadioGroup);
				id = mLegsYN.getCheckedRadioButtonId();
				if (id == R.id.confirmationQ3RadioGroupButton1) {
					q3response = 1;
				}
				if (id == R.id.confirmationQ3RadioGroupButton2) {
					q3response = 2;
				}
				if (id == R.id.confirmationQ3RadioGroupButton3) {
					q3response = 3;
				}

				RadioGroup mStripeYN = (RadioGroup) dialog
						.findViewById(R.id.confirmationQ2RadioGroup);
				id = mStripeYN.getCheckedRadioButtonId();
				if (id == R.id.confirmationQ2RadioGroupButton1) {
					q2response = 1;
				}
				if (id == R.id.confirmationQ2RadioGroupButton2) {
					q2response = 2;
				}
				if (id == R.id.confirmationQ2RadioGroupButton3) {
					q2response = 3;
				}

				RadioGroup mSizeYN = (RadioGroup) dialog
						.findViewById(R.id.confirmationQ1RadioGroup);
				id = mSizeYN.getCheckedRadioButtonId();
				if (id == R.id.confirmationQ1RadioGroupButton1) {
					q1response = 1;
				}
				if (id == R.id.confirmationQ1RadioGroupButton2) {
					q1response = 2;
				}
				if (id == R.id.confirmationQ1RadioGroupButton3) {
					q1response = 3;
				}

				thisReport.confirmation = String.valueOf(q1response)
						+ String.valueOf(q2response)
						+ String.valueOf(q3response);

				if (q1response == 1 || +q2response == 1 || +q3response == 1) {
					reportConfirmationCheck.setChecked(true);
				} else {
					reportConfirmationCheck.setChecked(false);
				}
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	public void buildCustomAlert(String message) {

		final Dialog dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.custom_alert);

		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));

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
				finish();

			}
		});

		dialog.show();

	}

	public void buildLocationAlert(String message) {

		final Dialog dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.custom_alert);

		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));

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

	public void buildAlertMessageNoGpsNoNet(String message) {

		final Dialog dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.custom_alert);

		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));

		dialog.setCancelable(false);

		TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
		alertText.setText(message);

		Button positive = (Button) dialog.findViewById(R.id.alertOK);
		Button negative = (Button) dialog.findViewById(R.id.alertCancel);

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivity(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

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

			if (locationManager == null) {
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			}

			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationListener1 = new mLocationListener();
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, locationListener1);
				gpsAvailable = true;
			}
			if (locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locationListener2 = new mLocationListener();
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListener2);
				networkLocationAvailable = true;
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// do nothing
		}
	}

	public void setLocale(String lang) {

		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		Intent refresh = new Intent(this, ReportTool.class);
		startActivity(refresh);
	}

	public void buildLeaveReportWarning() {

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("Exit?");
		dialog.setMessage("This report has not been saved. Are you sure you want to exit?");
		dialog.setCancelable(true);
		dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface d, int arg1) {

				Intent i = new Intent(ReportTool.this, Switchboard.class);
				startActivity(i);
				finish();

			}

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			buildLeaveReportWarning();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
