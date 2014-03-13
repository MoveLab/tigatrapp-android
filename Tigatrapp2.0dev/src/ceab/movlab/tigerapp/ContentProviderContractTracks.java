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

package ceab.movlab.tigerapp;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Record information for Content Provider.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderContractTracks {

	public ContentProviderContractTracks() {
	}

	public static final class Fixes implements BaseColumns {
		private Fixes() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ ContentProviderTracks.AUTHORITY + "/tracksTable");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.movlabtiger.fixes";

		public static final String KEY_ROWID = "_id";
		public static final String KEY_ACCURACY = "accuracy";
		public static final String KEY_ALTITUDE = "altitude";
		public static final String KEY_LATITUDE = "latitude";
		public static final String KEY_LONGITUDE = "longitude";
		public static final String KEY_PROVIDER = "provider";
		public static final String KEY_TIMELONG = "timelong";
		public static final String KEY_TIMESTAMP = "timestamp";
		public static final String KEY_POWER_LEVEL = "power_level";
		public static final String KEY_STATION_DEPARTURE_TIMELONG = "sdtimelong";
		public static final String KEY_DISPLAY = "display";
		public static final String KEY_UPLOADED = "uploaded";

		public static final int DISPLAY_TRUE = 1;
		public static final int DISPLAY_FALSE = 0;

		public static final String DB_CREATE_STATEMENT = "create table "
				+ ContentProviderTracks.DATABASE_TABLE + " ( " + KEY_ROWID
				+ " integer primary key autoincrement, " + KEY_ACCURACY
				+ " real, " + KEY_ALTITUDE + "  real, " + KEY_LATITUDE
				+ " real, " + KEY_LONGITUDE + " real, " + KEY_PROVIDER
				+ " text, " + KEY_TIMELONG + " long,  " + KEY_TIMESTAMP
				+ " text not null, " + KEY_POWER_LEVEL + " integer,"
				+ KEY_STATION_DEPARTURE_TIMELONG + " long, " + KEY_DISPLAY
				+ " integer, " + KEY_UPLOADED + " integer);";

		/** The names of all the fields contained in the location tracks table */
		public static final String[] KEYS_ALL = { KEY_ROWID, KEY_ACCURACY,
				KEY_ALTITUDE, KEY_LATITUDE, KEY_LONGITUDE, KEY_PROVIDER,
				KEY_TIMELONG, KEY_TIMESTAMP, KEY_POWER_LEVEL,
				KEY_STATION_DEPARTURE_TIMELONG, KEY_DISPLAY, KEY_UPLOADED };

		public static final String[] KEYS_LATLON = { KEY_ROWID, KEY_LATITUDE,
				KEY_LONGITUDE };

		public static final String[] KEYS_LATLONACC = { KEY_ROWID,
				KEY_LATITUDE, KEY_LONGITUDE, KEY_ACCURACY };

		public static final String[] KEYS_LATLONACCTIMES = { KEY_ROWID,
				KEY_LATITUDE, KEY_LONGITUDE, KEY_ACCURACY, KEY_TIMELONG,
				KEY_STATION_DEPARTURE_TIMELONG };

	}

}
