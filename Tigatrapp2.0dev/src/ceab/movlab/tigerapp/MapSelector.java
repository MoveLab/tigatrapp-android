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
 */

package ceab.movlab.tigerapp;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ceab.movelab.tigerapp.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Allows user to view their own data in a map (from the database on their phone
 * -- not from the server).
 * 
 * @author John R.B. Palmer
 * 
 * 
 */
public class MapSelector extends MapActivity {
	String TAG = "Map Selector";

	private Vibrator mVib;

	private MapView mapView;
	private MapController myMapController;

	MapOverlay mainOverlay;
	private List<Overlay> mapOverlays;

	static boolean satToggle;

	Context context;

	Button mOKB;
	Button mCancelB;

	LocationManager locationManager;
	LocationListener locationListener1;
	LocationListener locationListener2;
	Location currentLocation;

	public static final String LAT = "lat";
	public static final String LON = "lon";
	Resources res;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);
		if (PropertyHolder.getLanguage() == null) {
			Intent i2sb = new Intent(MapSelector.this, Switchboard.class);
			startActivity(i2sb);
			finish();
		} else {
			res = getResources();
			Util.setDisplayLanguage(res);
		}

		setContentView(R.layout.map_selector_dialog);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		satToggle = true;
		mapView.setSatellite(satToggle);

		myMapController = mapView.getController();
		myMapController.setCenter(Util.CEAB_COORDINATES);

		myMapController.setZoom(15);

		mOKB = (Button) findViewById(R.id.selectWhereOk);
		mCancelB = (Button) findViewById(R.id.selectWhereCancel);

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onResume() {

		context = getApplicationContext();

		mapOverlays = mapView.getOverlays();

		mainOverlay = new MapOverlay();

		mapOverlays.clear();

		mapOverlays.add(mainOverlay);
		mapView.postInvalidate();

		mOKB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent dataForReport = new Intent();
				if (mainOverlay.getSelectedLatLon() != null) {
					dataForReport.putExtra(LAT, mainOverlay.getSelectedLatLon()
							.getLatitudeE6() / 1E6);
					dataForReport.putExtra(LON, mainOverlay.getSelectedLatLon()
							.getLongitudeE6() / 1E6);
				}
				setResult(RESULT_OK, dataForReport);

				finish();
			}

		});

		mCancelB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent dataForReport = new Intent();
				setResult(RESULT_CANCELED, dataForReport);
				finish();
			}

		});

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationListener1 = new mLocationListener();
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener1);
			// Log.e(TAG, "gps listener started");
		}

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationListener2 = new mLocationListener();
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
			// Log.e(TAG, "network listener started");

		}

		super.onResume();

		res = getResources();
		Util.setDisplayLanguage(res);

	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	static final private int TOGGLE_VIEW = Menu.FIRST + 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, TOGGLE_VIEW, Menu.NONE, R.string.menu_toggle_view);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (TOGGLE_VIEW): {
			satToggle = !satToggle;
			mapView.setSatellite(satToggle);

			return true;
		}
		}
		return false;
	}

	class MapOverlay extends Overlay {

		private Bitmap fixPin;

		private GeoPoint selectedPoint;

		MapOverlay() {

			fixPin = BitmapFactory.decodeResource(getResources(),
					R.drawable.seek_thumb_pressed);
		}

		public GeoPoint getSelectedLatLon() {

			return selectedPoint;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);

			if (selectedPoint != null) {
				Point screenPoint = new Point();
				mapView.getProjection().toPixels(selectedPoint, screenPoint);

				canvas.drawBitmap(fixPin,
						screenPoint.x - fixPin.getWidth() / 2, screenPoint.y
								- fixPin.getHeight() / 2, null);
			}
			return;

		}

		@Override
		public boolean onTouchEvent(MotionEvent evt, MapView mapView) {
			super.onTouchEvent(evt, mapView);
			removeLocationUpdate("gps");
			removeLocationUpdate("network");
			return false;

		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapview) {

			removeLocationUpdate("gps");
			removeLocationUpdate("network");
			selectedPoint = new GeoPoint(p.getLatitudeE6(), p.getLongitudeE6());
			return true;

		}

	}

	private class mLocationListener implements LocationListener {

		/**
		 * Defines LocationListener behavior upon reception of a location fix
		 * update from the LocationManager.
		 */
		public void onLocationChanged(Location location) {

			// Quick return if given location is null or has an invalid time
			if (location == null || location.getTime() < 0) {

				return;
			} else {
				if (currentLocation == null
						|| (currentLocation != null && location.getAccuracy() < currentLocation
								.getAccuracy())) {

					currentLocation = location;

				}

				if (location.getAccuracy() < 5000) {
					removeLocationUpdate("gps");
					removeLocationUpdate("network");

					Double geoLat = currentLocation.getLatitude() * 1E6;
					Double geoLon = currentLocation.getLongitude() * 1E6;
					GeoPoint center = new GeoPoint(geoLat.intValue(),
							geoLon.intValue());
					myMapController.animateTo(center);

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
			}
		}

	}

	// utilities
	private void removeLocationUpdate(String provider) {
		LocationListener listener = provider.equals("gps") ? locationListener1
				: locationListener2;
		if (locationManager != null && listener != null)
			locationManager.removeUpdates(listener);
	}

}