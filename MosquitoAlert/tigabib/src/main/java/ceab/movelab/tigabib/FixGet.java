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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;

/**
 * Space Mapper's location recording service.
 * <p>
 * Dependencies: DriverMapActivity.java, TigerBroadcastReceiver.java,
 * FixGet.java, DriverMapActivity.java, SettingsActivity.java, Withdraw.java.
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
	private LocationListener gpsListener; // gps
	private LocationListener networkListener; // network

	Location bestLocation;

	WifiLock wifiLock;
	WakeLock wakeLock;

	Context context;

	public static String KEY_LAT = "extra_lat";
	public static String KEY_LON = "extra_lon";
	public static String KEY_TIME = "extra_time";
	public static String KEY_POWER = "extra_power";
	public static String KEY_TASK_FIX = "task_fix";

	private static final int ALARM_ID_STOP_FIX = -1123;

	boolean fixInProgress = false;

	boolean taskFix = false;

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

		wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MosquitTigreScreenDimWakeLock");

	}

	public void onStart(Intent intent, int startId) {

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		if (!PropertyHolder.hasConsented() || Util.privateMode(context)) {
			stopSelf();
		} else {

			String action = intent.getAction();

			if (action != null
					&& action.contains(Messages.stopFixAction(context))) {
				Util.logInfo(context, TAG, "stop fixget received");
				removeLocationUpdates();
				unWakeLock();
				if (bestLocation != null
						&& bestLocation.getAccuracy() < Util.MIN_ACCURACY) {
					useFix(context, bestLocation);
				}
				fixInProgress = false;
				stopSelf();
			} else {

				if (!fixInProgress) {
					fixInProgress = true;

					if (action != null
							&& action.contains(Messages.taskFixAction(context))) {
						taskFix = true;
					}

					Util.logInfo(context, TAG, "test");
					
					long thisWindow = taskFix ? Util.TASK_FIX_WINDOW : Util.LISTENER_WINDOW;

					AlarmManager alarmManager = (AlarmManager) context .getSystemService(Context.ALARM_SERVICE);
					int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

					Intent intent2StopFixGet = new Intent(context, FixGet.class);
					intent2StopFixGet.setAction(Messages.stopFixAction(context));

					alarmManager.set(alarmType,
							(SystemClock.elapsedRealtime() + thisWindow),
							PendingIntent.getService(context, ALARM_ID_STOP_FIX, intent2StopFixGet, 0));

					Util.logInfo(context, TAG, "set alarm to stop self at "
									+ Util.iso8601(System.currentTimeMillis() + thisWindow));

					// stopListening = null;
					bestLocation = null;

					gpsListener = null;
					networkListener = null;

					if (locationManager == null)
						locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

					if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						gpsListener = new mLocationListener();
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);

						// mGpsStatusListener= new GpsStatusListener();
						// locationManager.addGpsStatusListener(mGpsStatusListener);
					}

					if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
						networkListener = new mLocationListener();
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);

					}

					if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
							|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

						new CountDownTimer(thisWindow, (thisWindow / 2)) {

							public void onTick(long millisUntilFinished) {
								// nothing
							}

							public void onFinish() {
								fixInProgress = false;
								removeLocationUpdates();
								unWakeLock();

								if (bestLocation != null
										&& bestLocation.getAccuracy() < Util.MIN_ACCURACY) {
									useFix(context, bestLocation);
								}
								stopSelf();
							}
						}.start();

					}
				}
			}
		}
	};

	/**
	 * Destroy this FixGet service instance. Nothing else done.
	 */
	@Override
	public void onDestroy() {
		fixInProgress = false;
		removeLocationUpdates();
		unWakeLock();

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

					removeLocationUpdates();
					useFix(context, location);
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
			 * updates from both ceab.movelab.tigabib.providers and quit.
			 */
			if (status != LocationProvider.AVAILABLE) {
				removeLocationUpdate(provider);
				stopSelf();
			}
		}

	}

	// utilities
	private void removeLocationUpdates() {
		if (locationManager != null) {
			if (gpsListener != null)
				locationManager.removeUpdates(gpsListener);
			if (networkListener != null)
				locationManager.removeUpdates(networkListener);
		} else {
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			if (gpsListener != null)
				locationManager.removeUpdates(gpsListener);
			if (networkListener != null)
				locationManager.removeUpdates(networkListener);
		}
		gpsListener = null;
		networkListener = null;
		fixInProgress = false;
	}

	// utilities
	private void removeLocationUpdate(String provider) {
		if (locationManager != null) {
			if (provider == LocationManager.NETWORK_PROVIDER) {
				if (networkListener != null) {
					locationManager.removeUpdates(networkListener);
					networkListener = null;
				}
			} else {
				if (gpsListener != null) {
					locationManager.removeUpdates(gpsListener);
					gpsListener = null;
				}
			}
		} else {
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			if (provider == LocationManager.NETWORK_PROVIDER) {
				if (networkListener != null) {
					locationManager.removeUpdates(networkListener);
					networkListener = null;
				}
			} else {
				if (gpsListener != null) {
					locationManager.removeUpdates(gpsListener);
					gpsListener = null;
				}
			}
		}
	}

	private void useFix(Context context, Location location) {
		Util.logInfo(context, TAG, "useFix");
		Intent ufi = new Intent(context, FixUse.class);
		ufi.putExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_LAT),
				location.getLatitude());
		ufi.putExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_LON),
				location.getLongitude());
		ufi.putExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_TIME),
				location.getTime());
		ufi.putExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_POWER),
				Util.getBatteryProportion(context));
		ufi.putExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_TASK_FIX),
				taskFix);
		getApplication().startService(ufi);
		Util.logInfo(context, TAG, "just started fixusse");
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

}