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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Record information for Content Provider.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContProvContractMissions {

	public ContProvContractMissions() {
	}

	public static final class Tasks implements BaseColumns {
		private Tasks() {
		}


		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.movlabtiger.tasks";

		public static final String KEY_ROW_ID = "_id";
		public static final String KEY_ID = "id";

		public static final String KEY_TITLE = "title"; // for presets only -
														// not in db

		public static final String KEY_TITLE_CATALAN = "title_catalan";
		public static final String KEY_TITLE_SPANISH = "title_spanish";
		public static final String KEY_TITLE_ENGLISH = "title_english";

		public static final String KEY_SHORT_DESCRIPTION = "short_description"; // for
																				// presets
																				// only
																				// -
																				// not
																				// in
																				// db

		public static final String KEY_SHORT_DESCRIPTION_CATALAN = "short_description_catalan";
		public static final String KEY_SHORT_DESCRIPTION_SPANISH = "short_description_spanish";
		public static final String KEY_SHORT_DESCRIPTION_ENGLISH = "short_description_english";

		public static final String KEY_CREATION_TIME = "creation_time"; // long,
																		// unix
																		// time
		public static final String KEY_EXPIRATION_TIME = "expiration_time"; // long,
																			// unix
																			// time
		public static final String KEY_TRIGGERS = "triggers"; // json
																// array
																// string:
																// [{lat:_lat,
																// lon:_lon,
																// start_hour:
																// _start_hour,
																// end_hour:_end_hour},

		public static final String KEY_ACTIVE = "active"; // 1 or 0;
		public static final String KEY_TASK_JSON = "task_json";
		public static final String KEY_DONE = "done"; // int 0=no, 1=yes
		public static final String KEY_RESPONSES_JSON = "responses"; // text
		public static final String KEY_UPLOADED = "uploaded"; // int 0=no, 1=yes

		/** The names of all the fields contained in the reports table */
		public static final String[] KEYS_ALL = { KEY_ROW_ID, KEY_ID,
				KEY_TITLE_CATALAN, KEY_SHORT_DESCRIPTION_CATALAN,
				KEY_TITLE_SPANISH, KEY_SHORT_DESCRIPTION_SPANISH,
				KEY_TITLE_ENGLISH, KEY_SHORT_DESCRIPTION_ENGLISH,
				KEY_CREATION_TIME, KEY_EXPIRATION_TIME, KEY_ACTIVE,
				KEY_TASK_JSON, KEY_DONE, KEY_RESPONSES_JSON, KEY_UPLOADED };

		public static final String[] KEYS_ROW_ID = { KEY_ROW_ID };

		public static final String[] KEYS_TASKS_LIST = { KEY_ROW_ID, KEY_ID,
				KEY_TITLE_CATALAN, KEY_SHORT_DESCRIPTION_CATALAN,
				KEY_TITLE_SPANISH, KEY_SHORT_DESCRIPTION_SPANISH,
				KEY_TITLE_ENGLISH, KEY_SHORT_DESCRIPTION_ENGLISH,
				KEY_CREATION_TIME, KEY_EXPIRATION_TIME, KEY_ACTIVE,
				KEY_TASK_JSON, KEY_DONE };

		public static final String[] KEYS_DONE = { KEY_ROW_ID, KEY_DONE,
				KEY_EXPIRATION_TIME, KEY_ACTIVE };

		public static final String[] KEYS_TRIGGERS = { KEY_ROW_ID,
				KEY_TITLE_CATALAN, KEY_TITLE_SPANISH, KEY_TITLE_ENGLISH,
				KEY_EXPIRATION_TIME, KEY_ACTIVE, KEY_TRIGGERS, KEY_DONE };

	}

}
