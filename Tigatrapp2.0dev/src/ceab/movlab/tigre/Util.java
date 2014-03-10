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
 *
 * This file incorporates code from Space Mapper, which is subject 
 * to the following terms: 
 *
 * 		Space Mapper
 * 		Copyright (C) 2012, 2013 John R.B. Palmer
 * 		Contact: jrpalmer@princeton.edu
 *
 * 		Space Mapper is free software: you can redistribute it and/or modify 
 * 		it under the terms of the GNU General Public License as published by 
 * 		the Free Software Foundation, either version 3 of the License, or (at 
 * 		your option) any later version.
 * 
 * 		Space Mapper is distributed in the hope that it will be useful, but 
 * 		WITHOUT ANY WARRANTY; without even the implied warranty of 
 * 		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * 		See the GNU General Public License for more details.
 * 
 * 		You should have received a copy of the GNU General Public License along 
 * 		with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This file also incorporates code written by Chang Y. Chung and Necati E. Ozgencil 
 * for the Human Mobility Project, which is subject to the following terms: 
 * 
 * 		Copyright (C) 2010, 2011 Human Mobility Project
 *
 *		Permission is hereby granted, free of charge, to any person obtaining 
 *		a copy of this software and associated documentation files (the
 *		"Software"), to deal in the Software without restriction, including
 *		without limitation the rights to use, copy, modify, merge, publish, 
 *		distribute, sublicense, and/or sell copies of the Software, and to
 *		permit persons to whom the Software is furnished to do so, subject to
 *		the following conditions:
 *
 *		The above copyright notice and this permission notice shall be included
 *		in all copies or substantial portions of the Software.
 *
 *		THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *		EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *		MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *		IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *		CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *		TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *		SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * 
 */

package ceab.movlab.tigre;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

/**
 * Various static fields and methods used in the application, some taken from
 * Human Mobility Project.
 * 
 * @author Chang Y. Chung
 * @author Necati E. Ozgencil
 * @author John R.B. Palmer
 */
public class Util {

	public final static GeoPoint CEAB_COORDINATES = new GeoPoint(41686600,
			2799600);

	public final static boolean testingMode = false;

	public final static String MESSAGE_FIX_RECORDED = ".NEW_FIX_RECORDED";

	public static int JITTER_MAX_METERS = 250;

	public static String[] ALPHA_NUMERIC_DIGITS = { "0", "1", "2", "3", "4",
			"5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h",
			"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z" };

	public static boolean privateMode = false;

	public static int EXTRARUNS = 4;

	public static boolean flushGPSFlag = false;

	public static boolean redrawMap = false;

	public static long xTime = 1 * 60 * 60 * 1000;
	/**
	 * Default value for the interval between location fixes. In milliseconds.
	 */
	public static final long ALARM_INTERVAL = 15000; // 15 second

	public static final long UPLOAD_INTERVAL = 1000 * 60 * 60; // 1 hour

	/**
	 * Server URLs for uploads.
	 */
	public static final String URL_TIGERFINDER = "http://tce.ceab.csic.es/tigaDev2/tigerfinder2.0Dev.php";

	public static final String URL_DELETE_REPORT = "http://tce.ceab.csic.es/tigaDev2/tigerfinderdeleterep2.0Dev.php";

	public static final String URL_TIGERDRIVER = "xxx";

	public static final String URL_ACTIVATION = "xxx";

	public static final String URL_DELETE_TRIP = "xxx";

	public static final String URL_SAVE_TRIP = "xxx";

	public static final String URL_TIGERDRIVER_JSON = "xxx";

	/**
	 * Extension to append to all files saved for uploading.
	 */
	public static final String EXTENSION = ".dat";

	/**
	 * Maximum length of time to run location listeners during each fix attempt.
	 * In milliseconds.
	 */
	public static final long LISTENER_WINDOW = 5 * 1000;

	public static final long LONG_LISTENER_WINDOW = 60 * 1000;

	public final static long SECONDS = 1000;
	public final static long MINUTES = SECONDS * 60;
	public final static long HOURS = MINUTES * 60;
	public final static long DAYS = HOURS * 24;
	public final static long WEEKS = DAYS * 7;

	// Min average comfortable walking speed (cm/s) from Bohannon 1997,
	// http://ageing.oxfordjournals.org/content/26/1/15.full.pdf+html
	public static int WALKING_SPEED = 127;

	// Use the distance one would cover at walking speed capped at 80 (which is
	// standard city block size)
	public static int getMinDist(Context context) {

		PropertyHolder.init(context);

		int fixIntervalSeconds = (int) ((int) PropertyHolder.getAlarmInterval() / (int) SECONDS);

		int expectedWalkingDistanceMeters = (int) (WALKING_SPEED * fixIntervalSeconds) / 100;
		return Math.min(MIN_DIST, expectedWalkingDistanceMeters);
	}

	public static int MIN_DIST = 80;

	/**
	 * Value at which a GPS location will be preferred to a network location,
	 * even if the network location is listed with a higher accuracy.
	 */
	public static final float MIN_GPS_ACCURACY = 50;

	/**
	 * Value at which a location will be used, and both listeners stopped even
	 * if not yet at the end of the listener window.
	 */
	public static final float OPT_ACCURACY = 15;

	/**
	 * Value at which a location will be used, and both listeners stopped even
	 * if not yet at the end of the listener window - for long runs.
	 */
	public static final float OPT_ACCURACY_LONGRUNS = 50;

	/**
	 * Minimum accuracy necessary for location to be used.
	 */
	public static final float MIN_ACCURACY = 500;

	/**
	 * Default time for storing user data when user selects to do so. In days.
	 */
	public static final int STORAGE_DAYS = 7;

	/**
	 * Dummy variable indicating whether application is currently trying to
	 * upload data.
	 */
	public static boolean uploading = false;

	/**
	 * Default value for figuring out when alarm manager started counting. For
	 * use with the display timer in the DriverMapActivity activity.
	 */
	public static long countingFrom = 0;

	public static long lastFixStartedAt = 0;

	/**
	 * counter for how many fixes have been missed in a row.
	 */
	public static int missedFixes = 0;

	/**
	 * temp holder for info on latest fix.
	 */
	public static String lastFixTimeStamp = null;

	/**
	 * temp holder for info on latest fix.
	 */
	public static long lastFixTime = 0;

	/**
	 * temp holder for info on latest fix.
	 */
	public static double lastFixLat = 0;

	/**
	 * temp holder for info on latest fix.
	 */
	public static double lastFixLon = 0;

	/**
	 * holder for current value of the listener window
	 */
	public static long listenerTimer = LISTENER_WINDOW;

	/**
	 * Surrounds the given string in quotation marks. Taken from Human Mobility
	 * Project code written by Chang Y. Chung and Necati E. Ozgencil.
	 * 
	 * @param str
	 *            The string to be encased in quotation marks.
	 * @return The given string trimmed and encased in quotation marks.
	 */
	public static String enquote(String str) {
		final String dq = "\"";
		final String ddq = dq + dq;
		StringBuilder sb = new StringBuilder("");
		sb.append(dq);
		sb.append((str.trim()).replace(dq, ddq));
		sb.append(dq);
		return sb.toString();
	}

	/**
	 * Formats the given coordinate and converts to String form. Taken from
	 * Human Mobility Project code written by Chang Y. Chung and Necati E.
	 * Ozgencil.
	 * 
	 * @param coord
	 *            The coordinate value to be formatted.
	 * @return The properly formatted coordinate in String form
	 */
	public static String fmtCoord(double coord) {
		return String.format("%1$11.6f", coord);
	}

	/**
	 * Formats the given time and converts to String form. Taken from Human
	 * Mobility Project code written by Chang Y. Chung and Necati E. Ozgencil.
	 * 
	 * @param time
	 *            The time value to be formatted.
	 * @return The properly formatted time value in String form
	 */
	public static String iso8601(long time) {
		return String.format("%1$tFT%1$tT", time);
	}

	/**
	 * Formats the given time and converts to String form. Taken from Human
	 * Mobility Project code written by Chang Y. Chung and Necati E. Ozgencil.
	 * 
	 * @param datetime
	 *            The Date object, whose long time value must be formatted.
	 * @return The properly formatted time value of the Date Object in String
	 *         form
	 */
	public static String iso8601(Date datetime) {
		return iso8601(datetime.getTime());
	}

	/**
	 * Formats a date object for displaying it to the user.
	 * 
	 * @param date
	 *            The Date object to be formatted.
	 * @return The properly formatted time and date as a String.
	 * 
	 */
	public static String userDate(Date date) {
		SimpleDateFormat s = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		String format = s.format(date);
		return format;
	}

	/**
	 * Formats the location time, given as a long in milliseconds, for use in
	 * filenames.
	 * 
	 * @param locationTime
	 *            The long value to be formatted.
	 * @return The properly formatted time and date as a String.
	 */
	public static String fileNameDate(long locationTime) {
		Date date = new Date(locationTime);
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String format = s.format(date);
		return format;
	}

	/**
	 * Gets the current system time in milliseconds. Taken from Human Mobility
	 * Project code written by Chang Y. Chung and Necati E. Ozgencil.
	 * 
	 * @return The current system time in milliseconds.
	 */
	public static String now() {
		return iso8601(System.currentTimeMillis());
	}

	/**
	 * Displays a brief message on the phone screen. Taken from Human Mobility
	 * Project code written by Chang Y. Chung and Necati E. Ozgencil.
	 * 
	 * @param context
	 *            Interface to application environment
	 * @param msg
	 *            The message to be displayed to the user
	 */
	public static void toast(Context context, String msg) {

		TextView tv = new TextView(context);
		overrideFonts(context, tv);
		tv.setText(msg);
		Drawable bknd = context.getResources().getDrawable(
				R.drawable.white_border);
		tv.setBackgroundDrawable(bknd);
		tv.setPadding(20, 20, 20, 20);
		tv.setTextSize(20);

		Toast t = new Toast(context);
		t.setDuration(Toast.LENGTH_LONG);
		t.setView(tv);
		t.show();
	}

	public static void showHelp(Context context, String message, int image) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.check_help);
		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));
		TextView mText = (TextView) dialog.findViewById(R.id.checkHelpText);
		mText.setText(message);
		final ImageView mImage = (ImageView) dialog
				.findViewById(R.id.checkHelpImage);
		mImage.setImageResource(image);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public static void showHelp(Context context, String message) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.check_help);
		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));
		TextView mText = (TextView) dialog.findViewById(R.id.checkHelpText);
		mText.setText(message);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * Encrypt a byte array using RSA. Relies on the public key file stored in
	 * the raw folder (which is not included in the public source code).
	 * 
	 * @param context
	 *            The application context.
	 * @param in
	 *            The byte array to be encrypted.
	 * @return An encrypted byte array.
	 */
	public static byte[] encryptRSA(Context context, byte[] in) {
		// String TAG = "Util.encryptRSA";
		BufferedInputStream is = null;
		ByteArrayOutputStream bos = null;
		byte[] pk;
		byte[] result = null;

		try {
			is = new BufferedInputStream(context.getResources()
					.openRawResource(R.raw.pubkey));
			bos = new ByteArrayOutputStream();
			while (is.available() > 0) {
				bos.write(is.read());
			}

			pk = bos.toByteArray();

			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pk);

			KeyFactory kf;
			kf = KeyFactory.getInstance("RSA");
			PublicKey pkPublic;

			pkPublic = kf.generatePublic(publicKeySpec);

			// Encrypt
			Cipher pkCipher;

			pkCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");

			pkCipher.init(Cipher.ENCRYPT_MODE, pkPublic);

			result = pkCipher.doFinal(in);
		} catch (IllegalBlockSizeException e) {
			// logging exception, and simply letting the result return as null.
			// Log.e(TAG, "Exception " + e);
		} catch (BadPaddingException e) {
			// logging exception, and simply letting the result return as null.
			// Log.e(TAG, "Exception " + e);
		} catch (InvalidKeyException e) {
			// logging exception, and simply letting the result return as null.
			// Log.e(TAG, "Exception " + e);
		} catch (NoSuchAlgorithmException e) {
			// logging exception, and simply letting the result return as null.
			// Log.e(TAG, "Exception " + e);
		} catch (NoSuchPaddingException e) {
			// logging exception, and simply letting the result return as null.
			// Log.e(TAG, "Exception " + e);
		} catch (InvalidKeySpecException e) {
			// logging exception, and simply letting the result return as null.
			// Log.e(TAG, "Exception " + e);
		} catch (IOException e) {
			// logging exception, and simply letting the result return as null.
			// Log.e(TAG, "Exception " + e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					// logging exception
					// Log.e(TAG, "Exception " + e);
				}
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// logging exception
					// Log.e(TAG, "Exception " + e);
				}
			}
		}
		return result;
	}

	/**
	 * Saves a byte array to the internal storage directory.
	 * 
	 * @param context
	 *            The application context.
	 * @param filename
	 *            The file name to use.
	 * @param bytes
	 *            The byte array to be saved.
	 */
	public static void saveBytes(Context context, String filename, byte[] bytes) {
		// String TAG = "Util.saveFile";
		FileOutputStream fos = null;
		try {
			byte[] encryptedBytes = bytes;
			String FILENAME = filename;

			fos = context.openFileOutput(FILENAME + EXTENSION,
					Context.MODE_PRIVATE);

			fos.write(encryptedBytes);
			// Log.e(TAG, "encrypted file saved as " + FILENAME);

		} catch (IOException e) {
			// logging exception but doing nothing
			// Log.e(TAG, "Exception " + e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// logging exception but doing nothing
					// Log.e(TAG, "Exception " + e);

				}
			}

		}

	}

	/**
	 * Saves a string to the internal storage, placing it in line to be
	 * uploaded.
	 * 
	 * @param context
	 *            Application context.
	 * @param input
	 *            String to be saved.
	 * @param filePrefix
	 *            Prefix to place on the file. (So that it can be easily
	 *            identified later in data processing.
	 */
	public static void saveInput(Context context, String input,
			String filePrefix) {
		byte[] inputEnc;
		String FILENAME;
		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);
		try {
			inputEnc = encryptRSA(context, input.getBytes("UTF-8"));
			FILENAME = filePrefix + PropertyHolder.getUserId() + "_"
					+ fileNameDate(System.currentTimeMillis());
			saveBytes(context, FILENAME, inputEnc);
		} catch (Exception e) {
		}

	}

	public static String csvFixes(Cursor c) {

		int accuracy = c.getColumnIndexOrThrow("accuracy");
		int altitude = c.getColumnIndexOrThrow("altitude");
		int latitude = c.getColumnIndexOrThrow("latitude");
		int longitude = c.getColumnIndexOrThrow("longitude");
		int provider = c.getColumnIndexOrThrow("provider");
		int timestamp = c.getColumnIndexOrThrow("timestamp");
		StringBuilder sb = new StringBuilder("");
		sb.append("accuracy").append(",");
		sb.append("altitude").append(",");
		sb.append("latitude").append(",");
		sb.append("longitude").append(",");
		sb.append("provider").append(",");
		sb.append("timestamp");

		c.moveToFirst();
		while (!c.isAfterLast()) {
			sb.append("\n");
			sb.append(doubleFieldVal(c, accuracy)).append(",");
			sb.append(doubleFieldVal(c, altitude)).append(",");
			sb.append(doubleFieldVal(c, latitude)).append(",");
			sb.append(doubleFieldVal(c, longitude)).append(",");
			sb.append(Util.enquote(c.getString(provider))).append(",");
			sb.append(Util.enquote(c.getString(timestamp).trim()));
			c.moveToNext();
		}
		return sb.toString();
	}

	/**
	 * Converts the supposedly double value contained in the row, at which the
	 * given Cursor is pointing, and the col(umn) specified to its String
	 * representation.
	 * 
	 * @return The String representation of the value contained in the cell
	 *         [c.getPosition(), col]
	 */
	private static String doubleFieldVal(Cursor c, int col) {
		Double val = (Double) c.getDouble(col);
		return (val == null) ? "" : val.toString();
	}

	public static void overrideFonts(final Context context, final View v) {

		// turning this off for now
		/*
		 * try { if (v instanceof ViewGroup) { ViewGroup vg = (ViewGroup) v;
		 * 
		 * for (int i = 0; i < vg.getChildCount(); i++) { View child =
		 * vg.getChildAt(i); overrideFonts(context, child); }
		 * 
		 * } else if (v instanceof TextView) { ((TextView)
		 * v).setTypeface(Typeface.createFromAsset( context.getAssets(),
		 * "fonts/RobotoCondensed-Regular.ttf")); }
		 * 
		 * } catch (Exception e) { }
		 */
	}

	/**
	 * Checks if the phone has an internet connection.
	 * 
	 * @param context
	 *            The application context.
	 * @return True if phone has a connection; false if not.
	 */
	public static boolean isOnline(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static boolean uploadFile(byte[] bytes, String filename,
			String uploadurl) {

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

	public static int getBatteryLevel(Context context) {
		Intent batteryIntent = context.registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra("level", -1);
		int scale = batteryIntent.getIntExtra("scale", -1);

		// Error checking that probably isn't needed but I added just in case.
		if (level == -1 || scale == -1) {
			return -1;
		}

		int powerLevel = (int) Math.round(level * 100.0 / scale);

		return powerLevel;
	}

	public static String getString(JSONObject jsonObject, String key) {
		String result = "";
		if (jsonObject.has(key)) {
			try {
				result = jsonObject.getString(key);
			} catch (JSONException e) {
				// do nothing
			}
		}
		return result;
	}

	public static JSONArray StringArrayList2JsonArray(
			ArrayList<String> arrayList) {
		JSONArray result = new JSONArray();
		for (String e : arrayList) {
			result.put(e);
		}
		return result;
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static Bitmap getSmallerBitmap(File file, Context context,
			int pixelSize) throws FileNotFoundException, IOException {
		
		
		FileInputStream input = new FileInputStream(file);

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1)
				|| (onlyBoundsOptions.outHeight == -1))
			return null;

		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;

		double ratio = (originalSize > pixelSize) ? (originalSize / pixelSize)
				: 1.0;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;// optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		input = new FileInputStream(file);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

}
