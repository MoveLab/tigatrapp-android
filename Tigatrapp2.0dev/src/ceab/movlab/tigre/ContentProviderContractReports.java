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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Content provider definitions for <code>reports</code> content provider.
 * <p>
 * Apart from the <code>row_id</code> primary key (used since single primary key
 * is needed for certain Android functions), three fields together define a
 * unique record in the SQLITE database: <code>user_id</code>,
 * <code>report_id</code>, and <code>report_Version</code>.
 * 
 * @author John R.B. Palmer
 * @see ContentProviderReports
 * @see ContentProvierValuesReports
 * 
 */
public class ContentProviderContractReports {

	public ContentProviderContractReports() {
	}

	public static final class Reports implements BaseColumns {
		private Reports() {
		}

		/**
		 * The URI for this content provider.
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ ContentProviderReports.AUTHORITY + "/reportsTable");

		/**
		 * The content type.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.movlabtigre.reports";

		/**
		 * The <code>row_id</code> field serves as the primary key for this
		 * table. Although a compound primary key could also be created from
		 * <code>user_id</code>, <code>report_id</code>, and
		 * <code>version_id</code>, certain Android functions require a single
		 * primary key.
		 */
		public static final String KEY_ROW_ID = "_id";

		/**
		 * The <code>user_id</code> field links the <code>reports</code> table
		 * to the <code>users</code> table on the server. It is either a unique
		 * string that the user chooses when registering (and that is checked
		 * against the server database at that time to ensure no duplicates), or
		 * it is a unique UUID that is automatically assigned by default until
		 * the user chooses a custom ID.
		 * 
		 * @see Switchboard.onCreate
		 * @see Registration
		 * 
		 */
		public static final String KEY_USER_ID = "user_id";

		/**
		 * The <code>report_id</code> field contains the unique ID assigned to
		 * each report. Although reports may have multiple versions (when users
		 * edit a previously submitted report), the <code>report_id</code>
		 * represents a user's unique observation of a mosquito or a breeding
		 * site, which occurred at a given point in time.
		 * 
		 * @see KEY_REPORT_TIME
		 * @see Util.makeNewReportId
		 */
		public static final String KEY_REPORT_ID = "report_id";

		/**
		 * The <code>report_version</code> field gives the version number for a
		 * given report entry. This is an integer set to 1 when the user first
		 * creates the report. When the user edits a report, rather than
		 * modifying the report's original record, a new record is generated
		 * with the version number set to one higher than the previous one. Note
		 * that only the most recent version of any report is stored on the
		 * user's phone, but that all versions are stored on the server.
		 */
		public static final String KEY_REPORT_VERSION = "report_version";

		/**
		 * The <code>report_time</code> field contains the time at which the
		 * report was first created. It is set based on the user's phone's time
		 * and stored as a long representation of Unix time.
		 */
		public static final String KEY_REPORT_TIME = "report_time";

		/**
		 * The <code>version_time</code> field contains the time at which the
		 * version was created. It is set based on the user's phone's time and
		 * stored as a long representation of Unix time.
		 */
		public static final String KEY_VERSION_TIME = "version_time";

		/**
		 * The <code>type</code> field contains the type of report as an
		 * integer. Current options are: -1 (missing), 0 (report of an adult
		 * mosquito), and 1 (report of a mosquito breeding site).
		 */
		public static final String KEY_TYPE = "type";

		/**
		 * The <code>confirmation</code> field contains the users responses to
		 * the confirmation questions for the given report type. This should be JSON formatted string
		 */
		public static final String KEY_CONFIRMATION = "confirmation";
		
		public static final String KEY_CONFIRMATION_CODE = "confirmation_code";
		
		
		public static final String KEY_LOCATION_CHOICE = "location_choice";
		public static final String KEY_CURRENT_LOCATION_LON = "current_location_lon";
		public static final String KEY_CURRENT_LOCATION_LAT = "current_location_lat";
		public static final String KEY_SELECTED_LOCATION_LON = "selected_location_lon";
		public static final String KEY_SELECTED_LOCATION_LAT = "selected_location_lat";
		public static final String KEY_PHOTO_ATTACHED = "photo_attached";
		public static final String KEY_PHOTO_URIS = "photo_uris";		
		public static final String KEY_NOTE = "note";
		public static final String KEY_MAILING = "mailing";
		public static final String KEY_UPLOADED = "uploaded";
		public static final String KEY_SERVER_TIMESTAMP = "server_timestamp";
		public static final String KEY_DELETE_REPORT = "delete_report";
		public static final String KEY_LATEST_VERSION = "latest_version";

		/**
		 * Vector of all the fields contained in the reports table, to be used
		 * for quickly grabbing them all.
		 */
		public static final String[] KEYS_ALL = { KEY_ROW_ID, KEY_USER_ID, KEY_REPORT_ID,
				KEY_REPORT_VERSION, KEY_REPORT_TIME, KEY_VERSION_TIME,
				KEY_TYPE, KEY_CONFIRMATION, KEY_CONFIRMATION_CODE, KEY_LOCATION_CHOICE,
				KEY_CURRENT_LOCATION_LON, KEY_CURRENT_LOCATION_LAT,
				KEY_SELECTED_LOCATION_LON, KEY_SELECTED_LOCATION_LAT,
				KEY_PHOTO_ATTACHED,KEY_PHOTO_URIS, KEY_NOTE, KEY_MAILING,
				KEY_UPLOADED, KEY_SERVER_TIMESTAMP, KEY_DELETE_REPORT,
				KEY_LATEST_VERSION };

	}

}
