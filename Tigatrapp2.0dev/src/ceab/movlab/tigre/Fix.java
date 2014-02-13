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

	String tripid;
	double lat;
	double lng;
	double alt;
	float acc;
	String prov;
	long time;
	int pow;

	Fix(String _tripid, double _lat, double _lng, double _alt, float _acc,
			String _prov, long _time, int _pow) {

		tripid = _tripid;
		lat = _lat;
		lng = _lng;
		alt = _alt;
		acc = _acc;
		prov = _prov;
		time = _time;
		pow = _pow;

	}

	public String exportJSON(Context context) {

		PropertyHolder.init(context);

		String result = "";
		JSONObject object = new JSONObject();
		try {
			object.put("userid", PropertyHolder.getUserId());
			object.put("tripid", this.tripid);
			object.put("lat", String.valueOf(this.lat));
			object.put("lng", String.valueOf(this.lng));
			object.put("alt", String.valueOf(this.alt));
			object.put("acc", String.valueOf(this.acc));
			object.put("prov", String.valueOf(this.prov));
			object.put("time", String.valueOf(this.time));
			object.put("pow", String.valueOf(this.pow));

			result = object.toString();
		} catch (JSONException e) {
		}
		return result;
	}

	public String makeFileName(Context context) {

		PropertyHolder.init(context);
		return PropertyHolder.getUserId() + "_" + this.tripid + "_"
				+ Util.fileNameDate(this.time);
	}

	public byte[] encryptFix(Context context) {

		byte[] result = null;

		String thisFix = this.tripid + "," + this.lat + "," + this.lng + ","
				+ this.alt + "," + this.acc + "," + this.prov + "," + this.time +","
				+ this.pow;

		try {
			result = Util.encryptRSA(context, thisFix.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public boolean upload(Context context) {

		byte[] bytes = this.encryptFix(context);

		if (bytes == null) {
			return false;
		}

		String filename = this.makeFileName(context);
		String uploadurl = Util.URL_TIGERDRIVER;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		// DataInputStream inStream = null;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 64 * 1024; // old value 1024*1024
		ByteArrayInputStream byteArrayInputStream = null;
		boolean isSuccess = true;
		try {
			// ------------------ CLIENT REQUEST

			byteArrayInputStream = new ByteArrayInputStream(bytes);

			// open a URL connection to the Servlet
			URL url = new URL(uploadurl);
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// set timeout
			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ filename + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			// create a buffer of maximum size
			bytesAvailable = byteArrayInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = byteArrayInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = byteArrayInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = byteArrayInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			// Log.e(TAG,"UploadService Runnable:File is written");
			// fileInputStream.close();
			// dos.flush();
			// dos.close();
		} catch (Exception e) {
			// Log.e(TAG, "UploadService Runnable:Client Request error", e);
			isSuccess = false;
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					// Log.e(TAG, "exception" + e);

				}
			}
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					// Log.e(TAG, "exception" + e);

				}
			}

		}

		// ------------------ read the SERVER RESPONSE
		try {

			if (conn.getResponseCode() != 200) {
				isSuccess = false;
			}
		} catch (IOException e) {
			// Log.e(TAG, "Connection error", e);
			isSuccess = false;
		}

		return isSuccess;
	}

	public boolean uploadJSON(Context context) {

		String response = "";

		String uploadurl = Util.URL_TIGERDRIVER_JSON;

		try {

			// Create a new HttpClient and Post Header
			HttpPost httppost = new HttpPost(uploadurl);

			HttpParams myParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(myParams, 10000);
			HttpConnectionParams.setSoTimeout(myParams, 60000);
			HttpConnectionParams.setTcpNoDelay(myParams, true);

			httppost.setHeader("Content-type", "application/json");
			HttpClient httpclient = new DefaultHttpClient();

			StringEntity se = new StringEntity(exportJSON(context), HTTP.UTF_8);
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			httppost.setEntity(se);

			// Execute HTTP Post Request

			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			response = httpclient.execute(httppost, responseHandler);

		} catch (ClientProtocolException e) {

			Log.e("JSON", "client prot exc = " + e);

		} catch (IOException e) {
			Log.e("JSON", "IO exc = " + e);
		}

		if (response.contains("SUCCESS")) {

			return true;
		} else {
			return false;
		}

	}

}
