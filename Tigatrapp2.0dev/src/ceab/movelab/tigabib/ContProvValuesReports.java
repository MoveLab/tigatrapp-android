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

import android.content.ContentValues;
import ceab.movelab.tigabib.ContProvContractReports.Reports;

/**
 * Creates content values from report data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContProvValuesReports {

	/**
	 * Creates content values from report data.
	 * 
	 */
	public static ContentValues createReport(Report rep) {

		// Log.e(TAG, "we are in the createFix part of the DB adapter...");

		ContentValues initialValues = new ContentValues();

		initialValues.put(Reports.KEY_VERSION_UUID, rep.versionUUID);

		initialValues.put(Reports.KEY_USER_ID, rep.userId);

		initialValues.put(Reports.KEY_REPORT_ID, rep.reportId);

		initialValues.put(Reports.KEY_REPORT_VERSION, rep.reportVersion);

		initialValues.put(Reports.KEY_REPORT_TIME, rep.reportTime);

		initialValues.put(Reports.KEY_CREATION_TIME, rep.creation_time);

		initialValues.put(Reports.KEY_VERSION_TIME, rep.versionTime);

		initialValues.put(Reports.KEY_VERSION_TIME_STRING, rep.versionTimeString);
		
		initialValues.put(Reports.KEY_TYPE, rep.type);

		if (rep.confirmation != null)
			initialValues.put(Reports.KEY_CONFIRMATION, rep.confirmation);
		else 
			initialValues.put(Reports.KEY_CONFIRMATION, "none");

		initialValues.put(Reports.KEY_CONFIRMATION_CODE, rep.confirmationCode);

		initialValues.put(Reports.KEY_LOCATION_CHOICE, rep.locationChoice);

		if (rep.currentLocationLat != null)
			initialValues.put(Reports.KEY_CURRENT_LOCATION_LAT,
					rep.currentLocationLat.floatValue());

		if (rep.currentLocationLon != null)
			initialValues.put(Reports.KEY_CURRENT_LOCATION_LON,
					rep.currentLocationLon.floatValue());

		if (rep.selectedLocationLat != null)
			initialValues.put(Reports.KEY_SELECTED_LOCATION_LAT,
					rep.selectedLocationLat.floatValue());

		if (rep.selectedLocationLon != null)
			initialValues.put(Reports.KEY_SELECTED_LOCATION_LON,
					rep.selectedLocationLon.floatValue());

		initialValues.put(Reports.KEY_PHOTO_ATTACHED, rep.photoAttached);

		initialValues.put(Reports.KEY_PHOTO_URIS, rep.photoUrisJson.toString());

		if (rep.note != null)
			initialValues.put(Reports.KEY_NOTE, rep.note);

		initialValues.put(Reports.KEY_UPLOADED, rep.uploaded);

		initialValues.put(Reports.KEY_SERVER_TIMESTAMP, rep.serverTimestamp);

		initialValues.put(Reports.KEY_DELETE_REPORT, rep.deleteReport);

		initialValues.put(Reports.KEY_LATEST_VERSION, rep.latestVersion);

		if (rep.packageName != null)
			initialValues.put(Reports.KEY_PACKAGE_NAME, rep.packageName);

		if (rep.packageVersion != Report.MISSING)
			initialValues.put(Reports.KEY_PACKAGE_VERSION, rep.packageVersion);

		if (rep.phoneManufacturer != null)
			initialValues.put(Reports.KEY_PHONE_MANUFACTURER,
					rep.phoneManufacturer);

		if (rep.phoneModel != null)
			initialValues.put(Reports.KEY_PHONE_MODEL, rep.phoneModel);

		if (rep.os != null)
			initialValues.put(Reports.KEY_OS, rep.os);

		if (rep.osversion != null)
			initialValues.put(Reports.KEY_OS_VERSION, rep.osversion);

		if (rep.osLanguage != null)
			initialValues.put(Reports.KEY_OS_LANGUAGE, rep.osLanguage);

		if (rep.appLanguage != null)
			initialValues.put(Reports.KEY_APP_LANGUAGE, rep.appLanguage);

		if (rep.missionId != Report.MISSING)
			initialValues.put(Reports.KEY_MISSION_ID, rep.missionId);

		return initialValues;
	}
}
