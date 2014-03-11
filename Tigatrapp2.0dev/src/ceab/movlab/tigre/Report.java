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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * Defines map point objects used in DriverMapActivity.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Report {

	public static int MISSING = -1;
	public static int NO = 0;
	public static int YES = 1;

	public static int TYPE_ADULT = 0;
	public static int TYPE_BREEDING_SITE = 1;

	public static int LOCATION_CHOICE_CURRENT = 0;
	public static int LOCATION_CHOICE_SELECTED = 1;

	public static int CONFIRMATION_CODE_MISSING = -1;
	public static int CONFIRMATION_CODE_UNCONFIRMED = 0;
	public static int CONFIRMATION_CODE_POSITIVE = 1;

	
	
	String userId;
	String reportId;
	int reportVersion;
	long reportTime;
	long versionTime;
	int type;
	String confirmation;
	int confirmationCode;
	int locationChoice;
	Float currentLocationLat;
	Float currentLocationLon;
	Float selectedLocationLat;
	Float selectedLocationLon;
	int photoAttached;
	String note;
	int mailing;
	int uploaded;
	long serverTimestamp;
	int deleteReport;
	int latestVersion;

	ArrayList<Photo> photos;

	Report(String userId, String reportId, int reportVersion, long reportTime,
			int type, String confirmation, int confirmationCode, int locationChoice,
			float currentLocationLat, float currentLocationLon,
			float selectedLocationLat, float selectedLocationLon,
			int photoAttached, String note, int mailing, int uploaded,
			long serverTimestamp, int deleteReport, int latestVersion,
			ArrayList<Photo> photos) {

		this.reportId = reportId;
		this.userId = userId;
		this.reportVersion = reportVersion;
		this.reportTime = reportTime;
		this.type = type;
		this.confirmation = confirmation;
		this.confirmationCode = confirmationCode;
		this.locationChoice = locationChoice;
		this.currentLocationLat = currentLocationLat;
		this.currentLocationLon = currentLocationLon;
		this.selectedLocationLat = selectedLocationLat;
		this.selectedLocationLon = selectedLocationLon;
		this.photoAttached = NO;
		this.note = note;
		this.mailing = mailing;
		this.uploaded = uploaded;
		this.serverTimestamp = serverTimestamp;
		this.deleteReport = deleteReport;
		this.latestVersion = latestVersion;
		this.photos = photos;

	}

	Report(int type, String userId) {
		this.reportId = null;
		this.userId = userId;
		this.reportVersion = 0;
		this.reportTime = MISSING;
		this.type = type;
		this.confirmation = null;
		this.confirmationCode = -1;
		this.locationChoice = MISSING;
		this.currentLocationLat = null;
		this.currentLocationLon = null;
		this.selectedLocationLat = null;
		this.selectedLocationLon = null;
		this.photoAttached = NO;
		this.note = null;
		this.mailing = NO;
		this.uploaded = NO;
		this.serverTimestamp = -1;
		this.deleteReport = NO;
		this.latestVersion = YES;
		this.photos = new ArrayList<Photo>();
	}

	Report(String reportId, int reportVersion) {
		this.reportId = reportId;
		this.userId = null;
		this.reportVersion = reportVersion;
		this.reportTime = MISSING;
		this.type = MISSING;
		this.confirmation = null;
		this.confirmationCode = -1;
		this.locationChoice = MISSING;
		this.currentLocationLat = null;
		this.currentLocationLon = null;
		this.selectedLocationLat = null;
		this.selectedLocationLon = null;
		this.photoAttached = MISSING;
		this.note = null;
		this.mailing = MISSING;
		this.uploaded = MISSING;
		this.serverTimestamp = MISSING;
		this.deleteReport = MISSING;
		this.latestVersion = MISSING;
		this.photos = new ArrayList<Photo>();
	}

	public void clear() {

		reportId = null;
		userId = null;
		reportVersion = -1;
		reportTime = -1;
		type = -1;
		confirmation = null;
		confirmationCode = -1;
		locationChoice = -1;
		currentLocationLat = null;
		currentLocationLon = null;
		selectedLocationLat = null;
		selectedLocationLon = null;
		photoAttached = 0;
		note = null;
		mailing = 0;
		uploaded = 0;
		serverTimestamp = -1;
		deleteReport = 0;
		latestVersion = 0;
		photos.clear();

	}

	public long[] photoTimes2Array() {

		long[] result = new long[this.photos.size()];

		int i = 0;
		for (Photo p : this.photos) {
			result[i] = p.photoTime;
			i++;
		}
		return result;
	}

	public String[] photoUris2Array() {

		String[] result = new String[this.photos.size()];

		int i = 0;
		for (Photo p : this.photos) {
			result[i] = p.photoUri;
			i++;
		}
		return result;
	}

	public void reassemblePhotos(String[] uriArray, long[] timeArray) {

		if (uriArray.length == timeArray.length) {
			this.photos.clear();

			for (int i = 0; i < uriArray.length; i++) {
				this.photos.add(new Photo(this.reportId, this.reportVersion,
						uriArray[i], timeArray[i], 0, -1, 0));
			}
		}
	}
	
	
	public JSONArray photoUris2JsonArray() {

		JSONArray result = new JSONArray();

		for (Photo p : this.photos) {
			result.put(p.photoUri);
		}
		return result;
	}


	public JSONArray photos2JsonArray() {

		JSONArray result = new JSONArray();

		for (Photo p : this.photos) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("uri",p.photoUri);
				jo.put("time", p.photoTime);
			result.put(jo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return result;
	}


	
	public void addPhotosFromArrayList(String reportId,
			ArrayList<String> photoArrayList) {
		/*
		 * this.photos.clear(); for(String photo : photoArrayList){
		 * this.photos.add(new Photo())
		 * 
		 * Photo(String _reportRowID, String _photoUri, long _photoTime, int
		 * _uploaded, int _serverTimestamp, int _deletePhoto) {
		 * 
		 * }
		 */
	}

	public boolean upload(Context context) {

		// TODO

		return true;

	}

	public boolean deleteFromServer(Context context) {

		boolean success = false;

		PropertyHolder.init(context);

		FileInputStream fileInputStream = null;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		try {

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"userID\""
					+ lineEnd);
			dos.writeBytes("Content-Type: text/plain; charset=US-ASCII"
					+ lineEnd);
			dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd);
			dos.writeBytes(lineEnd);
			dos.writeBytes(PropertyHolder.getUserId() + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"reportID\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.reportId + lineEnd);

			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		} catch (IOException e) {

		} finally {
			if (dos != null) {
				try {
					dos.flush();
					dos.close();
				} catch (IOException e) {
				}
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
				}
			}

			ByteArrayInputStream content = new ByteArrayInputStream(
					baos.toByteArray());
			BasicHttpEntity entity = new BasicHttpEntity();
			entity.setContent(content);

			HttpPost httpPost = new HttpPost(Util.URL_DELETE_REPORT);

			httpPost.addHeader("Connection", "Keep-Alive");
			httpPost.addHeader("Content-Type", "multipart/form-data; boundary="
					+ boundary);

			httpPost.setEntity(entity);

			HttpClient client = new DefaultHttpClient();

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = "";
			try {
				response = client.execute(httpPost, responseHandler);
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			if (response.contains("SUCCESS")) {

				success = true;
			}

		}

		return success;

	}

}
