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

import java.util.Date;

import android.content.ContentValues;
import android.location.Location;
import ceab.movlab.tigerapp.ContentProviderContractTracks.Fixes;

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderValuesTracks {

	public static ContentValues createFix(double latitude, double longitude,
			long time, float power_proportion) {
		Date usertime = new Date(time);
		ContentValues initialValues = new ContentValues();
		initialValues.put(Fixes.KEY_LATITUDE, (double) latitude);
		initialValues.put(Fixes.KEY_LONGITUDE, (double) longitude);
		initialValues.put(Fixes.KEY_TIME, (long) time);
		initialValues.put(Fixes.KEY_POWER_LEVEL, power_proportion);
		initialValues.put(Fixes.KEY_UPLOADED, 0);

		return initialValues;
	}

}
