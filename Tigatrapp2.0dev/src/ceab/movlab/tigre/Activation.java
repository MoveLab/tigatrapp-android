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
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * Initial screen triggered when app starts.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Activation extends Activity {

	Context context;

	String code;

	ProgressBar progressbar;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activation);
		
		progressbar = (ProgressBar) findViewById(R.id.activationProgressbar);
		progressbar.setProgress(0);
		progressbar.setVisibility(View.INVISIBLE);


		context = getApplicationContext();
		PropertyHolder.init(context);

		Button positive = (Button) findViewById(R.id.alertOK);

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				EditText codeEntry = (EditText) findViewById(R.id.activationCodeEntry);
				code = codeEntry.getText().toString();

				new ActivationTask().execute(context);

			}
		});

	}

	@Override
	protected void onResume() {

		if (PropertyHolder.isActivated()) {

			startActivity(new Intent(Activation.this, Splash.class));
			finish();

		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	public boolean isOnline() {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public class ActivationTask extends AsyncTask<Context, Integer, Boolean> {


		int myProgress;

		int resultFlag;

		int OFFLINE = 0;
		int UPLOAD_ERROR = 1;
		int SUCCESS = 3;
		int BAD_CODE = 4;

		String response;

		@Override
		protected void onPreExecute() {

			resultFlag = SUCCESS;

			progressbar.setVisibility(View.VISIBLE);

			myProgress = 0;

		}

		protected Boolean doInBackground(Context... context) {

			publishProgress(1);

			// now test if there is a data connection
			if (!isOnline()) {
				resultFlag = OFFLINE;
				return false;
			}

			publishProgress(10);

			FileInputStream fileInputStream = null;

			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";

			publishProgress(20);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			publishProgress(30);

			try {

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"userid\""
						+ lineEnd);
				dos.writeBytes("Content-Type: text/plain; charset=US-ASCII"
						+ lineEnd);
				dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd);
				dos.writeBytes(lineEnd);
				// dos.writeBytes(PropertyHolder.getUserId() + lineEnd);
				dos.writeBytes(PropertyHolder.getUserId() + lineEnd);

				publishProgress(40);

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"code\""
						+ lineEnd + lineEnd);
				dos.writeBytes(code + lineEnd);

				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				publishProgress(50);

			} catch (IOException e) {

				resultFlag = UPLOAD_ERROR;

			} finally {
				if (dos != null) {
					try {
						dos.flush();
						dos.close();
					} catch (IOException e) {
						resultFlag = UPLOAD_ERROR;
					}
				}
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
						resultFlag = UPLOAD_ERROR;
					}
				}

				ByteArrayInputStream content = new ByteArrayInputStream(
						baos.toByteArray());
				BasicHttpEntity entity = new BasicHttpEntity();
				entity.setContent(content);

				publishProgress(60);

				HttpPost httpPost = new HttpPost(Util.URL_ACTIVATION);
				httpPost.addHeader("Connection", "Keep-Alive");
				httpPost.addHeader("Content-Type",
						"multipart/form-data; boundary=" + boundary);

				publishProgress(70);

				httpPost.setEntity(entity);

				HttpClient client = new DefaultHttpClient();

				publishProgress(80);

				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				response = "";
				try {

					response = client.execute(httpPost, responseHandler);

				} catch (ClientProtocolException e) {
					resultFlag = UPLOAD_ERROR;
				} catch (IOException e) {
					resultFlag = UPLOAD_ERROR;
				}

				publishProgress(90);

				if (response.contains("Activated")) {

					resultFlag = SUCCESS;

				} else {
					resultFlag = BAD_CODE;

				}

			}

			publishProgress(100);

			code = "";

			return true;
		}

		protected void onProgressUpdate(Integer... progress) {

			progressbar.setProgress(progress[0]);
		}

		protected void onPostExecute(Boolean result) {

			progressbar.setVisibility(View.INVISIBLE);

			if (result && resultFlag == SUCCESS) {

				PropertyHolder.setActivated(true);
//				Util.toast(context, getResources()
//						.getString(R.string.activated));

				startActivity(new Intent(Activation.this, Splash.class));
				finish();
				return;

			} else {

				if (resultFlag == OFFLINE) {
					Util.toast(
							context,
							getResources().getString(
									R.string.offline_activation));

				}

				if (resultFlag == UPLOAD_ERROR) {
					Util.toast(
							context,
							getResources().getString(
									R.string.upload_error_activation));

				}

				if (resultFlag == BAD_CODE) {
					Util.toast(context, response);

				}

			}

		}
	}

}