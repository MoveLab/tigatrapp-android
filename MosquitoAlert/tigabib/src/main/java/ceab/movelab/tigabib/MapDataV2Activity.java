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
 */

package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ceab.movelab.tigabib.ContProvContractReports.Reports;
import ceab.movelab.tigabib.model.NearbyReport;

/**
 * Allows user to view their own reports in a map (from the database on their phone)
 * and neighbours' reports from the server.
 * 
 * @author Màrius Garcia
 * 
 * 
 */
public class MapDataV2Activity extends FragmentActivity implements OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener  {
	private static final String TAG = "MapDataV2Activity";

	private static String lang;

	private static final int REQUEST_GOOGLE_PLAY_SERVICES = 999;

	private static final float ADULT_COLOR_HUE = 5.0f;
//	private static final float SITE_COLOR_HUE = 244.0f;
	private static final float NEARBY_COLOR_HUE = 36.0f; //190.0f;
//	int ADULT_COLOR_V1 = 0xffd95f02; // 217, 95, 2	// 26.0f
//	int SITE_COLOR_V1 = 0xff7570b3;	// 117, 112, 179 // 244.0f

	private static final int NEARBY_RADIUS = 5000;

	private RelativeLayout legendLayout;
	private ProgressBar progressbar;
	private boolean loadingData;

	private static boolean satToggle;

	// Provides the entry point to Google Play services.
	private boolean mGoogleServicesOk = false;
	private GoogleApiClient mGoogleApiClient;
	private GoogleMap mGoogleMap;
	private SupportMapFragment mMapFragment;
	private Map<Marker, MyMarkerItem> markerMap = new HashMap<>();
	// Represents a geographical location.
	private Location mLastLocation;
	private Location mLastLocationNeighbours;
	private LatLng currentCenter;

	//private FirebaseAnalytics mFirebaseAnalytics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());
		checkGoogleApiAvailability();

		// Obtain the FirebaseAnalytics instance
		//mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
	}

	private void onCreateContinue() {
		createGoogleApi();

		setContentView(R.layout.map_layout_v2);
		setTitle(getResources().getString(R.string.activity_label_map));

		mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview));
		// Loading map
		initializeMap();

		satToggle = false;

		legendLayout = (RelativeLayout)  findViewById(R.id.mapLegend);
		progressbar = (ProgressBar) findViewById(R.id.mapProgressbar);
		progressbar.setProgress(0);

		/*ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
				@Override
				public void onMenuVisibilityChanged(boolean isVisible) {
					if (isVisible) {
						// menu expanded
						Bundle bundle = new Bundle();
						mFirebaseAnalytics.logEvent("ma_evt_open_menu_map", bundle);
					} else {
						// menu collapsed
					}
				}
			});
		}*/
	}

	//http://stackoverflow.com/questions/31016722/googleplayservicesutil-vs-googleapiavailability
	private void checkGoogleApiAvailability() {
		GoogleApiAvailability api = GoogleApiAvailability.getInstance();
		int code = api.isGooglePlayServicesAvailable(this);
		if ( code == ConnectionResult.SUCCESS ) {
			onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
		} else if ( api.isUserResolvableError(code) &&
				api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES)) {
			// wait for onActivityResult call (see below)
		} else {
			Toast.makeText(this, api.getErrorString(code), Toast.LENGTH_LONG).show();
		}
	}

	protected void createGoogleApi() {
		// Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage functionality,
		// which automatically sets up the API client to handle Activity lifecycle events.
		// If your activity does not extend FragmentActivity, make sure to call connect() and disconnect() explicitly.
		if ( mGoogleApiClient == null ) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.enableAutoManage(this, 0, this)
					.build();
		}
	}


	@Override
	public void onConnected(Bundle connectionHint) {
Util.logInfo(TAG, "GMS: Connected to GoogleApiClient");
		// If the initial location was never previously requested, we use FusedLocationApi.getLastLocation() to get it.
		if ( mLastLocation == null ) {
			try {
				mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
				if ( mLastLocation != null && mGoogleMap != null ) {
					loadNeighbours(mLastLocation, NEARBY_RADIUS);
					LatLng myLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
					mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13), 250, null);
				}
			}
			catch (SecurityException se) {
				se.printStackTrace();
			}
		}
		startLocationUpdates();
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection to Google Play services was lost for some reason. We call connect() to attempt to re-establish the connection.
Util.logInfo(TAG, "GMS: Connection suspended");
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult result) {
		// Refer to the javadoc for ConnectionResult to see what error codes might be returned in onConnectionFailed.
Util.logInfo(TAG, "GMS: Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
	}

	public LocationRequest getLocationRequest() {
		return LocationRequestFactory.getLocationRequest();
	}

	protected void startLocationUpdates() {
Util.logInfo(TAG, "startLocationUpdates out");
		if ( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
Util.logInfo(TAG, "startLocationUpdates " + mGoogleApiClient);
			try {
				LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(), this)
						.setResultCallback(new ResultCallback<Status>() {
							@Override
							public void onResult(Status status) {
Util.logInfo(TAG, "GMS: startLocationUpdates, onResult");
							}
						});
			}
			catch (SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	/**
	 * Callback that fires when the location changes.
	 */
	@Override
	public void onLocationChanged(Location location) {
Util.logInfo(TAG, "GMS: onLocationChanged");
		mLastLocation = location;
		loadNeighbours(location, NEARBY_RADIUS);
		LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13), 250, null);

		if ( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 */
	private void initializeMap() {
		if ( mGoogleMap == null ) {
			mMapFragment.getMapAsync(this);
		}
	}

	private void setMapType() {
		if ( mGoogleMap != null )
			mGoogleMap.setMapType(satToggle ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_NORMAL);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mGoogleMap = googleMap;
		try {
			mGoogleMap.setMyLocationEnabled(true);
		} catch (SecurityException se) {
			se.printStackTrace();
		}

		setMapType();
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
		mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
		mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
		mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
		mGoogleMap.getUiSettings().setCompassEnabled(false);
		mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

		//GeoPoint myCenterPoint = currentCenter != null ? currentCenter : Util.CEAB_COORDINATES;
		//LatLng myLatLng = new LatLng(Util.CEAB_COORDINATES.getLatitudeE6() / 1E6, Util.CEAB_COORDINATES.getLongitudeE6() / 1E6);
		//mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(15);
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Util.CEAB_COORDINATES, 12));

/*		Testing markers ********************
		mGoogleMap.addMarker(new MarkerOptions()
				.position(myLatLng)
				.title("title")
				.snippet("my snippet")
				.icon(BitmapDescriptorFactory.defaultMarker(ADULT_COLOR_HUE)));
		LatLng myLatLng2 = new LatLng((myCenterPoint.getLatitudeE6() / 1E6)+0.032, (myCenterPoint.getLongitudeE6() / 1E6)+0.041);
		mGoogleMap.addMarker(new MarkerOptions()
				.position(myLatLng2)
				.title("title")
				.snippet("my snippet 222")
				.icon(BitmapDescriptorFactory.defaultMarker(SITE_COLOR_HUE)));*/

		mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				onTap(marker);
				return false;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		switch (requestCode) {
			case REQUEST_GOOGLE_PLAY_SERVICES:
					if (resultCode == Activity.RESULT_OK) {
						mGoogleServicesOk = true;
						onCreateContinue();
					}
					break;
			case 1: if (resultCode == Activity.RESULT_OK) {
						finish();
					}
					break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();

		if ( !Util.setDisplayLanguage(getResources()).equals(lang) ) {
			finish();
			startActivity(getIntent());
		}

		// [START set_current_screen]
		//mFirebaseAnalytics.setCurrentScreen(this, "ma_scr_map_data", "Map Data");
		// [END set_current_screen]

		if ( mGoogleServicesOk ) {
			setMapType();
			progressbar.setVisibility(View.VISIBLE);
			legendLayout.setVisibility(View.GONE);

			if ( !loadingData ) {
				loadingData = true;
				if ( mGoogleMap != null ) mGoogleMap.clear();
				new DataGrabberTask().execute(this);
			}
			if ( mLastLocationNeighbours != null ) loadNeighbours(mLastLocationNeighbours, NEARBY_RADIUS);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.view_data_menu, menu);
		Util.setMenuTextColor(menu);

		MenuItem mSat = menu.findItem(R.id.sat);
		MenuItem mStreet = menu.findItem(R.id.street);
		if ( satToggle )
			mSat.setChecked(true);
		else
			mStreet.setChecked(true);

		return true;
	}

/*	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if ( featureId == Window.FEATURE_ACTION_BAR ) {
			// Send Firebase Event
			Bundle bundle = new Bundle();
			mFirebaseAnalytics.logEvent("ma_evt_open_menu_map", bundle);
		}
		//
		else if ( featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && menu != null ){
			// Send Firebase Event
			Bundle bundle = new Bundle();
			mFirebaseAnalytics.logEvent("ma_evt_open_menu_map", bundle);
		}
		return super.onMenuOpened(featureId, menu);
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		Bundle bundle = new Bundle();
		if (item.getItemId() == R.id.language) {
			Intent i = new Intent(this, LanguageSelectorActivity.class);
			startActivity(i);
			/*// Send Firebase Event
			bundle.putString(FirebaseAnalytics.Param.SOURCE, "Map");
			mFirebaseAnalytics.logEvent("ma_evt_language_change", bundle);*/
			return true;
		} else if (item.getItemId() == R.id.sat) {
			item.setChecked(true);
			satToggle = true;
			setMapType();
			//mFirebaseAnalytics.logEvent("ma_evt_map_type", bundle);
			return true;
		} else if (item.getItemId() == R.id.street) {
			item.setChecked(true);
			satToggle = false;
			setMapType();
			//mFirebaseAnalytics.logEvent("ma_evt_map_type", bundle);
			return true;
		} else if (item.getItemId() == R.id.saveMap) {
			saveMapImage();
			//mFirebaseAnalytics.logEvent("ma_evt_map_save", bundle);
			return true;
		} else if (item.getItemId() == R.id.shareMap) {
			shareMap();
			//mFirebaseAnalytics.logEvent("ma_evt_map_share", bundle);
			return true;
		}

		return false;
	}

	private void onTap(final Marker myMarker) {
		final MyMarkerItem item = markerMap.get(myMarker);

		if ( item != null ) {
			Intent i = new Intent(this, ViewReportsTab.class);

			if (item.getPhotoUris() != null) {
				i.putExtra(Reports.KEY_PHOTO_URIS, item.getPhotoUris());
			}
			if (item.getResponses() != null) {
				i.putExtra(Reports.KEY_CONFIRMATION, item.getResponses());
			}
			if (item.getReportId() != null) {
				i.putExtra(Reports.KEY_REPORT_ID, item.getReportId());
			}
			if (item.getType() != Report.MISSING) {
				i.putExtra(Reports.KEY_TYPE, item.getType());
			}
			if (item.getSnippet() != null) {
				i.putExtra(Reports.KEY_NOTE, item.getSnippet());
			}
			if (item.getTitle() != null) {
				i.putExtra("title", item.getTitle());
			}
			i.putExtra("report_time", item.getReportTime());

			startActivity(i);
		}
	}

 	private void loadNeighbours(final Location myLocation, int radius) {

		if ( myLocation != null ) {
			//mGoogleMap.getCameraPosition().target // to get center of the map
			String nearbyUrl = Util.URL_TIGASERVER_API_ROOT + Util.API_NEARBY_REPORTS + "?format=json" +
					"&lat=" + myLocation.getLatitude() + "&lon=" + myLocation.getLongitude() + "&radius=" + radius;
			//nearbyUrl = Util.URL_TIGASERVER_API_ROOT + Util.API_NEARBY_REPORTS + "?format=json&lat=41.1&lon=2.12&radius=" + radius;
Util.logInfo(TAG, nearbyUrl);

			Ion.with(this)
				.load(nearbyUrl)
				.setHeader("Accept", "application/json")
				.setHeader("Content-type", "application/json")
				.setHeader("Authorization", Util.TIGASERVER_AUTHORIZATION)
				.as(new TypeToken<List<NearbyReport>>() {})
				.setCallback(new FutureCallback<List<NearbyReport>>() {
					@Override
					public void onCompleted(Exception e, List<NearbyReport> nearbyResults) {
						// do stuff with the result or error
						if ( nearbyResults != null ) {
Util.logInfo(TAG, nearbyResults.toString());
							LatLngBounds.Builder builder = new LatLngBounds.Builder();
							builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
							for ( NearbyReport nbr : nearbyResults ) {
								LatLng pointLatLng = new LatLng(nbr.getLat(), nbr.getLon());
								String myTitle = nbr.getSimplifiedAnnotation().getClassification();
								try {
									myTitle = getResources().getString(
											getResources().getIdentifier("tag_to_map_points_of_citizens_"+myTitle, "string",
											MapDataV2Activity.this.getPackageName()));
								}
								catch (Resources.NotFoundException e2) {
									e2.printStackTrace();
								}
								Marker marker = mGoogleMap.addMarker(new MarkerOptions()
									.position(pointLatLng)
									.title(myTitle)
									.icon(BitmapDescriptorFactory.defaultMarker(NEARBY_COLOR_HUE)));

								builder.include(marker.getPosition());
							}
							// https://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
							LatLngBounds bounds = builder.build();
							int padding = 20; // offset from edges of the map in pixels
							mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding), 600, null);
						}
					}
				});
			mLastLocationNeighbours = myLocation;
		}
	}

	/*
	* Draw selected locations on map. Returns true if drawn; false otherwise.
	*/
	public boolean drawFixes(ArrayList<MyMarkerItem> myAdultReports, ArrayList<MyMarkerItem> mySiteReports,
							 boolean clearMapOverlays, boolean recenter) {
		if ( mGoogleMap != null ) {
			// Clear any existing overlays if clearMapOverlays set to true
			if ( clearMapOverlays ) mGoogleMap.clear();

			if ( myAdultReports != null && myAdultReports.size() > 0 ) {
				for ( MyMarkerItem oli : myAdultReports ) {
					//LatLng pointLatLng = new LatLng(oli.getPoint().getLatitudeE6() / 1E6, oli.getPoint().getLongitudeE6() / 1E6);
					Marker marker = mGoogleMap.addMarker(new MarkerOptions()
							.position(oli.getPosition())
							.title(oli.getTitle())
							.snippet(oli.getSnippet())
							.icon(BitmapDescriptorFactory.defaultMarker(ADULT_COLOR_HUE)));
					markerMap.put(marker, oli);
				}
			}

			if ( mySiteReports != null && mySiteReports.size() > 0 ) {
				for (MyMarkerItem oli : mySiteReports) {
					//LatLng pointLatLng = new LatLng(oli.getPoint().getLatitudeE6() / 1E6, oli.getPoint().getLongitudeE6() / 1E6);
					Marker marker = mGoogleMap.addMarker(new MarkerOptions()
							.position(oli.getPosition())
							.title(oli.getTitle())
							.snippet(oli.getSnippet())
							.icon(BitmapDescriptorFactory.defaultMarker(ADULT_COLOR_HUE)));
					markerMap.put(marker, oli);
				}
			}

			if ( recenter && currentCenter != null ) {
				//LatLng myLatLng = new LatLng(currentCenter.getLatitudeE6() / 1E6, currentCenter.getLongitudeE6() / 1E6);
				LatLng myLatLng = currentCenter;
				mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13), 200, null);
				Location myLocation = new Location("MyLocation");
				myLocation.setLatitude(myLatLng.latitude);
				myLocation.setLongitude(myLatLng.longitude);
				loadNeighbours(myLocation, NEARBY_RADIUS);
			}
		}

		return true;
	}

	/*
	 * Draw selected locations on map. Returns true if drawn; false otherwise.
	 *
	public boolean drawFixesV1(ArrayList<MyOverlayItem> myAdultReports,
			ArrayList<MyOverlayItem> mySiteReports, boolean clearMapOverlays,
			boolean recenter) {

		// get mapview overlays
		mapOverlays = mapView.getOverlays();

		// Clear any existing overlays if cleraMapOverlays set to true
		if (clearMapOverlays)
			mapOverlays.clear();

		if (myAdultReports != null && myAdultReports.size() > 0) {
			// Drawable drawable = this.getResources().getDrawable(
			// R.drawable.report_icon_yellow);
			MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(adultMarker, this);
			for (MyOverlayItem oli : myAdultReports) {
				itemizedoverlay.addOverlay(oli);
			}
			itemizedoverlay.populateNow();
			mapOverlays.add(itemizedoverlay);
		}

		if (mySiteReports != null && mySiteReports.size() > 0) {
			MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(siteMarker, this);
			for (MyOverlayItem oli : mySiteReports) {
				itemizedoverlay.addOverlay(oli);
			}
			itemizedoverlay.populateNow();
			mapOverlays.add(itemizedoverlay);
		}

		if (recenter && currentCenter != null)
			myMapController.animateTo(currentCenter);

		mapView.postInvalidate();

		return true;
	}*/

/*	private Bitmap getMapImageV1() {
		mapView.setDrawingCacheEnabled(true);
		Bitmap bmp = Bitmap.createBitmap(mapView.getDrawingCache());
		mapView.setDrawingCacheEnabled(false);
		return bmp;
	}*/

    private File getDirectory() {
        File root = Environment.getExternalStorageDirectory();
        File directory = new File(root, getResources().getString(R.string.app_directory));
        directory.mkdirs();
		return directory;
    }

    private String getFilename() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        //Date date = new Date();
        String stringDate = dateFormat.format(new Date());
        return getResources().getString(R.string.saved_image_prefix) + stringDate + ".jpg";
    }

	GoogleMap.SnapshotReadyCallback snapshotSaveMap = new GoogleMap.SnapshotReadyCallback() {
		@Override
		public void onSnapshotReady(Bitmap snapshot) {
			try {
                File directory = getDirectory();
                String filename = getFilename();
				if ( directory.canWrite() ) {
					File f = new File(directory, filename);
					FileOutputStream out = new FileOutputStream(f);
					snapshot.compress(Bitmap.CompressFormat.JPEG, 95, out);
					out.close();

					MediaScannerConnection.scanFile(MapDataV2Activity.this, new String[] { f.getPath() },
							new String[] { "image/*" }, new MediaScannerConnection.OnScanCompletedListener() {
								public void onScanCompleted(String path, Uri uri) {
Util.logInfo(this.getClass().toString(), "Finished scanning " + path);
								}
							});
				} else {
					// Log.e(TAG, "cannot write file");
					Util.toast(MapDataV2Activity.this, getResources().getString(R.string.data_SD_unavailable));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				// Log.e(TAG, "cannot write file");
				Util.toast(MapDataV2Activity.this, getResources().getString(R.string.data_SD_unavailable));
				}
			}
		};

	GoogleMap.SnapshotReadyCallback snapshotShareMap = new GoogleMap.SnapshotReadyCallback() {
		@Override
		public void onSnapshotReady(Bitmap snapshot) {
			try {
				File directory = getDirectory();
				String filename = getFilename();
				if ( directory.canWrite() ) {
					File f = new File(directory, filename);
					FileOutputStream out = new FileOutputStream(f);
					snapshot.compress(Bitmap.CompressFormat.JPEG, 96, out);
					out.close();

					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("image/jpeg");
					share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
					// add a subject
					share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
					// build the body of the message to be shared
					String shareMessage = getResources().getString(R.string.project_website);
					// add the message
					share.putExtra(Intent.EXTRA_TEXT, shareMessage);
					startActivity(Intent.createChooser(share, getResources().getText(R.string.share_with)));

				} else {
					// Log.e(TAG, "cannot write file");
					Util.toast(MapDataV2Activity.this, getResources().getString(R.string.data_SD_unavailable));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				// Log.e(TAG, "cannot write file");
				Util.toast(MapDataV2Activity.this, getResources().getString(R.string.data_SD_unavailable));
			}
		}
	};

	private void saveMapImage() {
		if ( mGoogleMap != null ) mGoogleMap.snapshot(snapshotSaveMap);
	}

	private void shareMap() {
		if ( mGoogleMap != null ) mGoogleMap.snapshot(snapshotShareMap);
	}


/*	private void saveMapImageV1(Context context) {
		try {
			File root = Environment.getExternalStorageDirectory();
			File directory = new File(root, getResources().getString(R.string.app_directory));
			directory.mkdirs();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();
			String stringDate = dateFormat.format(date);
			String filename = getResources().getString(R.string.saved_image_prefix) + stringDate + ".jpg";
			if (directory.canWrite()) {
				File f = new File(directory, filename);
				FileOutputStream out = new FileOutputStream(f);
				Bitmap bmp = getMapImageV1();
				bmp.compress(Bitmap.CompressFormat.JPEG, 96, out);
				out.close();

				Util.toast(context, getResources().getString(R.string.data_saved) + " " + f);
				// Log.e(TAG, "File saved as " + file);
			} else {
				// Log.e(TAG, "cannot write file");
				Util.toast(context, getResources().getString(R.string.data_SD_unavailable));
			}
		} catch (IOException e) {
			// Log.e(TAG, "Could not write file " + e.getMessage());
			Util.toast(context, getResources().getString(R.string.data_SD_error));
		}
	}*/

	/*private void shareMapV1(Context context) {
		try {
			File root = Environment.getExternalStorageDirectory();
			File directory = new File(root, getResources().getString(R.string.app_directory));
			directory.mkdirs();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();
			String stringDate = dateFormat.format(date);
			String filename = getResources().getString(R.string.saved_image_prefix) + stringDate + ".jpg";
			if (directory.canWrite()) {
				File f = new File(directory, filename);
				FileOutputStream out = new FileOutputStream(f);
				Bitmap bmp = getMapImageV1();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.close();

				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/jpeg");
				share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
				// add a subject
				share.putExtra(Intent.EXTRA_SUBJECT, "Tigatrapp");
				// build the body of the message to be shared
				String shareMessage = getResources().getString(R.string.project_website);
				// add the message
				share.putExtra(Intent.EXTRA_TEXT, shareMessage);
				startActivity(Intent.createChooser(share, getResources().getText(R.string.share_with)));
			} else {
				// Log.e(TAG, "cannot write file");
				Util.toast(context, getResources().getString(R.string.data_SD_unavailable));
			}
		} catch (IOException e) {
			// Log.e(TAG, "Could not write file " + e.getMessage());
			Util.toast(context, getResources().getString(R.string.data_SD_error));
		}
	}*/

	/*class MapOverlay extends Overlay {
		private Paint mPaintPoint;
		private Paint mPaintPointBorder;
		private Paint mPaintPointDot;
		private GeoPoint[] pointsToDraw;

		MapOverlay(int color, GeoPoint[] points) {
			mPaintPoint = new Paint();
			mPaintPoint.setStyle(Paint.Style.FILL);
			mPaintPoint.setShadowLayer(6, 3, 3, 0xff000000);
			mPaintPoint.setColor(color);
			mPaintPoint.setAntiAlias(true);
			mPaintPointBorder = new Paint();
			mPaintPointBorder.setStyle(Paint.Style.STROKE);
			mPaintPointBorder.setColor(Color.BLACK);
			mPaintPointBorder.setAntiAlias(true);
			mPaintPointDot = new Paint();
			mPaintPointDot.setStyle(Paint.Style.FILL);
			mPaintPointDot.setColor(Color.BLACK);
			mPaintPointDot.setAntiAlias(true);
			this.setPointsToDraw(points);
		}

		public void setPointsToDraw(GeoPoint[] points) {
			pointsToDraw = points;
		}

		public GeoPoint[] getPointsToDraw() {
			return pointsToDraw;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			if (shadow || pointsToDraw == null || pointsToDraw.length == 0)
				return;
			for (GeoPoint p : pointsToDraw) {
				// convert point to pixels
				Point screenPts = new Point();
				GeoPoint pointToDraw = new GeoPoint(p.getLatitudeE6(), p.getLongitudeE6());
				mapView.getProjection().toPixels(pointToDraw, screenPts);
				int pointRadius = (int) mapView.getProjection().metersToEquatorPixels(5);
				int dotRadius = (int) mapView.getProjection().metersToEquatorPixels(2);
				canvas.drawCircle(screenPts.x, screenPts.y, pointRadius, mPaintPoint);
				canvas.drawCircle(screenPts.x, screenPts.y, pointRadius, mPaintPointBorder);
				canvas.drawCircle(screenPts.x, screenPts.y, dotRadius, mPaintPointDot);
			}
			return;
		}
	}*/

	public class DataGrabberTask extends AsyncTask<Context, Integer, Boolean> {

		int myProgress;

		ArrayList<MyMarkerItem> myAdultOverlayList = new ArrayList<>();
		ArrayList<MyMarkerItem> mySiteOverlayList = new ArrayList<>();


		@Override
		protected void onPreExecute() {
			myProgress = 0;
		}

		protected Boolean doInBackground(Context... context) {

			ContentResolver cr = getContentResolver();
			String sc = Reports.KEY_DELETE_REPORT + " = 0 AND "
					+ Reports.KEY_LATEST_VERSION + " = 1 AND "
					+ Reports.KEY_TYPE + " < 2";
			Cursor c = cr.query(Util.getReportsUri(context[0]), Reports.KEYS_ALL, sc, null, null);

			if ( c != null ) {
				if ( c.getCount() > 0 && c.moveToFirst() ) {
//				int deleteReportCol = c.getColumnIndexOrThrow(Reports.KEY_DELETE_REPORT);
//				int latestVersionCol = c.getColumnIndexOrThrow(Reports.KEY_LATEST_VERSION);
//				int rowIdCol = c.getColumnIndexOrThrow(Reports.KEY_ROW_ID);
//				int userIdCol = c.getColumnIndexOrThrow(Reports.KEY_USER_ID);
					int reportIdCol = c.getColumnIndexOrThrow(Reports.KEY_REPORT_ID);
					int reportTimeCol = c.getColumnIndexOrThrow(Reports.KEY_REPORT_TIME);
//				int reportVersionCol = c.getColumnIndexOrThrow(Reports.KEY_REPORT_VERSION);
					int typeCol = c.getColumnIndexOrThrow(Reports.KEY_TYPE);
					int confirmationCol = c.getColumnIndexOrThrow(Reports.KEY_CONFIRMATION);
					int locationChoiceCol = c.getColumnIndexOrThrow(Reports.KEY_LOCATION_CHOICE);
					int currentLocationLonCol = c.getColumnIndexOrThrow(Reports.KEY_CURRENT_LOCATION_LON);
					int currentLocationLatCol = c.getColumnIndexOrThrow(Reports.KEY_CURRENT_LOCATION_LAT);
					int selectedLocationLonCol = c.getColumnIndexOrThrow(Reports.KEY_SELECTED_LOCATION_LON);
					int selectedLocationLatCol = c.getColumnIndexOrThrow(Reports.KEY_SELECTED_LOCATION_LAT);
					int noteCol = c.getColumnIndexOrThrow(Reports.KEY_NOTE);
//				int photoAttachedCol = c.getColumnIndexOrThrow(Reports.KEY_PHOTO_ATTACHED);
					int photoUrisCol = c.getColumnIndexOrThrow(Reports.KEY_PHOTO_URIS);
//				int uploadedCol = c.getColumnIndexOrThrow(Reports.KEY_UPLOADED);
//				int serverTimestampCol = c.getColumnIndexOrThrow(Reports.KEY_SERVER_TIMESTAMP);

					int currentRecord = 0;
					int nRecords = c.getCount();

					while ( !c.isAfterLast() ) {
						//myProgress = (int) (((currentRecord++) / (float) nRecords) * 100);
						//publishProgress(myProgress);

						int locationChoice = c.getInt(locationChoiceCol);
						int thisType = c.getInt(typeCol);

/*						Double geoLat = c.getDouble(locationChoice == Report.LOCATION_CHOICE_SELECTED ? selectedLocationLatCol
								: currentLocationLatCol) * 1E6;
						Double geoLon = c.getDouble(locationChoice == Report.LOCATION_CHOICE_SELECTED ? selectedLocationLonCol
								: currentLocationLonCol) * 1E6;
						GeoPoint point = new GeoPoint(geoLat.intValue(), geoLon.intValue());

						MyOverlayItem overlayItem = new MyOverlayItem(
								point,
								(thisType == Report.TYPE_ADULT ? getResources().getString(R.string.view_report_title_adult)
										: getResources().getString(R.string.view_report_title_site))
										+ "\n" + Util.userDate(new Date(c.getLong(reportTimeCol))),
								c.getString(noteCol),
								c.getString(reportIdCol),
								c.getInt(typeCol),
								c.getString(photoUrisCol),
								c.getString(confirmationCol),
								c.getLong(reportTimeCol));*/

						Double geoLat = c.getDouble(locationChoice == Report.LOCATION_CHOICE_SELECTED ? selectedLocationLatCol
								: currentLocationLatCol);
						Double geoLon = c.getDouble(locationChoice == Report.LOCATION_CHOICE_SELECTED ? selectedLocationLonCol
								: currentLocationLonCol);
						LatLng point = new LatLng(geoLat, geoLon);

						MyMarkerItem markerItem = new MyMarkerItem(
								point,
								(thisType == Report.TYPE_ADULT ? getResources().getString(R.string.view_report_title_adult)
										: getResources().getString(R.string.view_report_title_site))
										+ "\n" + Util.userDate(new Date(c.getLong(reportTimeCol))),
								c.getString(noteCol),
								c.getString(reportIdCol),
								c.getInt(typeCol),
								c.getString(photoUrisCol),
								c.getString(confirmationCol),
								c.getLong(reportTimeCol));
						currentCenter = point;
						if ( thisType == Report.TYPE_ADULT )
							myAdultOverlayList.add(markerItem);
						else
							mySiteOverlayList.add(markerItem);

						c.moveToNext();
					}
				}
				c.close();
			}
			return true;
		}

		protected void onProgressUpdate(Integer... progress) {
			progressbar.setProgress(progress[0]);
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				if ( manager != null ) {
					boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

					boolean b = drawFixes(myAdultOverlayList, mySiteOverlayList, true, !statusOfGPS);
				}

			}
			progressbar.setVisibility(View.INVISIBLE);
			legendLayout.setVisibility(View.VISIBLE);

			loadingData = false;
		}
	}

}