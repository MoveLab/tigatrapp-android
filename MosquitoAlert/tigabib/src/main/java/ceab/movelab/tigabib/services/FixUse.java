package ceab.movelab.tigabib.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ceab.movelab.tigabib.ContProvContractMissions.Tasks;
import ceab.movelab.tigabib.ContProvValuesTracks;
import ceab.movelab.tigabib.Messages;
import ceab.movelab.tigabib.MissionModel;
import ceab.movelab.tigabib.PropertyHolder;
import ceab.movelab.tigabib.Util;

public class FixUse extends Service {
	private static final String TAG = "FixUse";

	private Context context;
	private boolean inUse = false;

	private double lat = 0;
	private double lon = 0;
	private long time = 0;
	private float power = 0;
	private boolean taskFix = false;

	@Override
	public void onCreate() {
Util.logInfo(TAG, "FixUse onCreate.");
		context = getApplicationContext();
		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(context);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//public void onStaStart(Intent intent, int startId){
Util.logInfo(TAG, "on start FixUse ");

		if ( !PropertyHolder.hasReconsented() || Util.privateMode() ) {
			stopSelf();
		} else {
			if ( !inUse ) {
				inUse = true;
				if ( intent != null
						&& intent.hasExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_LAT))
						&& intent.hasExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_LON))
						&& intent.hasExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_TIME))
						&& intent.hasExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_POWER))
						&& intent.hasExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_TASK_FIX))) {

					this.lat = intent.getDoubleExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_LAT), 0);
					this.lon = intent.getDoubleExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_LON), 0);
					this.time = intent.getLongExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_TIME), 0);
					this.power = intent.getFloatExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_POWER), 0);
					this.taskFix = intent.getBooleanExtra(Messages.makeIntentExtraKey(context, FixGet.KEY_TASK_FIX), false);

					Thread uploadThread = new Thread(null, doUseFix, "useFixBackground");
					uploadThread.start();
				}
			}
		}
		return START_STICKY_COMPATIBILITY;
	}

	private Runnable doUseFix = new Runnable() {
		public void run() {
			useFix();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void useFix() {
		if ( Math.abs(lat) < Util.FIX_LAT_CUTOFF ) {
			if ( !PropertyHolder.isInit() )
				PropertyHolder.init(context);
			ContentResolver cr = getContentResolver();

			double maskedLat = Math.floor(lat / Util.latMask) * Util.latMask;
			double maskedLon = Math.floor(lon / Util.lonMask) * Util.lonMask;
			long thisHour = Util.hour(time);

			//Fix thisFix = new Fix(maskedLat, maskedLon, time, power, taskFix); original version
			Fix thisFix = new Fix(maskedLat, maskedLon, thisHour, power, taskFix);
Util.logInfo(TAG, "this fix: " + thisFix.getLat() + ';' + thisFix.getLng() + ';' + thisFix.getTime() + ';' + thisFix.getPow());
			cr.insert(Util.getTracksUri(context),
					ContProvValuesTracks.createFix(thisFix.getLat(), thisFix.getLng(), thisFix.getTime(), thisFix.getPow(), taskFix));

			// Check for location-based tasks
			String sc1 = Tasks.KEY_TRIGGERS + " IS NOT NULL AND "
					+ Tasks.KEY_ACTIVE + " = 0 AND " + Tasks.KEY_DONE + " = 0 "
					+ " AND (" + Tasks.KEY_EXPIRATION_TIME + " >= " + System.currentTimeMillis()
					+ " OR " + Tasks.KEY_EXPIRATION_TIME + " = 0)";
Util.logInfo(TAG, "sql: " + sc1);

			// grab tasks that have location triggers, that are not yet active, that are not yet done, and that have not expired
			Cursor c = cr.query(Util.getMissionsUri(context), Tasks.KEYS_TRIGGERS, sc1, null, null);
Util.logInfo(TAG, "#location-based tasks: " + (c != null ? c.getCount() : "-1"));

			while ( c.moveToNext() ) {
				try {
					JSONArray theseTriggers = new JSONArray(c.getString(c.getColumnIndexOrThrow(Tasks.KEY_TRIGGERS)));
					for (int i = 0; i < theseTriggers.length(); i++) {
						JSONObject thisTrigger = theseTriggers.getJSONObject(i);
Util.logInfo(TAG, "thisTrigger: " + thisTrigger.toString());
Util.logInfo(TAG, "thisLoc Lat:" + lat + " Lon:" + lon);
Util.logInfo(TAG, "this trigger time lower bound equals null "
		+ (thisTrigger.getString(MissionModel.KEY_TASK_TRIGGER_TIME_LOWERBOUND).equals("null")));

						if ( lat >= thisTrigger.getDouble(MissionModel.KEY_TASK_TRIGGER_LAT_LOWERBOUND)
								&& lat <= thisTrigger.getDouble(MissionModel.KEY_TASK_TRIGGER_LAT_UPPERBOUND)
								&& lon >= thisTrigger.getDouble(MissionModel.KEY_TASK_TRIGGER_LON_LOWERBOUND)
								&& lon <= thisTrigger.getDouble(MissionModel.KEY_TASK_TRIGGER_LON_UPPERBOUND)
								&& (thisTrigger.getString(MissionModel.KEY_TASK_TRIGGER_TIME_LOWERBOUND).equals("null") ||
								(thisHour >= Util.triggerTime2HourInt(thisTrigger.getString(MissionModel.KEY_TASK_TRIGGER_TIME_LOWERBOUND))))
								&& (thisTrigger.getString(MissionModel.KEY_TASK_TRIGGER_TIME_UPPERBOUND).equals("null") ||
								(thisHour <= Util.triggerTime2HourInt(thisTrigger.getString(MissionModel.KEY_TASK_TRIGGER_TIME_LOWERBOUND))))
							) {
Util.logInfo(TAG, "task triggered");
							ContentValues cv = new ContentValues();
							cv.put(Tasks.KEY_ACTIVE, 1);
							int rowId = c.getInt(c.getColumnIndexOrThrow(Tasks.KEY_ROW_ID));
							String sc = Tasks.KEY_ROW_ID + " = " + rowId;
							cr.update(Util.getMissionsUri(context), cv, sc, null);

							Intent intent = new Intent(Messages.internalAction(context));
							intent.putExtra(Messages.INTERNAL_MESSAGE_EXTRA, Messages.SHOW_TASK_NOTIFICATION);
							if (PropertyHolder.getLanguage().equals("ca")) {
								intent.putExtra(Tasks.KEY_TITLE, c.getString(c.getColumnIndexOrThrow(Tasks.KEY_TITLE_CATALAN)));
							} else if (PropertyHolder.getLanguage().equals("es")) {
								intent.putExtra(Tasks.KEY_TITLE, c.getString(c.getColumnIndexOrThrow(Tasks.KEY_TITLE_SPANISH)));
							} else if (PropertyHolder.getLanguage().equals("en")) {
								intent.putExtra(Tasks.KEY_TITLE, c.getString(c.getColumnIndexOrThrow(Tasks.KEY_TITLE_ENGLISH)));
							}
							intent.setPackage(context.getPackageName());
							context.sendBroadcast(intent);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Util.logCrashlyticsException("FixUse JSON error", e);
				}
			}
			c.close();
		}

		inUse = false;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
