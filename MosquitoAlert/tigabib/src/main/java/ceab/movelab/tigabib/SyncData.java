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
 *
 * This file incorporates code from Space Mapper, which is subject 
 * to the following terms: 
 *
 * 		Space Mapper
 * 		Copyright (C) 2012, 2013 John R.B. Palmer
 * 		Contact: jrpalmer@princeton.edu
 *
 * 		Space Mapper is free software: you can redistribute it and/or modify 
 * 		it under the terms of the GNU General Public License as published by 
 * 		the Free Software Foundation, either version 3 of the License, or (at 
 * 		your option) any later version.
 * 
 * 		Space Mapper is distributed in the hope that it will be useful, but 
 * 		WITHOUT ANY WARRANTY; without even the implied warranty of 
 * 		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * 		See the GNU General Public License for more details.
 * 
 * 		You should have received a copy of the GNU General Public License along 
 * 		with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package ceab.movelab.tigabib;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ceab.movelab.tigabib.ContProvContractMissions.Tasks;
import ceab.movelab.tigabib.ContProvContractReports.Reports;
import ceab.movelab.tigabib.ContProvContractTracks.Fixes;
import ceab.movelab.tigabib.services.Fix;

/**
 * Uploads files to the server.
 * 
 * @author John R.B. Palmer
 * 
 */
public class SyncData extends Service {

	private static String TAG = "SyncData";

	private boolean uploading = false;

	Context context;

	ContentResolver cr;
	Cursor c;

	@Override
	public void onStart(Intent intent, int startId) {
		Util.logInfo(TAG, "on start");

		if ( !Util.isOnline(context) || Util.privateMode() ) {
			Util.logInfo(TAG, "offline or private mode, stopping service");
			stopSelf();
		} else {

			if ( !uploading && !Util.privateMode() ) {
				uploading = true;

				Thread uploadThread = new Thread(null, doSyncing, "uploadBackground");
				uploadThread.start();
			}
		}
	}

	private Runnable doSyncing = new Runnable() {
		public void run() {
			tryUploads();
		}
	};

	@Override
	public void onCreate() {
		context = getApplicationContext();
		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(context);
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	private void tryUploads() {
		// Check if user has registered on server - if not, try to register
		if ( !PropertyHolder.isRegistered() ) {
			Util.registerOnServer(context);
			try {
				// Get FCM token and register on server
				String token = FirebaseInstanceId.getInstance().getToken();
				Util.registerFCMToken(context, token, PropertyHolder.getUserId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// try to get config
		try {
			JSONObject configJson = new JSONObject(Util.getJSON(Util.API_CONFIGURATION, context));
			if ( configJson.has("samples_per_day") ) {
				int samplesPerDay = configJson.getInt("samples_per_day");
				Util.logInfo(TAG, "samples per day:" + samplesPerDay);

				if (samplesPerDay != PropertyHolder.getSamplesPerDay()) {
					Util.internalBroadcast(context, Messages.START_DAILY_SAMPLING);
					PropertyHolder.setSamplesPerDay(samplesPerDay);
					Util.logInfo(TAG, "set property holder");
				}
			}
		} catch (JSONException e) {
			Util.logError(TAG, "error: " + e);
		}

		// try to get missions
		// check last id on phone
		int latest_id = PropertyHolder.getLatestMissionId();

		String missionUrl = Util.API_MISSION + "?"
				+ (latest_id > 0 ? ("id_gt=" + latest_id) : "") + "&platform="
				+ (Util.debugMode() ? "beta" : "and") + "&versionfixuse_lte="
				+ Util.MAX_MISSION_VERSION;

		Util.logInfo(TAG, "mission array: " + missionUrl);

		try {
			JSONArray missions = new JSONArray(Util.getJSON(missionUrl, context));
			Util.logInfo(TAG, "missions: " + missions.toString());

			if (  missions.length() > 0 ) {
				for (int i = 0; i < missions.length(); i++) {
					JSONObject mission = missions.getJSONObject(i);

					cr = context.getContentResolver();
					cr.insert(Util.getMissionsUri(context), ContProvValuesMissions.createTask(mission));

					if (mission.has(Tasks.KEY_TRIGGERS)) {
						JSONArray theseTriggers = mission.getJSONArray(Tasks.KEY_TRIGGERS);

						if (theseTriggers.length() == 0) {
							Intent intent = new Intent(Messages.internalAction(context));
							intent.putExtra(Messages.INTERNAL_MESSAGE_EXTRA, Messages.SHOW_TASK_NOTIFICATION);
							if (PropertyHolder.getLanguage().equals("ca")) {
								intent.putExtra(Tasks.KEY_TITLE, mission.getString(Tasks.KEY_TITLE_CATALAN));
							} else if (PropertyHolder.getLanguage().equals("es")) {
								intent.putExtra(Tasks.KEY_TITLE, mission.getString(Tasks.KEY_TITLE_SPANISH));
							} else if (PropertyHolder.getLanguage().equals("en")) {
								intent.putExtra(Tasks.KEY_TITLE, mission.getString(Tasks.KEY_TITLE_ENGLISH));
							}
							context.sendBroadcast(intent);
							Intent intent2 = new Intent(Messages.SHOW_TASK_NOTIFICATION);
							LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
						}
					}

					// IF this is last mission, mark the row id in
					// PropertyHolder for next sync
					PropertyHolder.setLatestMissionId(mission.getInt("id"));
				}
			}

		} catch (JSONException e) {
			Util.logError(TAG, "error: " + e);
		}

		cr = getContentResolver();

		// start with Tracks
		c = cr.query(Util.getTracksUri(context), Fixes.KEYS_ALL, null, null, null);

		if ( c!= null && !c.moveToFirst()) {
			c.close();
		}

		int idIndex = c.getColumnIndexOrThrow(Fixes.KEY_ROWID);
		int latIndex = c.getColumnIndexOrThrow(Fixes.KEY_LATITUDE);
		int lngIndex = c.getColumnIndexOrThrow(Fixes.KEY_LONGITUDE);
		int powIndex = c.getColumnIndexOrThrow(Fixes.KEY_POWER_LEVEL);
		int timeIndex = c.getColumnIndexOrThrow(Fixes.KEY_TIME);
		int taskFixIndex = c.getColumnIndexOrThrow(Fixes.KEY_TASK_FIX);
		int uploadedIndex = c.getColumnIndexOrThrow(Fixes.KEY_UPLOADED);

		while (!c.isAfterLast()) {

			int thisId = c.getInt(idIndex);

			if (c.getInt(uploadedIndex) == 1) {
				cr.delete(Util.getTracksUri(context), Fixes.KEY_ROWID + " = " + String.valueOf(thisId), null);
			}

			Fix thisFix = new Fix(c.getDouble(latIndex), c.getDouble(lngIndex), c.getLong(timeIndex),
					c.getFloat(powIndex), (c.getInt(taskFixIndex) == 1));

			thisFix.exportJSON(context);

			int statusCode = Util.getResponseStatusCode(thisFix.upload(context));

			if (statusCode < 300 && statusCode > 0) {
				cr.delete(Util.getTracksUri(context), Fixes.KEY_ROWID + " = " + String.valueOf(thisId), null);
			}

			c.moveToNext();
		}

		c.close();

		// now reports
		c = cr.query(Util.getReportsUri(context), Reports.KEYS_ALL,
				Reports.KEY_UPLOADED + " != " + Report.UPLOADED_ALL, null, null);

		if ( c!=null && !c.moveToFirst()) {
			c.close();
		}

		int rowIdCol = c.getColumnIndexOrThrow(Reports.KEY_ROW_ID);
		int versionUUIDCol = c.getColumnIndexOrThrow(Reports.KEY_VERSION_UUID);
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

		int uploadedCol = c.getColumnIndexOrThrow(Reports.KEY_UPLOADED);
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

		while (!c.isAfterLast()) {
			Report report = new Report(context, c.getString(versionUUIDCol),
					c.getString(userIdCol), c.getString(reportIdCol),
					c.getInt(reportVersionCol), c.getLong(reportTimeCol),
					c.getString(creationTimeCol),
					c.getString(versionTimeStringCol), c.getInt(typeCol),
					c.getString(confirmationCol),
					c.getInt(confirmationCodeCol), c.getInt(locationChoiceCol),
					c.getFloat(currentLocationLatCol),
					c.getFloat(currentLocationLonCol),
					c.getFloat(selectedLocationLatCol),
					c.getFloat(selectedLocationLonCol),
					c.getInt(photoAttachedCol), c.getString(photoUrisCol),
					c.getString(noteCol), c.getInt(uploadedCol),
					c.getLong(serverTimestampCol), c.getInt(deleteReportCol),
					c.getInt(latestVersionCol), c.getString(packageNameCol),
					c.getInt(packageVersionCol),
					c.getString(phoneManufacturerCol),
					c.getString(phoneModelCol), c.getString(osCol),
					c.getString(osVersionCol), c.getString(osLanguageCol),
					c.getString(appLanguageCol), c.getInt(missionIDCol));

			int uploadResult = report.upload(context);
			if (uploadResult > 0) {

				String sc = Reports.KEY_ROW_ID + " = " + c.getInt(rowIdCol);

				// if not latest version or if this is a deletion, then delete
				// from phone
				if (report.latestVersion != 1 || report.reportVersion == -1 || report.deleteReport == 1) {
					cr.delete(Util.getReportsUri(context), sc, null);
				} else {
					// else mark record as uploaded
					ContentValues cv = new ContentValues();
					cv.put(Reports.KEY_UPLOADED, uploadResult);
					cr.update(Util.getReportsUri(context), cv, sc, null);
				}

			}

			c.moveToNext();
		}

		c.close();

		uploading = false;

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
