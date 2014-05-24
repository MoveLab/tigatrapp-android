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

package ceab.movlab.tigerapp;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Record information for Content Provider.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderContractPhotos {

	public ContentProviderContractPhotos() {
	}

	public static final class TigaPhotos implements BaseColumns {
		private TigaPhotos() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ ContentProviderPhotos.AUTHORITY + "/photosTable");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.movlabtiger.photos";

		public static final String KEY_ROW_ID = "_id";
		public static final String KEY_USER_ID = "user_id";
		public static final String KEY_REPORT_ID = "report_id";
		public static final String KEY_REPORT_VERSION = "report_version";
		public static final String KEY_PHOTO_URI = "photo_uri";
		public static final String KEY_PHOTO_TIME = "photo_time";
		public static final String KEY_UPLOADED = "uploaded";
		public static final String KEY_DELETE_PHOTO = "delete_photo";
		public static final String KEY_SERVER_TIMESTAMP = "server_timestamp";

		/** The names of all the fields contained in the reports table */
		public static final String[] KEYS_ALL = { KEY_ROW_ID, KEY_USER_ID, KEY_REPORT_ID,
				KEY_REPORT_VERSION, KEY_PHOTO_URI, KEY_PHOTO_TIME,
				KEY_UPLOADED, KEY_DELETE_PHOTO, KEY_SERVER_TIMESTAMP };

	}

}
