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

package ceab.movelab.tigabib.services;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import ceab.movelab.tigabib.PropertyHolder;
import ceab.movelab.tigabib.Util;

/**
 * Defines map point objects used in DriverMapActivity.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Fix {

	double lat;
	double lng;
	long time;
	float pow;
	private boolean taskFix;

	public Fix(double _lat, double _lng, long _time, float _pow, boolean _task_fix) {
		lat = _lat;
		lng = _lng;
		time = _time;
		pow = _pow;
		taskFix = _task_fix;
	}

	public JSONObject exportJSON(Context context) {
		PropertyHolder.init(context);

		JSONObject result = new JSONObject();
		try {
			if (taskFix)
				result.put("user_coverage_uuid", PropertyHolder.getUserId());
			else
				result.put("user_coverage_uuid", PropertyHolder.getUserCoverageId());
			
			result.put("fix_time", Util.ecma262(this.time));
			result.put("phone_upload_time", Util.ecma262(System.currentTimeMillis()));
			result.put("masked_lon", this.lng);
			result.put("masked_lat", this.lat);
			result.put("power", this.pow);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public HttpResponse upload(Context context) {
		//HttpResponse result = Util.postJSON(this.exportJSON(context), Util.API_FIXES, context);
		return Util.postJSON(this.exportJSON(context), Util.API_FIXES, context);
	}

}
