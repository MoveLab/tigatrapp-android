/*
 * Tigatrapp
 * Copyright (C) 2013  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
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

public class ReportTool extends Activity {

	private CountDownTimer countDownTimer;

	private boolean gpsAvailable;
	private boolean networkLocationAvailable;

	LocationManager locationManager;
	LocationListener locationListener1;
	LocationListener locationListener2;
	Location currentLocation;

	private Vibrator mVib;

	double selectedLat = -1;
	double selectedLon = -1;

	String time = "";
	boolean mailingSpecimen = false;
	String legs = "";
	String stripe = "";
	String size = "";
	String note = "";

	String herethere = "";

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

	ImageButton mPhoto;
	ImageButton mNote;
	ImageButton mSpecimen;
	ImageButton mHelp;
	ImageButton mSendRep;

	ImageView mainPhoto;

	RadioGroup locationRadioGroup;

	int doneColor;

	public static final int REQUEST_CODE_PHOTO = 1;
	public static final int REQUEST_CODE_MAPSELECTOR = 2;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.report_tool);

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
			herethere = icicle.getString("herethere");
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

		mVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

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

		sameLocButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mVib.vibrate(50);
					herethere = "here";
				}
				return false;
			}
		});

		otherLocButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mVib.vibrate(50);

					Intent i = new Intent(ReportTool.this, MapSelector.class);
					startActivityForResult(i, REQUEST_CODE_MAPSELECTOR);

				}
				return false;
			}
		});

		mPhoto = (ImageButton) findViewById(R.id.buttonFromCamera);
		if (photoAttached) {
			mPhoto.setImageResource(R.drawable.camera_pressed);
		} else {
			mPhoto.setImageResource(R.drawable.camera);
		}

		mPhoto.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {

					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {

					mVib.vibrate(50);

				}
				return false;
			}
		});

		mPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isIntentAvailable(context, MediaStore.ACTION_IMAGE_CAPTURE)) {
					dispatchTakePictureIntent(REQUEST_CODE_PHOTO);
				}
			}

		});

		mNote = (ImageButton) findViewById(R.id.buttonNote);
		if (noteAttached) {
			mNote.setImageResource(R.drawable.note_pressed);
		} else {
			mNote.setImageResource(R.drawable.note);
		}

		mNote.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {

					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {

					mVib.vibrate(50);

				}
				return false;
			}
		});

		mNote.setOnClickListener(new View.OnClickListener() {

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
							mNote.setImageResource(R.drawable.note_pressed);
							noteAttached = true;
						} else {
							mNote.setImageResource(R.drawable.note);
						}
						dialog.dismiss();
					}
				});

				dialog.show();

			}

		});

		mSpecimen = (ImageButton) findViewById(R.id.buttonMailingSpec);
		if (specAttached) {
			mSpecimen.setImageResource(R.drawable.mail_pressed);
		} else {
			mSpecimen.setImageResource(R.drawable.mail);
		}

		mSpecimen.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {

					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {

					mVib.vibrate(50);

				}
				return false;
			}
		});

		mSpecimen.setOnClickListener(new View.OnClickListener() {

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
							mSpecimen.setImageResource(R.drawable.mail_pressed);
							specAttached = true;
						}
						if (specId == R.id.specRadioButtonNo) {
							mailingSpecimen = false;
							mSpecimen.setImageResource(R.drawable.mail);
							specAttached = false;
						}

						dialog.dismiss();
					}
				});

				dialog.show();

			}

		});

		mHelp = (ImageButton) findViewById(R.id.getHelp);
		if (checklistDone) {
			mHelp.setImageResource(R.drawable.checklist_icon);
		} else {
			mHelp.setImageResource(R.drawable.checklist_icon);
		}

		mHelp.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mHelp.setBackgroundResource(R.drawable.blue_rectangle_pressed);
					mVib.vibrate(50);
				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					mHelp.setBackgroundResource(R.drawable.blue_rectangle);
					mVib.vibrate(50);
				}
				return false;
			}
		});

		mHelp.setOnClickListener(new View.OnClickListener() {

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
				legsQBRBYes.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						return false;
					}
				});

				final RadioButton legsQBRBNo;
				legsQBRBNo = (RadioButton) dialog
						.findViewById(R.id.q2_abdomenlegs_RadioButtonNo);
				legsQBRBNo.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						return false;
					}
				});

				final RadioButton sizeRadioButtonYes;

				sizeRadioButtonYes = (RadioButton) dialog
						.findViewById(R.id.q1_sizecolor_RadioButtonYes);
				sizeRadioButtonYes.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						return false;
					}
				});

				final RadioButton sizeRadioButtonNo;

				sizeRadioButtonNo = (RadioButton) dialog
						.findViewById(R.id.q1_sizecolor_RadioButtonNo);
				sizeRadioButtonNo.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						return false;
					}
				});

				final RadioButton stripeRadioButtonYes;

				stripeRadioButtonYes = (RadioButton) dialog
						.findViewById(R.id.q3_headthorax_RadioButtonYes);
				stripeRadioButtonYes.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						return false;
					}
				});

				final RadioButton stripeRadioButtonNo;

				stripeRadioButtonNo = (RadioButton) dialog
						.findViewById(R.id.q3_headthorax_RadioButtonNo);
				stripeRadioButtonNo.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						return false;
					}
				});

				final ImageButton q2legsQB = (ImageButton) dialog
						.findViewById(R.id.q2_abdomenlegs_helpButton);
				final ImageButton q3stripeQB = (ImageButton) dialog
						.findViewById(R.id.q3_headthorax_helpButton);
				final ImageButton q1sizeQB = (ImageButton) dialog
						.findViewById(R.id.q1_sizecolor_helpButton);

				q1sizeQB.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						if (e.getAction() == MotionEvent.ACTION_UP) {
							mVib.vibrate(50);
						}
						return false;
					}
				});

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

				q3stripeQB.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						if (e.getAction() == MotionEvent.ACTION_UP) {
							mVib.vibrate(50);
						}
						return false;
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

				q2legsQB.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent e) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							mVib.vibrate(50);
						}
						if (e.getAction() == MotionEvent.ACTION_UP) {
							mVib.vibrate(50);
						}
						return false;
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
							mHelp.setImageResource(R.drawable.checklist_icon);
							checklistDone = true;
						} else {
							mHelp.setImageResource(R.drawable.checklist_icon);
							checklistDone = false;
						}
						dialog.dismiss();
					}
				});

				dialog.show();

			}

		});

		mSendRep = (ImageButton) findViewById(R.id.buttonReportSubmit);

		mSendRep.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {

					mSendRep.setBackgroundResource(R.drawable.blue_rectangle_pressed);

					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					mSendRep.setBackgroundResource(R.drawable.blue_rectangle);

					mVib.vibrate(50);

				}
				return false;
			}
		});

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
		icicle.putString("herethere", herethere);
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
				mPhoto.setImageResource(R.drawable.camera_pressed);
				photoAttached = true;
			} else {
				mPhoto.setImageResource(R.drawable.camera);
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
				herethere = "there";
			} else {
				locationRadioGroup.check(R.id.whereRadioButtonHere);
				herethere = "here";
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

				thisPj = jitter(
						new GeoPoint(
								(int) (currentLocation.getLatitude() * 1E6),
								(int) (currentLocation.getLongitude() * 1E6)),
						Util.JITTER_MAX_METERS);
			}
			GeoPoint thatP = null;
			GeoPoint thatPj = null;
			if (selectedLat != -1 && selectedLon != -1) {
				thatP = new GeoPoint((int) (selectedLat * 1E6),
						(int) (selectedLon * 1E6));
				thatPj = jitter(new GeoPoint((int) (selectedLat * 1E6),
						(int) (selectedLon * 1E6)), Util.JITTER_MAX_METERS);
			}

			myProgress = 0;

			String purip = "";

			if (photoFileName != null) {
				Uri puri = Uri.fromFile(new File(directory, photoFileName));
				purip = puri.getPath();
			}

			newReport = new Report(reportID, System.currentTimeMillis(), size,
					legs, stripe, herethere,
					thisP == null ? null : String.valueOf((double) thisP
							.getLongitudeE6() / 1E6),
					thisP == null ? null : String.valueOf((double) thisP
							.getLatitudeE6() / 1E6),
					thatP == null ? null : String.valueOf((double) thatP
							.getLongitudeE6() / 1E6),
					thatP == null ? null : String.valueOf((double) thatP
							.getLatitudeE6() / 1E6),

					thisPj == null ? null : String.valueOf((double) thisPj
							.getLongitudeE6() / 1E6),
					thisPj == null ? null : String.valueOf((double) thisPj
							.getLatitudeE6() / 1E6),
					thatPj == null ? null : String.valueOf((double) thatPj
							.getLongitudeE6() / 1E6),
					thatPj == null ? null : String.valueOf((double) thatPj
							.getLatitudeE6() / 1E6), note,
					mailingSpecimen ? "si" : "no", photoAttached ? "si" : "no",

					purip);

		}

		protected Boolean doInBackground(Context... context) {

			// First save report to internal DB
			ContentResolver cr = getContentResolver();
			Uri dbUri = Reports.CONTENT_URI;
			cr.insert(dbUri,
					ContentProviderValuesReports.createReport(newReport));

			if (!Util.privateMode) {
				Cursor c = cr.query(Reports.CONTENT_URI, Reports.KEYS_ALL,
						Reports.KEY_UPLOADED + " == 0", null, null);

				// now test if there is a data connection
				if (!Util.isOnline(context[0])) {

					resultFlag = OFFLINE;
					return false;

				}

				if (!c.moveToFirst()) {
					c.close();
					resultFlag = DATABASE_ERROR;
					return false;
				}

				int reportIDCol = c.getColumnIndexOrThrow(Reports.KEY_REPORTID);
				int reporttimeCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORTTIME);
				int q1_sizecolorCol = c
						.getColumnIndexOrThrow(Reports.KEY_Q1_SIZECOLOR);
				int q2_abdomenlegsCol = c
						.getColumnIndexOrThrow(Reports.KEY_Q2_ABDOMENLEGS);
				int q3_headthoraxCol = c
						.getColumnIndexOrThrow(Reports.KEY_Q3_HEADTHORAX);
				int herethereCol = c
						.getColumnIndexOrThrow(Reports.KEY_HERETHERE);
				int here_lngCol = c.getColumnIndexOrThrow(Reports.KEY_HERE_LNG);
				int here_latCol = c.getColumnIndexOrThrow(Reports.KEY_HERE_LAT);
				int other_lngCol = c
						.getColumnIndexOrThrow(Reports.KEY_OTHER_LNG);
				int other_latCol = c
						.getColumnIndexOrThrow(Reports.KEY_OTHER_LAT);
				int here_lng_jCol = c
						.getColumnIndexOrThrow(Reports.KEY_HERE_LNG_J);
				int here_lat_jCol = c
						.getColumnIndexOrThrow(Reports.KEY_HERE_LAT_J);
				int other_lng_jCol = c
						.getColumnIndexOrThrow(Reports.KEY_OTHER_LNG_J);
				int other_lat_jCol = c
						.getColumnIndexOrThrow(Reports.KEY_OTHER_LAT_J);
				int noteCol = c.getColumnIndexOrThrow(Reports.KEY_NOTE);
				int mailingCol = c.getColumnIndexOrThrow(Reports.KEY_MAILING);
				int photo_attachedCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHOTO_ATTACHED);
				int photo_uriCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHOTOURI);

				int rowIDCol = c.getColumnIndexOrThrow(Reports.KEY_ROWID);

				int nReports = c.getCount();

				int currentRecord = 0;

				while (!c.isAfterLast()) {

					myProgress = (int) (((currentRecord + 1) / (float) nReports) * 100);
					publishProgress(myProgress);

					Report report = new Report(c.getString(reportIDCol),
							c.getLong(reporttimeCol),
							c.getString(q1_sizecolorCol),
							c.getString(q2_abdomenlegsCol),
							c.getString(q3_headthoraxCol),
							c.getString(herethereCol),
							c.getString(here_lngCol), c.getString(here_latCol),
							c.getString(other_lngCol),
							c.getString(other_latCol),
							c.getString(here_lng_jCol),
							c.getString(here_lat_jCol),
							c.getString(other_lng_jCol),
							c.getString(other_lat_jCol), c.getString(noteCol),
							c.getString(mailingCol),
							c.getString(photo_attachedCol),
							c.getString(photo_uriCol));

					FileInputStream fileInputStream = null;

					String lineEnd = "\r\n";
					String twoHyphens = "--";
					String boundary = "*****";

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(baos);

					try {

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"userID\""
								+ lineEnd);
						dos.writeBytes("Content-Type: text/plain; charset=US-ASCII"
								+ lineEnd);
						dos.writeBytes("Content-Transfer-Encoding: 8bit"
								+ lineEnd);
						dos.writeBytes(lineEnd);
						dos.writeBytes(PropertyHolder.getUserId() + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"reportID\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.reportID + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"reporttime\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.reporttime + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"q1_sizecolor\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.q1_sizecolor + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"q2_abdomenlegs\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.q2_abdomenlegs + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"q3_headthorax\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.q3_headthorax + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"herethere\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.herethere + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"here_lng\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.here_lng + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"here_lat\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.here_lat + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"other_lng\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.other_lng + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"other_lat\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.other_lat + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"here_lng_j\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.here_lng_j + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"here_lat_j\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.here_lat_j + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"other_lng_j\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.other_lng_j + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"other_lat_j\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.other_lat_j + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"note\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.note + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"mailing\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.mailing + lineEnd);

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"photo_attached\""
								+ lineEnd + lineEnd);
						dos.writeBytes(report.photo_attached + lineEnd);

						// Send a binary file
						if (photoAttached) {
							byte[] buffer;

							String filename = report.reportID + ".jpg";

							if (report.photo_uri != null) {
								dos.writeBytes(twoHyphens + boundary + lineEnd);
								dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\""
										+ filename + "\"" + lineEnd);
								dos.writeBytes("Content-Type: application/octet-stream"
										+ lineEnd);
								dos.writeBytes("Content-Transfer-Encoding: binary"
										+ lineEnd);
								dos.writeBytes(lineEnd);

								File photoFile = new File(report.photo_uri);
								fileInputStream = new FileInputStream(photoFile);

								// create a buffer of maximum size int
								// bytesAvailable,
								// bytesRead,
								int bufferSize, bytesAvailable, bytesRead;
								int maxBufferSize = 64 * 1024; // old value
																// 10241024
								bytesAvailable = fileInputStream.available();
								bufferSize = Math.min(bytesAvailable,
										maxBufferSize);
								buffer = new byte[bufferSize];

								bytesRead = fileInputStream.read(buffer, 0,
										bufferSize);

								while (bytesRead > 0) {
									dos.write(buffer, 0, bufferSize);
									bytesAvailable = fileInputStream
											.available();
									bufferSize = Math.min(bytesAvailable,
											maxBufferSize);
									bytesRead = fileInputStream.read(buffer, 0,
											bufferSize);
								}

								dos.writeBytes(lineEnd);

							}

						}

						dos.writeBytes(twoHyphens + boundary + twoHyphens
								+ lineEnd);
					} catch (IOException e) {

						resultFlag = UPLOAD_ERROR;

					} finally {
						if (dos != null) {
							try {
								dos.flush();
								dos.close();
							} catch (IOException e) {
								resultFlag = UPLOAD_ERROR;
							}
						}
						if (fileInputStream != null) {
							try {
								fileInputStream.close();
							} catch (IOException e) {
								resultFlag = UPLOAD_ERROR;
							}
						}

						ByteArrayInputStream content = new ByteArrayInputStream(
								baos.toByteArray());
						BasicHttpEntity entity = new BasicHttpEntity();
						entity.setContent(content);

						HttpPost httpPost = new HttpPost(Util.URL_TIGERFINDER);
						httpPost.addHeader("Connection", "Keep-Alive");
						httpPost.addHeader("Content-Type",
								"multipart/form-data; boundary=" + boundary);

						httpPost.setEntity(entity);

						HttpClient client = new DefaultHttpClient();

						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						String response = "";
						try {
							response = client
									.execute(httpPost, responseHandler);
						} catch (ClientProtocolException e) {
							resultFlag = UPLOAD_ERROR;
						} catch (IOException e) {
							resultFlag = UPLOAD_ERROR;
						}
						if (response.contains("SUCCESS")) {

							// mark record as uploaded
							ContentValues cv = new ContentValues();
							String sc = Reports.KEY_ROWID + " = "
									+ c.getInt(rowIDCol);
							cv.put(Reports.KEY_UPLOADED, 1);
							cr.update(dbUri, cv, sc, null);

						}

					}

					c.moveToNext();
					currentRecord++;
				}

				c.close();

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

					Intent uploaderIntent = new Intent(ReportTool.this,
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
		herethere = "";
		photoFileName = "";
		reportID = "";
		responses = "";
		message = null;
		photoAttached = false;
		specAttached = false;
		noteAttached = false;
		checklistDone = false;

	}

	/*
	 * Adapted from code by Chris Veness
	 * (http://www.movable-type.co.uk/scripts/latlong.html)
	 */
	public GeoPoint jitter(GeoPoint p, int max_meters) {

		Random rand = new Random();
		float d = rand.nextFloat() * max_meters;
		float brng = (float) SystemClock.uptimeMillis() / (2 * (float) Math.PI)
				+ (rand.nextFloat() * 2 * (float) Math.PI);

		int R = 6378140;
		double lat1 = (double) p.getLatitudeE6() / 1E6;
		double lon1 = (double) p.getLongitudeE6() / 1E6;

		double lat2 = Math.asin(Math.sin(Math.toRadians(lat1))
				* Math.cos(d / R) + Math.cos(Math.toRadians(lat1))
				* Math.sin(d / R) * Math.cos(brng));
		double lon2 = Math.toRadians(lon1)
				+ Math.atan2(
						Math.sin(brng) * Math.sin(d / R)
								* Math.cos(Math.toRadians(lat1)),
						Math.cos(d / R) - Math.sin(Math.toRadians(lat1))
								* Math.sin(Math.toRadians(lat2)));

		double lat2dd = Math.toDegrees(lat2);
		double lon2dd = Math.toDegrees(lon2);

		GeoPoint result = new GeoPoint((int) (lat2dd * 1E6),
				(int) (lon2dd * 1E6));

		return result;
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

}
