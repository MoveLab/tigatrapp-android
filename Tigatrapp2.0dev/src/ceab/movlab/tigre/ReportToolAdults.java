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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;

import com.google.android.maps.GeoPoint;

/**
 * Activity for identifying and reporting tiger mosquitoes.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ReportToolAdults extends Activity {

	private CountDownTimer countDownTimer;

	private boolean gpsAvailable;
	private boolean networkLocationAvailable;

	LocationManager locationManager;
	LocationListener locationListener1;
	LocationListener locationListener2;
	Location currentLocation;

	double selectedLat = -1;
	double selectedLon = -1;

	String time = "";
	boolean mailingSpecimen = false;
	String legs = "";
	String stripe = "";
	String size = "";
	String note = "";

	int locationChoice = -1;
	int LOCATION_CHOICE_SELECTED = 1;
	int LOCATION_CHOICE_CURRENT = 0;
	int LOCATION_CHOICE_MISSING = -1;

	File root;
	File directory;
	String photoFileName = "";

	String reportID = "";
	String responses = "";
	final Context context = this;

	String message;

	boolean photoAttached = false;
	boolean specAttached = false;
	boolean noteAttached = false;
	boolean checklistDone = false;

	CheckBox reportConfirmationCheck;
	CheckBox reportPhotoCheck;
	CheckBox reportNoteCheck;
	CheckBox reportMailingCheck;

	ImageButton reportPhotoAttachButton;

	ImageButton mSendRep;

	ImageView mainPhoto;

	RadioGroup locationRadioGroup;

	int doneColor;

	String lang;

	public static final int REQUEST_CODE_PHOTO = 1;
	public static final int REQUEST_CODE_MAPSELECTOR = 2;

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

		setContentView(R.layout.report_adult);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		if (icicle != null) {
			photoAttached = icicle.getBoolean("photoAttached");
			noteAttached = icicle.getBoolean("noteAttached");
			specAttached = icicle.getBoolean("specAttached");
			checklistDone = icicle.getBoolean("checklistDone");
			reportID = icicle.getString("reportID");
			size = icicle.getString("size");
			legs = icicle.getString("legs");
			stripe = icicle.getString("stripe");
			locationChoice = icicle.getInt("locationChoice");
			note = icicle.getString("note");
			mailingSpecimen = icicle.getBoolean("mailingSpecimen");
			photoFileName = icicle.getString("photoFileName");
			selectedLat = icicle.getDouble("selectedLat");
			selectedLon = icicle.getDouble("selectedLon");

		}

		root = Environment.getExternalStorageDirectory();

		directory = new File(root, getResources().getString(
				R.string.app_directory));
		directory.mkdirs();

		if (reportID == "") {
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

			reportID = digits[mRandom.nextInt(58)]
					+ digits[mRandom.nextInt(58)] + digits[mRandom.nextInt(58)]
					+ digits[mRandom.nextInt(58)];
		}
		// TODO set up date and time pickers so that the form actually pulls the
		// times that have been set

		// TextView mReportID = (TextView) findViewById(R.id.reportID);
		// mReportID.setText("Report ID: " + reportID);

		locationRadioGroup = (RadioGroup) findViewById(R.id.whereFoundRadioGroup);
		locationRadioGroup.check(R.id.whereRadioButtonHere);

		RadioButton sameLocButton = (RadioButton) findViewById(R.id.whereRadioButtonHere);
		RadioButton otherLocButton = (RadioButton) findViewById(R.id.whereRadioButtonOtherPlace);

		reportPhotoCheck = (CheckBox) findViewById(R.id.reportPhotoCheck);
		reportPhotoCheck.setChecked(photoAttached);

		reportPhotoCheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isIntentAvailable(context, MediaStore.ACTION_IMAGE_CAPTURE)) {
					dispatchTakePictureIntent(REQUEST_CODE_PHOTO);
				}
			}

		});

		reportPhotoAttachButton = (ImageButton) findViewById(R.id.reportPhotoAttachButton);
		reportPhotoAttachButton.setOnClickListener(new View.OnClickListener() {

			String m_chosen;

			@Override
			public void onClick(View v) {

				SimpleFileDialog FolderChooseDialog = new SimpleFileDialog(
						ReportToolAdults.this, "FolderChoose",
						new SimpleFileDialog.SimpleFileDialogListener() {
							@Override
							public void onChosenDir(String chosenDir) {
								// The code in this function will be executed
								// when the dialog OK button is pushed
								m_chosen = chosenDir;
							}
						});

				FolderChooseDialog.chooseFile_or_Dir();

			}

		});

		reportNoteCheck = (CheckBox) findViewById(R.id.reportNoteCheck);
		reportNoteCheck.setChecked(noteAttached);

		reportNoteCheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				dialog.setContentView(R.layout.add_note);
				Util.overrideFonts(context,
						dialog.findViewById(android.R.id.content));

				Button okB = (Button) dialog.findViewById(R.id.addNoteOKButton);
				// if button is clicked, close the custom dialog
				okB.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						EditText noteText = (EditText) dialog
								.findViewById(R.id.noteEditText);
						note = noteText.getText().toString();

						if (note.length() > 0) {
							reportNoteCheck.setChecked(true);
							noteAttached = true;
						} else {
							reportNoteCheck.setChecked(false);
						}
						dialog.dismiss();
					}
				});

				dialog.show();

			}

		});

		reportMailingCheck = (CheckBox) findViewById(R.id.reportMailingCheck);
		reportMailingCheck.setChecked(specAttached);

		reportMailingCheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				dialog.setContentView(R.layout.send_specimen);
				Util.overrideFonts(context,
						dialog.findViewById(android.R.id.content));

				Button okB = (Button) dialog
						.findViewById(R.id.sendSpecimenOKButton);
				// if button is clicked, close the custom dialog
				okB.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						RadioGroup mSpecYN = (RadioGroup) dialog
								.findViewById(R.id.specRadioGroup);

						int specId = mSpecYN.getCheckedRadioButtonId();
						if (specId == R.id.specRadioButtonYes) {
							mailingSpecimen = true;
							reportMailingCheck.setChecked(true);
							specAttached = true;
						}
						if (specId == R.id.specRadioButtonNo) {
							mailingSpecimen = false;
							reportMailingCheck.setChecked(false);
							specAttached = false;
						}

						dialog.dismiss();
					}
				});

				dialog.show();

			}

		});

		reportConfirmationCheck = (CheckBox) findViewById(R.id.reportConfirmationCheck);
		reportConfirmationCheck.setChecked(checklistDone);

		reportConfirmationCheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				dialog.setContentView(R.layout.tiger_checklist);

				Util.overrideFonts(context,
						dialog.findViewById(android.R.id.content));

				TextView title = (TextView) dialog.findViewById(R.id.title);
				title.setText(getResources().getString(
						R.string.identifying_mosquitoes_title));

				final RadioButton legsQBRBYes;
				legsQBRBYes = (RadioButton) dialog
						.findViewById(R.id.q2_abdomenlegs_RadioButtonYes);

				final RadioButton legsQBRBNo;
				legsQBRBNo = (RadioButton) dialog
						.findViewById(R.id.q2_abdomenlegs_RadioButtonNo);
				final RadioButton sizeRadioButtonYes;

				sizeRadioButtonYes = (RadioButton) dialog
						.findViewById(R.id.q1_sizecolor_RadioButtonYes);

				final RadioButton sizeRadioButtonNo;

				sizeRadioButtonNo = (RadioButton) dialog
						.findViewById(R.id.q1_sizecolor_RadioButtonNo);

				final RadioButton stripeRadioButtonYes;

				stripeRadioButtonYes = (RadioButton) dialog
						.findViewById(R.id.q3_headthorax_RadioButtonYes);

				final RadioButton stripeRadioButtonNo;

				stripeRadioButtonNo = (RadioButton) dialog
						.findViewById(R.id.q3_headthorax_RadioButtonNo);

				final ImageButton q2legsQB = (ImageButton) dialog
						.findViewById(R.id.q2_abdomenlegs_helpButton);
				final ImageButton q3stripeQB = (ImageButton) dialog
						.findViewById(R.id.q3_headthorax_helpButton);
				final ImageButton q1sizeQB = (ImageButton) dialog
						.findViewById(R.id.q1_sizecolor_helpButton);

				q1sizeQB.setOnClickListener(new OnClickListener() {
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

				q3stripeQB.setOnClickListener(new OnClickListener() {
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

				q2legsQB.setOnClickListener(new OnClickListener() {
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

				Button okB = (Button) dialog
						.findViewById(R.id.checklistButtonOK);
				// if button is clicked, close the custom dialog
				okB.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						int id;

						RadioGroup mLegsYN = (RadioGroup) dialog
								.findViewById(R.id.q2_abdomenlegs_RadioGroup);
						id = mLegsYN.getCheckedRadioButtonId();
						if (id == R.id.q2_abdomenlegs_RadioButtonYes) {
							legs = "si";
						}
						if (id == R.id.q2_abdomenlegs_RadioButtonNo) {
							legs = "no";
						}
						if (id == R.id.q2_abdomenlegs_RadioButtonDontKnow) {
							legs = "nose";
						}

						RadioGroup mStripeYN = (RadioGroup) dialog
								.findViewById(R.id.q3_headthorax_RadioGroup);
						id = mStripeYN.getCheckedRadioButtonId();
						if (id == R.id.q3_headthorax_RadioButtonYes) {
							stripe = "si";
						}
						if (id == R.id.q3_headthorax_RadioButtonNo) {
							stripe = "no";
						}
						if (id == R.id.q3_headthorax_RadioButtonDontKnow) {
							stripe = "nose";
						}

						RadioGroup mSizeYN = (RadioGroup) dialog
								.findViewById(R.id.q1_sizecolor_RadioGroup);
						id = mSizeYN.getCheckedRadioButtonId();
						if (id == R.id.q1_sizecolor_RadioButtonYes) {
							size = "si";
						}
						if (id == R.id.q1_sizecolor_RadioButtonNo) {
							size = "no";
						}
						if (id == R.id.q1_sizecolor_RadioButtonDontKnow) {
							size = "nose";
						}

						if (size.equals("si") || stripe.equals("si")
								|| legs.equals("si")) {
							reportConfirmationCheck.setChecked(true);
							checklistDone = true;
						} else {
							reportConfirmationCheck.setChecked(false);
							checklistDone = false;
						}
						dialog.dismiss();
					}
				});

				dialog.show();

			}

		});

		mSendRep = (ImageButton) findViewById(R.id.buttonReportSubmit);

		mSendRep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (currentLocation == null
						&& (selectedLat == -1 || selectedLon == -1)) {

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

					if (mailingSpecimen) {

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
		icicle.putBoolean("photoAttached", photoAttached);
		icicle.putBoolean("noteAttached", noteAttached);
		icicle.putBoolean("specAttached", specAttached);
		icicle.putBoolean("checklistDone", checklistDone);
		icicle.putString("reportID", reportID);
		icicle.putString("size", size);
		icicle.putString("legs", legs);
		icicle.putString("stripe", stripe);
		icicle.putInt("locationChoice", locationChoice);
		icicle.putString("note", note);
		icicle.putBoolean("mailingSpecimen", mailingSpecimen);
		icicle.putString("photoFileName", photoFileName);
		icicle.putDouble("selectedLat", selectedLat);
		icicle.putDouble("selectedLon", selectedLon);

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
		reportIdText.setText(reportID);

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

	private void dispatchTakePictureIntent(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();
			String stringDate = dateFormat.format(date);
			photoFileName = getResources().getString(
					R.string.saved_image_prefix)
					+ stringDate + ".jpg";

			Uri photoUri = Uri.fromFile(new File(directory, photoFileName));
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

		} catch (Exception e) {
			Log.e("ReportTool", "photo exception: " + e);
		}

		startActivityForResult(takePictureIntent, actionCode);

	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case (REQUEST_CODE_PHOTO): {

			if (resultCode == RESULT_OK) {
				reportPhotoCheck.setChecked(true);
				photoAttached = true;
			} else {
				reportPhotoCheck.setChecked(false);
				photoAttached = false;
			}
			break;

		}

		case (REQUEST_CODE_MAPSELECTOR): {

			selectedLat = -1;
			selectedLon = -1;

			if (data != null) {
				selectedLat = data.getDoubleExtra(MapSelector.LAT, -1);
				selectedLon = data.getDoubleExtra(MapSelector.LON, -1);
			}
			if (resultCode == RESULT_OK && selectedLat != -1
					&& selectedLon != -1) {
				locationRadioGroup.check(R.id.whereRadioButtonOtherPlace);
				locationChoice = LOCATION_CHOICE_SELECTED;
			} else {
				locationRadioGroup.check(R.id.whereRadioButtonHere);
				locationChoice = LOCATION_CHOICE_CURRENT;
			}
			break;
		}
		}

	}

	public class ReportUploadTask extends AsyncTask<Context, Integer, Boolean> {

		ProgressDialog prog;

		int myProgress;

		Report newReport;

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

			GeoPoint thisP = null;
			GeoPoint thisPj = null;
			if (currentLocation != null) {
				thisP = new GeoPoint(
						(int) (currentLocation.getLatitude() * 1E6),
						(int) (currentLocation.getLongitude() * 1E6));

			}
			GeoPoint thatP = null;
			GeoPoint thatPj = null;
			if (selectedLat != -1 && selectedLon != -1) {
				thatP = new GeoPoint((int) (selectedLat * 1E6),
						(int) (selectedLon * 1E6));
			}

			myProgress = 0;

			String purip = "";

			if (photoFileName != null) {
				Uri puri = Uri.fromFile(new File(directory, photoFileName));
				purip = puri.getPath();
			}

			newReport = new Report(PropertyHolder.getUserId(), reportID, 0,
					System.currentTimeMillis(), 0, size + legs + stripe,
					locationChoice, thisP == null ? null : new Float(
							thisP.getLongitudeE6() / 1E6), thisP == null ? null
							: new Float(thisP.getLatitudeE6() / 1E6),
					thatP == null ? null : new Float(
							thatP.getLongitudeE6() / 1E6), thatP == null ? null
							: new Float(thatP.getLatitudeE6() / 1E6),

					photoAttached ? 1 : 0, note, mailingSpecimen ? 1 : 0, 0,
					-1, 0, 1);

		}

		protected Boolean doInBackground(Context... context) {

			// First save report to internal DB
			ContentResolver cr = getContentResolver();
			Uri dbUri = Reports.CONTENT_URI;
			cr.insert(dbUri,
					ContentProviderValuesReports.createReport(newReport));

			if (!Util.privateMode) {

				// now test if there is a data connection
				if (!Util.isOnline(context[0])) {

					resultFlag = OFFLINE;
					return false;

				}

				if (newReport.upload(context[0]))
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

				newReport.clear();
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

					Intent uploaderIntent = new Intent(ReportToolAdults.this,
							FileUploader.class);
					startService(uploaderIntent);

					buildCustomAlert(getResources().getString(
							R.string.upload_error_report));

					newReport.clear();
					clearFields();

				}

				if (resultFlag == PRIVATE_MODE) {
					buildCustomAlert(getResources().getString(
							R.string.report_sent_confirmation));

					newReport.clear();
					clearFields();

				}
			}

		}
	}

	public void clearFields() {

		currentLocation = null;
		selectedLat = -1;
		selectedLon = -1;
		time = "";
		mailingSpecimen = false;
		legs = "";
		stripe = "";
		size = "";
		note = "";
		locationChoice = -1;
		photoFileName = "";
		reportID = "";
		responses = "";
		message = null;
		photoAttached = false;
		specAttached = false;
		noteAttached = false;
		checklistDone = false;

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
		Intent refresh = new Intent(this, ReportToolAdults.class);
		startActivity(refresh);
	}

}
