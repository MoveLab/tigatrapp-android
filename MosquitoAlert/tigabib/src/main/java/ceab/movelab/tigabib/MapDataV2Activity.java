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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ceab.movelab.tigabib.ContProvContractReports.Reports;

/**
 * Allows user to view their own data in a map (from the database on their phone
 * -- not from the server).
 * 
 * @author Màrius Garcia
 * 
 * 
 */
public class MapDataV2Activity extends FragmentActivity implements OnMapReadyCallback {

	private static final float ADULT_COLOR_HUE = 26.0f;
	private static final float SITE_COLOR_HUE = 39.0f;

	private String lang;

	private ProgressBar progressbar;
	private boolean loadingData;

//	public static MapView mapView;
//	private MapController myMapController;
//	private List<Overlay> mapOverlays;
	private GeoPoint currentCenter;

	private static boolean satToggle;

//	int ADULT_COLOR_V1 = 0xffd95f02;
//	int SITE_COLOR_V1 = 0xff7570b3;
	//MarkerDrawable siteMarker;
	//MarkerDrawable adultMarker;
	//ArrayList<GeoPoint> mPoints;
	//ArrayList<MyOverlayItem> mOverlaylist;

	//private Resources res;
	//private GoogleApiClient mGoogleApiClient;
	private GoogleMap mGoogleMap;
	private SupportMapFragment mMapFragment;
	private Map<Marker, MyOverlayItem> markerMap = new HashMap<>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.map_layout_v2);
		setTitle(getResources().getString(R.string.activity_label_map));

		mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview));
		// Loading map
		initializeMap();

		// pauseToggle = !PropertyHolder.isServiceOn();
		satToggle = false;

		progressbar = (ProgressBar) findViewById(R.id.mapProgressbar);
		progressbar.setProgress(0);
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 */
	private void initializeMap() {
		if (mGoogleMap == null) {
			mMapFragment.getMapAsync(this);
		}
	}

	private void setMapType() {
		if (mGoogleMap != null)
			mGoogleMap.setMapType(satToggle ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_NORMAL);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mGoogleMap = googleMap;
		try {
			mGoogleMap.setMyLocationEnabled(true);
		} catch (SecurityException se) {
		}

		setMapType();
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
		mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
		mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
		mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
		mGoogleMap.getUiSettings().setCompassEnabled(false);
		mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

		GeoPoint myCenterPoint = currentCenter != null ? currentCenter : Util.CEAB_COORDINATES;
		LatLng myLatLng = new LatLng(myCenterPoint.getLatitudeE6() / 1E6, myCenterPoint.getLongitudeE6() / 1E6);
		//mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(15);
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));

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

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		setMapType();

		progressbar.setVisibility(View.VISIBLE);

		if ( !loadingData ) {
			loadingData = true;
			new DataGrabberTask().execute(this);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_data_menu, menu);

		MenuItem mSat = menu.findItem(R.id.sat);
		MenuItem mStreet = menu.findItem(R.id.street);
		if ( satToggle )
			mSat.setChecked(true);
		else
			mStreet.setChecked(true);

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.language) {
			Intent i = new Intent(this, LanguageSelector.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.sat) {
			item.setChecked(true);
			satToggle = true;
			setMapType();
			return true;
		} else if (item.getItemId() == R.id.street) {
			item.setChecked(true);
			satToggle = false;
			setMapType();
			return true;
		} else if (item.getItemId() == R.id.saveMap) {
			saveMapImage();
			return true;
		} else if (item.getItemId() == R.id.shareMap) {
			shareMap();
			return true;
		}

		return false;
	}

	private void onTap(final Marker myMarker) {
		final MyOverlayItem item = markerMap.get(myMarker);

		if ( item != null ) {
			Intent i = new Intent(this, ViewReportsTab.class);
			if (item.photoUris != null) {
				i.putExtra(Reports.KEY_PHOTO_URIS, item.photoUris);
			}
			if (item.responses != null) {
				i.putExtra(Reports.KEY_CONFIRMATION, item.responses);
			}

			if (item.reportId != null) {
				i.putExtra(Reports.KEY_REPORT_ID, item.reportId);
			}
			if (item.type != Report.MISSING) {
				i.putExtra(Reports.KEY_TYPE, item.type);
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


	/*
	* Draw selected locations on map. Returns true if drawn; false otherwise.
	*/
	public boolean drawFixes(ArrayList<MyOverlayItem> myAdultReports, ArrayList<MyOverlayItem> mySiteReports,
							 boolean clearMapOverlays, boolean recenter) {
		// Clear any existing overlays if cleraMapOverlays set to true
		if (clearMapOverlays)
			//mGoogleMap.clear();

		if (myAdultReports != null && myAdultReports.size() > 0) {
			for (MyOverlayItem oli : myAdultReports) {
				LatLng pointLatLng = new LatLng(oli.getPoint().getLatitudeE6() / 1E6, oli.getPoint().getLongitudeE6() / 1E6);
				Marker marker = mGoogleMap.addMarker(new MarkerOptions()
						.position(pointLatLng)
						.title(oli.getTitle())
						.snippet(oli.getSnippet())
						.icon(BitmapDescriptorFactory.defaultMarker(ADULT_COLOR_HUE)));
				markerMap.put(marker, oli);
			}
		}

		if (mySiteReports != null && mySiteReports.size() > 0) {
			for (MyOverlayItem oli : mySiteReports) {
				LatLng pointLatLng = new LatLng(oli.getPoint().getLatitudeE6() / 1E6, oli.getPoint().getLongitudeE6() / 1E6);
				Marker marker = mGoogleMap.addMarker(new MarkerOptions()
						.position(pointLatLng)
						.title(oli.getTitle())
						.snippet(oli.getSnippet())
						.icon(BitmapDescriptorFactory.defaultMarker(SITE_COLOR_HUE)));
				markerMap.put(marker, oli);
			}
		}

		if (recenter && currentCenter != null) {
			LatLng myLatLng = new LatLng(currentCenter.getLatitudeE6() / 1E6, currentCenter.getLongitudeE6() / 1E6);
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15), 200, null);
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
		mGoogleMap.snapshot(snapshotSaveMap);
	}
	private void shareMap() {
		mGoogleMap.snapshot(snapshotShareMap);
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

		ArrayList<MyOverlayItem> myAdultOverlayList = new ArrayList<>();
		ArrayList<MyOverlayItem> mySiteOverlayList = new ArrayList<>();


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
				if ( c.getCount() > 0 && c.moveToFirst()) {
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

					while (!c.isAfterLast()) {

						myProgress = (int) (((currentRecord++) / (float) nRecords) * 100);

						publishProgress(myProgress);

						int locationChoice = c.getInt(locationChoiceCol);
						Double geoLat = c.getDouble(locationChoice == Report.LOCATION_CHOICE_SELECTED ? selectedLocationLatCol
								: currentLocationLatCol) * 1E6;
						Double geoLon = c.getDouble(locationChoice == Report.LOCATION_CHOICE_SELECTED ? selectedLocationLonCol
								: currentLocationLonCol) * 1E6;
						GeoPoint point = new GeoPoint(geoLat.intValue(), geoLon.intValue());

						final int thisType = c.getInt(typeCol);

						// NOTE THAT WE HAVE DECIDED TO HAVE ONLY THE USER'S REPORTS
						// ON THE PHONE DB, NOT OTHERS', SO NO NEED TO CHECK USER ID HERE

						// !!! MG - Build my own object
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
								c.getLong(reportTimeCol));
						currentCenter = point;
						if (thisType == Report.TYPE_ADULT)
							myAdultOverlayList.add(overlayItem);
						else
							mySiteOverlayList.add(overlayItem);

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
				drawFixes(myAdultOverlayList, mySiteOverlayList, true, true);
			}
			progressbar.setVisibility(View.INVISIBLE);
			loadingData = false;
		}
	}

}