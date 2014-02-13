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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Record information for Content Provider.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderContractReports {

	public ContentProviderContractReports() {
	}

	public static final class Reports implements BaseColumns {
		private Reports() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ ContentProviderReports.AUTHORITY + "/reportsTable");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.movlabtigre.reports";

		public static final String KEY_ROWID = "_id";
		public static final String KEY_REPORTID = "reportid";
		public static final String KEY_REPORTTIME = "reporttime";
		public static final String KEY_Q1_SIZECOLOR = "q1_sizecolor";
		public static final String KEY_Q2_ABDOMENLEGS = "q2_abdomenlegs";
		public static final String KEY_Q3_HEADTHORAX = "q3_headthorax";
		public static final String KEY_HERETHERE = "herethere";
		public static final String KEY_HERE_LNG = "here_lng";
		public static final String KEY_HERE_LAT = "here_lat";
		public static final String KEY_OTHER_LNG = "other_lng";
		public static final String KEY_OTHER_LAT = "other_lat";
		public static final String KEY_HERE_LNG_J = "here_lng_j";
		public static final String KEY_HERE_LAT_J = "here_lat_j";
		public static final String KEY_OTHER_LNG_J = "other_lng_j";
		public static final String KEY_OTHER_LAT_J = "other_lat_j";
		public static final String KEY_NOTE = "note";
		public static final String KEY_MAILING = "mailing";
		public static final String KEY_PHOTO_ATTACHED = "photo_attached";
		public static final String KEY_UPLOADED = "uploaded";
		public static final String KEY_PHOTOURI = "photo_uri";

		/** The names of all the fields contained in the reports table */
		public static final String[] KEYS_ALL = { KEY_ROWID, KEY_REPORTID, KEY_REPORTTIME,
				KEY_Q1_SIZECOLOR, KEY_Q2_ABDOMENLEGS, KEY_Q3_HEADTHORAX,
				KEY_HERETHERE, KEY_HERE_LNG, KEY_HERE_LAT, KEY_OTHER_LNG,
				KEY_OTHER_LAT, KEY_HERE_LNG_J, KEY_HERE_LAT_J, KEY_OTHER_LNG_J,
				KEY_OTHER_LAT_J, KEY_NOTE, KEY_MAILING, KEY_PHOTO_ATTACHED, KEY_UPLOADED, KEY_PHOTOURI };


	}

}
