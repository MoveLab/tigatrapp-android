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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.Cursor;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;

/**
 * Defines map point objects used in DriverMapActivity.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Report {

	public static int TYPE_MISSING = -1;
	public static int TYPE_ADULT = 0;
	public static int TYPE_BREEDING_SITE = 1;

	public static int LOCATION_CHOICE_MISSING = -1;
	public static int LOCATION_CHOICE_CURRENT = 0;
	public static int LOCATION_CHOICE_SELECTED = 1;

	String userId;
	String reportId;
	int reportVersion;
	long reportTime;
	long versionTime;
	int type;
	String confirmation;
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

	Report(String userId, String reportId, int reportVersion, long reportTime,
			int type, String confirmation, int locationChoice,
			float currentLocationLat, float currentLocationLon,
			float selectedLocationLat, float selectedLocationLon,
			int photoAttached, String note, int mailing, int uploaded,
			long serverTimestamp, int deleteReport, int latestVersion) {

		this.reportId = reportId;
		this.userId = userId;
		this.reportVersion = reportVersion;
		this.reportTime = reportTime;
		this.type = type;
		this.confirmation = confirmation;
		this.locationChoice = locationChoice;
		this.currentLocationLat = currentLocationLat;
		this.currentLocationLon = currentLocationLon;
		this.selectedLocationLat = selectedLocationLat;
		this.selectedLocationLon = selectedLocationLon;
		this.photoAttached = photoAttached;
		this.note = note;
		this.mailing = mailing;
		this.uploaded = uploaded;
		this.serverTimestamp = serverTimestamp;
		this.deleteReport = deleteReport;
		this.latestVersion = latestVersion;

	}

	public void clear() {

		reportId = null;
		userId = null;
		reportVersion = -1;
		reportTime = -1;
		type = -1;
		confirmation = null;
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

	}

	public boolean upload(Context context) {

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

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"reporttime\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.reportTime + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"confirmation\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.confirmation + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"herethere\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.locationChoice + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"here_lng\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.currentLocationLon + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"here_lat\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.currentLocationLat + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"other_lng\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.selectedLocationLon + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"other_lat\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.selectedLocationLat + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"note\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.note + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"mailing\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.mailing + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"photo_attached\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.photoAttached + lineEnd);

			// Send a binary file
			if (this.photoAttached == 1) {

				// CHECK this.photo_uri != null
				// CHECK new File(this.photoUri).exists())) {
				byte[] buffer;

				// TODO grab the photos from the photo table

				String filename = this.reportId + ".jpg";

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\""
						+ filename + "\"" + lineEnd);
				dos.writeBytes("Content-Type: application/octet-stream"
						+ lineEnd);
				dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
				dos.writeBytes(lineEnd);

				// GET FROM PHOTO TABLE File photoFile = new
				// File(this.photo_uri);
				// FIX fileInputStream = new FileInputStream(photoFile);

				// create a buffer of maximum size int
				// bytesAvailable,
				// bytesRead,
				int bufferSize, bytesAvailable, bytesRead;
				int maxBufferSize = 64 * 1024; // old value 10241024
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				dos.writeBytes(lineEnd);

			}

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

			HttpPost httpPost = new HttpPost(
					this.deleteReport == 1 ? Util.URL_DELETE_REPORT
							: Util.URL_TIGERFINDER);

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
