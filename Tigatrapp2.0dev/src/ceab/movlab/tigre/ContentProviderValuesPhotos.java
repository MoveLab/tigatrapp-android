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
import ceab.movlab.tigre.ContentProviderContractPhotos.TigaPhotos;

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderValuesPhotos {

	/**
	 * Creates content values from photo data.
	 * 
	 */
	public static ContentValues createPhoto(Photo ph) {

		ContentValues initialValues = new ContentValues();

		initialValues.put(TigaPhotos.KEY_REPORT_ROW_ID, ph.reportRowID);

		if (ph.photoUri != null)
			initialValues.put(TigaPhotos.KEY_PHOTO_URI, ph.photoUri);

		initialValues.put(TigaPhotos.KEY_PHOTO_TIME, ph.photoTime);

		initialValues.put(TigaPhotos.KEY_UPLOADED, ph.uploaded);

		initialValues.put(TigaPhotos.KEY_UPLOADED, ph.uploaded);

		initialValues.put(TigaPhotos.KEY_SERVER_TIMESTAMP, ph.serverTimestamp);

		initialValues.put(TigaPhotos.KEY_DELETE_PHOTO, ph.deletePhoto);

		initialValues.put(TigaPhotos.KEY_UPLOADED, 0);

		return initialValues;
	}

}
