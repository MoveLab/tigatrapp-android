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

package ceab.movlab.tigerapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;
import ceab.movlab.tigerapp.ContentProviderContractTracks.Fixes;

/**
 * Space Mapper's location recording service.
 * <p>
 * Dependencies: DriverMapActivity.java, TigerBroadcastReceiver.java,
 * FixGet.java, DriverMapActivity.java, Settings.java, Withdraw.java.
 * <p>
 * This class is a modified version of the one used in the Human Mobility
 * Project.
 * 
 * @author Chang Y. Chung
 * @author Necati E. Ozgencil
 * @author Kathleen Li
 * @author John R.B. Palmer
 */
public class FixGet extends Service {
	private static final String TAG = "FixGet";
	private LocationManager locationManager;
	private LocationListener locationListener1; // gps
	private LocationListener locationListener2; // network

	public static String NEW_RECORD = "newRecord";

	GpsStatusListener mGpsStatusListener;

	public static final String NEW_FIX_RECORDED = "ceab.movelab.tigerapp.New_Fix_Recorded";
	public static final String FIX_STARTED = "Fix_Started";

	boolean gotLocation = false;
	Location bestLocation;
	Location bestGpsLocation;

	WifiLock wifiLock;
	WakeLock wakeLock;

	StopReceiver stopReceiver;
	IntentFilter stopFilter;
	Context context;

	boolean fixInProgress = false;

	/**
	 * Creates a new FixGet service instance.<br>
	 * Begins location recording process. Creates a location manager and two
	 * location listeners. Begins requesting updates from both the GPS and
	 * network services, with one location listener receiving updates from one
	 * provider.
	 * <p>
	 * If either provider is unavailable, no updates will ever be returned to
	 * the corresponding location listener.
	 */
	@Override
	public void onCreate() {

		context = getApplicationContext();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		wifiLock = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE)).createWifiLock(
				WifiManager.WIFI_MODE_SCAN_ONLY, "MosquitTigreWifiLock");

		wakeLock = ((PowerManager) context
				.getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP,
				"MosquitTigreScreenDimWakeLock");

	}

	public void onStart(Intent intent, int startId) {

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);
		if (!PropertyHolder.hasConsented() || PropertyHolder.getLanguage() == null) {
			stopSelf();
		}

		if (fixInProgress == false) {
			fixInProgress = true;
			stopFilter = new IntentFilter(
					TigerBroadcastReceiver.STOP_FIXGET_MESSAGE);
			stopReceiver = new StopReceiver();
			registerReceiver(stopReceiver, stopFilter);

			// stopListening = null;
			bestLocation = null;

			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

				if (Util.flushGPSFlag == true) {

					clearGPS();
					injectNewXTRA();
					Util.flushGPSFlag = false;
				}

				locationListener1 = new mLocationListener();
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, locationListener1);

				// mGpsStatusListener= new GpsStatusListener();
				// locationManager.addGpsStatusListener(mGpsStatusListener);

			}

			if (locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locationListener2 = new mLocationListener();
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListener2);

			}

		}
	};

	/**
	 * Destroy this FixGet service instance. Nothing else done.
	 */
	@Override
	public void onDestroy() {
		removeLocationUpdate("gps");

		try {
			unregisterReceiver(stopReceiver);
		} catch (IllegalArgumentException e) {

		}

		removeLocationUpdate("network");

		unWakeLock();

		locationListener1 = null;
		locationListener2 = null;
		locationManager = null;
		fixInProgress = false;

	}

	/**
	 * Returns Object that receives client interactions.
	 * 
	 * @return The Object that receives interactions from clients.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients.
	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};

	private class GpsStatusListener implements GpsStatus.Listener {

		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Inner class to listen to LocationManager. <br>
	 * Defines LocationListener behavior.
	 */
	private class mLocationListener implements LocationListener {

		/**
		 * Defines LocationListener behavior upon reception of a location fix
		 * update from the LocationManager.
		 */
		public void onLocationChanged(Location location) {

			Context context = getApplicationContext();

			// Quick return if given location is null or has an invalid time
			if (location == null || location.getTime() < 0) {

				return;
			} else {

				// if the location is within the optimum accuracy
				// then use it and stop.
				if ((location.getAccuracy() <= Util.OPT_ACCURACY)) {

					removeLocationUpdate("gps");
					removeLocationUpdate("network");
					useFix(context, location);
					fixInProgress = false;

					stopSelf();
				} else {

					// if no best location set yet, current location is best
					if (bestLocation == null) {

						bestLocation = location;
						return;
					} else if (location.getAccuracy() < bestLocation
							.getAccuracy()) {

						bestLocation = location;
						return;
					} else
						// if conditions not met, then return and
						// keep trying

						return;
				}
			}
		}

		/**
		 * Defines behavior when the given provider is disabled.
		 * 
		 * @param provider
		 *            The provider to be disabled
		 */
		public void onProviderDisabled(String provider) {
			removeLocationUpdate(provider);
			if (locationListener1 == null && locationListener2 == null)
				fixInProgress = false;
			stopSelf();
		}

		/**
		 * Defines behavior when the given provider is re-enabled. Currently no
		 * behavior is defined.
		 * 
		 * @param provider
		 *            The provider to be re-enabled
		 */
		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			/*
			 * If provider service is no longer available, stop trying to get
			 * updates from both providers and quit.
			 */
			if (status == LocationProvider.OUT_OF_SERVICE) {
				removeLocationUpdate(provider);
				if (locationListener1 == null && locationListener2 == null)
					fixInProgress = false;

				stopSelf();
			}
		}

	}

	// utilities
	private void removeLocationUpdate(String provider) {
		LocationListener listener = provider.equals("gps") ? locationListener1
				: locationListener2;
		if (listener != null)
			locationManager.removeUpdates(listener);
	}

	private void useFix(Context context, Location location) {
		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);
		ContentResolver cr = getContentResolver();

		double thisLat = location.getLatitude();
		double maskedLat = Math.floor(thisLat / Util.latMask) * Util.latMask;
		double thisLon = location.getLongitude();
		double maskedLon = Math.floor(thisLon / Util.lonMask) * Util.lonMask;

		Log.i("FG", "thisLat: " + thisLat);
		Log.i("FG", "thisLon: " + thisLon);
		Log.i("FG", "maskedLat: " + maskedLat);
		Log.i("FG", "maskedLon: " + maskedLon);

		cr.insert(Fixes.CONTENT_URI, ContentProviderValuesTracks.createFix(
				maskedLat, maskedLon, location.getTime(),
				Util.getBatteryLevel(context)));

		// Check for location-based tasks

		int thisHour = Util.hour(location.getTime());

		Log.i("FG", "thisHour: " + thisHour);

		String sc1 = Tasks.KEY_LOCATION_TRIGGERS_JSON + " IS NOT NULL AND "
				+ Tasks.KEY_ACTIVE + " = 0 AND " + Tasks.KEY_DONE + " = 0 AND "
				+ Tasks.KEY_EXPIRATION_DATE + " >= "
				+ System.currentTimeMillis();

		Log.i("FG", sc1);

		// grab tasks that have location triggers, that are not yet active, that
		// are not yet done, and that have not expired
		Cursor c = cr.query(Tasks.CONTENT_URI, Tasks.KEYS_TRIGGERS, sc1, null,
				null);

		while (c.moveToNext()) {

			try {
				JSONArray theseTriggers = new JSONArray(
						c.getString(c
								.getColumnIndexOrThrow(Tasks.KEY_LOCATION_TRIGGERS_JSON)));

				for (int i = 0; i < theseTriggers.length(); i++) {

					JSONObject thisTrigger = theseTriggers.getJSONObject(i);

					Log.i("FG", "thisTrigger: " + thisTrigger.toString());
					Log.i("FG", ""
							+ (maskedLat == thisTrigger.getDouble("lat")));
					Log.i("FG", ""
							+ (maskedLon == thisTrigger.getDouble("lon")));
					Log.i("FG",
							"" + (thisHour >= thisTrigger.getInt("start_hour")));
					Log.i("FG",
							"" + (thisHour <= thisTrigger.getInt("end_hour")));

					if (maskedLat == thisTrigger.getDouble("lat")
							&& maskedLon == thisTrigger.getDouble("lon")
							&& thisHour >= thisTrigger.getInt("start_hour")
							&& thisHour <= thisTrigger.getInt("end_hour")) {

						Log.i("FG", "task triggered");

						ContentValues cv = new ContentValues();
						int rowId = c.getInt(c
								.getColumnIndexOrThrow(Tasks.KEY_ROW_ID));
						String sc = Tasks.KEY_ROW_ID + " = " + rowId;
						cv.put(Tasks.KEY_ACTIVE, 1);
						cr.update(Tasks.CONTENT_URI, cv, sc, null);

						Intent intent = new Intent(
								TigerBroadcastReceiver.TIGER_TASK_MESSAGE);
						intent.putExtra(Tasks.KEY_TASK_HEADING, c.getString(c
								.getColumnIndexOrThrow(Tasks.KEY_TASK_HEADING)));
						context.sendBroadcast(intent);
					}
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// TODO UPLOAD

		unWakeLock();
	}

	public void wakeUpAndWakeLock() {

		if (!wifiLock.isHeld()) {

			try {
				wifiLock.acquire();

			} catch (Exception e) {
			}

		}
		if (!wakeLock.isHeld()) {

			try {
				wakeLock.acquire();

			} catch (Exception e) {
			}

		}
	}

	public void unWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();

		}
		if (wifiLock != null && wifiLock.isHeld()) {
			wifiLock.release();

		}
	}

	public void injectNewXTRA() {
		Bundle bundle = new Bundle();
		locationManager.sendExtraCommand("gps", "force_xtra_injection", bundle);
		locationManager.sendExtraCommand("gps", "force_time_injection", bundle);

	}

	public void clearGPS() {
		locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,
				"delete_aiding_data", null);
	}

	public class StopReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			removeLocationUpdate("gps");
			removeLocationUpdate("network");
			try {
				unregisterReceiver(stopReceiver);
			} catch (IllegalArgumentException e) {

			}
			unWakeLock();
			locationListener1 = null;
			locationListener2 = null;
			locationManager = null;
			fixInProgress = false;
			if (bestLocation != null
					&& bestLocation.getAccuracy() < Util.MIN_ACCURACY) {
				useFix(context, bestLocation);
			}
			stopSelf();
		}

	}

}