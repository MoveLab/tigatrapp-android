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

import java.util.Date;

import android.content.ContentValues;
import android.location.Location;
import ceab.movlab.tigre.ContentProviderContractTracks.Fixes;

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderValuesTracks {

	public static ContentValues createFix(String tripid, float accuracy,
			double altitude, double latitude, double longitude,
			String provider, long time, int power_level,
			long station_departure_timelong, int display) {
		Date usertime = new Date(time);
		ContentValues initialValues = new ContentValues();
		initialValues.put(Fixes.KEY_TRIPID, tripid);
		initialValues.put(Fixes.KEY_ACCURACY, (double) accuracy);
		initialValues.put(Fixes.KEY_ALTITUDE, (double) altitude);
		initialValues.put(Fixes.KEY_LATITUDE, (double) latitude);
		initialValues.put(Fixes.KEY_LONGITUDE, (double) longitude);
		initialValues.put(Fixes.KEY_PROVIDER, provider);
		initialValues.put(Fixes.KEY_TIMELONG, (long) time);
		initialValues.put(Fixes.KEY_TIMESTAMP, Util.userDate(usertime));
		initialValues.put(Fixes.KEY_POWER_LEVEL, power_level);
		initialValues.put(Fixes.KEY_STATION_DEPARTURE_TIMELONG,
				station_departure_timelong);
		initialValues.put(Fixes.KEY_DISPLAY, display);
		initialValues.put(Fixes.KEY_UPLOADED, 0);

		return initialValues;
	}

	public static ContentValues createFix(String tripid, Location loc,
			int power_level, long station_departure_timelong, int display) {
		return createFix(tripid, loc.getAccuracy(), loc.getAltitude(),
				loc.getLatitude(), loc.getLongitude(), loc.getProvider(),
				loc.getTime(), power_level, station_departure_timelong, display);
	}

}
