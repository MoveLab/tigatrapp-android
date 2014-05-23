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

import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ceab.movelab.tigerapp.R;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

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
	public static int TYPE_MISSION = 2;

	public static int LOCATION_CHOICE_CURRENT = 0;
	public static int LOCATION_CHOICE_SELECTED = 1;

	public static int CONFIRMATION_CODE_MISSING = -1;
	public static int CONFIRMATION_CODE_UNCONFIRMED = 0;
	public static int CONFIRMATION_CODE_POSITIVE = 1;

	// model for photo uri json arrays:
	public static String KEY_PHOTO_URI = "photo_uri";
	public static String KEY_PHOTO_TIME = "photo_time";

	String userId;
	String reportId;
	String version_UUID;
	int reportVersion;
	// Note I am creating a creation time string that will capture the local
	// time zone when created. Will leave also the long field for displaying
	// user (will be read with current time zone). Should rename the long
	// variable to something that makes more sense now. Doing same with version
	// time...
	long reportTime;
	String creation_time;
	long versionTime;
	String version_time;
	int type;
	String confirmation;
	int confirmationCode;
	int locationChoice;
	Float currentLocationLat;
	Float currentLocationLon;
	Float selectedLocationLat;
	Float selectedLocationLon;
	int photoAttached;
	JSONArray photoUrisJson;
	// ArrayList<Photo> photos;
	String note;
	int uploaded;
	long serverTimestamp;
	int deleteReport;
	int latestVersion;
	String packageName;
	int packageVersion;
	String phoneManufacturer;
	String phoneModel;
	String OS;
	String OSversion;
	String osLanguage;
	String appLanguage;
	String mission_UUID;

	Report(String version_UUID, String userId, String reportId,
			int reportVersion, long reportTime, String creation_time,
			String version_time, int type, String confirmation,
			int confirmationCode, int locationChoice, float currentLocationLat,
			float currentLocationLon, float selectedLocationLat,
			float selectedLocationLon, int photoAttached,
			String photoUrisString, String note, int uploaded,
			long serverTimestamp, int deleteReport, int latestVersion,
			String packageName, int packageVersion,
			String phoneManufacturer, String phoneModel, String OS,
			String OSversion, String osLanguage, String appLanguage,
			String mission_UUID) {

		this.version_UUID = version_UUID;

		this.photoUrisJson = new JSONArray();

		this.reportId = reportId;
		this.userId = userId;
		this.reportVersion = reportVersion;
		this.reportTime = reportTime;
		this.creation_time = creation_time;
		this.version_time = version_time;
		this.type = type;
		this.confirmation = confirmation;
		this.confirmationCode = confirmationCode;
		this.locationChoice = locationChoice;
		this.currentLocationLat = currentLocationLat;
		this.currentLocationLon = currentLocationLon;
		this.selectedLocationLat = selectedLocationLat;
		this.selectedLocationLon = selectedLocationLon;
		this.photoAttached = NO;

		if (photoUrisString != null) {
			try {
				this.photoUrisJson = new JSONArray(photoUrisString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.note = note;
		this.uploaded = uploaded;
		this.serverTimestamp = serverTimestamp;
		this.deleteReport = deleteReport;
		this.latestVersion = latestVersion;
		this.packageName = packageName;
		this.packageVersion = packageVersion;
		this.phoneManufacturer = phoneManufacturer;
		this.phoneModel = phoneModel;
		this.OS = OS;
		this.OSversion = OSversion;
		this.osLanguage = osLanguage;
		this.appLanguage = appLanguage;

		this.mission_UUID = mission_UUID;
	}

	Report(int type, String userId) {
		this.version_UUID = UUID.randomUUID().toString();
		this.reportId = null;
		this.userId = userId;
		this.reportVersion = 0;
		this.reportTime = MISSING;
		this.creation_time = null;
		this.version_time = null;
		this.type = type;
		this.confirmation = null;
		this.confirmationCode = -1;
		this.locationChoice = MISSING;
		this.currentLocationLat = null;
		this.currentLocationLon = null;
		this.selectedLocationLat = null;
		this.selectedLocationLon = null;
		this.photoAttached = NO;
		this.photoUrisJson = new JSONArray();
		this.note = null;
		this.uploaded = NO;
		this.serverTimestamp = -1;
		this.deleteReport = NO;
		this.latestVersion = YES;
		this.packageName = null;
		this.packageVersion = MISSING;
		this.phoneManufacturer = null;
		this.phoneModel = null;
		this.OS = null;
		this.OSversion = null;
		this.osLanguage = null;
		this.appLanguage = null;
		this.mission_UUID = null;

	}

	Report(String reportId, int reportVersion) {
		this.version_UUID = UUID.randomUUID().toString();
		this.reportId = reportId;
		this.userId = null;
		this.reportVersion = reportVersion;
		this.reportTime = MISSING;
		this.creation_time = null;
		this.version_time = null;
		this.type = MISSING;
		this.confirmation = null;
		this.confirmationCode = -1;
		this.locationChoice = MISSING;
		this.currentLocationLat = null;
		this.currentLocationLon = null;
		this.selectedLocationLat = null;
		this.selectedLocationLon = null;
		this.photoAttached = MISSING;
		this.photoUrisJson = new JSONArray();
		this.note = null;
		this.uploaded = MISSING;
		this.serverTimestamp = MISSING;
		this.deleteReport = MISSING;
		this.latestVersion = MISSING;
		this.packageName = null;
		this.packageVersion = MISSING;
		this.phoneManufacturer = null;
		this.phoneModel = null;
		this.OS = null;
		this.OSversion = null;
		this.osLanguage = null;
		this.appLanguage = null;
		this.mission_UUID = null;

	}

	public void clear() {

		version_UUID = null;
		reportId = null;
		userId = null;
		reportVersion = -1;
		reportTime = -1;
		creation_time = null;
		version_time = null;
		type = -1;
		confirmation = null;
		confirmationCode = -1;
		locationChoice = -1;
		currentLocationLat = null;
		currentLocationLon = null;
		selectedLocationLat = null;
		selectedLocationLon = null;
		photoAttached = 0;
		this.photoUrisJson = new JSONArray();
		note = null;
		uploaded = 0;
		serverTimestamp = -1;
		deleteReport = 0;
		latestVersion = 0;
		this.packageName = null;
		this.packageVersion = MISSING;
		this.phoneManufacturer = null;
		this.phoneModel = null;
		this.OS = null;
		this.OSversion = null;
		this.osLanguage = null;
		this.appLanguage = null;
		this.mission_UUID = null;

	}

	public boolean setPhotoUris(String photoUris) {
		boolean result = false;
		try {
			this.photoUrisJson = new JSONArray(photoUris);
			result = true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public boolean addPhoto(String photoUri, int photoTime) {
		boolean result = false;
		try {
			if (this.photoUrisJson == null) {
				this.photoUrisJson = new JSONArray(photoUri);
			} else {

				JSONObject thisPhoto = new JSONObject();
				thisPhoto.put(KEY_PHOTO_URI, photoUri);
				thisPhoto.put(KEY_PHOTO_TIME, photoTime);
				this.photoUrisJson.put(thisPhoto);
			}
			result = true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public boolean deletePhoto(String photoUri, int photoTime) {
		boolean result = false;
		try {
			if (this.photoUrisJson == null) {
				return true;
			} else {
				JSONArray newArray = new JSONArray();

				for (int i = 0; i < this.photoUrisJson.length(); i++) {
					JSONObject thisJsonObj = this.photoUrisJson
							.getJSONObject(i);
					if (!(thisJsonObj.getString(KEY_PHOTO_URI).equals(photoUri) && thisJsonObj
							.getInt(KEY_PHOTO_TIME) == photoTime)) {
						newArray.put(thisJsonObj);
					}
				}
				this.photoUrisJson = newArray;
				result = true;
			}
			result = true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String getPhotoUri(JSONArray jsonPhotos, int position) {
		String result = null;
		if (jsonPhotos == null || jsonPhotos.length() < 1) {
			// nothing
		} else {
			try {
				result = jsonPhotos.getJSONObject(position).getString(
						KEY_PHOTO_URI);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public static JSONArray deletePhoto(JSONArray jsonPhotos, String photoUri,
			int photoTime) {
		JSONArray result = null;
		try {
			if (jsonPhotos == null) {

				// nothing

			} else {
				JSONArray newArray = new JSONArray();

				for (int i = 0; i < jsonPhotos.length(); i++) {
					JSONObject thisJsonObj = jsonPhotos.getJSONObject(i);
					if (!(thisJsonObj.getString(KEY_PHOTO_URI).equals(photoUri) && thisJsonObj
							.getInt(KEY_PHOTO_TIME) == photoTime)) {
						newArray.put(thisJsonObj);
					}
				}
				result = newArray;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static JSONArray deletePhoto(JSONArray jsonPhotos, int pos) {
		JSONArray result = null;
		try {
			if (jsonPhotos == null) {

				// nothing

			} else {
				JSONArray newArray = new JSONArray();

				for (int i = 0; i < jsonPhotos.length(); i++) {
					JSONObject thisJsonObj = jsonPhotos.getJSONObject(i);
					if (i != pos) {
						newArray.put(thisJsonObj);
					}
				}
				result = newArray;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject exportJSON(Context context) {

		PropertyHolder.init(context);

		JSONObject result = new JSONObject();
		try {
			result.put("version_UUID", this.version_UUID);
			result.put("version_number", this.reportVersion);
			result.put("report_id", this.reportId);
			// exporting current time as phone upload time on assumption that
			// JSON export is being done immediately before upload
			result.put("phone_upload_time",
					Util.ecma262(System.currentTimeMillis()));
			result.put("creation_time", this.creation_time);
			result.put("version_time", this.version_time);
			result.put("type", Util.reportType2String(this.type));
			result.put("location_choice",
					Util.locationChoice2String(this.locationChoice));
			if (this.currentLocationLon != null)
				result.put("current_location_lon", this.currentLocationLon);
			if (this.currentLocationLat != null)
				result.put("current_location_lat", this.currentLocationLat);
			if (this.selectedLocationLon != null)
				result.put("selected_location_lon", this.selectedLocationLon);
			if (this.selectedLocationLat !=null)
				result.put("selected_location_lat", this.selectedLocationLat);
			result.put("note", this.note);
			result.put("package_name", this.packageName);
			result.put("package_version", this.packageVersion);
			result.put("device_manufacturer", this.phoneManufacturer);
			result.put("device_model", this.phoneModel);
			result.put("os", this.OS);
			result.put("os_version", this.OSversion);
			result.put("os_language", this.osLanguage);
			result.put("app_language", this.appLanguage);

			// making responses array
			if(this.confirmation != null){
			JSONArray responsesArray = new JSONArray();
			JSONObject thisConfirmation = new JSONObject(this.confirmation);
			Iterator<String> iter = thisConfirmation.keys();
			while (iter.hasNext()) {
				String key = iter.next();
				try {
					
					JSONObject innerJSON = new JSONObject();
					JSONObject itemJSON = thisConfirmation.getJSONObject(key);
					innerJSON.put("question", itemJSON.get("item_text"));
					innerJSON.put("answer", itemJSON.get("item_response"));
					
					responsesArray.put(innerJSON);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			result.put("responses", responsesArray);
			}
			result.put("user", PropertyHolder.getUserId());
			if (this.type == TYPE_MISSION)
				result.put("mission", this.mission_UUID);

		} catch (JSONException e) {
		}
		return result;
	}

	public boolean upload(Context context) {

		boolean result = false;

		// TESTING ONLY NOW
		JSONObject data = this.exportJSON(context);
		Log.i("Report JSON conversion:", data.toString());
		result = Util.putJSON(data, Util.API_REPORT);

		return result;

	}

}
