package ceab.movelab.tigabib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import ceab.movelab.tigabib.ContProvContractMissions.Tasks;

public class FixUse extends Service {

	private static String TAG = "FixUse";

	private boolean inUse = false;

	Context context;

	ContentResolver cr;
	Cursor c;

	double lat = 0;
	double lon = 0;
	long time = 0;
	float power = 0;

	@Override
	public void onStart(Intent intent, int startId) {

		Util.logInfo(context, TAG, "on start");

		if (!inUse) {
			inUse = true;

			this.lat = intent.getDoubleExtra(
					Messages.makeIntentExtraKey(context, FixGet.KEY_LAT), 0);
			this.lon = intent.getDoubleExtra(
					Messages.makeIntentExtraKey(context, FixGet.KEY_LON), 0);
			this.time = intent.getLongExtra(
					Messages.makeIntentExtraKey(context, FixGet.KEY_TIME), 0);
			this.power = intent.getFloatExtra(
					Messages.makeIntentExtraKey(context, FixGet.KEY_POWER), 0);

			Thread uploadThread = new Thread(null, doUseFix, "useFixBackground");
			uploadThread.start();

		}
	};

	private Runnable doUseFix = new Runnable() {
		public void run() {
			useFix();
		}
	};

	@Override
	public void onCreate() {

		// Log.e(TAG, "FileUploader onCreate.");

		context = getApplicationContext();
		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	private void useFix() {

		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);
		ContentResolver cr = getContentResolver();

		double maskedLat = Math.floor(lat / Util.latMask) * Util.latMask;
		double maskedLon = Math.floor(lon / Util.lonMask) * Util.lonMask;

		Fix thisFix = new Fix(maskedLat, maskedLon, time, power);

		Util.logInfo(context, TAG, "this fix: " + thisFix.lat + ';'
				+ thisFix.lng + ';' + thisFix.time + ';' + thisFix.pow);

		cr.insert(Util.getTracksUri(context), ContProvValuesTracks.createFix(
				thisFix.lat, thisFix.lng, thisFix.time, thisFix.pow));

		// Check for location-based tasks

		int thisHour = Util.hour(time);

		String sc1 = Tasks.KEY_TRIGGERS + " IS NOT NULL AND "
				+ Tasks.KEY_ACTIVE + " = 0 AND " + Tasks.KEY_DONE
				+ " = 0 AND (" + Tasks.KEY_EXPIRATION_TIME + " >= "
				+ System.currentTimeMillis() + " OR "
				+ Tasks.KEY_EXPIRATION_TIME + " = 0)";

		Util.logInfo(context, TAG, "sql: " + sc1);

		// grab tasks that have location triggers, that are not yet active, that
		// are not yet done, and that have not expired
		Cursor c = cr.query(Util.getMissionsUri(context), Tasks.KEYS_TRIGGERS,
				sc1, null, null);

		while (c.moveToNext()) {

			try {
				JSONArray theseTriggers = new JSONArray(c.getString(c
						.getColumnIndexOrThrow(Tasks.KEY_TRIGGERS)));

				for (int i = 0; i < theseTriggers.length(); i++) {

					JSONObject thisTrigger = theseTriggers.getJSONObject(i);

					Util.logInfo(context, TAG,
							"thisTrigger: " + thisTrigger.toString());
					Util.logInfo(context, TAG, "thisLoc Lat:" + lat + " Lon:"
							+ lon);

					Util.logInfo(
							context,
							TAG,
							"this trigger time lower bound equals null "
									+ (thisTrigger
											.getString(MissionModel.KEY_TASK_TRIGGER_TIME_LOWERBOUND)
											.equals("null")));

					if (lat >= thisTrigger
							.getDouble(MissionModel.KEY_TASK_TRIGGER_LAT_LOWERBOUND)
							&& lat <= thisTrigger
									.getDouble(MissionModel.KEY_TASK_TRIGGER_LAT_UPPERBOUND)
							&& lon >= thisTrigger
									.getDouble(MissionModel.KEY_TASK_TRIGGER_LON_LOWERBOUND)
							&& lon <= thisTrigger
									.getDouble(MissionModel.KEY_TASK_TRIGGER_LON_UPPERBOUND)
							&& (thisTrigger
									.getString(
											MissionModel.KEY_TASK_TRIGGER_TIME_LOWERBOUND)
									.equals("null") || (thisHour >= Util
									.triggerTime2HourInt(thisTrigger
											.getString(MissionModel.KEY_TASK_TRIGGER_TIME_LOWERBOUND))))
							&& (thisTrigger
									.getString(
											MissionModel.KEY_TASK_TRIGGER_TIME_UPPERBOUND)
									.equals("null") || (thisHour <= Util
									.triggerTime2HourInt(thisTrigger
											.getString(MissionModel.KEY_TASK_TRIGGER_TIME_LOWERBOUND)))

							)) {

						Util.logInfo(context, TAG, "task triggered");

						ContentValues cv = new ContentValues();
						int rowId = c.getInt(c
								.getColumnIndexOrThrow(Tasks.KEY_ROW_ID));
						String sc = Tasks.KEY_ROW_ID + " = " + rowId;
						cv.put(Tasks.KEY_ACTIVE, 1);
						cr.update(Util.getMissionsUri(context), cv, sc, null);

						Intent intent = new Intent(
								Messages.internalAction(context));
						intent.putExtra(Messages.INTERNAL_MESSAGE_EXTRA,
								Messages.SHOW_TASK_NOTIFICATION);
						if (PropertyHolder.getLanguage().equals("ca")) {
							intent.putExtra(
									Tasks.KEY_TITLE,
									c.getString(c
											.getColumnIndexOrThrow(Tasks.KEY_TITLE_CATALAN)));
						} else if (PropertyHolder.getLanguage().equals("es")) {
							intent.putExtra(
									Tasks.KEY_TITLE,
									c.getString(c
											.getColumnIndexOrThrow(Tasks.KEY_TITLE_SPANISH)));
						} else if (PropertyHolder.getLanguage().equals("en")) {
							intent.putExtra(
									Tasks.KEY_TITLE,
									c.getString(c
											.getColumnIndexOrThrow(Tasks.KEY_TITLE_ENGLISH)));
						}
						context.sendBroadcast(intent);
					}
				}

			} catch (IllegalArgumentException e) {
				Util.logError(context, TAG, "error: " + e);
			} catch (JSONException e) {
				Util.logError(context, TAG, "error: " + e);
			}

		}

		c.close();

		inUse = false;

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
