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
 */

package ceab.movlab.tigre;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;
import ceab.movlab.tigre.ContentProviderContractTracks.Fixes;
import ceab.movlab.tigre.ContentProviderContractTrips.Trips;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Allows user to view their own data in a map (from the database on their phone
 * -- not from the server).
 * 
 * @author John R.B. Palmer
 * 
 * 
 */
public class ViewDataActivity extends MapActivity {
	String TAG = "DriverMapActivity";
	private TextView mReceiversOffWarning;
	private TextView tripRecordingText;
	private LinearLayout stopTripArea;

	boolean loadingData;

	private MapView mapView;
	private MapController myMapController;
	private List<Overlay> mapOverlays;
	GeoPoint nCenter;
	GeoPoint currentCenter;

	ImageButton mSTB;

	boolean satToggle;

	int BORDER_COLOR_MAP = 0xee4D2EFF;
	int FILL_COLOR_MAP = 0x554D2EFF;

	int BORDER_COLOR_SAT = 0xeeD9FCFF;
	int FILL_COLOR_SAT = 0xbbD9FCFF;

	int[] PATHCOLORS = { 0xffDD1E2F, 0xffEBB035, 0xff06A2CB, 0xff218559,
			0xffff0000, 0xff00ff00, 0xff0000ff, 0xff00ffff, 0xffff00ff,
			0xffffff00 };

	boolean drawConfidenceCircles = false;

	ProgressBar progressbar;

	ArrayList<MapPoint> mPoints;

	ArrayList<ArrayList<MapPoint>> mTrips;

	ArrayList<OverlayItem> mOverlaylist;

	final Context context = this;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.map_layout);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		myMapController = mapView.getController();
		myMapController.setCenter(currentCenter != null ? currentCenter
				: Util.CEAB_COORDINATES);
		myMapController.setZoom(15);
		satToggle = false;
		mapView.setSatellite(satToggle);

		// pauseToggle = !PropertyHolder.isServiceOn();

		mPoints = new ArrayList<MapPoint>();
		mTrips = new ArrayList<ArrayList<MapPoint>>();
		mOverlaylist = new ArrayList<OverlayItem>();

		progressbar = (ProgressBar) findViewById(R.id.mapProgressbar);
		progressbar.setProgress(0);

		mReceiversOffWarning = (TextView) findViewById(R.id.receiversOffWarning);
		mReceiversOffWarning.setVisibility(View.INVISIBLE);
		tripRecordingText = (TextView) findViewById(R.id.tripRecordingText);
		tripRecordingText.setVisibility(View.INVISIBLE);
		stopTripArea = (LinearLayout) findViewById(R.id.stop_trip_area);
		stopTripArea.setVisibility(View.INVISIBLE);

		mSTB = (ImageButton) findViewById(R.id.button_stop_trip);

		mSTB.setVisibility(View.INVISIBLE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			finish();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateMap(Intent intent) {

		Context context = getApplicationContext();

		if (loadingData == false) {
			loadingData = true;
			new DataGrabberTask().execute(context);
		}

	}

	@Override
	protected void onResume() {

		mapView.setSatellite(satToggle);

		progressbar.setVisibility(View.VISIBLE);

		if (loadingData == false) {
			loadingData = true;
			new DataGrabberTask().execute(context);
		}

		super.onResume();

	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	static final private int TOGGLE_VIEW = Menu.FIRST + 2;
	static final private int TOGGLE_CCS = Menu.FIRST + 3;
	static final private int SAVE_MAP = Menu.FIRST + 4;
	static final private int SHARE_MAP = Menu.FIRST + 5;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, TOGGLE_VIEW, Menu.NONE, R.string.menu_toggle_view);
		menu.add(0, TOGGLE_CCS, Menu.NONE, R.string.menu_toggle_ccs);
		menu.add(0, SAVE_MAP, Menu.NONE, R.string.menu_map_save);
		menu.add(0, SHARE_MAP, Menu.NONE, R.string.menu_map_share);

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
		case (TOGGLE_CCS): {

			drawConfidenceCircles = !drawConfidenceCircles;

			drawFixes(mOverlaylist, mTrips, satToggle, true, true);

			return true;
		}
		case (SAVE_MAP): {
			Context context = getApplicationContext();
			saveMapImage(context);
			return true;
		}
		case (SHARE_MAP): {
			Context context = getApplicationContext();
			shareMap(context);
			return true;
		}

		}
		return false;
	}

	/*
	 * Draw selected locations on map. Returns true if drawn; false otherwise.
	 */
	private boolean drawFixes(ArrayList<OverlayItem> overlaylist,
			ArrayList<ArrayList<MapPoint>> tripdata, boolean sat,
			boolean clearMapOverlays, boolean recenter) {

		// get mapview overlays
		mapOverlays = mapView.getOverlays();

		// Clear any existing overlays if cleraMapOverlays set to true
		if (clearMapOverlays)
			mapOverlays.clear();

		if (tripdata != null && tripdata.size() > 0) {

			int tripN = 0;
			for (ArrayList<MapPoint> data : tripdata) {

				if (data != null && data.size() > 0) {

					int nData = data.size();

					// turn data into array
					MapPoint[] dataArray = new MapPoint[nData];

					int i = 0;
					for (MapPoint p : data) {
						dataArray[i] = p.copy();
						i++;
					}

					// set colors depending on satellite background
					int BC = sat ? BORDER_COLOR_SAT : BORDER_COLOR_MAP;
					int FC = sat ? FILL_COLOR_SAT : FILL_COLOR_MAP;

					mapOverlays.add(new MapOverlay(BC, FC, PATHCOLORS[tripN
							% PATHCOLORS.length], dataArray));

					currentCenter = new GeoPoint(dataArray[nData - 1].lat,
							dataArray[nData - 1].lon);
				}
				tripN++;

			}

		}

		if (overlaylist != null && overlaylist.size() > 0) {
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.report_icon_yellow);
			MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable,
					this);
			for (OverlayItem oli : overlaylist) {
				itemizedoverlay.addOverlay(oli);
				
				if(currentCenter == null){
					currentCenter = oli.getPoint();
				}
			}

			itemizedoverlay.populateNow();
			mapOverlays.add(itemizedoverlay);

		}
		
		if (recenter && currentCenter != null)
			myMapController.animateTo(currentCenter);


		mapView.postInvalidate();

		return true;
	}

	private Bitmap getMapImage() {
		mapView.setDrawingCacheEnabled(true);
		Bitmap bmp = Bitmap.createBitmap(mapView.getDrawingCache());
		mapView.setDrawingCacheEnabled(false);
		return bmp;
	}

	private void saveMapImage(Context context) {

		try {
			File root = Environment.getExternalStorageDirectory();
			File directory = new File(root, getResources().getString(
					R.string.app_directory));
			directory.mkdirs();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();
			String stringDate = dateFormat.format(date);
			String filename = getResources().getString(
					R.string.saved_image_prefix)
					+ stringDate + ".jpg";
			if (directory.canWrite()) {
				File f = new File(directory, filename);
				FileOutputStream out = new FileOutputStream(f);
				Bitmap bmp = getMapImage();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.close();

				Util.toast(context,
						getResources().getString(R.string.data_saved) + " " + f);
				// Log.e(TAG, "File saved as " + file);
			} else {
				// Log.e(TAG, "cannot write file");

				Util.toast(context,
						getResources().getString(R.string.data_SD_unavailable));
			}

		} catch (IOException e) {
			// Log.e(TAG, "Could not write file " + e.getMessage());

			Util.toast(context, getResources()
					.getString(R.string.data_SD_error));
		}

	}

	private void shareMap(Context context) {

		try {
			File root = Environment.getExternalStorageDirectory();
			File directory = new File(root, getResources().getString(
					R.string.app_directory));
			directory.mkdirs();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd_HH-mm-ss");
			Date date = new Date();
			String stringDate = dateFormat.format(date);
			String filename = getResources().getString(
					R.string.saved_image_prefix)
					+ stringDate + ".jpg";
			if (directory.canWrite()) {
				File f = new File(directory, filename);
				FileOutputStream out = new FileOutputStream(f);
				Bitmap bmp = getMapImage();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.close();

				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/jpeg");

				share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

				startActivity(Intent.createChooser(share, getResources()
						.getText(R.string.share_with)));

			} else {
				// Log.e(TAG, "cannot write file");

				Util.toast(context,
						getResources().getString(R.string.data_SD_unavailable));
			}

		} catch (IOException e) {
			// Log.e(TAG, "Could not write file " + e.getMessage());

			Util.toast(context, getResources()
					.getString(R.string.data_SD_error));
		}

	}

	class MapOverlay extends Overlay {

		private Paint mPaintBorder;
		private Paint mPaintFill;

		private Paint mPaintPath;
		private Paint mPaintPoint;
		private Paint mPaintPointBorder;
		private Paint mPaintPointDot;

		private Bitmap normalfixPin;
		private Bitmap currentfixPin;
		private Bitmap startfixPin;

		private MapPoint[] pointsToDraw;
		private MapPoint[] pointsToDrawNormalIcons;

		MapOverlay(int border, int fill, int pathcolor, MapPoint[] points) {
			mPaintBorder = new Paint();
			mPaintBorder.setStyle(Paint.Style.STROKE);
			mPaintBorder.setAntiAlias(true);
			mPaintBorder.setColor(border);
			mPaintFill = new Paint();
			mPaintFill.setStyle(Paint.Style.FILL);
			mPaintFill.setColor(fill);

			mPaintPath = new Paint();
			mPaintPath.setStyle(Paint.Style.STROKE);
			mPaintPath.setStrokeWidth(4);
			mPaintPath.setShadowLayer(6, 3, 3, 0xff000000);
			mPaintPath.setColor(pathcolor);
			mPaintPath.setAntiAlias(true);

			mPaintPoint = new Paint();
			mPaintPoint.setStyle(Paint.Style.FILL);
			mPaintPoint.setShadowLayer(6, 3, 3, 0xff000000);
			mPaintPoint.setColor(pathcolor);
			mPaintPoint.setAntiAlias(true);

			mPaintPointBorder = new Paint();
			mPaintPointBorder.setStyle(Paint.Style.STROKE);
			mPaintPointBorder.setColor(Color.BLACK);
			mPaintPointBorder.setAntiAlias(true);

			mPaintPointDot = new Paint();
			mPaintPointDot.setStyle(Paint.Style.FILL);
			mPaintPointDot.setColor(Color.BLACK);
			mPaintPointDot.setAntiAlias(true);

			normalfixPin = BitmapFactory.decodeResource(getResources(),
					R.drawable.old_fix_pin);

			currentfixPin = BitmapFactory.decodeResource(getResources(),
					R.drawable.new_fix_pin);

			startfixPin = BitmapFactory.decodeResource(getResources(),
					R.drawable.start_pin);

			this.setPointsToDraw(points);
		}

		public void setPointsToDraw(MapPoint[] points) {
			pointsToDraw = points;

			if (points != null && points.length > 2) {

				pointsToDrawNormalIcons = new MapPoint[points.length - 2];

				for (int i = 1; i < (points.length - 1); i++) {
					pointsToDrawNormalIcons[i - 1] = points[i];
				}

			}

		}

		public MapPoint[] getPointsToDraw() {
			return pointsToDraw;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);

			if (shadow || pointsToDraw == null || pointsToDraw.length == 0)
				return;

			// first draw path
			Path path = new Path();

			MapPoint p0 = pointsToDraw[0];
			Point screenPt0 = new Point();
			GeoPoint pointToDraw0 = new GeoPoint(p0.lat, p0.lon);
			mapView.getProjection().toPixels(pointToDraw0, screenPt0);

			path.moveTo(screenPt0.x, screenPt0.y);

			for (MapPoint p : pointsToDraw) {
				// convert point to pixels
				Point screenPts = new Point();
				GeoPoint pointToDraw = new GeoPoint(p.lat, p.lon);
				mapView.getProjection().toPixels(pointToDraw, screenPts);
				path.lineTo(screenPts.x, screenPts.y);
				int pointradius = (int) mapView.getProjection()
						.metersToEquatorPixels(5);
				canvas.drawCircle(screenPts.x, screenPts.y, pointradius,
						mPaintPoint);

				if (drawConfidenceCircles) {

					int radius = (int) mapView.getProjection()
							.metersToEquatorPixels(p.acc);
					canvas.drawCircle(screenPts.x, screenPts.y, radius,
							mPaintBorder);
					canvas.drawCircle(screenPts.x, screenPts.y, radius,
							mPaintFill);

				}

			}

			canvas.drawPath(path, mPaintPath);

			// draw start and stop points as unique dots

			int ssPointRadius = 6;
			int ssDotRadius = 2;

			MapPoint sp = pointsToDraw[0];
			Point screenPtsSp = new Point();
			GeoPoint pointToDrawSp = new GeoPoint(sp.lat, sp.lon);
			mapView.getProjection().toPixels(pointToDrawSp, screenPtsSp);

			canvas.drawCircle(screenPtsSp.x, screenPtsSp.y, ssPointRadius,
					mPaintPoint);
			canvas.drawCircle(screenPtsSp.x, screenPtsSp.y, ssPointRadius,
					mPaintPointBorder);
			canvas.drawCircle(screenPtsSp.x, screenPtsSp.y, ssDotRadius,
					mPaintPointDot);

			// if there is more than one point, draw the current point as unique
			// icon
			if (pointsToDraw.length > 1) {
				MapPoint cp = pointsToDraw[pointsToDraw.length - 1];
				Point screenPtsCp = new Point();
				GeoPoint pointToDrawCp = new GeoPoint(cp.lat, cp.lon);
				mapView.getProjection().toPixels(pointToDrawCp, screenPtsCp);
				canvas.drawCircle(screenPtsCp.x, screenPtsCp.y, ssPointRadius,
						mPaintPoint);
				canvas.drawCircle(screenPtsCp.x, screenPtsCp.y, ssPointRadius,
						mPaintPointBorder);
				canvas.drawCircle(screenPtsCp.x, screenPtsCp.y, ssDotRadius,
						mPaintPointDot);

			}

			return;
		}

	}

	public class DataGrabberTask extends AsyncTask<Context, Integer, Boolean> {

		int myProgress;

		int nFixes;

		ArrayList<MapPoint> results = new ArrayList<MapPoint>();

		TripsArray tripResults;

		ArrayList<String> tripids = new ArrayList<String>();

		ArrayList<OverlayItem> overlaylist = new ArrayList<OverlayItem>();

		GeoPoint center;

		@Override
		protected void onPreExecute() {

			myProgress = 0;
		}

		protected Boolean doInBackground(Context... context) {

			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(Trips.CONTENT_URI, Trips.KEYS_ALL, null, null,
					Trips.KEY_ROWID + " ASC");

			if (c != null) {
				if (c.moveToFirst()) {

					int tripidCol = c.getColumnIndexOrThrow(Trips.KEY_TRIPID);
					while (!c.isAfterLast()) {
						tripids.add(c.getString(tripidCol));
						c.moveToNext();
					}
				}
				c.close();
			}

			int nTrips = tripids.size();

			if (nTrips > 0) {
				tripResults = new TripsArray(nTrips);

				int tripN = 0;
				for (String tripid : tripids) {

					final String selectionString = Fixes.KEY_TRIPID + " = '"
							+ tripid + "' AND " + Fixes.KEY_DISPLAY + " = "
							+ Fixes.DISPLAY_TRUE;

					c = cr.query(Fixes.CONTENT_URI, Fixes.KEYS_LATLONACCTIMES,
							selectionString, null, null);

					if (c.moveToFirst()) {

						int latCol = c
								.getColumnIndexOrThrow(Fixes.KEY_LATITUDE);
						int lonCol = c
								.getColumnIndexOrThrow(Fixes.KEY_LONGITUDE);
						int accCol = c
								.getColumnIndexOrThrow(Fixes.KEY_ACCURACY);
						int idCol = c.getColumnIndexOrThrow(Fixes.KEY_ROWID);
						int timeCol = c
								.getColumnIndexOrThrow(Fixes.KEY_TIMELONG);
						int sdtimeCol = c
								.getColumnIndexOrThrow(Fixes.KEY_STATION_DEPARTURE_TIMELONG);

						nFixes = c.getCount();

						// float lastAcc = 0;

						int currentRecord = 0;

						while (!c.isAfterLast()) {
							myProgress = (int) (((currentRecord + 1) / (float) nFixes) * 100);
							publishProgress(myProgress);

							// Escape early if cancel() is called
							if (isCancelled())
								break;

							// First grabbing double values of lat lon and time
							Double lat = c.getDouble(latCol);
							Double lon = c.getDouble(lonCol);
							float acc = c.getFloat(accCol);
							long entryTime = c.getLong(timeCol);
							long exitTime = c.getLong(sdtimeCol);

							Double geoLat = lat * 1E6;
							Double geoLon = lon * 1E6;

							tripResults.mpal[tripN].al.add(new MapPoint(geoLat
									.intValue(), geoLon.intValue(), acc,
									entryTime, exitTime, MapPoint.ICON_NORMAL));

							c.moveToNext();

							currentRecord++;
						}
					}
					c.close();

					tripN++;
				}
			}

			// now the reports

			c = cr.query(Reports.CONTENT_URI, Reports.KEYS_ALL, null, null,
					null);

			if (c.moveToFirst()) {

				while (!c.isAfterLast()) {

					int reportIDCol = c
							.getColumnIndexOrThrow(Reports.KEY_REPORTID);
					int reporttimeCol = c
							.getColumnIndexOrThrow(Reports.KEY_REPORTTIME);
					int q1_sizecolorCol = c
							.getColumnIndexOrThrow(Reports.KEY_Q1_SIZECOLOR);
					int q2_abdomenlegsCol = c
							.getColumnIndexOrThrow(Reports.KEY_Q2_ABDOMENLEGS);
					int q3_headthoraxCol = c
							.getColumnIndexOrThrow(Reports.KEY_Q3_HEADTHORAX);
					int herethereCol = c
							.getColumnIndexOrThrow(Reports.KEY_HERETHERE);
					int here_lngCol = c
							.getColumnIndexOrThrow(Reports.KEY_HERE_LNG);
					int here_latCol = c
							.getColumnIndexOrThrow(Reports.KEY_HERE_LAT);
					int other_lngCol = c
							.getColumnIndexOrThrow(Reports.KEY_OTHER_LNG);
					int other_latCol = c
							.getColumnIndexOrThrow(Reports.KEY_OTHER_LAT);
					int here_lng_jCol = c
							.getColumnIndexOrThrow(Reports.KEY_HERE_LNG_J);
					int here_lat_jCol = c
							.getColumnIndexOrThrow(Reports.KEY_HERE_LAT_J);
					int other_lng_jCol = c
							.getColumnIndexOrThrow(Reports.KEY_OTHER_LNG_J);
					int other_lat_jCol = c
							.getColumnIndexOrThrow(Reports.KEY_OTHER_LAT_J);
					int noteCol = c.getColumnIndexOrThrow(Reports.KEY_NOTE);
					int mailingCol = c
							.getColumnIndexOrThrow(Reports.KEY_MAILING);
					int photo_attachedCol = c
							.getColumnIndexOrThrow(Reports.KEY_PHOTO_ATTACHED);
					int photo_uriCol = c
							.getColumnIndexOrThrow(Reports.KEY_PHOTOURI);

					int rowIDCol = c.getColumnIndexOrThrow(Reports.KEY_ROWID);

					String herethere = c.getString(herethereCol);

					Double geoLat = c
							.getDouble(herethere.equals("there") ? other_latCol
									: here_latCol) * 1E6;
					Double geoLng = c
							.getDouble(herethere.equals("there") ? other_lngCol
									: here_lngCol) * 1E6;

					GeoPoint point = new GeoPoint(geoLat.intValue(),
							geoLng.intValue());

					OverlayItem overlayitem = new OverlayItem(point,
							"Tigre troballa: codi " + c.getString(reportIDCol),
							Util.userDate(new Date(c.getLong(reporttimeCol))));
					overlaylist.add(overlayitem);

					c.moveToNext();

				}

			}
			c.close();

			return true;

		}

		protected void onProgressUpdate(Integer... progress) {
			progressbar.setProgress(progress[0]);
		}

		protected void onPostExecute(Boolean result) {

			if (result) {

				if (tripResults != null && tripResults.mpal.length > 0) {

					for (MapPointArrayList p : tripResults.mpal) {
						mTrips.add(p.al);

					}
				}

				if (overlaylist != null && overlaylist.size() > 0) {
					for (OverlayItem oli : overlaylist) {
						mOverlaylist.add(oli);
					}
				}
				drawFixes(mOverlaylist, mTrips, satToggle, true, true);

			}

			progressbar.setVisibility(View.INVISIBLE);

			loadingData = false;

		}

	}

	public class MapPointArrayList {

		ArrayList<MapPoint> al;

		public MapPointArrayList() {

			al = new ArrayList<MapPoint>();
		}

	}

	public class TripsArray {

		MapPointArrayList[] mpal;

		public TripsArray(int size) {

			mpal = new MapPointArrayList[size];

			for (int i = 0; i < size; i++) {
				mpal[i] = new MapPointArrayList();
			}

		}
	}

}