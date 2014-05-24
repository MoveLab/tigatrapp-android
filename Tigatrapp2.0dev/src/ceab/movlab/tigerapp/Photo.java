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
public class Photo {

	String userId;
	String reportId;
	int reportVersion;
	String photoUri;
	long photoTime;
	int uploaded;
	int serverTimestamp;
	int deletePhoto;

	Photo(String reportId, int reportVersion, String photoUri, long photoTime,
			int uploaded, int serverTimestamp, int deletePhoto) {

		this.userId = PropertyHolder.getUserId();
		this.reportId = reportId;
		this.reportVersion = reportVersion;
		this.photoUri = photoUri;
		this.photoTime = photoTime;
		this.uploaded = uploaded;
		this.serverTimestamp = serverTimestamp;
		this.deletePhoto = deletePhoto;
	}


	Photo(String userId, String reportId, int reportVersion, String photoUri, long photoTime,
			int uploaded, int serverTimestamp, int deletePhoto) {

		this.userId = userId;
		this.reportId = reportId;
		this.reportVersion = reportVersion;
		this.photoUri = photoUri;
		this.photoTime = photoTime;
		this.uploaded = uploaded;
		this.serverTimestamp = serverTimestamp;
		this.deletePhoto = deletePhoto;
	}

	
	public void clear() {

		this.reportId = null;
		this.reportVersion = Report.MISSING;
		this.photoUri = null;
		this.photoTime = Report.MISSING;
		this.uploaded = Report.NO;
		this.serverTimestamp = Report.MISSING;
		this.deletePhoto = Report.NO;

	}

}
