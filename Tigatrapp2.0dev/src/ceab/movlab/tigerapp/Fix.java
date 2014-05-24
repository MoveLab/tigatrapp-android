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

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

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

	Fix(double _lat, double _lng, long _time, float _pow) {

		lat = _lat;
		lng = _lng;
		time = _time;
		pow = _pow;

	}

	public JSONObject exportJSON(Context context) {

		PropertyHolder.init(context);

		JSONObject result = new JSONObject();
		try {
			result.put("user", PropertyHolder.getUserId());
			result.put("fix_time", Util.ecma262(this.time));
			result.put("phone_upload_time", Util.ecma262(System.currentTimeMillis()));
			result.put("masked_lon", this.lng);
			result.put("masked_lat", this.lat);
			result.put("power", this.pow);

		} catch (JSONException e) {
		}
		return result;
	}

	

	public boolean upload(Context context) {

		boolean result = false;

			result = Util.putJSON(this.exportJSON(context), Util.API_FIXES);

		return result;


	}

}
