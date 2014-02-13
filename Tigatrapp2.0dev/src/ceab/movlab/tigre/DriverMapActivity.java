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
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import ceab.movlab.tigre.ContentProviderContractTracks.Fixes;
import ceab.movlab.tigre.ContentProviderContractTrips.Trips;

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
public class DriverMapActivity extends MapActivity {
	String TAG = "DriverMapActivity";

	private TextView mReceiversOffWarning;
	private TextView tripRecordingText;

	boolean loadingData;

	private Vibrator mVib;
	Timer myTimer;
	TimerTask delayTask;

	Trip thisTrip;

	boolean deleteTrip;

	private MapView mapView;
	private MapController myMapController;
	private List<Overlay> mapOverlays;
	GeoPoint nCenter;
	GeoPoint currentCenter;

	boolean satToggle;

	Double lastLat = null;
	Double lastLon = null;
	Double lastGeoLat = null;
	Double lastGeoLon = null;

	private int lastRecId = 0;

	MapOverlay mainOverlay;

	String stringLat;
	String stringLng;
	String stringAlt;
	String stringAcc;
	String stringProvider;
	String stringTime;
	ImageButton mSTB;

	int BORDER_COLOR_MAP = 0xee4D2EFF;
	int FILL_COLOR_MAP = 0x554D2EFF;

	int BORDER_COLOR_SAT = 0xeeD9FCFF;
	int FILL_COLOR_SAT = 0xbbD9FCFF;

	boolean drawConfidenceCircles = false;

	ProgressBar progressbar;

	ArrayList<MapPoint> mPoints;

	boolean _mustDraw = true;

	final Context context = this;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map_layout);

		Util.overrideFonts(this, findViewById(android.R.id.content));

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		myMapController = mapView.getController();
		myMapController.setCenter(Util.CEAB_COORDINATES);
		myMapController.setZoom(15);
		satToggle = false;
		mapView.setSatellite(satToggle);

		// pauseToggle = !PropertyHolder.isServiceOn();

		mPoints = new ArrayList<MapPoint>();

		mainOverlay = null;
		stringLat = null;
		stringLng = null;
		stringAlt = null;
		stringAcc = null;
		stringProvider = null;
		stringTime = null;


		mSTB = (ImageButton) findViewById(R.id.button_stop_trip);

		mSTB.setVisibility(View.INVISIBLE);

		progressbar = (ProgressBar) findViewById(R.id.mapProgressbar);
		progressbar.setProgress(0);

		mVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

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

	public class FixReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getBooleanExtra(FixGet.NEW_RECORD, false) == true) {
				updateMap(intent);

			}

		}

	}

	public void updateMap(Intent intent) {

		Context context = getApplicationContext();

		if (loadingData == false) {
			loadingData = true;
			new DataGrabberTask().execute(context);
		}

	}

	FixReceiver fixReceiver;

	@Override
	protected void onResume() {

		mapView.setSatellite(satToggle);

		progressbar.setVisibility(View.VISIBLE);

		if (loadingData == false) {
			loadingData = true;
			new DataGrabberTask().execute(context);
		}

		IntentFilter fixFilter;
		fixFilter = new IntentFilter(getResources().getString(
				R.string.internal_message_id)
				+ Util.MESSAGE_FIX_RECORDED);
		fixReceiver = new FixReceiver();
		registerReceiver(fixReceiver, fixFilter);

		mSTB.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {

					mSTB.setBackgroundResource(R.drawable.blue_rectangle_pressed);

					mVib.vibrate(50);

				}
				if (e.getAction() == MotionEvent.ACTION_UP) {
					mSTB.setBackgroundResource(R.drawable.blue_rectangle);

					mVib.vibrate(50);

				}
				return false;
			}
		});

		mSTB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(DriverMapActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				dialog.setContentView(R.layout.stop_trip);

				Util.overrideFonts(context,
						dialog.findViewById(android.R.id.content));

				final TextView q1l = (TextView) dialog
						.findViewById(R.id.transportedTigerLabel);
				final RadioGroup specRadioGroup = (RadioGroup) dialog
						.findViewById(R.id.specRadioGroup);
				final TextView qb = (TextView) dialog
						.findViewById(R.id.transportedTigerQuestionTwoLabel);
				final RadioGroup rb = (RadioGroup) dialog
						.findViewById(R.id.specQuestionTwoRadioGroup);

				final RadioGroup stopTripRadioGroup = (RadioGroup) dialog
						.findViewById(R.id.stopTripRadioGroup);
				stopTripRadioGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {

								if (checkedId == R.id.stopTripRadioButtonSave) {
									q1l.setVisibility(View.VISIBLE);
									specRadioGroup.setVisibility(View.VISIBLE);

								} else {
									q1l.setVisibility(View.INVISIBLE);
									specRadioGroup
											.setVisibility(View.INVISIBLE);
									qb.setVisibility(View.INVISIBLE);
									rb.setVisibility(View.INVISIBLE);

								}

							}

						});

				specRadioGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {

								if (checkedId == R.id.specRadioButtonYes) {
									qb.setVisibility(View.VISIBLE);
									rb.setVisibility(View.VISIBLE);

								} else {
									qb.setVisibility(View.INVISIBLE);
									rb.setVisibility(View.INVISIBLE);

								}

							}

						});

				Button b2 = (Button) dialog.findViewById(R.id.buttonStopTripOK);
				// if button is clicked, close the custom dialog
				b2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						thisTrip = null;
						if (stopTripRadioGroup.getCheckedRadioButtonId() == R.id.stopTripRadioButtonDelete) {
							deleteTrip = true;
							thisTrip = new Trip(PropertyHolder.getTripId(), 1,
									"", "", 0, 0);
							new TripUploadTask().execute(context);
							dialog.dismiss();
						} else if (stopTripRadioGroup.getCheckedRadioButtonId() == R.id.stopTripRadioButtonSave) {
							deleteTrip = false;
							String trans = "";
							String tig = "";
							if (specRadioGroup.getCheckedRadioButtonId() == R.id.specRadioButtonYes)
								trans = "si";
							if (specRadioGroup.getCheckedRadioButtonId() == R.id.specRadioButtonNo)
								trans = "no";
							if (specRadioGroup.getCheckedRadioButtonId() == R.id.specRadioButtonDontKnow)
								trans = "nose";
							if (rb.getCheckedRadioButtonId() == R.id.specSubRadioButtonYes)
								tig = "si";
							if (rb.getCheckedRadioButtonId() == R.id.specSubRadioButtonNo)
								tig = "no";
							if (rb.getCheckedRadioButtonId() == R.id.specSubRadioButtonDontKnow)
								tig = "nose";
							thisTrip = new Trip(PropertyHolder.getTripId(), 0,
									trans, tig, PropertyHolder.tripStartTime(),
									System.currentTimeMillis());
							new TripUploadTask().execute(context);
							dialog.dismiss();
						}

						// if neither save nor delete has been selected, do
						// nothing

					}
				});

				Button b3 = (Button) dialog
						.findViewById(R.id.buttonReturnToTrip);
				// if button is clicked, close the custom dialog
				b3.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();

			}
		});

		mReceiversOffWarning = (TextView) findViewById(R.id.receiversOffWarning);

		mReceiversOffWarning.setVisibility(View.INVISIBLE);

		tripRecordingText = (TextView) findViewById(R.id.tripRecordingText);
		tripRecordingText.setVisibility(View.VISIBLE);

		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				mReceiversOffWarning.setVisibility(View.VISIBLE);

				tripRecordingText.setVisibility(View.INVISIBLE);

				mReceiversOffWarning.setText(getResources().getString(
						R.string.noGPSnoNet));
			} else {
				mReceiversOffWarning.setVisibility(View.VISIBLE);
				tripRecordingText.setVisibility(View.INVISIBLE);

				mReceiversOffWarning.setText(getResources().getString(
						R.string.noGPS));
			}

			mReceiversOffWarning.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(
							android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

				}
			});
		}

		super.onResume();

	}

	@Override
	protected void onPause() {
		unregisterReceiver(fixReceiver);

		super.onPause();
	}

	static final private int LOCATE_NOW = Menu.FIRST;
	static final private int FLUSH_GPS = Menu.FIRST + 1;
	static final private int TOGGLE_VIEW = Menu.FIRST + 2;
	static final private int TOGGLE_CCS = Menu.FIRST + 3;
	static final private int SAVE_MAP = Menu.FIRST + 4;
	static final private int SHARE_MAP = Menu.FIRST + 5;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, LOCATE_NOW, Menu.NONE, R.string.menu_locate_now);
		menu.add(0, FLUSH_GPS, Menu.NONE, R.string.menu_flush_gps);
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
		case (LOCATE_NOW): {

			// intent to upload
			Intent i = new Intent(DriverMapActivity.this, FixGet.class);
			// start the intent
			startService(i);

			return true;
		}
		case (FLUSH_GPS): {

			buildFlushGPSAlert();
			return true;
		}
		case (TOGGLE_CCS): {

			drawConfidenceCircles = !drawConfidenceCircles;

			drawFixes(mPoints, satToggle, true, true);

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
	private boolean drawFixes(ArrayList<MapPoint> data, boolean sat,
			boolean clearMapOverlays, boolean recenter) {

		int nData = data.size();

		// quick return if no data
		if (nData == 0)
			return false;

		// turn data into array
		MapPoint[] dataArray = new MapPoint[nData];

		int i = 0;
		for (MapPoint p : data) {
			dataArray[i] = p.copy();
			i++;
		}

		// get mapview overlays
		mapOverlays = mapView.getOverlays();

		// Clear any existing overlays if cleraMapOverlays set to true
		if (clearMapOverlays)
			mapOverlays.clear();

		// set colors depending on satellite background
		int BC = sat ? BORDER_COLOR_SAT : BORDER_COLOR_MAP;
		int FC = sat ? FILL_COLOR_SAT : FILL_COLOR_MAP;

		mainOverlay = new MapOverlay(BC, FC);

		mainOverlay.setPointsToDraw(dataArray);
		mapOverlays.add(mainOverlay);
		mapView.postInvalidate();

		currentCenter = new GeoPoint(dataArray[nData - 1].lat,
				dataArray[nData - 1].lon);

		if (recenter)
			myMapController.animateTo(currentCenter);

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

		private Bitmap normalfixPin;
		private Bitmap currentfixPin;
		private Bitmap startfixPin;

		private MapPoint[] pointsToDraw;
		private MapPoint[] pointsToDrawNormalIcons;

		MapOverlay(int border, int fill) {
			mPaintBorder = new Paint();
			mPaintBorder.setStyle(Paint.Style.STROKE);
			mPaintBorder.setAntiAlias(true);
			mPaintBorder.setColor(border);
			mPaintFill = new Paint();
			mPaintFill.setStyle(Paint.Style.FILL);
			mPaintFill.setColor(fill);

			normalfixPin = BitmapFactory.decodeResource(getResources(),
					R.drawable.old_fix_pin);

			currentfixPin = BitmapFactory.decodeResource(getResources(),
					R.drawable.new_fix_pin);

			startfixPin = BitmapFactory.decodeResource(getResources(),
					R.drawable.start_pin);

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

			// First draw confidence circles if toggled

			if (drawConfidenceCircles) {
				for (MapPoint p : pointsToDraw) {
					// convert point to pixels
					Point screenPts = new Point();
					GeoPoint pointToDraw = new GeoPoint(p.lat, p.lon);
					mapView.getProjection().toPixels(pointToDraw, screenPts);
					int radius = (int) mapView.getProjection()
							.metersToEquatorPixels(p.acc);
					canvas.drawCircle(screenPts.x, screenPts.y, radius,
							mPaintBorder);
					canvas.drawCircle(screenPts.x, screenPts.y, radius,
							mPaintFill);
				}
			}

			// Now draw icons
			if (pointsToDrawNormalIcons != null
					&& pointsToDrawNormalIcons.length > 0) {

				for (MapPoint p : pointsToDrawNormalIcons) {
					// convert point to pixels
					Point screenPts = new Point();
					GeoPoint pointToDraw = new GeoPoint(p.lat, p.lon);
					mapView.getProjection().toPixels(pointToDraw, screenPts);
					canvas.drawBitmap(normalfixPin,
							screenPts.x - normalfixPin.getWidth() / 2,
							screenPts.y - normalfixPin.getHeight(), null);
				}
			}

			// draw start point as unique icon
			MapPoint sp = pointsToDraw[0];
			Point screenPtsSp = new Point();
			GeoPoint pointToDrawSp = new GeoPoint(sp.lat, sp.lon);
			mapView.getProjection().toPixels(pointToDrawSp, screenPtsSp);
			canvas.drawBitmap(startfixPin,
					screenPtsSp.x - startfixPin.getWidth() / 2, screenPtsSp.y
							- startfixPin.getHeight(), null);

			// if there is more than one point, draw the current point as unique
			// icon
			if (pointsToDraw.length > 1) {
				MapPoint cp = pointsToDraw[pointsToDraw.length - 1];
				Point screenPtsCp = new Point();
				GeoPoint pointToDrawCp = new GeoPoint(cp.lat, cp.lon);
				mapView.getProjection().toPixels(pointToDrawCp, screenPtsCp);
				canvas.drawBitmap(currentfixPin,
						screenPtsCp.x - currentfixPin.getWidth() / 2,
						screenPtsCp.y - currentfixPin.getHeight(), null);
			}

			return;
		}

	}

	public class DataGrabberTask extends AsyncTask<Context, Integer, Boolean> {

		int myProgress;

		int nFixes;

		ArrayList<MapPoint> results = new ArrayList<MapPoint>();

		GeoPoint center;

		@Override
		protected void onPreExecute() {

			myProgress = 0;
		}

		protected Boolean doInBackground(Context... context) {

			results.clear();

			final String selectionString = Fixes.KEY_TRIPID + " = '"
					+ PropertyHolder.getTripId() + "' AND " + Fixes.KEY_DISPLAY
					+ " = " + Fixes.DISPLAY_TRUE + " AND " + Fixes.KEY_ROWID
					+ " > " + lastRecId;

			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(Fixes.CONTENT_URI, Fixes.KEYS_LATLONACCTIMES,
					selectionString, null, null);

			if (!c.moveToFirst()) {
				c.close();

				return false;
			}

			int latCol = c.getColumnIndexOrThrow(Fixes.KEY_LATITUDE);
			int lonCol = c.getColumnIndexOrThrow(Fixes.KEY_LONGITUDE);
			int accCol = c.getColumnIndexOrThrow(Fixes.KEY_ACCURACY);
			int idCol = c.getColumnIndexOrThrow(Fixes.KEY_ROWID);
			int timeCol = c.getColumnIndexOrThrow(Fixes.KEY_TIMELONG);
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

				lastRecId = c.getInt(idCol);

				// First grabbing double values of lat lon and time
				Double lat = c.getDouble(latCol);
				Double lon = c.getDouble(lonCol);
				float acc = c.getFloat(accCol);
				long entryTime = c.getLong(timeCol);
				long exitTime = c.getLong(sdtimeCol);

				Double geoLat = lat * 1E6;
				Double geoLon = lon * 1E6;

				results.add(new MapPoint(geoLat.intValue(), geoLon.intValue(),
						acc, entryTime, exitTime, MapPoint.ICON_NORMAL));

				c.moveToNext();
				currentRecord++;
			}
			c.close();
			return true;

		}

		protected void onProgressUpdate(Integer... progress) {
			progressbar.setProgress(progress[0]);
		}

		protected void onPostExecute(Boolean result) {

			if (result) {

				if (results != null && results.size() > 0) {

					for (MapPoint p : results) {
						mPoints.add(p);
					}

				}
			}

			drawFixes(mPoints, satToggle, true, true);

			progressbar.setVisibility(View.INVISIBLE);

			mSTB.setVisibility(View.VISIBLE);

			loadingData = false;

		}

	}

	public void buildCustomAlert(String message) {

		final Dialog dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.custom_alert);

		Util.overrideFonts(context, dialog.findViewById(android.R.id.content));

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
				finish();

			}
		});

		dialog.show();

	}

	private void buildFlushGPSAlert() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.renew_gps_alert))
				.setCancelable(true)
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {

								Util.flushGPSFlag = true;

								dialog.cancel();
							}
						})

				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.cancel();
							}
						});

		final AlertDialog alert = builder.create();
		alert.show();
	}

	public class TripUploadTask extends AsyncTask<Context, Integer, Boolean> {

		ProgressDialog prog;

		int myProgress;

		ContentResolver cr;

		int resultFlag;

		int OFFLINE_DELETE = 0;
		int OFFLINE_SAVE = 1;
		int UPLOAD_ERROR_DELETE = 2;
		int UPLOAD_ERROR_SAVE = 3;
		int SUCCESS_DELETE = 4;
		int SUCCESS_SAVE = 5;

		@Override
		protected void onPreExecute() {

			cr = getContentResolver();

			prog = new ProgressDialog(context);
			if (deleteTrip) {
				prog.setTitle(getResources().getString(
						R.string.progtitle_trip_deleting));
				// prog.setMessage(getResources().getString(
				// R.string.progmessage_trip_deleting));
			} else {
				prog.setTitle(getResources().getString(
						R.string.progtitle_trip_saving));
				// prog.setMessage(getResources().getString(
				// R.string.progmessage_trip_saving));

			}
			prog.setIndeterminate(false);
			prog.setMax(100);
			prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prog.show();

			myProgress = 0;

		}

		protected Boolean doInBackground(Context... context) {

			publishProgress(1);

			// Stop service if it is currently running
			Intent i2FixGet = new Intent(DriverMapActivity.this, FixGet.class);
			stopService(i2FixGet);

			// now unschedule
			Intent intent = new Intent("ceab.movlab.tigre.UNSCHEDULE_SERVICE");
			context[0].sendBroadcast(intent);

			PropertyHolder.setServiceOn(false);

			publishProgress(10);

			if (deleteTrip) {

				cr.delete(Fixes.CONTENT_URI, Fixes.KEY_TRIPID + " = '"
						+ PropertyHolder.getTripId() + "'", null);

				if (Util.isOnline(context[0]) && !Util.privateMode) {
					if (thisTrip.upload(context[0])) {

						publishProgress(60);

						cr.insert(Trips.CONTENT_URI, ContentProviderValuesTrips
								.createTrip(thisTrip, 1));

						publishProgress(80);

						resultFlag = SUCCESS_DELETE;

					} else {

						publishProgress(60);

						cr.insert(Trips.CONTENT_URI,
								ContentProviderValuesTrips.createTrip(thisTrip));

						Intent uploaderIntent = new Intent(
								DriverMapActivity.this, FileUploader.class);
						startService(uploaderIntent);

						publishProgress(80);

						resultFlag = UPLOAD_ERROR_DELETE;

					}
				} else {

					publishProgress(60);

					cr.insert(Trips.CONTENT_URI,
							ContentProviderValuesTrips.createTrip(thisTrip));

					Intent uploadSchedulerIntent = new Intent(
							"ceab.movlab.tigre.UPLOADS_NEEDED");
					context[0].sendBroadcast(uploadSchedulerIntent);

					publishProgress(80);

					resultFlag = OFFLINE_DELETE;

				}
			} else {

				if (Util.isOnline(context[0]) && !Util.privateMode) {
					if (thisTrip.upload(context[0])) {

						publishProgress(60);

						cr.insert(Trips.CONTENT_URI, ContentProviderValuesTrips
								.createTrip(thisTrip, 1));

						publishProgress(80);

						resultFlag = SUCCESS_SAVE;

					} else {

						publishProgress(60);

						cr.insert(Trips.CONTENT_URI,
								ContentProviderValuesTrips.createTrip(thisTrip));

						Intent uploaderIntent = new Intent(
								DriverMapActivity.this, FileUploader.class);
						startService(uploaderIntent);

						publishProgress(80);

						resultFlag = UPLOAD_ERROR_SAVE;
					}

				} else {

					publishProgress(60);

					cr.insert(Trips.CONTENT_URI,
							ContentProviderValuesTrips.createTrip(thisTrip));

					Intent uploadSchedulerIntent = new Intent(
							"ceab.movlab.tigre.UPLOADS_NEEDED");
					context[0].sendBroadcast(uploadSchedulerIntent);

					publishProgress(80);

					resultFlag = OFFLINE_SAVE;

				}
			}

			publishProgress(100);

			return true;
		}

		protected void onProgressUpdate(Integer... progress) {

			prog.setProgress(progress[0]);

		}

		protected void onPostExecute(Boolean result) {

			prog.dismiss();

			if (resultFlag == SUCCESS_DELETE) {
				Util.toast(context,
						getResources().getString(R.string.trip_deleted));
				finish();
			} else if (resultFlag == SUCCESS_SAVE) {
				Util.toast(context,
						getResources().getString(R.string.trip_data_sent));
				finish();
			} else if (resultFlag == UPLOAD_ERROR_DELETE) {
				buildCustomAlert(getResources().getString(
						R.string.upload_error_trip_delete));
			} else if (resultFlag == UPLOAD_ERROR_SAVE) {
				buildCustomAlert(getResources().getString(
						R.string.upload_error_trip_save));
			} else if (resultFlag == OFFLINE_SAVE) {
				if(Util.privateMode){
					buildCustomAlert(getResources().getString(
							R.string.saved));					
				} else{
				buildCustomAlert(getResources().getString(
						R.string.offline_trip_save));
				}
			} else if (resultFlag == OFFLINE_DELETE) {
				buildCustomAlert(getResources().getString(
						R.string.offline_trip_delete));
			}
		}
	}

}