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

package ceab.movlab.tigre;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;
import ceab.movlab.tigre.ContentProviderContractTracks.Fixes;
import ceab.movlab.tigre.ContentProviderContractTrips.Trips;

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

			ContentResolver cr = getContentResolver();

			// start with Tracks
			Cursor c = cr.query(Fixes.CONTENT_URI, Fixes.KEYS_ALL,
					Fixes.KEY_UPLOADED + " = 0", null, null);

			if (!c.moveToFirst()) {
				c.close();
				trackUploadsNeeded = false;
			}

			int idIndex = c.getColumnIndexOrThrow(Fixes.KEY_ROWID);
			int accIndex = c.getColumnIndexOrThrow(Fixes.KEY_ACCURACY);
			int altIndex = c.getColumnIndexOrThrow(Fixes.KEY_ALTITUDE);
			int latIndex = c.getColumnIndexOrThrow(Fixes.KEY_LATITUDE);
			int lngIndex = c.getColumnIndexOrThrow(Fixes.KEY_LONGITUDE);
			int powIndex = c.getColumnIndexOrThrow(Fixes.KEY_POWER_LEVEL);
			int provIndex = c.getColumnIndexOrThrow(Fixes.KEY_PROVIDER);
			int timeIndex = c.getColumnIndexOrThrow(Fixes.KEY_TIMELONG);
			int tripidIndex = c.getColumnIndexOrThrow(Fixes.KEY_TRIPID);

			while (!c.isAfterLast()) {

				int thisId = c.getInt(idIndex);

				Fix thisFix = new Fix(c.getString(tripidIndex),
						c.getDouble(latIndex), c.getDouble(lngIndex),
						c.getDouble(altIndex), c.getFloat(accIndex),
						c.getString(provIndex), c.getLong(timeIndex),
						c.getInt(powIndex));

				if (thisFix.upload(context)) {

					if (Util.testingMode) {

						thisFix.uploadJSON(context);
					}

					ContentValues cv = new ContentValues();
					String sc = Fixes.KEY_ROWID + " = "
							+ String.valueOf(thisId);
					cv.put(Fixes.KEY_UPLOADED, 1);
					cr.update(Fixes.CONTENT_URI, cv, sc, null);

				}

				c.moveToNext();

			}

			c.close();

			// now trips
			c = cr.query(Trips.CONTENT_URI, Trips.KEYS_ALL, Trips.KEY_UPLOADED
					+ " = 0", null, null);

			if (!c.moveToFirst()) {
				c.close();
				tripUploadsNeeded = false;
			}

			idIndex = c.getColumnIndexOrThrow(Trips.KEY_ROWID);
			tripidIndex = c.getColumnIndexOrThrow(Trips.KEY_TRIPID);
			int delIndex = c.getColumnIndexOrThrow(Trips.KEY_DELETE_TRIP);
			int traIndex = c
					.getColumnIndexOrThrow(Trips.KEY_TRANSPORTED_MOSQUITO);
			int tigIndex = c.getColumnIndexOrThrow(Trips.KEY_MOSQUITO_IS_TIGER);
			int stIndex = c.getColumnIndexOrThrow(Trips.KEY_START_TIME);
			int etIndex = c.getColumnIndexOrThrow(Trips.KEY_END_TIME);

			while (!c.isAfterLast()) {

				int thisId = c.getInt(idIndex);

				Trip thisTrip = new Trip(c.getString(tripidIndex),
						c.getInt(delIndex), c.getString(traIndex),
						c.getString(tigIndex), c.getLong(stIndex),
						c.getLong(etIndex));

				if (thisTrip.upload(context)) {

					ContentValues cv = new ContentValues();
					String sc = Trips.KEY_ROWID + " = "
							+ String.valueOf(thisId);
					cv.put(Trips.KEY_UPLOADED, 1);
					cr.update(Trips.CONTENT_URI, cv, sc, null);

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

			int reportIDCol = c.getColumnIndexOrThrow(Reports.KEY_REPORTID);
			int reporttimeCol = c.getColumnIndexOrThrow(Reports.KEY_REPORTTIME);
			int q1_sizecolorCol = c
					.getColumnIndexOrThrow(Reports.KEY_Q1_SIZECOLOR);
			int q2_abdomenlegsCol = c
					.getColumnIndexOrThrow(Reports.KEY_Q2_ABDOMENLEGS);
			int q3_headthoraxCol = c
					.getColumnIndexOrThrow(Reports.KEY_Q3_HEADTHORAX);
			int herethereCol = c.getColumnIndexOrThrow(Reports.KEY_HERETHERE);
			int here_lngCol = c.getColumnIndexOrThrow(Reports.KEY_HERE_LNG);
			int here_latCol = c.getColumnIndexOrThrow(Reports.KEY_HERE_LAT);
			int other_lngCol = c.getColumnIndexOrThrow(Reports.KEY_OTHER_LNG);
			int other_latCol = c.getColumnIndexOrThrow(Reports.KEY_OTHER_LAT);
			int here_lng_jCol = c.getColumnIndexOrThrow(Reports.KEY_HERE_LNG_J);
			int here_lat_jCol = c.getColumnIndexOrThrow(Reports.KEY_HERE_LAT_J);
			int other_lng_jCol = c
					.getColumnIndexOrThrow(Reports.KEY_OTHER_LNG_J);
			int other_lat_jCol = c
					.getColumnIndexOrThrow(Reports.KEY_OTHER_LAT_J);
			int noteCol = c.getColumnIndexOrThrow(Reports.KEY_NOTE);
			int mailingCol = c.getColumnIndexOrThrow(Reports.KEY_MAILING);
			int photo_attachedCol = c
					.getColumnIndexOrThrow(Reports.KEY_PHOTO_ATTACHED);
			int photo_uriCol = c.getColumnIndexOrThrow(Reports.KEY_PHOTOURI);

			int rowIDCol = c.getColumnIndexOrThrow(Reports.KEY_ROWID);

			while (!c.isAfterLast()) {

				Report report = new Report(c.getString(reportIDCol),
						c.getLong(reporttimeCol), c.getString(q1_sizecolorCol),
						c.getString(q2_abdomenlegsCol),
						c.getString(q3_headthoraxCol),
						c.getString(herethereCol), c.getString(here_lngCol),
						c.getString(here_latCol), c.getString(other_lngCol),
						c.getString(other_latCol), c.getString(here_lng_jCol),
						c.getString(here_lat_jCol),
						c.getString(other_lng_jCol),
						c.getString(other_lat_jCol), c.getString(noteCol),
						c.getString(mailingCol),
						c.getString(photo_attachedCol),
						c.getString(photo_uriCol));

				if (report.upload(context)) {

					// mark record as uploaded
					ContentValues cv = new ContentValues();
					String sc = Reports.KEY_ROWID + " = " + c.getInt(rowIDCol);
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
					"ceab.movlab.tigre.UPLOADS_NOT_NEEDED");
			context.sendBroadcast(uploadSchedulerIntent);

		} else {
			Intent uploadSchedulerIntent = new Intent(
					"ceab.movlab.tigre.UPLOADS_NEEDED");
			context.sendBroadcast(uploadSchedulerIntent);

		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
