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

public class ContentProviderContractTasks {

	public ContentProviderContractTasks() {
	}

	public static final class Tasks implements BaseColumns {
		private Tasks() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ ContentProviderTasks.AUTHORITY + "/tasksTable");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.movlabtiger.tasks";

		public static final String KEY_ROW_ID = "_id";
		public static final String KEY_TASK_ID = "task_id";
		public static final String KEY_TASK_HEADING = "task_title";
		public static final String KEY_TASK_SHORT_DESCRIPTION = "task_short_description";
		public static final String KEY_DATE = "date"; // long, unix time
		public static final String KEY_EXPIRATION_DATE = "expiration_date"; // long, unix time
		public static final String KEY_TASK_JSON = "task_json";
		public static final String KEY_DONE = "done"; // int 0=no, 1=yes
		public static final String KEY_RESPONSES_JSON = "responses"; // text
		public static final String KEY_UPLOADED = "uploaded"; // int 0=no, 1=yes

		/** The names of all the fields contained in the reports table */
		public static final String[] KEYS_ALL = { KEY_ROW_ID, KEY_TASK_ID,
				KEY_TASK_HEADING, KEY_TASK_SHORT_DESCRIPTION, KEY_DATE,
				KEY_EXPIRATION_DATE, KEY_TASK_JSON, KEY_DONE,
				KEY_RESPONSES_JSON, KEY_UPLOADED };

		public static final String[] KEYS_TASKS_LIST = { KEY_ROW_ID, KEY_TASK_HEADING,
				KEY_TASK_SHORT_DESCRIPTION, KEY_DATE, KEY_TASK_JSON};

		public static final String[] KEYS_DONE = { KEY_ROW_ID, KEY_DONE, KEY_EXPIRATION_DATE};

	}

}
