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

import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import ceab.movlab.tigre.ContentProviderContractTracks.Fixes;

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

	public static final String NEW_FIX_RECORDED = "ceab.movlab.tigre.New_Fix_Recorded";
	public static final String FIX_STARTED = "Fix_Started";

	boolean gotLocation = false;
	Location bestLocation;
	Location bestGpsLocation;

	WifiLock wifiLock;
	WakeLock wakeLock;

	StopReceiver stopReceiver;
	IntentFilter stopFilter;

	boolean fixInProgress = false;

	private int minDist = 0;

	public int extraRuns = Util.EXTRARUNS;

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

		Context context = getApplicationContext();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		wifiLock = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE)).createWifiLock(
				WifiManager.WIFI_MODE_SCAN_ONLY, "MosquitTigreWifiLock");

		wakeLock = ((PowerManager) context
				.getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP,
				"MosquitTigreScreenDimWakeLock");

		minDist = Util.getMinDist(context);

	}

	public void onStart(Intent intent, int startId) {

		if (fixInProgress == false) {
			fixInProgress = true;
			stopFilter = new IntentFilter(
					TigerBroadcastReceiver.STOP_FIXGET_MESSAGE);
			stopReceiver = new StopReceiver();
			registerReceiver(stopReceiver, stopFilter);

			announceFixStarted();

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

		unregisterReceiver(stopReceiver);
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

				// if the location is within the optimum accuracy (for either
				// normal or long runs),
				// then use it and stop.
				if ((location.getAccuracy() <= Util.OPT_ACCURACY || (Util.missedFixes > 0 && location
						.getAccuracy() < Util.OPT_ACCURACY_LONGRUNS))) {

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
						// current and best location are gps, use for new bets
						// whichever is better
					} else if (location.getProvider().equals("gps")
							&& bestLocation.getProvider().equals("gps")
							&& location.getAccuracy() < bestLocation
									.getAccuracy()) {

						bestLocation = location;
						return;

						// if current location is gps and best location is
						// network,
						// use gps for new best if it is below the minimum gps
						// accuracy or better than current
					} else if (location.getProvider().equals("gps")
							&& bestLocation.getProvider().equals("network")
							&& (location.getAccuracy() <= Util.MIN_GPS_ACCURACY || location
									.getAccuracy() < bestLocation.getAccuracy())) {
						bestLocation = location;
						return;

						// if current location is network and best is network,
						// use
						// for new best whichever is better
					} else if (location.getProvider().equals("network")
							&& bestLocation.getProvider().equals("network")
							&& location.getAccuracy() < location.getAccuracy()) {

						bestLocation = location;
						return;

						// if current location is network and best is gps, use
						// current as new best if gps accuracy is above the
						// minimum threshhold or is better than network
					} else if (location.getProvider().equals("network")
							&& bestLocation.getProvider().equals("gps")
							&& (bestLocation.getAccuracy() > Util.MIN_GPS_ACCURACY || location
									.getAccuracy() < bestLocation.getAccuracy())) {

						bestLocation = location;
						return;
					} else
						// if none of these conditions are met, then return and
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

	private void announceFix(Location location, boolean newRecord) {

		// reset missed fixes counter to zero
		Util.missedFixes = 0;
		Date usertime = new Date(location.getTime());
		Util.lastFixTimeStamp = Util.userDate(usertime);
		Util.lastFixTime = location.getTime();
		Util.lastFixLat = location.getLatitude();
		Util.lastFixLon = location.getLongitude();

		// inform the main display
		Context context = getApplicationContext();
		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);
		Intent intent = new Intent(getResources().getString(
				R.string.internal_message_id)
				+ Util.MESSAGE_FIX_RECORDED);
		Bundle bundle = new Bundle();
		bundle.putBoolean(NEW_RECORD, newRecord);
		intent.putExtras(bundle);

		sendBroadcast(intent);
	}

	private void announceFixStarted() {

		Intent intent = new Intent(FIX_STARTED);
		sendBroadcast(intent);
		Util.lastFixStartedAt = SystemClock.elapsedRealtime();
	}

	private void useFix(Context context, Location location) {
		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);
		ContentResolver cr = getContentResolver();
		float dist = -1;
		Cursor c = cr.query(Fixes.CONTENT_URI, Fixes.KEYS_LATLON,
				Fixes.KEY_TRIPID + " = '" + PropertyHolder.getTripId() + "'",
				null, null);
		int id = -1;
		if (c.moveToLast()) {
			int latCol = c.getColumnIndexOrThrow(Fixes.KEY_LATITUDE);
			int lonCol = c.getColumnIndexOrThrow(Fixes.KEY_LONGITUDE);
			int idCol = c.getColumnIndexOrThrow(Fixes.KEY_ROWID);
			float[] distResult = new float[1];
			id = c.getInt(idCol);
			Location.distanceBetween(c.getDouble(latCol), c.getDouble(lonCol),
					location.getLatitude(), location.getLongitude(), distResult);
			dist = distResult[0];
		}
		c.close();
		if (dist > minDist || dist == -1) {
			// create new entry with time and sdtime both set to the
			// location time. (We will update sdtime in next chunk of code
			// if the person
			// stays in same 50 m radius.).
			cr.insert(Fixes.CONTENT_URI, ContentProviderValuesTracks.createFix(
					PropertyHolder.getTripId(), location,
					Util.getBatteryLevel(context), location.getTime(),
					Fixes.DISPLAY_TRUE));
			announceFix(location, true);
		} else if (id > 0) {
			// update the prior location with a new station departure time so
			// that this can be used in the user display. Note this will not be
			// sent to the server since this prior locaiton is already marked as
			// uploaded.
			ContentValues cv = new ContentValues();
			String sc = Fixes.KEY_ROWID + " = " + id;
			cv.put(Fixes.KEY_STATION_DEPARTURE_TIMELONG, location.getTime());
			cr.update(Fixes.CONTENT_URI, cv, sc, null);

			// now create a new record to be uploaded to the server (but not
			// displayed on the user map, so as to avoid too many points on the
			// map)
			cr.insert(Fixes.CONTENT_URI, ContentProviderValuesTracks.createFix(
					PropertyHolder.getTripId(), location,
					Util.getBatteryLevel(context), location.getTime(),
					Fixes.DISPLAY_FALSE));

			announceFix(location, false);
		}
		Intent uploaderIntent = new Intent(FixGet.this, FileUploader.class);
		startService(uploaderIntent);
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

			if (extraRuns > 0
					&& Util.missedFixes > 0
					&& Util.missedFixes % 10 == 1
					&& locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

				// turn on screen to get network location woken up
				wakeUpAndWakeLock();

				// Check if online and if so try to inject new XTRA into GPS for
				// first extra run
				if (extraRuns == Util.EXTRARUNS) {

					NetworkInfo netInfo = ((ConnectivityManager) context
							.getSystemService(Context.CONNECTIVITY_SERVICE))
							.getActiveNetworkInfo();

					if (netInfo != null && netInfo.isConnected()) {
						injectNewXTRA();

					}
				}

				extraRuns = extraRuns - 1;
			} else {

				extraRuns = Util.EXTRARUNS;

				// stop both listeners if running
				if (locationListener1 != null) {
					locationManager.removeUpdates(locationListener1);
					// Log.e(TAG, "gps listener stopped by timer");
				}
				if (locationListener2 != null) {
					locationManager.removeUpdates(locationListener2);
					// Log.e(TAG, "network listener stopped by timer");
				}
				// use best location if one exists and if it is below minimum
				// threshhold
				if (bestLocation != null
						&& bestLocation.getAccuracy() < Util.MIN_ACCURACY) {

					useFix(context, bestLocation);

				} else {

					if (locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						Util.missedFixes = Util.missedFixes + 1;
					}
				}

				// locationManager = null;
				fixInProgress = false;
				stopSelf();

			}

		}

	}

}