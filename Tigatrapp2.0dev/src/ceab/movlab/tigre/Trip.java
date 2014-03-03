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

/**
 * Defines map point objects used in DriverMapActivity.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Trip {

	String tripid;
	int delete;
	String transported_mosquito;
	String mosquito_is_tiger;
	long start_time;
	long end_time;

	Trip(String tripid, int delete, String transported_mosquito,
			String mosquito_is_tiger, long start_time, long end_time) {

		this.tripid = tripid;
		this.delete = delete;
		this.transported_mosquito = transported_mosquito;
		this.mosquito_is_tiger = mosquito_is_tiger;
		this.start_time = start_time;
		this.end_time = end_time;

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
			dos.writeBytes("Content-Disposition: form-data; name=\"userid\""
					+ lineEnd);
			dos.writeBytes("Content-Type: text/plain; charset=US-ASCII"
					+ lineEnd);
			dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd);
			dos.writeBytes(lineEnd);
			dos.writeBytes(PropertyHolder.getUserId() + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"tripid\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.tripid + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"transported_mosquito\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.transported_mosquito + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"mosquito_is_tiger\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.mosquito_is_tiger + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"start_time\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.start_time + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"end_time\""
					+ lineEnd + lineEnd);
			dos.writeBytes(this.end_time + lineEnd);

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
					this.delete == 1 ? Util.URL_DELETE_TRIP
							: Util.URL_SAVE_TRIP);
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
