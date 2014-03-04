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

import android.content.ContentValues;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderValuesReports {

	/**
	 * Creates content values from report data.
	 * 
	 */
	public static ContentValues createReport(Report rep) {

		// Log.e(TAG, "we are in the createFix part of the DB adapter...");

		ContentValues initialValues = new ContentValues();


		if (rep.userId != null);
		initialValues.put(Reports.KEY_USER_ID, rep.userId);

		if (rep.reportId != null);
		initialValues.put(Reports.KEY_REPORT_ID, rep.reportId);

		initialValues.put(Reports.KEY_REPORT_VERSION, rep.reportVersion);

		initialValues.put(Reports.KEY_REPORT_TIME, rep.reportTime);

		initialValues.put(Reports.KEY_VERSION_TIME, rep.versionTime);

		initialValues.put(Reports.KEY_TYPE, rep.type);

		initialValues.put(Reports.KEY_CONFIRMATION, rep.confirmation);

		initialValues.put(Reports.KEY_LOCATION_CHOICE, rep.locationChoice);

		if (rep.currentLocationLat != null);
		initialValues.put(Reports.KEY_CURRENT_LOCATION_LAT,
				rep.currentLocationLat);

		if (rep.currentLocationLon != null);
		initialValues.put(Reports.KEY_CURRENT_LOCATION_LON,
				rep.currentLocationLon);

		if (rep.selectedLocationLat != null);
		initialValues.put(Reports.KEY_SELECTED_LOCATION_LAT,
				rep.selectedLocationLat);

		if (rep.selectedLocationLon != null);
		initialValues.put(Reports.KEY_SELECTED_LOCATION_LON,
				rep.selectedLocationLon);

		initialValues.put(Reports.KEY_PHOTO_ATTACHED, rep.photoAttached);

		if (rep.note != null);
		initialValues.put(Reports.KEY_NOTE, rep.note);

		initialValues.put(Reports.KEY_MAILING, rep.mailing);

		initialValues.put(Reports.KEY_UPLOADED, rep.uploaded);

		initialValues.put(Reports.KEY_SERVER_TIMESTAMP, rep.serverTimestamp);

		initialValues.put(Reports.KEY_DELETE_REPORT, rep.deleteReport);

		initialValues.put(Reports.KEY_LATEST_VERSION, rep.latestVersion);

		return initialValues;
	}

}
