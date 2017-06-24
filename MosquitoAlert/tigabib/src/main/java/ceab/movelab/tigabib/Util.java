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

package ceab.movelab.tigabib;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import static android.media.CamcorderProfile.get;


/**
 * Various static fields and methods used in the application, some taken from
 * Human Mobility Project.
 * 
 * @author Chang Y. Chung
 * @author Necati E. Ozgencil
 * @author John R.B. Palmer
 */
public class Util {

	private static String TAG = "Util";

	public static final int MAX_MISSION_VERSION = 1;

	// if we ignore fixes above 60 degrees latitude or below -60 degrees
	// latitude, then the size of the
	// .05x.05 degree areas should all be larger than 15.5 km. See
	// http://msi.nga.mil/MSISiteContent/StaticFiles/Calculators/degree.html
	public static int FIX_LAT_CUTOFF = 60;

	public static boolean privateMode() {
//		boolean result = false;
//		if (context.getResources().getString(R.string.private_mode).equals("true"))
//			result = true;
		return BuildConfig.PRIVATE_MODE;
	}

//	public static boolean debugMode(Context context) {
////		boolean result = false;
////		if (context.getResources().getString(R.string.debug_mode).equals("true"))
////			result = true;
//		return BuildConfig.DEBUG;
//	}

	public static boolean debugMode() {		// !!!!$$!!!!
		return BuildConfig.DEBUG;
	}

	public static void logError(String tag, String message) {
		if ( debugMode() )
			Log.e(tag, message);
	}

	public static void logInfo(String tag, String message) {
		if ( debugMode() )
			Log.i(tag, message);
	}

	public static void internalBroadcast(Context context, String message) {
		Intent i = new Intent(Messages.internalAction(context));
		i.putExtra(Messages.INTERNAL_MESSAGE_EXTRA, message);
		context.sendBroadcast(i);
	}

	/**
	 * RSS URL for Spanish.
	 */
	public static final String URL_RSS_ES = UtilLocal.URL_PROJECT + "feed/";

	/**
	 * RSS URL for Catalan.
	 */
	public static final String URL_RSS_CA = UtilLocal.URL_PROJECT + "feed/";

	/**
	 * Server API URL.
	 */
	public static final String URL_TIGASERVER_API_ROOT = UtilLocal.URL_TIGASERVER_API_ROOT;

	/**
	 * API user endpoint.
	 */
	public static final String API_USER = UtilLocal.API_USER;

	/**
	 * API report endpoint.
	 */
	public static final String API_REPORT = UtilLocal.API_REPORT;

	/**
	 * API photo endpoint.
	 */
	public static final String API_PHOTO = UtilLocal.API_PHOTO;

	/**
	 * API fix endpoint.
	 */
	public static final String API_FIXES = UtilLocal.API_FIXES;

	/**
	 * API mission endpoint.
	 */
	public static final String API_MISSION = UtilLocal.API_MISSION;

    /**
     * API notification endpoint.
     */
    public static final String API_NOTIFICATION = UtilLocal.API_NOTIFICATION;

    /**
     * API score endpoint.
     */
    public static final String API_SCORE = UtilLocal.API_SCORE;


	/**
	 * API token endpoint.
	 */
	public static final String API_TOKEN = UtilLocal.API_TOKEN;

	/**
	 * API nearby reports endpoint.
	 */
	public static final String API_NEARBY_REPORTS = UtilLocal.API_NEARBY_REPORTS;

	/**
	 * API config endpoint.
	 */
	public static final String API_CONFIGURATION = UtilLocal.API_CONFIGURATION;

	/**
	 * Server authorization.
	 */
	//public final static String TIGASERVER_API_KEY = UtilLocal.TIGASERVER_API_KEY;
	//public final static String TIGASERVER_CLIENT_ID = UtilLocal.TIGASERVER_CLIENT_ID;
	public final static String TIGASERVER_AUTHORIZATION = UtilLocal.TIGASERVER_AUTHORIZATION;

	public final static String EXTENSION = ".dat";

	public final static LatLng CEAB_COORDINATES = new LatLng(41.683600, 2.799600);

	public final static double latMask = 0.025;
	public final static double lonMask = 0.025;

/*
	public static String[] ALPHA_NUMERIC_DIGITS = { "0", "1", "2", "3", "4",
			"5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h",
			"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z" };
*/

	public static int DEFAULT_SAMPLES_PER_DAY = 5;

	/**
	 * Default value for the interval between location fixes. In milliseconds.
	 */
	public static final long ALARM_INTERVAL = 1000 * 60 * 60; // 1 hour

	public static final long TASK_FIX_WINDOW = 1000 * 15; // 15 seconds

	//public static final long UPLOAD_INTERVAL = 1000 * 60 * 60; // 1 hour

	/**
	 * Maximum length of time to run location listeners during each fix attempt.
	 * In milliseconds.
	 */
	public static final long LISTENER_WINDOW = 5 * 1000;

	public final static long SECONDS = 1000;
	public final static long MINUTES = SECONDS * 60;
	public final static long HOURS = MINUTES * 60;
	public final static long DAYS = HOURS * 24;
	//public final static long WEEKS = DAYS * 7;

	/**
	 * Value at which a location will be used, and both listeners stopped even
	 * if not yet at the end of the listener window.
	 */
	public static final float OPT_ACCURACY = 1000;

	/**
	 * Minimum accuracy necessary for location to be used.
	 */
	public static final float MIN_ACCURACY = 5000;

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

	public static String ecma262(long time) {
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSZ";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		return sdf.format(new Date(time));
	}

	public static long string2Long(String date_string, String format) {
		long result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		ParsePosition pos = new ParsePosition(0);
		try {
			Date d = sdf.parse(date_string);
			result = d.getTime();

		} catch (ParseException e) {
			// just leave result as 0 in this case
		}

		return result;
	}

	public static long ecma262String2Long(Context context, String ecma262) {
		long result = 0;
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		ParsePosition pos = new ParsePosition(0);
		try {
			Date d = sdf.parse(ecma262, pos);
			if (pos.getIndex() == 0)
				throw new ParseException("Unparseable date: \"" + ecma262 + "\"", pos.getErrorIndex());
			else
				result = d.getTime();
		} catch (ParseException e) {
			Util.logError(TAG, "exception: " + e);
		}
		return result;
	}

	public static int triggerTime2HourInt(String triggerTime) {
		int result = 0;
		result = Integer.parseInt(triggerTime.substring(0, 1));
		return result;
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
		//String format = s.format(date);
		return s.format(date);
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

	public static int hour(long unixtime) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(unixtime);
		return cal.get(Calendar.HOUR_OF_DAY);
		//return hour;
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
		toastTimed(context, msg, Toast.LENGTH_SHORT);
	}

	public static void toastTimed(Context context, String msg, int time) {
		TextView tv = new TextView(context);
		tv.setText(msg);
		Drawable bknd = context.getResources().getDrawable(R.drawable.white_border);
		tv.setBackgroundDrawable(bknd);
		tv.setPadding(40, 20, 40, 20);
		tv.setTextSize(20);

		final Toast t = new Toast(context);
		t.setDuration(time);
		t.setView(tv);
		tv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				t.cancel();
				return true;
			}

		});

		t.show();
	}

	
	public static void showHelp(Context context, String message, int image) {
		final Context thisContext = context;
		final Dialog dialog = new Dialog(thisContext);
		final int thisImage = image;
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.check_help);
		TextView mText = (TextView) dialog.findViewById(R.id.checkHelpText);
		mText.setText(Html.fromHtml(message, null, new TigaTagHandler()));
		final ImageView mImage = (ImageView) dialog.findViewById(R.id.checkHelpImage);
		mImage.setImageResource(thisImage);
		mImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(thisContext);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.photo_view);
				ImageView iv = (ImageView) dialog.findViewById(R.id.photoView);
				// TODO find better way of choosing max pixel
				// size -- based on screen
				iv.setImageResource(thisImage);
				iv.setOnClickListener(new View.OnClickListener() {
					public void onClick(View View3) {
						dialog.dismiss();
					}
				});
				dialog.setCanceledOnTouchOutside(true);
				dialog.setCancelable(true);
				dialog.show();
			}

		});
		ImageView cancelButton = (ImageView) dialog.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public static void showHelp(Context context, String message) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.check_help);
		TextView mText = (TextView) dialog.findViewById(R.id.checkHelpText);
		mText.setText(Html.fromHtml(message, null, new TigaTagHandler()));
		final ImageView mImage = (ImageView) dialog.findViewById(R.id.checkHelpImage);
		mImage.setVisibility(View.GONE);

		ImageView cancelButton = (ImageView) dialog.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		// dialog.setCanceledOnTouchOutside(true);
		dialog.show();
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

			fos = context.openFileOutput(FILENAME + EXTENSION, Context.MODE_PRIVATE);
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
	 * Checks if the phone has an internet connection.
	 * 
	 * @param context
	 *            The application context.
	 * @return True if phone has a connection; false if not.
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		Util.logInfo(TAG, "Not online");
		return false;
	}

/*	public static int getBatteryLevel(Context context) {
		Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra("level", -1);
		int scale = batteryIntent.getIntExtra("scale", -1);

		// Error checking that probably isn't needed but I added just in case.
		if (level == -1 || scale == -1) {
			return -1;
		}

		//int powerLevel = (int) Math.round(level * 100.0 / scale);
		return (int) Math.round(level * 100.0 / scale);
	}*/

	public static float getBatteryProportion(Context context) {
		Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra("level", -1);
		int scale = batteryIntent.getIntExtra("scale", -1);

		// Error checking that probably isn't needed but I added just in case.
		if (level == -1 || scale == -1) {
			return -1;
		}

		float powerProportion = (float) level / scale;
		Util.logInfo(TAG, "battery level: " + level);
		Util.logInfo(TAG, "battery scale: " + scale);
		Util.logInfo(TAG, "battery prop: " + powerProportion);
		return powerProportion;
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

	/*public static LayerDrawable createMarker(int color) {

		ShapeDrawable border = new ShapeDrawable(new OvalShape());
		border.getPaint().setColor(Color.BLACK);
		border.getPaint().setStyle(Paint.Style.STROKE);

		border.setBounds(0, 0, 80, 80);
		ShapeDrawable fill = new ShapeDrawable(new OvalShape());
		fill.getPaint().setColor(Color.BLACK);
		fill.getPaint().setStyle(Paint.Style.FILL);

		fill.setBounds(0, 0, 80, 80);
		Drawable drawableArray[] = new Drawable[] { fill, border };
		LayerDrawable layerDraw = new LayerDrawable(drawableArray);
		// layerDraw.setLayerInset(0, 15, 15, 0, 0);//set offset of first layer
		// layerDraw.setLayerInset(1,40,40,0,0);//set offset for second layer
		return layerDraw;
	}
*/
	/*public static Drawable createMarker2(int color) {

		ShapeDrawable border = new ShapeDrawable(new OvalShape());
		border.getPaint().setColor(Color.BLACK);
		border.getPaint().setStyle(Paint.Style.STROKE);

		border.setBounds(0, 0, 80, 80);
		ShapeDrawable fill = new ShapeDrawable(new OvalShape());
		fill.getPaint().setColor(Color.BLACK);
		fill.getPaint().setStyle(Paint.Style.FILL);

		fill.setBounds(0, 0, 80, 80);
		Drawable drawableArray[] = new Drawable[] { fill, border };
		LayerDrawable layerDraw = new LayerDrawable(drawableArray);
		layerDraw.setBounds(0, 0, 80, 80);
		// layerDraw.setLayerInset(0, 15, 15, 0, 0);//set offset of first layer
		// layerDraw.setLayerInset(1,40,40,0,0);//set offset for second layer
		return fill;
	}
*/
	public static String setDisplayLanguage(Resources res) {
		String lang = PropertyHolder.getLanguage();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		String oldLang = conf.locale.getLanguage();
		if (!oldLang.equals(lang)) {
			Locale myLocale = new Locale(lang);
			conf.locale = myLocale;
			res.updateConfiguration(conf, dm);
		}
		return lang;
	}

	public static int postPhoto(Context context, String uri, String filename, String versionUUID) {

		int response = 0;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		// DataInputStream inStream = null;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 64 * 1024; // old value 1024*1024
		FileInputStream fileInputStream = null;
		try {
			// ------------------ CLIENT REQUEST
			fileInputStream = new FileInputStream(uri);

			// open a URL connection to the Servlet
			URL url = new URL(URL_TIGASERVER_API_ROOT + API_PHOTO);
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// set timeout
			conn.setConnectTimeout(240000);
			conn.setReadTimeout(240000);
			// Use a post method.
			conn.setRequestMethod("POST");
			// conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Authorization", TIGASERVER_AUTHORIZATION);
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"report\"" + lineEnd + lineEnd);
			dos.writeBytes(versionUUID + lineEnd);

			dos.writeBytes(twoHyphens + boundary + lineEnd);

			dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\"" + filename + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(twoHyphens + boundary + lineEnd);
		} catch (Exception e) {
			Util.logError(TAG, "error: " + e);
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					Log.e(TAG, "exception" + e);

				}
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "exception" + e);

				}
			}

		}

		// ------------------ read the SERVER RESPONSE
		try {
			if (conn != null) {
				response = conn.getResponseCode();
				Log.d(TAG, "response: " + conn.getResponseMessage());
			}
		} catch (IOException e) {
			Log.e(TAG, "Connection error", e);
		}

		return response;
	}

	/**
	 * Uploads JSONObject to Tigaserver API using HTTP PUT request
	 * 
	 * @author: John Palmer
	 * 
	 * @param jsonData
	 *            JSONObject to be uploaded.
	 * @param path
	 *            String representing URL to the server API.
	 */
	public static HttpResponse postJSON(JSONObject jsonData, String apiEndpoint, Context context) {
		HttpResponse result = null;
		if (!isOnline(context)) {
			return null;
		} else {

			try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 3000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 3000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

				DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(URL_TIGASERVER_API_ROOT + apiEndpoint);
				StringEntity se = new StringEntity(jsonData.toString(), "UTF-8");
				httpost.setEntity(se);
				httpost.setHeader("Accept", "application/json");
				httpost.setHeader("Content-type", "application/json");
				httpost.setHeader("Authorization", TIGASERVER_AUTHORIZATION);

				result = httpclient.execute(httpost);

			} catch (UnsupportedEncodingException e) {
				Util.logError(TAG, "error: " + e);
			} catch (IOException e) {
				Util.logError(TAG, "error: " + e);
			}
			return result;
		}
	}

	public static int getResponseStatusCode(HttpResponse httpResponse) {

		int statusCode = 0;
		if (httpResponse != null) {
			StatusLine status = httpResponse.getStatusLine();
			statusCode = status.getStatusCode();
		}
		return statusCode;
	}

	public static JSONObject parseResponse(Context context,
			HttpResponse response) {
		JSONObject json = new JSONObject();
		if (response != null) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				json = new JSONObject(builder.toString());

			} catch (UnsupportedEncodingException e) {
				Util.logError(TAG, "error: " + e);
			} catch (IllegalStateException e) {
				Util.logError(TAG, "error: " + e);
			} catch (IOException e) {
				Util.logError(TAG, "error: " + e);
			} catch (JSONException e) {
				Util.logError(TAG, "error: " + e);
			}
		}
		return json;
	}

	public static String getJSON(String apiEndpoint, Context context) {

		if (!isOnline(context)) {
			return "";
		} else {

			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpGet httpGet = new HttpGet(URL_TIGASERVER_API_ROOT + apiEndpoint);

			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Content-type", "application/json");
			httpGet.setHeader("Authorization", TIGASERVER_AUTHORIZATION);

			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				Util.logInfo(TAG, "Status code:" + statusCode);

				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					Util.logInfo(TAG, "failed to get JSON data");
				}
			} catch (ClientProtocolException e) {
				Util.logError(TAG, "error: " + e);
			} catch (IOException e) {
				Util.logError(TAG, "error: " + e);
			}

			return builder.toString();
		}
	}

	public static String reportType2String(int reportType) {
		String result = "";
		if (reportType == Report.TYPE_ADULT)
			result = "adult";
		else if (reportType == Report.TYPE_BREEDING_SITE)
			result = "site";
		else if (reportType == Report.TYPE_MISSION)
			result = "mission";
		return result;
	}

	public static String locationChoice2String(int locationChoice) {
		String result = "";
		if (locationChoice == Report.LOCATION_CHOICE_CURRENT)
			result = "current";
		else if (locationChoice == Report.LOCATION_CHOICE_SELECTED)
			result = "selected";
		else if (locationChoice == Report.LOCATION_CHOICE_MISSING)
			result = "missing";
		return result;
	}

	public static Boolean registerOnServer(Context context) {
Util.logInfo(TAG, "register on server");

		Boolean result = false;
		JSONObject jsonUUID;
		try {
			jsonUUID = new JSONObject();
			jsonUUID.put("user_UUID", PropertyHolder.getUserId());
Util.logInfo(TAG, "register json: " + jsonUUID.toString());
			// !!! update http calls
			int statusCode = Util.getResponseStatusCode(Util.postJSON(jsonUUID, Util.API_USER, context));
Util.logInfo(TAG, "register status code: " + statusCode);

			if (statusCode < 300 && statusCode > 0) {
				PropertyHolder.setRegistered(true);
				result = true;
			} else {
				result = false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			// try creating UUID again
			//PropertyHolder.setUserId(UUID.randomUUID().toString());
			// consider looping back but make sure this will not lead to chaos.
			// registerOnServer();
			result = false;
		}
		return result;
	}

	public static void registerFCMToken(Context ctx, String token, String userId) {
		if ( token != null ) {
			String tokenUrl = Util.URL_TIGASERVER_API_ROOT + Util.API_TOKEN + "?token=" + token + "&user_id=" + userId;
Util.logInfo("==============", "TEST");
Log.d("===========", "BuildConfig.DEBUG >> " + BuildConfig.DEBUG);  // !!!!
Log.d("===========", tokenUrl);

			Ion.with(ctx)
					.load(tokenUrl)
					.setHeader("Accept", "application/json")
					//.setHeader("Content-type", "application/json")
					//.setLogging("Token", Log.VERBOSE)
					.setHeader("Authorization", UtilLocal.TIGASERVER_AUTHORIZATION)
					.setBodyParameter("token", token)
					.setBodyParameter("user_id", PropertyHolder.getUserId())
					.asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject result) {
							// do stuff with the result or error
							if ( result != null ) {
								Util.logInfo(this.getClass().getName(), "sendRegistrationToServer >> " + result.toString());
							}
						}
					});
		}
	}

	public static String makeReportId() {
		Random mRandom = new Random();

		// I am removing potentially confusing characters 0, o, and O
		String[] digits = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
				"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
				"N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
				"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
				"m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

		/*
		 * I am giving the report IDs 4 digits using the set of 62 alphanumeric
		 * characters taking (capitalization into account). If we would receive
		 * 1000 reports, the probability of at least two ending up with the same
		 * random ID is about .03 (based on the Taylor approximation solution to
		 * the birthday paradox: 1- exp((-(1000^2))/((62^4)*2))). For 100
		 * reports, the probability is about .0003. Since each report is also
		 * linked to a unique userID, and since the only consequence of a double
		 * ID would be to make it harder for us to link a mailed sample to a
		 * report -- assuming the report with the double ID included a mailed
		 * sample -- this seems like a reasonable risk to take. We could reduce
		 * the probability by adding digits, but then it would be harder for
		 * users to record their report IDs.
		 * 
		 * UPDATE: I now removed 0 and o and O to avoid confusion, so the
		 * probabilities would need to be recalculated...
		 */

		return digits[mRandom.nextInt(58)] + digits[mRandom.nextInt(58)]
				+ digits[mRandom.nextInt(58)] + digits[mRandom.nextInt(58)];
	}

	public static Uri getReportsUri(Context context) {
		return Uri.parse("content://" + context.getResources().getString(R.string.content_provider_auth_reports) + "/" + ContProvReports.DATABASE_TABLE);
	}

	public static Uri getMissionsUri(Context context) {
		return Uri.parse("content://" + context.getResources().getString(
						R.string.content_provider_auth_missions) + "/" + ContProvMissions.DATABASE_TABLE);
	}

	public static Uri getTracksUri(Context context) {
		return Uri.parse("content://" + context.getResources().getString(
						R.string.content_provider_auth_tracks) + "/" + ContProvTracks.DATABASE_TABLE);
	}

	public static void buildCustomAlert(Context context, String message) {

		final Dialog dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_alert);
		dialog.setCancelable(false);

		TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
		alertText.setText(message);

		Button positive = (Button) dialog.findViewById(R.id.alertOK);
		Button negative = (Button) dialog.findViewById(R.id.alertCancel);
		negative.setVisibility(View.GONE);

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();
	}

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
               String[] proj = { MediaStore.Images.Media.DATA };
               Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
               int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
               cursor.moveToFirst();
               return cursor.getString(column_index);
    }
	
}
