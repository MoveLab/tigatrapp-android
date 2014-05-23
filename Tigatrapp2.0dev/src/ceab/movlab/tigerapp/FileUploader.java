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
 * This file also incorporates code from Funf, which is subject to the following terms: 
 * 
 * 		Funf: Open Sensing Framework
 *		Copyright (C) 2010-2011 Nadav Aharony, Wei Pan, Alex Pentland. 
 * 		Acknowledgments: Alan Gardner
 * 		Contact: nadav@media.mit.edu
 * 
 * 		Funf is free software: you can redistribute it and/or modify
 * 		it under the terms of the GNU Lesser General Public License as 
 * 		published by the Free Software Foundation, either version 3 of 
 * 		the License, or (at your option) any later version. 
 * 
 * 		Funf is distributed in the hope that it will be useful, but 
 * 		WITHOUT ANY WARRANTY; without even the implied warranty of 
 * 		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * 		See the GNU Lesser General Public License for more details.
 * 
 */

package ceab.movlab.tigerapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import ceab.movlab.tigerapp.ContentProviderContractReports.Reports;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;
import ceab.movlab.tigerapp.ContentProviderContractTracks.Fixes;

/**
 * Uploads files to the server.
 * 
 * @author John R.B. Palmer
 * 
 */
public class FileUploader extends Service {
	private boolean uploading = false;

	Context context;
	boolean reportUploadsNeeded = true;
	boolean trackUploadsNeeded = true;
	boolean tripUploadsNeeded = true;

	@Override
	public void onStart(Intent intent, int startId) {

		if (!uploading && !Util.privateMode) {
			uploading = true;

			Thread uploadThread = new Thread(null, doFileUploading,
					"uploadBackground");
			uploadThread.start();

		}
	};

	private Runnable doFileUploading = new Runnable() {
		public void run() {
			tryUploads();
		}
	};

	@Override
	public void onCreate() {

		// Log.e(TAG, "FileUploader onCreate.");

		context = getApplicationContext();
		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);
	}

	@Override
	public void onDestroy() {

	}

	private void tryUploads() {

		if (Util.isOnline(context)) {
			// Log.e(TAG, "FileUploader online.");

			// Check if user has registered on server - if not, try to register
			// first and only do other uploads once this is done
			if (PropertyHolder.isRegistered() || Util.registerOnServer()) {

				// try to get config
				try {
					JSONObject configJson = new JSONObject(
							Util.getJSON(Util.API_CONFIGURATION));
					if (configJson != null && configJson.has("samples_per_day")) {
						int samplesPerDay = configJson
								.getInt("samples_per_day");
						Log.i("Samples per day", "downloaded: " + samplesPerDay);

						if (samplesPerDay != PropertyHolder.getSamplesPerDay()) {
							Intent i = new Intent(
									TigerBroadcastReceiver.START_SAMPLING_MESSAGE);
							sendBroadcast(i);
							PropertyHolder.setSamplesPerDay(samplesPerDay);
							Log.i("Samples per day", "set to: " + samplesPerDay);
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// try to get missions
				try {

					JSONArray missions = new JSONArray(
							Util.getJSON(Util.API_MISSION));
					if (missions != null && missions.length() > 0) {
						for (int i = 0; 1 < missions.length(); i++) {
							JSONObject mission = missions.getJSONObject(i);
							ContentResolver cr = context.getContentResolver();
							cr.insert(Tasks.CONTENT_URI,
									ContentProviderValuesTasks
											.createTask(mission));

						}

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ContentResolver cr = getContentResolver();

				// start with Tracks
				Cursor c = cr.query(Fixes.CONTENT_URI, Fixes.KEYS_ALL,
						Fixes.KEY_UPLOADED + " = 0", null, null);

				if (!c.moveToFirst()) {
					c.close();
					trackUploadsNeeded = false;
				}

				int idIndex = c.getColumnIndexOrThrow(Fixes.KEY_ROWID);
				int latIndex = c.getColumnIndexOrThrow(Fixes.KEY_LATITUDE);
				int lngIndex = c.getColumnIndexOrThrow(Fixes.KEY_LONGITUDE);
				int powIndex = c.getColumnIndexOrThrow(Fixes.KEY_POWER_LEVEL);
				int timeIndex = c.getColumnIndexOrThrow(Fixes.KEY_TIME);

				while (!c.isAfterLast()) {

					int thisId = c.getInt(idIndex);

					Fix thisFix = new Fix(c.getDouble(latIndex),
							c.getDouble(lngIndex), c.getLong(timeIndex),
							c.getFloat(powIndex));

					thisFix.exportJSON(context);

					if (thisFix.upload(context)) {


						ContentValues cv = new ContentValues();
						String sc = Fixes.KEY_ROWID + " = "
								+ String.valueOf(thisId);
						cv.put(Fixes.KEY_UPLOADED, 1);
						cr.update(Fixes.CONTENT_URI, cv, sc, null);

					}

					c.moveToNext();

				}

				c.close();

				// now reports
				c = cr.query(Reports.CONTENT_URI, Reports.KEYS_ALL,
						Reports.KEY_UPLOADED + " = 0", null, null);

				if (!c.moveToFirst()) {
					c.close();
					reportUploadsNeeded = false;
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
				int versionTimeCol = c
						.getColumnIndexOrThrow(Reports.KEY_VERSION_TIME);
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
				int missionUUIDCol = c
						.getColumnIndexOrThrow(Reports.KEY_MISSION_UUID);

				while (!c.isAfterLast()) {

					Report report = new Report(c.getString(versionUUIDCol),
							c.getString(userIdCol), c.getString(reportIdCol),
							c.getInt(reportVersionCol),
							c.getLong(reportTimeCol),
							c.getString(creationTimeCol),
							c.getString(versionTimeCol), c.getInt(typeCol),
							c.getString(confirmationCol),
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
							c.getString(appLanguageCol),
							c.getString(missionUUIDCol));
					if (report.upload(context)) {

						// mark record as uploaded
						ContentValues cv = new ContentValues();
						String sc = Reports.KEY_ROW_ID + " = "
								+ c.getInt(rowIdCol);
						cv.put(Reports.KEY_UPLOADED, 1);
						cr.update(Reports.CONTENT_URI, cv, sc, null);

					}

					c.moveToNext();
				}

				c.close();

			}

			uploading = false;

			if (reportUploadsNeeded == false && trackUploadsNeeded == false
					&& tripUploadsNeeded == false) {
				Intent uploadSchedulerIntent = new Intent(
						"ceab.movelab.tigerapp.UPLOADS_NOT_NEEDED");
				context.sendBroadcast(uploadSchedulerIntent);

			} else {
				Intent uploadSchedulerIntent = new Intent(
						TigerBroadcastReceiver.UPLOADS_NEEDED_MESSAGE);
				context.sendBroadcast(uploadSchedulerIntent);

			}

		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
