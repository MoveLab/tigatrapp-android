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

public class ContentProviderContractTrips {

	public ContentProviderContractTrips() {
	}

	public static final class Trips implements BaseColumns {
		private Trips() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ ContentProviderTrips.AUTHORITY + "/tripsTable");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.movlabtigre.trips";

		public static final String KEY_ROWID = "_id";
		public static final String KEY_TRIPID = "tripid";
		public static final String KEY_DELETE_TRIP = "delete_trip";
		public static final String KEY_TRANSPORTED_MOSQUITO = "transported_mosquito";
		public static final String KEY_MOSQUITO_IS_TIGER = "mosquito_is_tiger";
		public static final String KEY_START_TIME = "start_time";
		public static final String KEY_END_TIME = "end_time";

		public static final String KEY_UPLOADED = "uploaded";

		/** The names of all the fields contained in the reports table */
		public static final String[] KEYS_ALL = { KEY_ROWID, KEY_TRIPID,
				KEY_DELETE_TRIP, KEY_TRANSPORTED_MOSQUITO,
				KEY_MOSQUITO_IS_TIGER, KEY_START_TIME, KEY_END_TIME, KEY_UPLOADED };

	}

}
