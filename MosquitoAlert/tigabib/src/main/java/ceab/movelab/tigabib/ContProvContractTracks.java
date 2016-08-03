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

import android.provider.BaseColumns;

/**
 * Record information for Content Provider.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContProvContractTracks {

	public ContProvContractTracks() {
	}

	public static final class Fixes implements BaseColumns {
		private Fixes() {
		}

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.movlabtiger.fixes";

		public static final String KEY_ROWID = "_id";
		public static final String KEY_LATITUDE = "latitude";
		public static final String KEY_LONGITUDE = "longitude";
		public static final String KEY_TIME = "time";
		public static final String KEY_POWER_LEVEL = "power_level";
		public static final String KEY_UPLOADED = "uploaded";
		public static final String KEY_TASK_FIX = "task_fix";

		public static final String DB_CREATE_STATEMENT = "create table "
				+ ContProvTracks.DATABASE_TABLE + " ( " + KEY_ROWID
				+ " integer primary key autoincrement, " + KEY_LATITUDE
				+ " real, " + KEY_LONGITUDE + " real, " + KEY_TIME + " long,  "
				+ KEY_POWER_LEVEL + " real," + KEY_UPLOADED + " integer,"
				+ KEY_TASK_FIX + " integer);";

		/** The names of all the fields contained in the location tracks table */
		public static final String[] KEYS_ALL = { KEY_ROWID, KEY_LATITUDE,
				KEY_LONGITUDE, KEY_TIME, KEY_POWER_LEVEL, KEY_UPLOADED, KEY_TASK_FIX };

		public static final String[] KEYS_LATLON = { KEY_ROWID, KEY_LATITUDE,
				KEY_LONGITUDE };

		public static final String[] KEYS_LATLONACC = { KEY_ROWID,
				KEY_LATITUDE, KEY_LONGITUDE };

		public static final String[] KEYS_LATLONACCTIMES = { KEY_ROWID,
				KEY_LATITUDE, KEY_LONGITUDE, KEY_TIME };

	}

}
