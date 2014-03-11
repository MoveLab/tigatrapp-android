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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import ceab.movlab.tigre.ContentProviderContractReports.Reports;

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
public class ViewDataActivity extends MapActivity {

	boolean loadingData;

	public static MapView mapView;
	private MapController myMapController;
	private List<Overlay> mapOverlays;
	GeoPoint currentCenter;

	static boolean satToggle;

	int ADULT_COLOR = 0xffd95f02;

	int SITE_COLOR = 0xff7570b3;

	LayerDrawable siteMarker;
	LayerDrawable adultMarker;

	ProgressBar progressbar;

	ArrayList<GeoPoint> mPoints;

	ArrayList<MyOverlayItem> mOverlaylist;

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

		mPoints = new ArrayList<GeoPoint>();
		mOverlaylist = new ArrayList<MyOverlayItem>();

		progressbar = (ProgressBar) findViewById(R.id.mapProgressbar);
		progressbar.setProgress(0);


		ShapeDrawable adultMarkerBorder = new ShapeDrawable(new OvalShape());
		adultMarkerBorder.getPaint().setColor(Color.BLACK);
		adultMarkerBorder.getPaint().setStyle(Paint.Style.FILL);;
		adultMarkerBorder.setBounds(0, 0, 10, 10);
		ShapeDrawable adultMarkerFill = new ShapeDrawable(new OvalShape());
		adultMarkerFill.getPaint().setColor(ADULT_COLOR);
		adultMarkerFill.getPaint().setStyle(Paint.Style.FILL);;
		adultMarkerFill.setBounds(0, 0, 10, 10);

		Drawable[] ad = {adultMarkerFill, adultMarkerBorder};
		adultMarker = new LayerDrawable(ad);

		ShapeDrawable siteMarkerBorder = new ShapeDrawable(new OvalShape());
		siteMarkerBorder.getPaint().setColor(Color.BLACK);
		siteMarkerBorder.getPaint().setStyle(Paint.Style.FILL);;
		siteMarkerBorder.setBounds(0, 0, 10, 10);
		ShapeDrawable siteMarkerFill = new ShapeDrawable(new OvalShape());
		siteMarkerFill.getPaint().setColor(SITE_COLOR);
		siteMarkerFill.getPaint().setStyle(Paint.Style.FILL);;
		siteMarkerFill.setBounds(0, 0, 10, 10);

		Drawable[] sd = {adultMarkerFill, adultMarkerBorder};
		siteMarker = new LayerDrawable(ad);

		
		
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
	static final private int SAVE_MAP = Menu.FIRST + 4;
	static final private int SHARE_MAP = Menu.FIRST + 5;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, TOGGLE_VIEW, Menu.NONE, R.string.menu_toggle_view);
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
	public boolean drawFixes(ArrayList<MyOverlayItem> myAdultReports,
			ArrayList<MyOverlayItem> mySiteReports,
			ArrayList<GeoPoint> othersAdultReports,
			ArrayList<GeoPoint> othersSiteReports, boolean clearMapOverlays,
			boolean recenter) {

		// get mapview overlays
		mapOverlays = mapView.getOverlays();

		// Clear any existing overlays if cleraMapOverlays set to true
		if (clearMapOverlays)
			mapOverlays.clear();

		if (othersAdultReports != null && othersAdultReports.size() > 0) {
			mapOverlays.add(new MapOverlay(ADULT_COLOR, othersAdultReports
					.toArray(new GeoPoint[othersAdultReports.size()])));
		}
		if (othersSiteReports != null && othersSiteReports.size() > 0) {
			mapOverlays.add(new MapOverlay(SITE_COLOR, othersSiteReports
					.toArray(new GeoPoint[othersSiteReports.size()])));
		}

		if (myAdultReports != null && myAdultReports.size() > 0) {
			// Drawable drawable = this.getResources().getDrawable(
			// R.drawable.report_icon_yellow);
			MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(
					adultMarker, this);
			for (MyOverlayItem oli : myAdultReports) {
				itemizedoverlay.addOverlay(oli);
			}
			itemizedoverlay.populateNow();
			mapOverlays.add(itemizedoverlay);
		}

		if (mySiteReports != null && mySiteReports.size() > 0) {
			// Drawable drawable = this.getResources().getDrawable(
			// R.drawable.new_fix_pin);
			MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(
					siteMarker, this);
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
				GeoPoint pointToDraw = new GeoPoint(p.getLatitudeE6(),
						p.getLongitudeE6());
				mapView.getProjection().toPixels(pointToDraw, screenPts);
				int pointRadius = (int) mapView.getProjection()
						.metersToEquatorPixels(5);
				int dotRadius = (int) mapView.getProjection()
						.metersToEquatorPixels(2);
				canvas.drawCircle(screenPts.x, screenPts.y, pointRadius,
						mPaintPoint);
				canvas.drawCircle(screenPts.x, screenPts.y, pointRadius,
						mPaintPointBorder);
				canvas.drawCircle(screenPts.x, screenPts.y, dotRadius,
						mPaintPointDot);
			}
			return;
		}
	}

	public class DataGrabberTask extends AsyncTask<Context, Integer, Boolean> {

		int myProgress;

		int nFixes;

		ArrayList<MyOverlayItem> myAdultOverlaylist = new ArrayList<MyOverlayItem>();
		ArrayList<MyOverlayItem> mySiteOverlaylist = new ArrayList<MyOverlayItem>();
		ArrayList<GeoPoint> othersAdultReports = new ArrayList<GeoPoint>();
		ArrayList<GeoPoint> othersSiteReports = new ArrayList<GeoPoint>();

		GeoPoint center;

		@Override
		protected void onPreExecute() {

			myProgress = 0;
		}

		protected Boolean doInBackground(Context... context) {

			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(Reports.CONTENT_URI, Reports.KEYS_ALL, null,
					null, null);

			if (c.moveToFirst()) {
				int deleteReportCol = c
						.getColumnIndexOrThrow(Reports.KEY_DELETE_REPORT);
				int latestVersionCol = c
						.getColumnIndexOrThrow(Reports.KEY_LATEST_VERSION);
				int rowIdCol = c.getColumnIndexOrThrow(Reports.KEY_ROW_ID);
				int userIdCol = c.getColumnIndexOrThrow(Reports.KEY_USER_ID);
				int reportIdCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_ID);
				int reportTimeCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_TIME);
				int reportVersionCol = c
						.getColumnIndexOrThrow(Reports.KEY_REPORT_VERSION);
				int typeCol = c.getColumnIndexOrThrow(Reports.KEY_TYPE);
				int confirmationCol = c
						.getColumnIndexOrThrow(Reports.KEY_CONFIRMATION);
				int locationChoiceCol = c
						.getColumnIndexOrThrow(Reports.KEY_LOCATION_CHOICE);
				int currentLocationLonCol = c
						.getColumnIndexOrThrow(Reports.KEY_CURRENT_LOCATION_LON);
				int currentLocationLatCol = c
						.getColumnIndexOrThrow(Reports.KEY_CURRENT_LOCATION_LAT);
				int selectedLocationLonCol = c
						.getColumnIndexOrThrow(Reports.KEY_SELECTED_LOCATION_LON);
				int selectedLocationLatCol = c
						.getColumnIndexOrThrow(Reports.KEY_SELECTED_LOCATION_LAT);
				int noteCol = c.getColumnIndexOrThrow(Reports.KEY_NOTE);
				int mailingCol = c.getColumnIndexOrThrow(Reports.KEY_MAILING);
				int photoAttachedCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHOTO_ATTACHED);
				int photoUrisCol = c
						.getColumnIndexOrThrow(Reports.KEY_PHOTO_URIS);
				int uploadedCol = c.getColumnIndexOrThrow(Reports.KEY_UPLOADED);
				int serverTimestampCol = c
						.getColumnIndexOrThrow(Reports.KEY_SERVER_TIMESTAMP);
				int locationChoice = c.getInt(locationChoiceCol);

				while (!c.isAfterLast()) {

					if (c.getInt(deleteReportCol) == 0
							&& c.getInt(latestVersionCol) == 1) {
						Double geoLat = c
								.getDouble(locationChoice == Report.LOCATION_CHOICE_SELECTED ? selectedLocationLatCol
										: currentLocationLatCol) * 1E6;
						Double geoLon = c
								.getDouble(locationChoice == Report.LOCATION_CHOICE_SELECTED ? selectedLocationLonCol
										: currentLocationLonCol) * 1E6;
						GeoPoint point = new GeoPoint(geoLat.intValue(),
								geoLon.intValue());

						final int thisType = c.getInt(typeCol);

						if (c.getString(userIdCol).equals(
								PropertyHolder.getUserId())) {
							MyOverlayItem overlayitem = new MyOverlayItem(
									point,
									(thisType == Report.TYPE_ADULT ? "Adult Report"
											: "Breeding Site Report")
											+ "\n"
											+ Util.userDate(new Date(c
													.getLong(reportTimeCol))),
									c.getString(noteCol),
									c.getString(reportIdCol),
									c.getInt(typeCol),
									c.getString(photoUrisCol));
							currentCenter = point;
							if (thisType == Report.TYPE_ADULT)
								myAdultOverlaylist.add(overlayitem);
							else
								mySiteOverlaylist.add(overlayitem);
						} else {
							if (thisType == Report.TYPE_ADULT)
								othersAdultReports.add(point);
							else
								othersSiteReports.add(point);
						}
					}
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
				drawFixes(myAdultOverlaylist, mySiteOverlaylist,
						othersAdultReports, othersSiteReports, true, true);
			}
			progressbar.setVisibility(View.INVISIBLE);
			loadingData = false;
		}

	}

}