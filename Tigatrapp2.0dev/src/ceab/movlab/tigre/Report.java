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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
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


/**
 * Defines map point objects used in DriverMapActivity.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Report {

	String reportID;
	long reporttime;
	String q1_sizecolor; // ENUM("si", "no", "nose"),
	String q2_abdomenlegs; // ENUM("si", "no", "nose"),
	String q3_headthorax; // ENUM("si", "no", "nose"),
	String herethere; // ENUM("here", "there"),
	String here_lng; // FLOAT(10,6),
	String here_lat; // FLOAT(10,6),
	String other_lng; // FLOAT(10,6),
	String other_lat; // FLOAT(10,6),
	String here_lng_j; // FLOAT(10,6),
	String here_lat_j; // FLOAT(10,6),
	String other_lng_j; // FLOAT(10,6),
	String other_lat_j; // FLOAT(10,6),
	String note;
	String mailing;
	String photo_attached;
	String photo_uri;

	Report(String _reportID, long _reporttime, String _q1_sizecolor,
			String _q2_abdomenlegs, String _q3_headthorax,
			String _herethere, String _here_lng, String _here_lat,
			String _other_lng, String _other_lat, String _here_lng_j,
			String _here_lat_j, String _other_lng_j, String _other_lat_j,
			String _note, String _mailing, String _photo_attached,
			String _photoUri) {

		reportID = _reportID;
		reporttime = _reporttime;
		q1_sizecolor = _q1_sizecolor;
		q2_abdomenlegs = _q2_abdomenlegs;
		q3_headthorax = _q3_headthorax;
		herethere = _herethere;
		here_lng = _here_lng;
		here_lat = _here_lat;
		other_lng = _other_lng;
		other_lat = _other_lat;
		here_lng_j = _here_lng_j;
		here_lat_j = _here_lat_j;
		other_lng_j = _other_lng_j;
		other_lat_j = _other_lat_j;
		note = _note;
		mailing = _mailing;
		photo_attached = _photo_attached;
		photo_uri = _photoUri;

	}

	public void clear() {

		reportID = null;
		reporttime = -1;
		q1_sizecolor = null;
		q2_abdomenlegs = null;
		q3_headthorax = null;
		herethere = null;
		here_lng = null;
		here_lat = null;
		other_lng = null;
		other_lat = null;
		here_lng_j = null;
		here_lat_j = null;
		other_lng_j = null;
		other_lat_j = null;
		note = null;
		mailing = null;
		photo_attached = null;
		photo_uri = null;

	}

public boolean upload(Context context){
	
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
		dos.writeBytes(this.reportID + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"reporttime\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.reporttime + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"q1_sizecolor\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.q1_sizecolor + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"q2_abdomenlegs\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.q2_abdomenlegs + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"q3_headthorax\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.q3_headthorax + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"herethere\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.herethere + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"here_lng\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.here_lng + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"here_lat\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.here_lat + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"other_lng\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.other_lng + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"other_lat\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.other_lat + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"here_lng_j\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.here_lng_j + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"here_lat_j\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.here_lat_j + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"other_lng_j\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.other_lng_j + lineEnd);

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"other_lat_j\""
				+ lineEnd + lineEnd);
		dos.writeBytes(this.other_lat_j + lineEnd);

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
		dos.writeBytes(this.photo_attached + lineEnd);

		// Send a binary file
		if (this.photo_attached.equals("si") && this.photo_uri != null
				&& (new File(this.photo_uri).exists())) {
			byte[] buffer;

			String filename = this.reportID + ".jpg";

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\""
					+ filename + "\"" + lineEnd);
			dos.writeBytes("Content-Type: application/octet-stream"
					+ lineEnd);
			dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
			dos.writeBytes(lineEnd);

			File photoFile = new File(this.photo_uri);
			fileInputStream = new FileInputStream(photoFile);

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

		HttpPost httpPost = new HttpPost(Util.URL_TIGERFINDER);
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
