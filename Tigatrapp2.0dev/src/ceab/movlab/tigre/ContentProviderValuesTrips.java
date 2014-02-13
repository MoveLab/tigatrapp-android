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

import android.content.ContentValues;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;
import ceab.movlab.tigre.ContentProviderContractTrips.Trips;

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderValuesTrips {

	public static ContentValues createTrip(Trip trip) {

		// Log.e(TAG, "we are in the createFix part of the DB adapter...");

		ContentValues initialValues = new ContentValues();
		initialValues.put(Trips.KEY_DELETE_TRIP, trip.delete);
		initialValues.put(Trips.KEY_END_TIME, trip.end_time);
		initialValues.put(Trips.KEY_MOSQUITO_IS_TIGER, trip.mosquito_is_tiger);
		initialValues.put(Trips.KEY_START_TIME, trip.start_time);
		initialValues.put(Trips.KEY_TRANSPORTED_MOSQUITO,
				trip.transported_mosquito);
		initialValues.put(Trips.KEY_TRIPID, trip.tripid);
		initialValues.put(Trips.KEY_UPLOADED, 0);
		return initialValues;
	}
	
	public static ContentValues createTrip(Trip trip, int uploaded) {

		// Log.e(TAG, "we are in the createFix part of the DB adapter...");

		ContentValues initialValues = new ContentValues();
		initialValues.put(Trips.KEY_DELETE_TRIP, trip.delete);
		initialValues.put(Trips.KEY_END_TIME, trip.end_time);
		initialValues.put(Trips.KEY_MOSQUITO_IS_TIGER, trip.mosquito_is_tiger);
		initialValues.put(Trips.KEY_START_TIME, trip.start_time);
		initialValues.put(Trips.KEY_TRANSPORTED_MOSQUITO,
				trip.transported_mosquito);
		initialValues.put(Trips.KEY_TRIPID, trip.tripid);
		initialValues.put(Trips.KEY_UPLOADED, uploaded);
		return initialValues;
	}


}
