package ceab.movelab.tigabib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import ceab.movelab.tigabib.ContProvContractMissions.Tasks;
import ceab.movelab.tigabib.ContProvContractReports.Reports;
import ceab.movelab.tigabib.ContProvContractTracks.Fixes;

public class Settings extends Activity {

	private static String TAG = "Settings";

	final Context context = this;
	Resources res;
	String lang;

	ToggleButton tb;
	Boolean on;
	TextView tv;

	LinearLayout debugView;
	TextView sampleView;
	Button fixButton;

	Button syncButton;
	Button languageButton;

	ContentResolver cr;
	Cursor c;

	NewSamplesReceiver newSamplesReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		lang = Util.setDisplayLanguage(res);

		setContentView(R.layout.settings);

		on = PropertyHolder.isServiceOn();

		languageButton = (Button) findViewById(R.id.languageButton);
		languageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(Settings.this, LanguageSelector.class);
				startActivity(i);

			}

		});

		syncButton = (Button) findViewById(R.id.syncButton);
		syncButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Util.logInfo(context, TAG, "sync button clicked");
				new SyncTask().execute(context);

			}

		});

		tb = (ToggleButton) findViewById(R.id.service_button);
		tv = (TextView) findViewById(R.id.service_message);

		tb.setChecked(on);
		tv.setText(on ? getResources().getString(R.string.sampling_is_on)
				: getResources().getString(R.string.sampling_is_off));
		tb.setOnClickListener(new ToggleButton.OnClickListener() {
			public void onClick(View view) {
				on = !on;
				tb.setChecked(on);
				if (on) {
					long lastScheduleTime = PropertyHolder
							.lastSampleScheduleMade();
					if (System.currentTimeMillis() - lastScheduleTime > 1000 * 60 * 60 * 24) {
						Util.internalBroadcast(context,
								Messages.START_DAILY_SAMPLING);
					}
				} else {
					Util.internalBroadcast(context,
							Messages.STOP_DAILY_SAMPLING);
					// I am setting this here even though it is also set by the
					// broadcast receiver because in the demo version the
					// broadcast receiver never makes it to that stage
					PropertyHolder.setServiceOn(false);
				}

			}

		});

		debugView = (LinearLayout) findViewById(R.id.debugView);
		if (Util.debugMode(context)) {
			debugView.setVisibility(View.VISIBLE);
			sampleView = (TextView) findViewById(R.id.sampleView);
			sampleView.setText(PropertyHolder.getCurrentFixTimes());
			fixButton = (Button) findViewById(R.id.fixButton);
			fixButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Util.internalBroadcast(context, Messages.START_TASK_FIX);
					Util.internalBroadcast(context,
							Messages.START_DAILY_SAMPLING);
				}
			});
		} else
			debugView.setVisibility(View.GONE);

	}

	@Override
	protected void onResume() {

		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		IntentFilter newSamplesFilter;
		newSamplesFilter = new IntentFilter(
				Messages.newSamplesReadyAction(context));
		newSamplesReceiver = new NewSamplesReceiver();
		registerReceiver(newSamplesReceiver, newSamplesFilter);

		super.onResume();

	}

	@Override
	protected void onPause() {
		try {
			unregisterReceiver(newSamplesReceiver);
		} catch (Exception e) {
			Util.logError(context, TAG,
					"error unregistering newSamplesReceiver");
		}
		super.onResume();

	}

	public class NewSamplesReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (Util.debugMode(context))
				sampleView.setText(PropertyHolder.getCurrentFixTimes());

		}

	}

	public class SyncTask extends AsyncTask<Context, Integer, Boolean> {

		ProgressDialog prog;

		int myProgress;

		int resultFlag;

		int OFFLINE = 0;
		int UPLOAD_ERROR = 1;
		int SUCCESS = 3;
		int PRIVATE_MODE = 4;

		@Override
		protected void onPreExecute() {

			PropertyHolder.init(context);
			resultFlag = SUCCESS;

			prog = new ProgressDialog(context);
			prog.setTitle(getResources().getString(R.string.settings_syncing));
			prog.setIndeterminate(false);
			prog.setMax(100);
			prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prog.show();

			myProgress = 0;

		}

		protected Boolean doInBackground(Context... context) {

			myProgress = 2;
			publishProgress(myProgress);

			myProgress = 4;
			publishProgress(myProgress);

			if (!Util.privateMode(context[0])) {

				// now test if there is a data connection
				if (!Util.isOnline(context[0])) {

					resultFlag = OFFLINE;
					return false;

				}
				if (!PropertyHolder.isRegistered())
					Util.registerOnServer(context[0]);

				myProgress = 5;
				publishProgress(myProgress);

				// try to get config
				try {
					JSONObject configJson = new JSONObject(Util.getJSON(
							Util.API_CONFIGURATION, context[0]));
					if (configJson != null && configJson.has("samples_per_day")) {
						int samplesPerDay = configJson
								.getInt("samples_per_day");
						Util.logInfo(context[0], TAG, "samples per day:"
								+ samplesPerDay);

						if (samplesPerDay != PropertyHolder.getSamplesPerDay()) {
							Util.internalBroadcast(context[0],
									Messages.START_DAILY_SAMPLING);
							PropertyHolder.setSamplesPerDay(samplesPerDay);
							Util.logInfo(context[0], TAG, "set property holder");
						}

					}
				} catch (JSONException e) {
					Util.logError(context[0], TAG, "error: " + e);
					resultFlag = UPLOAD_ERROR;
				}

				myProgress = 10;
				publishProgress(myProgress);

				// try to get missions
				// check last id on phone
				int latest_id = PropertyHolder.getLatestMissionId();

				String missionUrl = Util.API_MISSION + "?"
						+ (latest_id > 0 ? ("id_gt=" + latest_id) : "") + "&platform="
						+ (Util.debugMode(context[0]) ? "beta" : "and") + "&version_lte="
						+ Util.MAX_MISSION_VERSION;

				Util.logInfo(context[0], TAG, "mission array: " + missionUrl);

				try {

					JSONArray missions = new JSONArray(
							Util.getJSON(missionUrl, context[0]));

					Util.logInfo(context[0], TAG, "missions: " + missions.toString());


					if (missions != null && missions.length() > 0) {
						for (int i = 0; i < missions.length(); i++) {
							JSONObject mission = missions.getJSONObject(i);

							cr = context[0].getContentResolver();
							cr.insert(Util.getMissionsUri(context[0]),
									ContProvValuesMissions.createTask(mission));

							if (mission.has(Tasks.KEY_TRIGGERS)) {
								JSONArray theseTriggers = mission
										.getJSONArray(Tasks.KEY_TRIGGERS);

								if (theseTriggers.length() == 0) {
									Intent intent = new Intent(
											Messages.internalAction(context[0]));
									intent.putExtra(
											Messages.INTERNAL_MESSAGE_EXTRA,
											Messages.SHOW_TASK_NOTIFICATION);
									if (PropertyHolder.getLanguage().equals(
											"ca")) {
										intent.putExtra(
												Tasks.KEY_TITLE,
												mission.getString(Tasks.KEY_TITLE_CATALAN));
									} else if (PropertyHolder.getLanguage()
											.equals("es")) {
										intent.putExtra(
												Tasks.KEY_TITLE,
												mission.getString(Tasks.KEY_TITLE_SPANISH));
									} else if (PropertyHolder.getLanguage()
											.equals("en")) {
										intent.putExtra(
												Tasks.KEY_TITLE,
												mission.getString(Tasks.KEY_TITLE_ENGLISH));
									}
									context[0].sendBroadcast(intent);

								}

							}

							// IF this is last mission, mark the row id in
							// PropertyHolder for next sync
							PropertyHolder.setLatestMissionId(mission
									.getInt("id"));

						}

					}

				} catch (JSONException e) {
					Util.logError(context[0], TAG, "error: " + e);
					resultFlag = UPLOAD_ERROR;
				}

				myProgress = 15;
				publishProgress(myProgress);

				cr = getContentResolver();

				// start with Tracks
				c = cr.query(Util.getTracksUri(context[0]), Fixes.KEYS_ALL,
						Fixes.KEY_UPLOADED + " = 0", null, null);

				if (!c.moveToFirst()) {
					c.close();
				}

				int idIndex = c.getColumnIndexOrThrow(Fixes.KEY_ROWID);
				int latIndex = c.getColumnIndexOrThrow(Fixes.KEY_LATITUDE);
				int lngIndex = c.getColumnIndexOrThrow(Fixes.KEY_LONGITUDE);
				int powIndex = c.getColumnIndexOrThrow(Fixes.KEY_POWER_LEVEL);
				int timeIndex = c.getColumnIndexOrThrow(Fixes.KEY_TIME);
				int taskFixIndex = c.getColumnIndexOrThrow(Fixes.KEY_TASK_FIX);

				int fixtotal = c.getCount();
				int fixcounter = 1;

				while (!c.isAfterLast()) {

					myProgress = myProgress + 40 * fixcounter / fixtotal;
					publishProgress(myProgress);
					fixcounter++;

					int thisId = c.getInt(idIndex);

					Fix thisFix = new Fix(c.getDouble(latIndex),
							c.getDouble(lngIndex), c.getLong(timeIndex),
							c.getFloat(powIndex), (c.getInt(taskFixIndex)==1));

					thisFix.exportJSON(context[0]);

					int statusCode = Util.getResponseStatusCode(thisFix
							.upload(context[0]));

					if (statusCode < 300 && statusCode > 0) {

						ContentValues cv = new ContentValues();
						String sc = Fixes.KEY_ROWID + " = "
								+ String.valueOf(thisId);
						cv.put(Fixes.KEY_UPLOADED, 1);
						cr.update(Util.getTracksUri(context[0]), cv, sc, null);

					} else {
						resultFlag = UPLOAD_ERROR;
					}

					c.moveToNext();

				}

				c.close();

				// now reports
				c = cr.query(Util.getReportsUri(context[0]), Reports.KEYS_ALL,
						Reports.KEY_UPLOADED + " != " + Report.UPLOADED_ALL,
						null, null);

				if (!c.moveToFirst()) {
					c.close();
				}

				int rowIdCol = c.getColumnIndexOrThrow(Reports.KEY_ROW_ID);
				int versionUUIDCol = c
						.getColumnIndexOrThrow(Reports.KEY_VERSION_UUID);
				int userIdCol = c.getColumnIndexOrThrow(Reports.KEY_USER_ID);
				int reportIdCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_ID);
				int reportTimeCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_TIME);
				int creationTimeCol = c
						.getColumnIndexOrThrow(Reports.KEY_CREATION_TIME);
				int reportVersionCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_VERSION);
				int versionTimeStringCol = c
						.getColumnIndexOrThrow(Reports.KEY_VERSION_TIME_STRING);
				int typeCol = c.getColumnIndexOrThrow(Reports.KEY_TYPE);
				int confirmationCol = c
						.getColumnIndexOrThrow(Reports.KEY_CONFIRMATION);
				int confirmationCodeCol = c
						.getColumnIndexOrThrow(Reports.KEY_CONFIRMATION_CODE);
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
				int photoAttachedCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHOTO_ATTACHED);
				int photoUrisCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHOTO_URIS);

				int uploadedCol = c.getColumnIndexOrThrow(Reports.KEY_UPLOADED);
				int serverTimestampCol = c
						.getColumnIndexOrThrow(Reports.KEY_SERVER_TIMESTAMP);
				int deleteReportCol = c
						.getColumnIndexOrThrow(Reports.KEY_DELETE_REPORT);
				int latestVersionCol = c
						.getColumnIndexOrThrow(Reports.KEY_LATEST_VERSION);
				int packageNameCol = c
						.getColumnIndexOrThrow(Reports.KEY_PACKAGE_NAME);
				int packageVersionCol = c
						.getColumnIndexOrThrow(Reports.KEY_PACKAGE_VERSION);
				int phoneManufacturerCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHONE_MANUFACTURER);
				int phoneModelCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHONE_MODEL);
				int osCol = c.getColumnIndexOrThrow(Reports.KEY_OS);
				int osVersionCol = c
						.getColumnIndexOrThrow(Reports.KEY_OS_VERSION);
				int osLanguageCol = c
						.getColumnIndexOrThrow(Reports.KEY_OS_LANGUAGE);
				int appLanguageCol = c
						.getColumnIndexOrThrow(Reports.KEY_APP_LANGUAGE);
				int missionIDCol = c
						.getColumnIndexOrThrow(Reports.KEY_MISSION_ID);

				int reporttotal = c.getCount();
				int reportcounter = 1;

				while (!c.isAfterLast()) {

					myProgress = myProgress + 40 * reportcounter / reporttotal;
					publishProgress(myProgress);
					reportcounter++;

					Report report = new Report(context[0],
							c.getString(versionUUIDCol),
							c.getString(userIdCol), c.getString(reportIdCol),
							c.getInt(reportVersionCol),
							c.getLong(reportTimeCol),
							c.getString(creationTimeCol),
							c.getString(versionTimeStringCol),
							c.getInt(typeCol), c.getString(confirmationCol),
							c.getInt(confirmationCodeCol),
							c.getInt(locationChoiceCol),
							c.getFloat(currentLocationLatCol),
							c.getFloat(currentLocationLonCol),
							c.getFloat(selectedLocationLatCol),
							c.getFloat(selectedLocationLonCol),
							c.getInt(photoAttachedCol),
							c.getString(photoUrisCol), c.getString(noteCol),
							c.getInt(uploadedCol),
							c.getLong(serverTimestampCol),
							c.getInt(deleteReportCol),
							c.getInt(latestVersionCol),
							c.getString(packageNameCol),
							c.getInt(packageVersionCol),
							c.getString(phoneManufacturerCol),
							c.getString(phoneModelCol), c.getString(osCol),
							c.getString(osVersionCol),
							c.getString(osLanguageCol),
							c.getString(appLanguageCol), c.getInt(missionIDCol));

					int uploadResult = report.upload(context[0]);
					if (uploadResult > 0) {
						// mark record as uploaded
						ContentValues cv = new ContentValues();
						String sc = Reports.KEY_ROW_ID + " = "
								+ c.getInt(rowIdCol);
						cv.put(Reports.KEY_UPLOADED, uploadResult);
						cr.update(Util.getReportsUri(context[0]), cv, sc, null);

					} else {
						resultFlag = UPLOAD_ERROR;
					}

					c.moveToNext();
				}

				c.close();

				if (resultFlag == SUCCESS) {
					myProgress = 100;
					publishProgress(myProgress);
				}

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
				Util.toast(context,
						getResources().getString(R.string.sync_success));

			} else {

				if (resultFlag == OFFLINE) {

					Util.buildCustomAlert(context,
							getResources().getString(R.string.offline_sync));

				}

				if (resultFlag == UPLOAD_ERROR) {

					Util.buildCustomAlert(context,
							getResources().getString(R.string.sync_error));

				}

				if (resultFlag == PRIVATE_MODE) {
					Util.buildCustomAlert(context,
							getResources().getString(R.string.sync_success));

				}
			}

		}
	}

}