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

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContentProviderValuesReports {

	/**
	 * Creates content values from report data.
	 * 
	 */
	public static ContentValues createReport(Report rep) {

		// Log.e(TAG, "we are in the createFix part of the DB adapter...");

		ContentValues initialValues = new ContentValues();
		
		if(rep.here_lat != null)
		initialValues.put(Reports.KEY_HERE_LAT, Float.valueOf(rep.here_lat).floatValue());
		
		if(rep.here_lat_j != null) 
		initialValues.put(Reports.KEY_HERE_LAT_J, 
				Float.valueOf(rep.here_lat_j).floatValue());
		
		if(rep.here_lng != null) 
		initialValues.put(Reports.KEY_HERE_LNG,
				Float.valueOf(rep.here_lng).floatValue());
		
		if(rep.here_lng_j != null)
		initialValues.put(Reports.KEY_HERE_LNG_J,
				 Float.valueOf(rep.here_lng_j).floatValue());
		
		if(rep.herethere!=null)
		initialValues.put(Reports.KEY_HERETHERE, rep.herethere);
		
		if(rep.mailing!=null)
		initialValues.put(Reports.KEY_MAILING, rep.mailing);
	
		if(rep.note!=null)
		initialValues.put(Reports.KEY_NOTE, rep.note);
		
		if(rep.other_lat!=null)
		initialValues.put(Reports.KEY_OTHER_LAT,  Float.valueOf(rep.other_lat).floatValue());

		if(rep.other_lat_j!=null)
		initialValues.put(Reports.KEY_OTHER_LAT_J,
				 Float.valueOf(rep.other_lat_j)
						.floatValue());
		
		if(rep.other_lng!=null)
		initialValues.put(Reports.KEY_OTHER_LNG, rep.other_lng == null ? null
				: Float.valueOf(rep.other_lng).floatValue());

		if(rep.other_lng_j!=null)
		initialValues.put(Reports.KEY_OTHER_LNG_J,
				rep.other_lng_j == null ? null : Float.valueOf(rep.other_lng_j)
						.floatValue());
		
		if(rep.photo_attached!=null)
		initialValues.put(Reports.KEY_PHOTO_ATTACHED, rep.photo_attached);
		
		if(rep.q1_sizecolor!=null)			
		initialValues.put(Reports.KEY_Q1_SIZECOLOR, rep.q1_sizecolor);
		
		if(rep.q2_abdomenlegs!=null)			
		initialValues.put(Reports.KEY_Q2_ABDOMENLEGS, rep.q2_abdomenlegs);

		if(rep.q3_headthorax!=null)
		initialValues.put(Reports.KEY_Q3_HEADTHORAX, rep.q3_headthorax);
		
		initialValues.put(Reports.KEY_REPORTTIME, rep.reporttime);
		
		initialValues.put(Reports.KEY_REPORTID, rep.reportID);

		if(rep.photo_uri!=null)
		initialValues.put(Reports.KEY_PHOTOURI, rep.photo_uri);

		initialValues.put(Reports.KEY_UPLOADED, 0);

		return initialValues;
	}

}
